package Server;

import DcmsApp.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import Conf.ServerCenterLocation.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import Conf.ServerCenterLocation;

public class DcmsServer {

	public static void main(String args[]) {
		try {
//			// create servant and register it with the ORB
//			DcmsServerImpl mtlServer = new DcmsServerImpl(ServerCenterLocation.MTL);
//			//DcmsServerImpl lvlServer = new DcmsServerImpl(ServerCenterLocation.LVL);
//			//DcmsServerImpl ddoServer = new DcmsServerImpl(ServerCenterLocation.DDO);
//
//			
//			// create and initialize the ORB
//			ORB orb = ORB.init(args, null);
//			// get reference to rootpoa & activate the POAManager
//			POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
//			rootPoa.the_POAManager().activate();
//
//			mtlServer.setORB(orb);
//			
//			org.omg.CORBA.Object mtlCorbaRef = rootPoa.servant_to_reference(mtlServer);
//	        //org.omg.CORBA.Object lvlCorbaRef = rootPoa.servant_to_reference(lvlServer);
//	        //org.omg.CORBA.Object ddoCorbaRef = rootPoa.servant_to_reference(ddoServer);
//	        Dcms mtlRef = DcmsHelper.narrow(mtlCorbaRef);
//	        //Dcms lvlRef = DcmsHelper.narrow(lvlCorbaRef);
//	        //Dcms ddoRef = DcmsHelper.narrow(ddoCorbaRef);
//	        
////			mtlServer.setORB(orb);
////			lvlServer.setORB(orb);
////			ddoServer.setORB(orb);
//
//			// get the root naming context
//			// NameService invokes the name service
//			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//			// Use NamingContextExt which is part of the Interoperable
//			// Naming Service (INS) specification.
//			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//
//			// bind the Object Reference in Naming
//			String name = "Hello";
//			NameComponent mtlPath[] = ncRef.to_name("MTLServer");
//	        //NameComponent lvlPath[] = ncRef.to_name("LVLServer");
//	        //NameComponent ddoPath[] = ncRef.to_name("DDOServer");
//	        ncRef.rebind(mtlPath, mtlRef );
//	        //ncRef.rebind(lvlPath, lvlRef );
//	        //ncRef.rebind(ddoPath, ddoRef );
//
//			System.out.println("DCMS Server ready and waiting ...");
//
//			// wait for invocations from clients
//			orb.run();
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
		      Dcms mtlhref = DcmsHelper.narrow(mtlRef);
		      Dcms lvlhref = DcmsHelper.narrow(lvlRef);
		      Dcms ddohref = DcmsHelper.narrow(ddoRef);

			  
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
