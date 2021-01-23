package aya.ext.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
    //private PrintWriter _writer;
    private DataOutputStream _writer;
    
    public static AyaSocket createClient(String ip, int port) throws IOException {
    	InetAddress addr = resolveIP(ip);
    	Socket sock = new Socket(addr, port);
    	return new AyaSocket(sock, addr, port);
    }

	public AyaSocket(Socket sock, InetAddress _ip2, int port) throws IOException {
		_port = port;
		_sock = sock;
		//_writer = new PrintWriter(_sock.getOutputStream(), true);
		_writer = new DataOutputStream(_sock.getOutputStream());
		_reader = new BufferedReader(new InputStreamReader(_sock.getInputStream()));
	}
	
	public void close() {
		try {
			_writer.close();
			_sock.close();
			_reader.close();
		} catch (IOException e) {
			// pass
		}
	}

	public boolean isOpen() {
		return _sock != null && _sock.isConnected();
	}


	public void send(String data) throws IOException {
		_writer.writeBytes(data);
		_writer.flush();
	}

	public String recv() {
		try {
			String data = _reader.readLine();
			if (data == null) {
				return "";
			} else {
				return data;
			}
		} catch (IOException e) {
			return "";
		}
	}
	
	public InetAddress getAddr() {
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
		InetAddress addr;
		if (ip == null || ip.equals("")) { // || ip.equals("localhost")) {
			addr = InetAddress.getLocalHost();
		} else {
			addr = InetAddress.getByName(ip);
		}
		return addr;
	}
	
}
