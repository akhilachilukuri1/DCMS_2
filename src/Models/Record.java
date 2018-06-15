package Models;
/**
 * This class creates the Object of Record type and implements serialization
 */
import java.io.Serializable;

//This Class holds the setters and getters for the firstName,lastName and ID for the every record that is inserted into the server
public abstract class Record implements Serializable{
	private String firstName;
	private String lastname;
	private String recordID;
	
	//instantiating the record object with the given details
		public Record(String recordID, String firstName, String lastname) {
			this.setFirstName(firstName);
			this.setLastName(lastname);
			this.setRecordID(recordID);
		}

	
	public abstract String serialize();

//getting the record ID 
	public String getRecordID() {
		return recordID;
	}
//setting the record ID for the every record that is inserted into the server
	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}

	public Record() {

	}
//getting the first name of the record
	public String getFirstName() {
		return firstName;
	}
//setting the first name of the record
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
//getting the last name of the record
	public String getLastName() {
		return lastname;
	}
//setting the last name of the record
	public void setLastName(String lastname) {
		this.lastname = lastname;
	}

}