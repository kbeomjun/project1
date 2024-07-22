package atm4_db;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	public static void main(String[] args) {
		int port = 5001;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket socket = serverSocket.accept();
				Server s = new Server(socket);
				s.start();
			}
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}
}