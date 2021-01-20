package aya.ext.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AyaSocketServer {

	protected ServerSocket _server;
	protected InetAddress _ip;
	protected int _port;
	
	AyaSocketServer(String ip, int port) throws IOException {
		if (ip == null || ip.equals("") || ip.equals("localhost")) {
			_ip = InetAddress.getLocalHost();
		} else {
			_ip = InetAddress.getByName(ip);
		}
		_port = port;
		_server = new ServerSocket(port, 1, _ip);
	}
	
	AyaSocket accept() throws IOException {
		Socket sock = _server.accept();
		return new AyaSocket(sock, _ip, _port);
	}

	int getPort() {
		return _port;
	}
	
	public void close() throws IOException {
		_server.close();
	}

	@Override
	public String toString() {
		return "SocketServer(" + _ip.getHostAddress() + ":" + _port + ")";
	}
}
