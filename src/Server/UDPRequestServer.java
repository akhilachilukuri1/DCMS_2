package Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import Conf.ServerCenterLocation;
import Models.Record;

public class UDPRequestServer extends Thread {
	DatagramSocket serverSocket;
	ServerCenterLocation location;
	private DatagramPacket receivePacket;
	private DcmsServerImpl server;
	private Logger loggerInstance;

	public UDPRequestServer(DatagramPacket pkt, DcmsServerImpl serverImp) {
		receivePacket = pkt;
		server = serverImp;
		try {
			serverSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		byte[] responseData;
		try {
			String inputPkt = new String(receivePacket.getData()).trim();
			if (inputPkt.equals("GET_RECORD_COUNT")) {
				responseData = Integer.toString(getRecCount()).getBytes();
				serverSocket.send(new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
						receivePacket.getPort()));
			}

			loggerInstance.log(Level.INFO, "Received " + inputPkt + " from " + location);
		} catch (Exception e) {

		}
	}

	private int getRecCount() {
		int count = 0;
		for (Map.Entry<String, List<Record>> entry : server.recordsMap.entrySet()) {
			List<Record> list = entry.getValue();
			count += list.size();
		}
		return count;
	}
}
