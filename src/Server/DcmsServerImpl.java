package Server;

import DcmsApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import Conf.Constants;
import Conf.LogManager;
import Conf.ServerCenterLocation;
import Models.Record;
import Models.Student;
import Models.Teacher;

/**
 * 
 * DcmsServerImpl performs all the functionality for MTL,LVL and DDO server Locations
 *
 */


class DcmsServerImpl extends DcmsPOA {
	private ORB orb;

	LogManager logManager;
	ServerUDP serverUDP;
	String IPaddress;
	public HashMap<String, List<Record>> recordsMap;
	int studentCount = 0;
	int teacherCount = 0;
	String recordsCount;
	String location;

	public DcmsServerImpl(ServerCenterLocation loc) {
		logManager = new LogManager(loc.toString());
		recordsMap = new HashMap<>();
		serverUDP = new ServerUDP(loc, logManager.logger, this);
		serverUDP.start();
		location = loc.toString();
		setIPAddress(loc);
	}

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	public void shutdown() {
		orb.shutdown(false);
	}

	private void setIPAddress(ServerCenterLocation loc) {
		switch (loc) {
		case MTL:
			IPaddress = Constants.MTL_SERVER_ADDRESS;
			break;
		case LVL:
			IPaddress = Constants.LVL_SERVER_ADDRESS;
			break;
		case DDO:
			IPaddress = Constants.DDO_SERVER_ADDRESS;
			break;
		}
	}


	/**
	 *Once the teacher record is created,
	 *createTRRecord function returns the record ID of the teacher record created to the client
	 *@param managerID gets the managerID 
	 *@param teacherField values of the teacher attribute concatenated by the comma
	 *which are received from the client 
	 *  
	 */
		
	@Override
	public synchronized String createTRecord(String managerID,String teacher) {

		String temp[] = teacher.split(",");
		//String managerID = temp[0];
		String teacherID = "TR" + (++teacherCount);
		String firstName = temp[0];
		String lastname = temp[1];
		String address = temp[2];
		String phone = temp[3];
		String specialization = temp[4];
		String location = temp[5];
		Teacher teacherObj = new Teacher(managerID, teacherID, firstName, lastname, address, phone, specialization,
				location);
		String key = lastname.substring(0, 1);
		String message = addRecordToHashMap(key, teacherObj, null);
		System.out.println("teacher is added " + teacherObj + " with this key " + key + " by Manager " + managerID);
		logManager.logger.log(Level.INFO, "Teacher record created " + teacherID + " by Manager : " + managerID);
		return teacherID;

	}
	
	/**
	 *Once the student record is created,
	 *the function createSRecord returns the record ID of the student record created to the client
	 *@param managerID gets the managerID 
	 *@param studentFields values of the student attribute concatenated by the comma
	 *which are received the client 
	 *  
	 */
	
	@Override
	public synchronized String createSRecord(String managerID,String student) {

		String temp[] = student.split(",");
		//String managerID = temp[0];
		String firstName = temp[0];
		String lastName = temp[1];
		String CoursesRegistered = temp[2];
		List<String> courseList = putCoursesinList(CoursesRegistered);
		String status = temp[3];
		String statusDate = temp[4];
		String studentID = "SR" + (++studentCount);
		Student studentObj = new Student(managerID, studentID, firstName, lastName, courseList, status, statusDate);
		String key = lastName.substring(0, 1);
		String message = addRecordToHashMap(key, null, studentObj);
		if (message.equals("success")) {
			System.out
					.println(" Student is added " + studentObj + " with this key " + key + " by Manager " + managerID);
			logManager.logger.log(Level.INFO, "Student record created " + studentID + " by manager : " + managerID);
		}
		return studentID;
	}
	
	
	/**
	 *Adds the Teacher and Student to the HashMap
	 *the function addRecordToHashMap returns the success message, if the student / teacher record is created successfully 
	 *else returns Error message
	 *@param key gets the key of the recordID stored in the HashMap 
	 *@param teacher gets the teacher object if received from createTRecord function
	 *@param student gets the student object if received from createSRecord function  
	 *which are received the respective functions. 
	 *  
	 */
	
	private synchronized String addRecordToHashMap(String key, Teacher teacher, Student student) {
		String message = "Error";
		if (teacher != null) {
			List<Record> recordList = recordsMap.get(key);
			if (recordList != null) {
				recordList.add(teacher);
			} else {
				List<Record> records = new ArrayList<Record>();
				records.add(teacher);
				recordList = records;
			}
			recordsMap.put(key, recordList);
			message = "success";
		}

		if (student != null) {
			List<Record> recordList = recordsMap.get(key);
			if (recordList != null) {
				recordList.add(student);
			} else {
				List<Record> records = new ArrayList<Record>();
				records.add(student);
				recordList = records;
			}
			recordsMap.put(key, recordList);
			message = "success";
		}

		return message;
	}

