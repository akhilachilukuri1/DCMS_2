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

class DcmsServerImpl extends DcmsPOA {
	private ORB orb;

	LogManager logManager;
	ServerUDP serverUDP;
	String IPaddress;
	public static HashMap<String, List<Record>> recordsMap;
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

	// implement sayHello() method
	public String sayHello() {
		return "\nHello world !!\n";
	}

	// implement shutdown() method
	public void shutdown() {
		orb.shutdown(false);
	}

	// getting the location instance
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

	@Override
	public synchronized String createTRecord(String teacher) {

		System.out.println("Teacher: "+teacher);
		String temp[] = teacher.split(",");
		
		
		String managerID = temp[0];
		String teacherID = "TR"+ (++teacherCount);
		String firstName = temp[1];
		String lastname = temp[2];
		String address= temp[3];
		String phone = temp[4];
		String specialization = temp[5];
		String location = temp[6];
		
		Teacher teacherObj = new Teacher(managerID, teacherID, firstName,  lastname, address,  phone, specialization,  location);
		String key = lastname.substring(0, 1);
		// adding the teacher record to HashMap
		String message = addRecordToHashMap(key, teacherObj, null);

		//System.out.println(recordsMap);

		System.out.println("teacher is added " + teacherObj + " with this key " + key+" by Manager "+managerID );
		logManager.logger.log(Level.INFO, "Teacher record created " + teacherID + " by Manager : "+ managerID);
		return teacherID;

		
	}

	@Override
	public synchronized String createSRecord(String student) {

		
		String temp[] = student.split(",");
		
		String managerID= temp[0];
		String firstName= temp[1];
		String lastName= temp[2];
		String CoursesRegistered= temp[3];
		String status= temp[4];
		String statusDate= temp[5];
		String studentID = "SR" + (++studentCount);
		
		Student studentObj = new Student(managerID, studentID,  firstName,  lastName,  CoursesRegistered,  status, statusDate);
	
		String key = lastName.substring(0, 1);
		// adding the student record to HashMap
		String message = addRecordToHashMap(key, null, studentObj);

		//System.out.println(recordsMap);

		System.out.println(" Student is added " + studentObj + " with this key " + key+" by Manager "+managerID  );
		logManager.logger.log(Level.INFO, "Student record created " + studentID+" by manager : "+ managerID);

		return studentID;
	}

	

