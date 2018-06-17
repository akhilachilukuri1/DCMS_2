package Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Conf.Constants;
import Conf.ServerCenterLocation;
import Models.Record;
import Models.Student;
import Models.Teacher;
import Server.UDPRequestServer;;

public class ServerUDP extends Thread {
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	Logger loggerInstance;
	String recordCount;
	DcmsServerImpl server;
	int c;

	public ServerUDP(ServerCenterLocation loc, Logger logger, DcmsServerImpl serverImp) {
		location = loc;
		loggerInstance = logger;
		this.server = serverImp;
		c = 0;
		try {
			switch (loc) {
			case MTL:
				serverSocket = new DatagramSocket(Constants.UDP_PORT_NUM_MTL);
				udpPortNum = Constants.UDP_PORT_NUM_MTL;
				logger.log(Level.INFO, "MTL UDP Server Started");
				break;
			case LVL:
				serverSocket = new DatagramSocket(Constants.UDP_PORT_NUM_LVL);
				udpPortNum = Constants.UDP_PORT_NUM_LVL;
				logger.log(Level.INFO, "LVL UDP Server Started");
				break;
			case DDO:
				serverSocket = new DatagramSocket(Constants.UDP_PORT_NUM_DDO);
				udpPortNum = Constants.UDP_PORT_NUM_DDO;
				logger.log(Level.INFO, "DDO UDP Server Started");
				break;
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void run() {
		byte[] receiveData;
		while (true) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				System.out.println("Received pkt :: " + new String(receivePacket.getData()));
				String inputPkt = new String(receivePacket.getData()).trim();
				new UDPRequestServer(receivePacket, server).start();
				loggerInstance.log(Level.INFO, "Received " + inputPkt + " from " + location);
			} catch (Exception e) {
			}
		}
	}
	
	public void receiveRecord() {
		byte[] receiveData;
		while (true) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				ByteArrayInputStream in = new ByteArrayInputStream(receiveData);
			    ObjectInputStream is = new ObjectInputStream(in);
			    Record rec= (Record) is.readObject();
				//String inputPkt = new String(receivePacket.getData()).trim();
				DcmsServerImpl server=DcmsServer.serverRepo.get(location);
				if(rec instanceof Teacher)
				{
					Teacher teacher=(Teacher) rec;
					
					server.createTRecord(((Teacher) rec).getManagerID(), teacher.getFirstName() + ","
							+ teacher.getLastName() + "," + teacher.getAddress() + "," + teacher.getPhone() + ","
							+ teacher.getSpecilization() + "," + teacher.getLocation());
				}else
				{
					Student student=(Student)rec;
					server.createSRecord(((Teacher) rec).getManagerID(),student.getFirstName() + ","
					+ student.getLastName() + "," + student.getCoursesRegistered() + ","
					+ student.isStatus() + "," + student.getStatusDate());
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
}