	/**
	 *
	 *returns the current server record count
	 *  
	 */
	
	private int getCurrServerCnt() {
		int count = 0;
		for (Map.Entry<String, List<Record>> entry : this.recordsMap.entrySet()) {
			List<Record> list = entry.getValue();
			count += list.size();
		}
		return count;
	}

	/**
	 *invokes record count on the corresponding MTL/LVL/DDO server to get record count on all the servers
	 *  
	 */
	
	@Override
	public String getRecordCount() {
		String recordCount = null;
		UDPRequestProvider[] req = new UDPRequestProvider[2];
		int counter = 0;
		ArrayList<String> locList = new ArrayList<>();
		locList.add("MTL");
		locList.add("LVL");
		locList.add("DDO");
		for (String loc : locList) {
			if (loc == this.location) {
				recordCount = loc + "," + getCurrServerCnt();
			} else {
				try {
					req[counter] = new UDPRequestProvider(DcmsServer.serverRepo.get(loc));
				} catch (IOException e) {
					logManager.logger.log(Level.SEVERE, e.getMessage());
				}
				req[counter].start();
				counter++;
			}
		}
		for (UDPRequestProvider request : req) {
			try {
				request.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			recordCount += " , " + request.getRemoteRecordCount().trim();
		}
		return recordCount;
	}

	
	/**
	 *The edit record function performs the edit operation on the server and returns the appropriate message
	 *@param managerID gets the managerID 
	 *@param recordID gets the recordID to be edited
	 *@param fieldname gets the fieldname to be edited for the given recordID
	 *@param newvalue gets the newvalue to be replaced to the given fieldname 
	 *from the client
	 */

	@Override
	public String editRecord(String managerID, String recordID, String fieldname, String newvalue) {
		String type = recordID.substring(0, 2);
		if (type.equals("TR")) {
			return editTRRecord(managerID, recordID, fieldname, newvalue);
		} else if (type.equals("SR")) {
			return editSRRecord(managerID, recordID, fieldname, newvalue);
		}
		logManager.logger.log(Level.INFO, "Record edit successful");
		return "Operation not performed!";
	}

	
	/**
	 *Performs the transfer record to the remoteCenterServer by calling the  transferTRRecord or transferSRRecord
	 *and return the appropriate message
	 *@param managerID gets the managerID 
	 *@param recordID gets the recordID to be edited
	 *@param remoteCenterServerName gets the location to transfer the recordID 
	 *from the client
	 */
	public String transferRecord(String ManagerID, String recordID, String remoteCenterServerName) {
		String type = recordID.substring(0, 2);
		if (type.toUpperCase().equals("TR")) {
			return transferTRRecord(ManagerID, recordID, remoteCenterServerName);
		}else if (type.toUpperCase().equals("SR")) {
			return transferSRRecord(ManagerID, recordID, remoteCenterServerName);
		}
		logManager.logger.log(Level.INFO, "Record transfer successful");
		return "Operation not performed!";
	}
	
	
	/**
	 *Performs the transfer Teacher record to the destination remoteCenterServer and return the appropriate message
	 *@param managerID gets the managerID 
	 *@param recordID gets the recordID to be edited
	 *@param remoteCenterServerName gets the location to transfer the recordID 
	 *from the client
	 */
	
	public synchronized String transferTRRecord(String ManagerID, String recordID, String remoteCenterServerName) {
		logManager.logger.log(Level.INFO, ManagerID + " has initiated the record transfer for the recordID: " + recordID
				+ " operation from " + this.IPaddress + " to " + remoteCenterServerName);
		String TeacherID = null;
		
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

			if (record.isPresent()) {
				if (!this.location.equalsIgnoreCase(remoteCenterServerName)) {
					Teacher teachobj = (Teacher) record.get();
					System.out.println("Techer transfer$$" + teachobj);
					if (remoteCenterServerName.equalsIgnoreCase("MTL")) {
						String key = val.getKey();
						mylist.remove(teachobj);
						recordsMap.put(key, mylist);
						TeacherID = DcmsServer.mtlhref.createTRecord(ManagerID,teachobj.getFirstName() + ","
								+ teachobj.getLastName() + "," + teachobj.getAddress() + "," + teachobj.getPhone() + ","
								+ teachobj.getSpecilization() + "," + teachobj.getLocation());
						logManager.logger.log(Level.INFO,
								ManagerID + " has tranferred the teacher Record with recordID " + recordID + " from "
										+ teachobj.getLocation() + " to MTL with recordID " + TeacherID + "in MTL");
						return "record with ID " + recordID + " in " + teachobj.getLocation()
								+ " is transferred to MTL with recordID " + TeacherID + " in MTL";
					} else if (remoteCenterServerName.equalsIgnoreCase("LVL")) {
						String key = val.getKey();
						mylist.remove(teachobj);
						recordsMap.put(key, mylist);
						TeacherID = DcmsServer.lvlhref.createTRecord(ManagerID,teachobj.getFirstName() + ","
								+ teachobj.getLastName() + "," + teachobj.getAddress() + "," + teachobj.getPhone() + ","
								+ teachobj.getSpecilization() + "," + teachobj.getLocation());
						logManager.logger.log(Level.INFO,
								ManagerID + " has tranferred the teacher Record with recordID " + recordID + " from "
										+ teachobj.getLocation() + " to LVL with recordID " + TeacherID + " in LVL");
						return "record with ID " + recordID + " in " + teachobj.getLocation()
								+ " is transferred to LVL with recordID " + TeacherID + " in LVL";
					} else if (remoteCenterServerName.equalsIgnoreCase("DDO")) {
						String key = val.getKey();
						mylist.remove(teachobj);
						recordsMap.put(key, mylist);
						TeacherID = DcmsServer.ddohref.createTRecord(ManagerID,teachobj.getFirstName() + ","
								+ teachobj.getLastName() + "," + teachobj.getAddress() + "," + teachobj.getPhone() + ","
								+ teachobj.getSpecilization() + "," + teachobj.getLocation());
						logManager.logger.log(Level.INFO,
								ManagerID + " has tranferred the teacher Record with recordID " + recordID + " from "
										+ teachobj.getLocation() + "to DDO with recordID " + TeacherID + " in DDO");
						return "record with ID " + recordID + " in " + teachobj.getLocation()
								+ " is transferred to DDO with recordID " + TeacherID + " in DDO";
					}
					logManager.logger.log(Level.INFO, "Updated the records\t" + location);
					return "record with ID " + recordID + " is not transferred successfully ";

				}

				else {
					return "Transfer aborted to same server..Try different server!!";
				}
			}
		}
		return "Record with " + recordID + " not found";

	}
	