	// adding the records into HashMap
		private synchronized static String addRecordToHashMap(String key, Teacher teacher, Student student) {

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

	
	
	
	
	private int getCurrServerCnt() {
		int count = 0;
		for (Map.Entry<String, List<Record>> entry : this.recordsMap.entrySet()) {
			List<Record> list = entry.getValue();
			count += list.size();
			// System.out.println(entry.getKey()+" "+list.size());
		}
		return count;
	}

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

	// Editing student and teacher records
	@Override
	public String editRecord(String managerID,String recordID, String fieldname, String newvalue) {
		String type = recordID.substring(0, 2);

		if (type.equals("TR")) {
			return editTRRecord(managerID,recordID, fieldname, newvalue);
		}

		else if (type.equals("SR")) {
			return editSRRecord(managerID,recordID, fieldname, newvalue);
		}

		logManager.logger.log(Level.INFO, "Record edit successful");

		return "Operation not performed!";
	}
	public String transferRecord(String ManagerID, String recordID, String remoteCenterServerName) {
		String type = recordID.substring(0, 2);

		if (type.equals("TR")) {
			return transferTRRecord(ManagerID, recordID, remoteCenterServerName);
		}

		else if (type.equals("SR")) {
			return transferSRRecord(ManagerID, recordID, remoteCenterServerName);
		}

		logManager.logger.log(Level.INFO, "Record transfer successful");

		return "Operation not performed!";
	}
	public synchronized String transferTRRecord(String ManagerID,String recordID,String remoteCenterServerName)
	{
		logManager.logger.log(Level.INFO,ManagerID+" has initiated the record transfer for the recordID: "+recordID+" operation from "+this.IPaddress+" to "+remoteCenterServerName);
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

			// System.out.println(record);
			if (record.isPresent()) {
				
					Teacher teachobj=(Teacher) record.get();
					System.out.println("Techer transfer$$"+teachobj);
					if(remoteCenterServerName.equalsIgnoreCase("MTL"))
					{
						String key=val.getKey();
						mylist.remove(teachobj);
						recordsMap.put(key, mylist);
						DcmsServer.mtlhref.createTRecord(ManagerID+","+teachobj.getFirstName()+","+teachobj.getLastName()+","+teachobj.getAddress()+","+teachobj.getPhone()+","+teachobj.getSpecilization()+","+teachobj.getLocation());
						logManager.logger.log(Level.INFO, ManagerID+"has tranferred the teacher Record with "+recordID+"to MTL");
						return "record with ID "+recordID+" transferred to MTL ";
					}else
						if(remoteCenterServerName.equalsIgnoreCase("LVL"))
						{
							String key=val.getKey();
							mylist.remove(teachobj);
							recordsMap.put(key, mylist);
							DcmsServer.lvlhref.createTRecord(ManagerID+","+teachobj.getFirstName()+","+teachobj.getLastName()+","+teachobj.getAddress()+","+teachobj.getPhone()+","+teachobj.getSpecilization()+","+teachobj.getLocation());
							logManager.logger.log(Level.INFO, ManagerID+"has tranferred the teacher Record with "+recordID+"to LVL");
							return "record with ID "+recordID+" transferred to LVL ";
						}
						else
							if(remoteCenterServerName.equalsIgnoreCase("DDO"))
							{
								String key=val.getKey();
								mylist.remove(teachobj);
								recordsMap.put(key, mylist);
								DcmsServer.ddohref.createTRecord(ManagerID+","+teachobj.getFirstName()+","+teachobj.getLastName()+","+teachobj.getAddress()+","+teachobj.getPhone()+","+teachobj.getSpecilization()+","+teachobj.getLocation());
								logManager.logger.log(Level.INFO, ManagerID+"has tranferred the teacher Record with "+recordID+"to DDO");
								return "record with ID "+recordID+" transferred to DDO ";
							}
					logManager.logger.log(Level.INFO, "Updated the records\t" + location);
					return "record with ID "+recordID+" is not transferred successfully ";
				
			}
		}
		return "Record with " + recordID + " not found";

	
	
	}
	public synchronized String transferSRRecord(String ManagerID,String recordID,String remoteCenterServerName)
	{
		logManager.logger.log(Level.INFO,ManagerID+" has initiated the record transfer for the recordID: "+recordID+" operation from "+this.IPaddress+" to "+remoteCenterServerName);
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

			// System.out.println(record);
			if (record.isPresent()) {
				
					Student studentobj=(Student) record.get();
					System.out.println("Student transfer$$"+studentobj);
					if(remoteCenterServerName.equalsIgnoreCase("MTL"))
					{
						String key=val.getKey();
						mylist.remove(studentobj);
						recordsMap.put(key, mylist);
						DcmsServer.mtlhref.createSRecord(ManagerID+","+studentobj.getFirstName()+","+studentobj.getLastName()+","+studentobj.getCoursesRegistered()+","+studentobj.isStatus()+","+studentobj.getStatusDate());
						logManager.logger.log(Level.INFO, ManagerID+"has tranferred the student Record with "+recordID+"to MTL");
						return "record with ID "+recordID+" transferred to MTL ";
					}else
						if(remoteCenterServerName.equalsIgnoreCase("LVL"))
						{
							String key=val.getKey();
							mylist.remove(studentobj);
							recordsMap.put(key, mylist);
							DcmsServer.lvlhref.createSRecord(ManagerID+","+studentobj.getFirstName()+","+studentobj.getLastName()+","+studentobj.getCoursesRegistered()+","+studentobj.isStatus()+","+studentobj.getStatusDate());
							logManager.logger.log(Level.INFO, ManagerID+"has tranferred the student Record with "+recordID+"to LVL");
							return "record with ID "+recordID+" transferred to LVL ";
						}
						else
							if(remoteCenterServerName.equalsIgnoreCase("DDO"))
							{
								String key=val.getKey();
								mylist.remove(studentobj);
								recordsMap.put(key, mylist);
								DcmsServer.ddohref.createSRecord(ManagerID+","+studentobj.getFirstName()+","+studentobj.getLastName()+","+studentobj.getCoursesRegistered()+","+studentobj.isStatus()+","+studentobj.getStatusDate());
								logManager.logger.log(Level.INFO, ManagerID+"has tranferred the student Record with "+recordID+"to DDO");
								return "record with ID "+recordID+" transferred to DDO ";
							}
					logManager.logger.log(Level.INFO, "Updated the records\t" + location);
					return "record with ID "+recordID+" is not transferred successfully ";
				
			}
		}
		return "Record with " + recordID + " not found";

	
	}
	
	// Editing students records
	private synchronized String editSRRecord(String maangerID,String recordID, String fieldname, String newvalue) {

		// System.out.println(recordsMap);

		for (Entry<String, List<Record>> value : recordsMap.entrySet()) {

			List<Record> mylist = value.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();
			if (record.isPresent()) {
				if (record.isPresent() && fieldname.equals("Status")) {
					((Student) record.get()).setStatus(newvalue);
					logManager.logger.log(Level.INFO, maangerID+"Updated the records\t" + location);
					return "Updated record with status :: " + newvalue;
					// ((Student) record.get()).setStatus(null);
				} else if (record.isPresent() && fieldname.equals("StatusDate")) {
					((Student) record.get()).setStatusDate(newvalue);
					logManager.logger.log(Level.INFO, maangerID+"Updated the records\t" + location);
					return "Updated record with status date :: " + newvalue;
				}
				else {
					System.out.println("Record with " + recordID + " not found" );
					logManager.logger.log(Level.INFO, "Record with " + recordID + "not found!" + location);
					return "Record with " + recordID + " not found";

				}
					
			}
		}
		return "Record with " + recordID + "not found!";
	}

	// Editing Teacher records
	private  synchronized String editTRRecord(String managerID,String recordID, String fieldname, String newvalue) {
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();

			// System.out.println(record);
			if (record.isPresent()) {
				if (record.isPresent() && fieldname.equals("Phone")) {
					((Teacher) record.get()).setPhone(newvalue);
					logManager.logger.log(Level.INFO, managerID+"Updated the records\t" + location);
					return "Updated record with Phone :: " + newvalue;
				}

				else if (record.isPresent() && fieldname.equals("Address")) {
					((Teacher) record.get()).setAddress(newvalue);
					logManager.logger.log(Level.INFO, managerID+"Updated the records\t" + location);
					return "Updated record with address :: " + newvalue;
				}

				else if (record.isPresent() && fieldname.equals("Location")) {
					((Teacher) record.get()).setLocation(newvalue);
					logManager.logger.log(Level.INFO, managerID+"Updated the records\t" + location);
					return "Updated record with location :: " + newvalue;
				}
				else {
					System.out.println("Record with " + recordID + " not found" );
					logManager.logger.log(Level.INFO, "Record with " + recordID + "not found!" + location);
					return "Record with " + recordID + " not found";
				}
			}
		}
		return "Record with " + recordID + " not found";
	}


		

	@Override
	public synchronized String editRecordForCourses(String managerID, String recordID, String fieldName, String NewCourses) {
		
		for (Entry<String, List<Record>> value : recordsMap.entrySet()) {

			List<Record> mylist = value.getValue();
			Optional<Record> record = mylist.stream().filter(x -> x.getRecordID().equals(recordID)).findFirst();
			
			if (record.isPresent() && fieldName.equals("CoursesRegistered")) {
				((Student) record.get()).setCoursesRegistered(NewCourses);
				logManager.logger.log(Level.INFO, managerID+"Updated the records\t" + location);
				System.out.println(record.get().getRecordID() +" Student record edited with " + NewCourses+" by Manager "+managerID  );
				return "Updated record with new courses  :: " + NewCourses;
			}
			else
			{
				//System.out.println("Record with " + recordID + " not found" );
				logManager.logger.log(Level.INFO, "Record with " + recordID + "not found!" + location);
				return "Record with " + recordID + " not found";
			}
		}
		
		
		return "Updated record with new courses  :: " + NewCourses;
	}
}