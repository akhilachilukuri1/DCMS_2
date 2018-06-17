package Server;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Conf.ServerCenterLocation;

import Conf.Constants;
import Models.Record;

public class UDPRequestProvider extends Thread {
	private static final String MTL = null;
	private static final String LVL = null;
	private static final String DDO = null;
	private String recordCount;
	private Logger logger;
	private DcmsServerImpl server;
	
	/**
	 * UDPRequestProvider handles the UDP message call and transfers the necessary record
	 * @param server is the Impl object
	 */

	public UDPRequestProvider(DcmsServerImpl server) throws IOException {
		this.server = server;
	}

	public String getRemoteRecordCount() {
		return recordCount;
	}

	/**
	 * Routes the packet to the respective server address
	 */
	
	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte[] data = "GET_RECORD_COUNT".getBytes();
			// System.out.println(server.location);
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(server.IPaddress),
					server.serverUDP.udpPortNum);
			socket.send(packet);
			data = new byte[100];
			socket.receive(new DatagramPacket(data, data.length));
			recordCount = server.location + "," + new String(data);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
	
	/**
	 * Transfers the entire record in the form of an UDP message
	 * @param rec contains the entire record to be transfered
	 * @param loc is the location object
	 */
	
	public void transferRecord(Record rec,ServerCenterLocation loc) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte[] data = rec.getBytes();
			String IPaddress=null;
			// System.out.println(server.location);
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
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(IPaddress),
					server.serverUDP.udpPortNum);
			socket.send(packet);
			data = new byte[100];
			socket.receive(new DatagramPacket(data, data.length));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
	
}