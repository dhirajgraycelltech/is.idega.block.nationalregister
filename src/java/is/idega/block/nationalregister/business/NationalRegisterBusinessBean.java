package is.idega.block.nationalregister.business;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.nationalregister.data.NationalRegister;
import is.idega.block.nationalregister.data.NationalRegisterHome;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public class NationalRegisterBusinessBean extends IBOServiceBean implements NationalRegisterBusiness, UserGroupPlugInBusiness {
	
	private static int icelandCountryPK = -1;
	private static Gender maleGender = null;
	private static Gender femaleGender = null;
	private static HashMap postalCodes = null;
	
	public NationalRegister getEntryBySSN(String ssn) {
		try {
			Collection c = getNationalRegisterHome().findAllBySSN(ssn);

			if (c != null) {
				Iterator it = c.iterator();
				if (it.hasNext()) {
					return (NationalRegister) it.next();
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace(System.err);
		}
		catch (FinderException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public boolean updateEntry(
		String symbol,
		String oldId,
		String ssn,
		String familyId,
		String name,
		String commune,
		String street,
		String building,
		String floor,
		String sex,
		String maritialStatus,
		String empty,
		String prohibitMarking,
		String nationality,
		String placeOfBirth,
		String spouseSSN,
		String fate,
		String parish,
		String po,
		String address,
		// Gimmi, because of E36
		String addressCode,
		String dateOfModification,
		String placementCode,
		String dateOfCreation,
		String lastDomesticAddress,
		String agentSsn,
		String sNew,
		String addressName,
		String dateOfDeletion,
		String newSsnOrName,
		String dateOfBirth	, 
		Group citizenGroup) {

		
		String oldFamilyID = null;
		
			
		try {
			UserBusiness userBiz = (UserBusiness) getServiceInstance(UserBusiness.class);	
			NationalRegister reg = getEntryBySSN(ssn);
			if (reg == null) {
				reg = getNationalRegisterHome().create();
			} else {
				oldFamilyID = reg.getFamilyId();
			}
			reg.setAddress(address);
			reg.setBuilding(building);
			reg.setCommune(commune);
			reg.setFamilyId(familyId);
			reg.setFate(fate);
			reg.setFloor(floor);
			reg.setMaritalStatus(maritialStatus);
			reg.setName(name);
			reg.setNationality(nationality);
			reg.setOldId(oldId);
			reg.setParish(parish);
			reg.setPlaceOfBirth(placeOfBirth);
			reg.setPO(po);
			reg.setProhibitMarking(prohibitMarking);
			reg.setSex(sex);
			reg.setSpouseSSN(spouseSSN);
			reg.setSSN(ssn);
			reg.setStreet(street);
			reg.setSymbol(symbol);
			reg.setAddressCode(addressCode);
			reg.setDateOfModification(dateOfModification);
			reg.setPlacementCode(placementCode);
			reg.setDateOfCreation(dateOfCreation);
			reg.setLastDomesticAddress(lastDomesticAddress);
			reg.setAgentSSN(agentSsn);
			reg.setIsNew(sNew);
			reg.setAddressName(addressName);
			reg.setDateOfDeletion(dateOfDeletion);
			reg.setNewSsnOrName(newSsnOrName);
			reg.setDateOfBirth(dateOfBirth);
			
			reg.store();
			
			IWTimestamp t = new IWTimestamp();
			
			String day = ssn.substring(0,2);
			String month = ssn.substring(2,4);
			String year = ssn.substring(4,6);
				
			int iDay = Integer.parseInt(day);
			int iMonth = Integer.parseInt(month);
			int iYear = Integer.parseInt(year);
			if (ssn.substring(9).equals("9"))
				iYear += 1900;
			else if (ssn.substring(9).equals("0"))
				iYear += 2000;
			else if (ssn.substring(9).equals("8"))
				iYear += 1800;
			t.setHour(0);
			t.setMinute(0);
			t.setSecond(0);
			t.setMilliSecond(0);
			t.setDay(iDay);
			t.setMonth(iMonth);
			t.setYear(iYear);
			
			Gender gender = getGender(sex);

			User user = userBiz.createUserByPersonalIDIfDoesNotExist(name,ssn,gender,t);
			//user.setDisplayName(name);

			if (newSsnOrName != null && "".equalsIgnoreCase(newSsnOrName)) {
				try {
					Long.parseLong(newSsnOrName);
					user.setPersonalID(newSsnOrName);
					user.store();
					log("Changing user's personalID to "+newSsnOrName);
				} catch (NumberFormatException n) {
					user.setFullName(newSsnOrName);
					user.store();
					log("Changing user's name to "+newSsnOrName);
				}
			}
			
			
			
			updateUserAddress(user, userBiz, address, po);
			
			if (citizenGroup != null) {
				citizenGroup.addGroup(user);
				user.setPrimaryGroup(citizenGroup);
				user.store();
			}

			FamilyLogic familyLogic = getFamilyLogic();
			familyLogic.updateFamilyForUser(familyId, user);

//			userBiz.updateUsersMainAddressOrCreateIfDoesNotExist((Integer) user.getPrimaryKey(), address, postalCodeId, null, null, null, null, null);
//			userBiz.updateUsersCoAddressOrCreateIfDoesNotExist((Integer) user.getPrimaryKey(), address, postalCodeId, null, null, null, null, null);
		}
		catch (CreateException e) {
			e.printStackTrace();
			return false;
		}
		catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		catch (FinderException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public void updateUserPersonalID(String oldPersonalID, String newPersonalID) throws IBOLookupException{
		UserBusiness userBiz = (UserBusiness) getServiceInstance(UserBusiness.class);	
		NationalRegister reg = getEntryBySSN(oldPersonalID);

		if (reg != null) {
			reg.setSSN(newPersonalID);
			reg.store();
		}
	}
	
	public void updateUserOldID(String oldID, String personalID) throws IBOLookupException{
		UserBusiness userBiz = (UserBusiness) getServiceInstance(UserBusiness.class);	
		NationalRegister reg = getEntryBySSN(personalID);

		if (reg != null) {
			reg.setOldId(oldID);
			reg.store();
		}
	}
	
	public void updateUserAddress(User user, UserBusiness userBiz, String address, String po) throws RemoteException, CreateException {
		PostalCode postalCode = getPostalCode(po);

		userBiz.updateUsersMainAddressOrCreateIfDoesNotExist(user, address, postalCode, null, null, null, null, null);
		userBiz.updateUsersCoAddressOrCreateIfDoesNotExist(user, address, postalCode, null, null, null, null, null);
	}

	public PostalCode getPostalCode(String po) throws RemoteException {
		if (postalCodes == null) {
			postalCodes = new HashMap();
		}
		
		if (po != null && !po.trim().equals("")) {
			if (postalCodes.containsKey(po)) {
				return (PostalCode) postalCodes.get(po);
			} else {
				try {				
					PostalCode poCode = ((PostalCodeHome)getIDOHome(PostalCode.class)).findByPostalCodeAndCountryId(po,getIcelandicCountryPK());
					postalCodes.put(po, poCode);
					System.out.println("NationalRegisterBusinessBean : looking up postal code "+po);
					return poCode;
				}
				catch(FinderException e) {
					postalCodes.put(po, null);
					System.out.println("NationalRegisterBusinessBean : looking up postal code NULL");
					return null;
				}
			}
		} 
		return null;
	}

	private Gender getGender(String sex) throws RemoteException, FinderException {
		if (maleGender == null || femaleGender == null) {
			GenderHome home = (GenderHome) getIDOHome(Gender.class);
			maleGender = home.getMaleGender();
			femaleGender = home.getFemaleGender();
			System.out.println("NationalRegisterBusinessBean : setting up gender");
		}
		if (sex == null) {
			return null;
		} else if (sex.equals("1") || sex.equals("3")) {
			return maleGender;
		}	else {
			return femaleGender;
		}
		
	}

	private int getIcelandicCountryPK() throws RemoteException, FinderException {
		if (icelandCountryPK < 1) {
			Country country = ((CountryHome)getIDOHome(Country.class)).findByIsoAbbreviation("IS");
			icelandCountryPK = ((Integer) country.getPrimaryKey()).intValue();
			System.out.println("NationalRegisterBusinessBean : setting icelandCountryPK ("+icelandCountryPK+")");
		}
		return icelandCountryPK;
	}

	protected NationalRegisterHome getNationalRegisterHome() {
		NationalRegisterHome home = null;
		try {
			home = (NationalRegisterHome) IDOLookup.getHome(NationalRegister.class);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}

		return home;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeUserRemove(com.idega.user.data.User)
	 */
	public void beforeUserRemove(User user) throws RemoveException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterUserCreate(com.idega.user.data.User)
	 */
	public void afterUserCreate(User user) throws CreateException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeGroupRemove(com.idega.user.data.Group)
	 */
	public void beforeGroupRemove(Group group) throws RemoveException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterGroupCreate(com.idega.user.data.Group)
	 */
	public void afterGroupCreate(Group group) throws CreateException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getPresentationObjectClass()
	 */
	public Class getPresentationObjectClass() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateEditor(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateEditor(Group group) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateViewer(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateViewer(Group group) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getUserPropertiesTabs(com.idega.user.data.User)
	 */
	public List getUserPropertiesTabs(User user) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupPropertiesTabs(com.idega.user.data.Group)
	 */
	public List getGroupPropertiesTabs(Group group) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getMainToolbarElements()
	 */
	public List getMainToolbarElements() throws RemoteException {
		List list = new ArrayList(1);
		list.add(new NationalRegisterFileImportHandlerPlugin());
		return list;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupToolbarElements(com.idega.user.data.Group)
	 */
	public List getGroupToolbarElements(Group group) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getListViewerFields()
	 */
	public Collection getListViewerFields() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#findGroupsByFields(java.util.Collection, java.util.Collection, java.util.Collection)
	 */
	public Collection findGroupsByFields(Collection listViewerFields, Collection finderOperators, Collection listViewerFieldValues) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#isUserAssignableFromGroupToGroup(com.idega.user.data.User, com.idega.user.data.Group, com.idega.user.data.Group)
	 */
	public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.business.UserGroupPlugInBusiness#isUserSuitedForGroup(com.idega.user.data.User, com.idega.user.data.Group)
	 */
	public String isUserSuitedForGroup(User user, Group targetGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	public FamilyLogic getFamilyLogic() throws RemoteException {
		return (FamilyLogic) IBOLookup.getServiceInstance(getIWApplicationContext(), FamilyLogic.class);
	}

}