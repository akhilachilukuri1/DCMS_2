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
import java.util.Properties;

class DcmsServerImpl extends DcmsPOA {
	private ORB orb;

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

	LogManager logManager;
	ServerUDP serverUDP;
	String IPaddress;
	HashMap<String, List<Record>> recordsMap;
	static int studentCount = 0;
	static int teacherCount = 0;
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
	public String createTRecord(String teacher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSRecord(String student) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRecordCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editRecord(String recordID, String fieldname, String newvalue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editRecordForCourses(String recordID, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		return null;
	}
}