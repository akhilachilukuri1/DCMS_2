package Server;

import DcmsApp.*;
import org.omg.CosNaming.*;

import java.util.HashMap;

import java.io.File;
import Conf.Constants;
import org.omg.CORBA.*;
import Conf.ServerCenterLocation.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import Conf.ServerCenterLocation;

public class DcmsServer {
	static DcmsServerImpl serverMTL,serverLVL,serverDDO;
	static HashMap<String,DcmsServerImpl> serverRepo;
	static Dcms mtlhref,lvlhref,ddohref;
	private static void init(){

		boolean isMtlDir = new File(Constants.LOG_DIR+ServerCenterLocation.MTL.toString()).mkdir();
		boolean isLvlDir = new File(Constants.LOG_DIR+ServerCenterLocation.LVL.toString()).mkdir();
		boolean isDdoDir = new File(Constants.LOG_DIR+ServerCenterLocation.DDO.toString()).mkdir();
		boolean globalDir = new File(Constants.LOG_DIR+"ServerGlobal").mkdir();

		serverMTL = new DcmsServerImpl(ServerCenterLocation.MTL);
		serverLVL = new DcmsServerImpl(ServerCenterLocation.LVL);
		serverDDO = new DcmsServerImpl(ServerCenterLocation.DDO);
		
		serverRepo = new HashMap<>();
		serverRepo.put("MTL",serverMTL);
		serverRepo.put("LVL",serverLVL);
		serverRepo.put("DDO",serverDDO);
	}
	public static void main(String args[]) {
		try {
			
			 init();
			 // create and initialize the ORB
		      ORB orb = ORB.init(args, null);

		      // get reference to rootpoa & activate the POAManager
		      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		      rootpoa.the_POAManager().activate();

		      // create servant and register it with the ORB
		      DcmsServerImpl mtlServer = new DcmsServerImpl(ServerCenterLocation.MTL);
		      DcmsServerImpl lvlServer = new DcmsServerImpl(ServerCenterLocation.LVL);
		      DcmsServerImpl ddoServer = new DcmsServerImpl(ServerCenterLocation.DDO);
		      mtlServer.setORB(orb); 
		      lvlServer.setORB(orb);
		      ddoServer.setORB(orb);
		      
		      // get object reference from the servant
		      org.omg.CORBA.Object mtlRef = rootpoa.servant_to_reference(mtlServer);
		      org.omg.CORBA.Object lvlRef = rootpoa.servant_to_reference(lvlServer);
		      org.omg.CORBA.Object ddoRef = rootpoa.servant_to_reference(ddoServer);
		       mtlhref = DcmsHelper.narrow(mtlRef);
		       lvlhref = DcmsHelper.narrow(lvlRef);
		       ddohref = DcmsHelper.narrow(ddoRef);

			  
		      // get the root naming context
		      // NameService invokes the name service
		      org.omg.CORBA.Object objRef =
		          orb.resolve_initial_references("NameService");
		      // Use NamingContextExt which is part of the Interoperable
		      // Naming Service (INS) specification.
		      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		      NameComponent mtlPath[] = ncRef.to_name( "MTL" );
		      NameComponent lvlPath[] = ncRef.to_name( "LVL" );
		      NameComponent ddoPath[] = ncRef.to_name( "DDO" );

		      ncRef.rebind(mtlPath, mtlhref);
		      ncRef.rebind(lvlPath, lvlhref);
		      ncRef.rebind(ddoPath, ddohref);

		      System.out.println("DCMS Server ready and waiting ...");
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

	}
}