	/**
	 *Performs the transfer Student record to the destination remoteCenterServer and return the appropriate message
	 *@param managerID gets the managerID 
	 *@param recordID gets the recordID to be edited
	 *@param remoteCenterServerName gets the location to transfer the recordID 
	 *from the client
	 */

	public synchronized String transferSRRecord(String ManagerID, String recordID, String remoteCenterServerName) {
		logManager.logger.log(Level.INFO, ManagerID + " has initiated the record transfer for the recordID: " + recordID
				+ " operation from " + this.IPaddress + " to " + remoteCenterServerName);
		String StudentID = null;
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

			// System.out.println(record);
			if (record.isPresent()) {
				if (!this.location.equalsIgnoreCase(remoteCenterServerName)) {
					Student studentobj = (Student) record.get();
					System.out.println("Student transfer$$" + studentobj);
					if (remoteCenterServerName.equalsIgnoreCase("MTL")) {
						String key = val.getKey();
						mylist.remove(studentobj);
						recordsMap.put(key, mylist);
						StudentID = DcmsServer.mtlhref.createSRecord(ManagerID,studentobj.getFirstName() + ","
								+ studentobj.getLastName() + "," + studentobj.getCoursesRegistered() + ","
								+ studentobj.isStatus() + "," + studentobj.getStatusDate());
						logManager.logger.log(Level.INFO,
								ManagerID + " has tranferred the student Record with recordID " + recordID + " from "
										+ this.location + " to MTL with recordID " + StudentID + " in MTL");
						return "record with ID " + recordID + " in " + this.location
								+ " is transferred to MTL with recordID " + StudentID + " in MTL";
					} else if (remoteCenterServerName.equalsIgnoreCase("LVL")) {
						String key = val.getKey();
						mylist.remove(studentobj);
						recordsMap.put(key, mylist);
						StudentID = DcmsServer.lvlhref.createSRecord(ManagerID,studentobj.getFirstName() + ","
								+ studentobj.getLastName() + "," + studentobj.getCoursesRegistered() + ","
								+ studentobj.isStatus() + "," + studentobj.getStatusDate());
						logManager.logger.log(Level.INFO,
								ManagerID + "has tranferred the student Record with recordID " + recordID + " from "
										+ this.location + " to LVL with recordID " + StudentID + " in LVL");
						return "record with ID " + recordID + " in " + this.location
								+ " is transferred to LVL with recordID " + StudentID + " in LVL";
					} else if (remoteCenterServerName.equalsIgnoreCase("DDO")) {
						String key = val.getKey();
						mylist.remove(studentobj);
						recordsMap.put(key, mylist);
						StudentID = DcmsServer.ddohref.createSRecord(ManagerID,studentobj.getFirstName() + ","
								+ studentobj.getLastName() + "," + studentobj.getCoursesRegistered() + ","
								+ studentobj.isStatus() + "," + studentobj.getStatusDate());
						logManager.logger.log(Level.INFO,
								ManagerID + "has tranferred the student Record with recordID " + recordID + " from "
										+ this.location + " to DDO with recordID " + StudentID + " in DDO");
						return "record with ID " + recordID + " in " + this.location
								+ " is transferred to DDO with recordID " + StudentID + " in DDO";
					}
					logManager.logger.log(Level.INFO, "Updated the records\t" + location);
					return "record with ID " + recordID + " is not transferred successfully ";

				} else {
					return "Transfer aborted to same server..Try different server!!";
				}
			}
		}
		return "Record with " + recordID + " not found";

	}


	/**
	 *The putCoursesinList function adds the newCourses to the List
	 *@param newvalue gets the newcourses value and adds to the list 
	 *
	 */

	
	private List<String> putCoursesinList(String newvalue) {
		String[] courses = newvalue.split("//");
		ArrayList<String> courseList = new ArrayList<>();
		for (String course : courses)
			courseList.add(course);
		return courseList;
	}

	/**
	 *The editSRRecord function performs the edit operation on the student record and returns the appropriate message
	 *@param managerID gets the managerID 
	 *@param recordID gets the recordID to be edited
	 *@param fieldname gets the fieldname to be edited for the given recordID
	 *@param newvalue gets the newvalue to be replaced to the given fieldname 
	 *from the client
	 */
	
	private synchronized String editSRRecord(String maangerID, String recordID, String fieldname, String newvalue) {
		for (Entry<String, List<Record>> value : recordsMap.entrySet()) {
			List<Record> mylist = value.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();
			if (record.isPresent()) {
				if (record.isPresent() && fieldname.equals("Status")) {
					((Student) record.get()).setStatus(newvalue);
					logManager.logger.log(Level.INFO, maangerID + "Updated the records\t" + location);
					return "Updated record with status :: " + newvalue;
				} else if (record.isPresent() && fieldname.equals("StatusDate")) {
					((Student) record.get()).setStatusDate(newvalue);
					logManager.logger.log(Level.INFO, maangerID + "Updated the records\t" + location);
					return "Updated record with status date :: " + newvalue;
				} else if (record.isPresent() && fieldname.equals("CoursesRegistered")) {
					List<String> courseList = putCoursesinList(newvalue);
					((Student) record.get()).setCoursesRegistered(courseList);
					return "Updated record with courses :: " + courseList;
				} else {
					System.out.println("Record with " + recordID + " not found");
					logManager.logger.log(Level.INFO, "Record with " + recordID + "not found!" + location);
					return "Record with " + recordID + " not found";
				}
			}
		}
		return "Record with " + recordID + "not found!";
	}

	
	/**
	 *The editTRRecord function performs the edit operation on the Teacher record and returns the appropriate message
	 *@param managerID gets the managerID 
	 *@param recordID gets the recordID to be edited
	 *@param fieldname gets the fieldname to be edited for the given recordID
	 *@param newvalue gets the newvalue to be replaced to the given fieldname 
	 *from the client
	 */
	
	
	private synchronized String editTRRecord(String managerID, String recordID, String fieldname, String newvalue) {
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

			if (record.isPresent()) {
				if (record.isPresent() && fieldname.equals("Phone")) {
					((Teacher) record.get()).setPhone(newvalue);
					logManager.logger.log(Level.INFO, managerID + "Updated the records\t" + location);
					return "Updated record with Phone :: " + newvalue;
				}

				else if (record.isPresent() && fieldname.equals("Address")) {
					((Teacher) record.get()).setAddress(newvalue);
					logManager.logger.log(Level.INFO, managerID + "Updated the records\t" + location);
					return "Updated record with address :: " + newvalue;
				}

				else if (record.isPresent() && fieldname.equals("Location")) {
					((Teacher) record.get()).setLocation(newvalue);
					logManager.logger.log(Level.INFO, managerID + "Updated the records\t" + location);
					return "Updated record with location :: " + newvalue;
				} else {
					System.out.println("Record with " + recordID + " not found");
					logManager.logger.log(Level.INFO, "Record with " + recordID + "not found!" + location);
					return "Record with " + recordID + " not found";
				}
			}
		}
		return "Record with " + recordID + " not found";
	}
}