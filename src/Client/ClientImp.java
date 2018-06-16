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
	LogManager logManager = null;
	Dcms serverLoc = null;
	static NamingContextExt ncRef = null;

	ClientImp(String[] args, ServerCenterLocation location, String ManagerID) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb
					.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);

			if (location == ServerCenterLocation.MTL) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("MTL"));
			} else if (location == ServerCenterLocation.LVL) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("LVL"));
			} else if (location == ServerCenterLocation.DDO) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("DDO"));
			}
			boolean mgrID = new File(Constants.LOG_DIR + ManagerID).mkdir();
			logManager = new LogManager(ManagerID);
		} catch (Exception e) {
			System.out.println("ERROR : " + e);
			e.printStackTrace(System.out);
		}
	}

	public String createTRecord(String teacherField) {
		logManager.logger.log(Level.INFO,
				"Initiating T record object creation request");
		String result = "";
		String teacherID = "";
		teacherID = serverLoc.createTRecord(teacherField);
		if (teacherID != null)
			result = "Teacher record is created and assigned with " + teacherID;
		else
			result = "Teacher record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	
	public String createSRecord(String studentFields) {
		logManager.logger.log(Level.INFO,
				"Initiating S record object creation request");
		String result = "";
		String studentID = "";
		studentID = serverLoc.createSRecord(studentFields);
		if (studentID != null)
			result = "student record is created and assigned with " + studentID;
		else
			result = "student record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	public String getRecordCounts() {
		String count = "";
		logManager.logger.log(Level.INFO, "Initiating record count request");
		count = serverLoc.getRecordCount();
		logManager.logger.log(Level.INFO, "received....count as follows");
		logManager.logger.log(Level.INFO, count);
		return count;
	}

	public String transferRecord(String ManagerID, String recordID,
			String location) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record transfer request");
		message = serverLoc.transferRecord(ManagerID, recordID, location);
		System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}

	public String editRecord(String managerID, String recordID, String fieldname,
			String newvalue) {
		String message = "";
		logManager.logger.log(Level.INFO,
				managerID + "has Initiated the record edit request for " + recordID);
		message = serverLoc.editRecord(managerID, recordID, fieldname, newvalue);
		System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}
}