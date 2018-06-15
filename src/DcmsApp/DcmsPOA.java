package DcmsApp;


/**
* DcmsApp/DcmsPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Dcms.idl
* Wednesday, June 13, 2018 11:30:18 PM EDT
*/

public abstract class DcmsPOA extends org.omg.PortableServer.Servant
 implements DcmsApp.DcmsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("sayHello", new java.lang.Integer (0));
    _methods.put ("createTRecord", new java.lang.Integer (1));
    _methods.put ("createSRecord", new java.lang.Integer (2));
    _methods.put ("getRecordCount", new java.lang.Integer (3));
    _methods.put ("editRecord", new java.lang.Integer (4));
    _methods.put ("transferRecord", new java.lang.Integer (5));
    _methods.put ("editRecordForCourses", new java.lang.Integer (6));
    _methods.put ("shutdown", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // DcmsApp/Dcms/sayHello
       {
         String $result = null;
         $result = this.sayHello ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // DcmsApp/Dcms/createTRecord
       {
         String teacher = in.read_string ();
         String $result = null;
         $result = this.createTRecord (teacher);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // DcmsApp/Dcms/createSRecord
       {
         String student = in.read_string ();
         String $result = null;
         $result = this.createSRecord (student);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // DcmsApp/Dcms/getRecordCount
       {
         String $result = null;
         $result = this.getRecordCount ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // DcmsApp/Dcms/editRecord
       {
         String managerID = in.read_string ();
         String recordID = in.read_string ();
         String fieldname = in.read_string ();
         String newvalue = in.read_string ();
         String $result = null;
         $result = this.editRecord (managerID, recordID, fieldname, newvalue);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // DcmsApp/Dcms/transferRecord
       {
         String managerID = in.read_string ();
         String recordID = in.read_string ();
         String location = in.read_string ();
         String $result = null;
         $result = this.transferRecord (managerID, recordID, location);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 6:  // DcmsApp/Dcms/editRecordForCourses
       {
         String managerID = in.read_string ();
         String recordID = in.read_string ();
         String fieldName = in.read_string ();
         String newValue = in.read_string ();
         String $result = null;
         $result = this.editRecordForCourses (managerID, recordID, fieldName, newValue);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // DcmsApp/Dcms/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:DcmsApp/Dcms:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Dcms _this() 
  {
    return DcmsHelper.narrow(
    super._this_object());
  }

  public Dcms _this(org.omg.CORBA.ORB orb) 
  {
    return DcmsHelper.narrow(
    super._this_object(orb));
  }


} // class DcmsPOA
