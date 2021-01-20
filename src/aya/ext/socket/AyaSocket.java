package aya.ext.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import aya.Aya;

public class AyaSocket {

	protected InetAddress _ip;
	protected int _port;
	private Socket _sock;
	private BufferedReader _reader;
    private PrintWriter _writer;
    
    public static AyaSocket createClient(String ip, int port) throws IOException {
    	InetAddress addr = resolveIP(ip);
    	Socket sock = new Socket(addr, port);
    	return new AyaSocket(sock, addr, port);
    }

	public AyaSocket(Socket sock, InetAddress _ip2, int port) throws IOException {
		_port = port;
		_sock = sock;
		_writer = new PrintWriter(_sock.getOutputStream(), true);
		_reader = new BufferedReader(new InputStreamReader(_sock.getInputStream()));
	}
	
	public void close() {
		_writer.close();
		try {
			_sock.close();
			_reader.close();
		} catch (IOException e) {
			// pass
		}
	}

	public boolean isOpen() {
		return _sock != null && _sock.isConnected();
	}


	public void send(String data) {
		Aya.getInstance().getOut().println("Sending data: " + data);
		_writer.print(data);
	}

	public String recv() {
		try {
			return _reader.readLine();
		} catch (IOException e) {
			return "";
		}
	}
	
	public InetAddress getIP() {
		return _ip;
	}
	
	public int getPort() {
		return _port;
	}
	
	@Override
	public String toString() {
		return "Socket(" + _ip.getHostAddress() + ":" + _port + ")";
	}
	
	public static InetAddress resolveIP(String ip) throws UnknownHostException {
		if (ip == null || ip.equals("") || ip.equals("localhost")) {
			return InetAddress.getLocalHost();
		} else {
			return InetAddress.getByName(ip);
		}
	}
	
}
