package aya.ext.socket;

import java.io.IOException;

import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.instruction.named.NamedInstructionStore;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.util.Casting;

public class SocketInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		SocketManager socket_manager = new SocketManager();
		
		addInstruction(new NamedOperator("socket.open_server", "ip::str port::int: Open a socket server") {
			@Override
			public void execute(Block block) {
				final Obj obj_port = block.pop();
				final Obj obj_ip = block.pop();
				if (obj_port.isa(Obj.NUM)) {
					int port = Casting.asNumber(obj_port).toInt();
					String ip = obj_ip.str();
					int result = SocketManager.NULL_ID;

					try {
						result = socket_manager.openServer(ip, port);
					} catch (IOException e) {
						result = SocketManager.NULL_ID;
						throw new IOError(opName(), ip + ":" + port, e);
					}
					
					block.push(Num.fromInt(result));
				} else {
					throw new TypeError(this, "SN");
				}
			}
		});
		
		
		addInstruction(new NamedOperator("socket.accept", "server_id::int: Open a connection on the server") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				int sock_id = socket_manager.accept(id);
				block.push(Num.fromInt(sock_id));
			}
		});

		addInstruction(new NamedOperator("socket.open_client", "ip::str port::int: Open a socket client") {
			@Override
			public void execute(Block block) {
				final Obj obj_port = block.pop();
				final Obj obj_ip = block.pop();
				if (obj_port.isa(Obj.NUM)) {
					int port = Casting.asNumber(obj_port).toInt();
					String ip = obj_ip.str();
					try {
						int result = socket_manager.openClient(ip, port);
						block.push(Num.fromInt(result));
					} catch (IOException e) {
						throw new IOError(opName(), ip + ":" + port, e);
					}
					
				} else {
					throw new TypeError(this, "SN");
				}
			}
		});


		addInstruction(new NamedOperator("socket.send", "data::str id::int: Send data on a socket") {
			@Override
			public void execute(Block block) {
				final Obj obj_id = block.pop();
				final Obj obj_data = block.pop();
				if (obj_id.isa(Obj.NUM)) {
					int id = Casting.asNumber(obj_id).toInt();
					AyaSocket sock = socket_manager.getSocket(id);
					String data = obj_data.str();
					try {
						sock.send(data);
					} catch (IOException e) {
						throw new IOError(opName(), sock.getAddr() + ":" + sock.getPort(), e);
					}
				} else {
					throw new TypeError(this, "SN");
				}
			}
		});
		
		
		addInstruction(new NamedOperator("socket.close", "id::num: Close a socket or server") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				// May be either a socket or a server
				socket_manager.closeSocket(id);
				socket_manager.closeSocketServer(id);
			}
		});

		
		addInstruction(new NamedOperator("socket.recv", "id::num: Read from a socket") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				AyaSocket sock = socket_manager.getSocket(id);
				block.push(List.fromString(sock.recv()));
			}
		});

		addInstruction(new NamedOperator("socket.get_addr", "id::num: Get the socket's connection addr") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				block.push(List.fromString(socket_manager.getIP(id).toString()));
			}
		});

		addInstruction(new NamedOperator("socket.get_port", "id::num: Get the socket's connection port") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				block.push(Num.fromInt(socket_manager.getPort(id)));
			}
		});

	}
	
	private static int getSingleIntArg(NamedOperator i, Block block) {
		final Obj obj_id = block.pop();
		if (obj_id.isa(Obj.NUM)) {
			return Casting.asNumber(obj_id).toInt();
		} else {
			throw new TypeError(i, "N");
		}
	}
}
