package Client;

import DcmsApp.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;

public class DcmsClient
{
  static Dcms dcmsImplMTL,dcmsImplLVL,dcmsImplDDO;

  public static void main(String args[])
    {
      try{
        // create and initialize the ORB
	ORB orb = ORB.init(args, null);

        // get the root naming context
        org.omg.CORBA.Object objRef = 
	    orb.resolve_initial_references("NameService");
        // Use NamingContextExt instead of NamingContext. This is 
        // part of the Interoperable naming Service.  
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
 
        // resolve the Object Reference in Naming
        dcmsImplMTL = DcmsHelper.narrow(ncRef.resolve_str("MTL"));
        dcmsImplLVL = DcmsHelper.narrow(ncRef.resolve_str("LVL"));
        dcmsImplDDO = DcmsHelper.narrow(ncRef.resolve_str("DDO"));

        System.out.println("Obtained a handle on server object: " + dcmsImplMTL);
        System.out.println(dcmsImplMTL.sayHello()+" from MTL");
        System.out.println(dcmsImplMTL.sayHello()+" from LVL");
        System.out.println(dcmsImplMTL.sayHello()+" from DDO");
        //dcmsImpl.shutdown();

	} catch (Exception e) {
          System.out.println("ERROR : " + e) ;
	  e.printStackTrace(System.out);
	  }
    }

}
