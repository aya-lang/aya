package aya.ext.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import aya.exceptions.AyaRuntimeException;

public class SocketManager {
	public static final int NULL_ID = 0;

	HashMap<Integer, AyaSocket> _sockets;
	HashMap<Integer, AyaSocketServer> _servers;
	
	public SocketManager() {
		_sockets = new HashMap<Integer, AyaSocket>();
		_servers = new HashMap<Integer, AyaSocketServer>();
	}

	public int openServer(String host, int port) throws IOException {
		// Close any existing server on the same port
		for (Entry<Integer, AyaSocketServer> x : _servers.entrySet()) {
			if (x.getValue().getPort() == port) {
				try {
					x.getValue().close();
				} catch (IOException ioe) {
					// pass
				}
				_servers.remove(x.getKey());
				break;
			}
		}

		AyaSocketServer srv = new AyaSocketServer(host, port);
		int hash = srv.hashCode();
		_servers.put(hash, srv);
		return hash;
	}
	
	public int openClient(String host, int port) throws IOException {
		return storeSocket(AyaSocket.createClient(host, port));
	}
		
	public int storeSocket(AyaSocket sock) {
		int hash = sock.hashCode();
		_sockets.put(hash, sock);
		return hash;
	}
	
	public AyaSocket getSocket(int id) {
		AyaSocket sock = _sockets.get(id);
		if (sock == null) {
			throw new AyaRuntimeException("Socket " + id + " is either closed or does not exist");
		} else {
			return sock;
		}
	}

	public AyaSocketServer getSocketServer(int id) {
		AyaSocketServer srv = _servers.get(id);
		if (srv == null) {
			throw new AyaRuntimeException("Socket " + id + " is either closed or does not exist");
		} else {
			return srv;
		}
	}
	
	public void closeSocket(int id) {
		AyaSocket sock = _sockets.get(id);
		if (sock != null) {
			sock.close();
			_sockets.remove(id);
		}
	}

	public void closeSocketServer(int id) {
		AyaSocket srv = _sockets.get(id);
		if (srv != null) {
			srv.close();
			_sockets.remove(id);
		}
	}

	public int accept(int id) {
		AyaSocketServer srv = _servers.get(id);
		if (srv != null) {
			try {
				return storeSocket(srv.accept());
			} catch (IOException e) {
				throw new AyaRuntimeException("Unable to accept connection on server with id " + id + ": " + e.getMessage());
			}
		} else {
			throw new AyaRuntimeException("Server with id " + id + " does not exist");
		}
	}

	public int getPort(int id) {
		if (_servers.containsKey(id)) {
			return _servers.get(id).getPort();
		} else if (_sockets.containsKey(id)) {
			return _sockets.get(id).getPort();
		} else {
			notFound(id);
			return 0;
		}
	}

	public String getIP(int id) {
		if (_servers.containsKey(id)) {
			return _servers.get(id).getAddr().toString();
		} else if (_sockets.containsKey(id)) {
			return _sockets.get(id).getAddr().toString();
		} else {
			notFound(id);
			return "";
		}
	}
	
	private void notFound(int id) {
		throw new AyaRuntimeException("Server or socket with id " + id + " does not exist");
	}


}
