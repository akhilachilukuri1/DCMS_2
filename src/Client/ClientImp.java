package Client;

import java.io.File;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;



import DcmsApp.Dcms;
import DcmsApp.DcmsHelper;
import Conf.LogManager;
import Conf.*;
/*Implementation of Client class*/

public class ClientImp {
	//static Dcms dcmsImplMTL,dcmsImplLVL,dcmsImplDDO;

	LogManager logManager = null;
	Dcms serverLoc=null;
	//ICenterServer iCenterServer = null;
	static NamingContextExt ncRef=null;
	ClientImp(String[] args,ServerCenterLocation location,String ManagerID)
	{
	 try{
	        // create and initialize the ORB
		ORB orb = ORB.init(args, null);

	        // get the root naming context
	        org.omg.CORBA.Object objRef = 
		    orb.resolve_initial_references("NameService");
	        // Use NamingContextExt instead of NamingContext. This is 
	        // part of the Interoperable naming Service.  
	        ncRef = NamingContextExtHelper.narrow(objRef);
	 
	        // resolve the Object Reference in Naming
	    	if (location == ServerCenterLocation.MTL) {
	    		serverLoc = DcmsHelper.narrow(ncRef.resolve_str("MTL"));
			} else if (location == ServerCenterLocation.LVL) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("LVL"));
			} else if (location == ServerCenterLocation.DDO) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("DDO"));
			}
		boolean mgrID = new File(Constants.LOG_DIR+ManagerID).mkdir();
		logManager = new LogManager(ManagerID);
	}
	 catch (Exception e) {

         System.out.println("ERROR : " + e) ;

	  e.printStackTrace(System.out);

	  }
	}
	
//converting the teacher details given by the manager into a POJO instance and sending to the server
	public String createTRecord(String teacherField) {
		logManager.logger.log(Level.INFO, "Initiating T record object creation request");
		String result = "";
		String teacherID = "";
		/*Teacher teacher = new Teacher(teacherID, firstName, lastName);
		teacher.setFirstName(firstName);
		teacher.setLastName(lastName);
		teacher.setAddress(address);
		teacher.setPhone(phone);
		teacher.setSpecilization(specilization);
		teacher.setLocation(location);*/

		
			teacherID = serverLoc.createTRecord(teacherField);
		
		if (teacherID != null)
			result = "Teacher record is created and assigned with " + teacherID;
		else
			result = "Teacher record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}
//converting the student details given by the manager into a POJO instance and sending to the server
	public String createSRecord(String studentFields) {
		logManager.logger.log(Level.INFO, "Initiating S record object creation request");
		String result = "";
		String studentID = "";
	/*	Student student = new Student(studentID, firstName, lastName);
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setStatus(status);
		student.setStatusDate(statusDate);
		student.setCoursesRegistered(coursesRegistered);*/

		
			studentID = serverLoc.createSRecord(studentFields);
		
		if (studentID != null)
			result = "student record is created and assigned with " + studentID;
		else
			result = "student record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}
//sending the request for getting the record count in all the servers
	public String getRecordCounts() {
		String count = "";
		logManager.logger.log(Level.INFO, "Initiating record count request");
		
			count = serverLoc.getRecordCount();
		
		logManager.logger.log(Level.INFO, "received....count as follows");
		logManager.logger.log(Level.INFO, count);
		return count;
	}
	public String transferRecord(String ManagerID, String recordID, String location) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record transfer request");
		
			message = serverLoc.transferRecord(ManagerID, recordID, location);
			System.out.println(message);
		
		logManager.logger.log(Level.INFO, message);
		return message;
	}
//sending the request for editing the record in the server by giving the new values
	public String editRecord(String recordID, String fieldname, String newvalue) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record edit request");
		
			message = serverLoc.editRecord(recordID, fieldname, newvalue);
			System.out.println(message);
		
		logManager.logger.log(Level.INFO, message);
		return message;
	}
//sending the request for editing the courses registered for a given student 
	public String editRecordForCourses(String recordID, String fieldname, String newCourses) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record edit request");
		
			serverLoc.editRecordForCourses(recordID, fieldname, newCourses);
		
		logManager.logger.log(Level.INFO, message);
		return message;
	}
}