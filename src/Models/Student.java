package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//This class holds the getters and setters for the details of the student
public class Student extends Record implements Serializable {
	String firstName;
	String lastName;
	String CoursesRegistered;
	String status;
	String statusDate;
	String studentID;
	String ManagerID;
	
	
	// created
	public Student(String managerID,String studentID, String firstName, String lastname, String CoursesRegistered, String status,
			String statusDate) {
		// calling the record class constructor with the given details
		super(studentID, firstName, lastname);
		this.setManagerID( managerID);
		this.setCoursesRegistered(CoursesRegistered);
		this.setStatus(status);
		this.setStatusDate(statusDate);
	}

	// getting the First name of the student
	public String getFirstName() {
		return firstName;
	}

	// setting the first name of the student
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// getting the last name of the student
	public String getLastName() {
		return lastName;
	}

	// setting the last name of the student
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// getting the list of the courses registered
	public String getCoursesRegistered() {
		return CoursesRegistered;
	}

	// setting the courses registered to the student instance
	public void setCoursesRegistered(String courses) {
		CoursesRegistered = courses;
	}

	// getting the status of the student
	public String isStatus() {
		return status;
	}

	// setting the status of the student
	public void setStatus(String status) {
		this.status = status;
	}

	// getting the status Date of the student
	public String getStatusDate() {
		return statusDate;
	}

	// setting the status date of the student
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	// getting the student ID
	public String getStudentID() {
		return studentID;
	}

	// setting the student ID
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}
	
	public String getManagerID() {
		return ManagerID;
	}


	public void setManagerID(String managerID) {
		ManagerID = managerID;
	}


	public String serialize() {
		return "Student" +getManagerID() + getRecordID() + "," + getFirstName() + "," + getLastName() + "," + getCoursesRegistered()
				+ "," + isStatus() + "," + getStatusDate();
	}

	public String toString() {
		return this.getManagerID()+" "+ this.getRecordID() + " " + this.getFirstName() + " " + this.getLastName() + " "
				+ this.getCoursesRegistered() + " " + this.isStatus() + " " + this.getStatusDate();
	}

}