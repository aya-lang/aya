package aya.ext.socket;

import java.io.IOException;

import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
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
		
		addInstruction(new NamedInstruction("socket.open_server", "ip::str port::int: Open a socket server") {
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
					}
					
					block.push(Num.fromInt(result));
				} else {
					throw new TypeError(this, "SN");
				}
			}
		});
		
		
		addInstruction(new NamedInstruction("socket.accept", "server_id::int: Open a connection on the server") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				int sock_id = socket_manager.accept(id);
				block.push(Num.fromInt(sock_id));
			}
		});

		addInstruction(new NamedInstruction("socket.open_client", "ip::str port::int: Open a socket client") {
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
						throw new RuntimeException(e);
						//throw new AyaRuntimeException("Unable to open connection on " + ip + ":" + port + e.getMessage());
					}
					
				} else {
					throw new TypeError(this, "SN");
				}
			}
		});


		addInstruction(new NamedInstruction("socket.send", "data::str id::int: Send data on a socket") {
			@Override
			public void execute(Block block) {
				final Obj obj_id = block.pop();
				final Obj obj_data = block.pop();
				if (obj_id.isa(Obj.NUM)) {
					int id = Casting.asNumber(obj_id).toInt();
					AyaSocket sock = socket_manager.getSocket(id);
					String data = obj_data.str();
					sock.send(data);
				} else {
					throw new TypeError(this, "SN");
				}
			}
		});
		
		
		addInstruction(new NamedInstruction("socket.close", "id::num: Close a socket or server") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				// May be either a socket or a server
				socket_manager.closeSocket(id);
				socket_manager.closeSocketServer(id);
			}
		});

		
		addInstruction(new NamedInstruction("socket.recv", "id::num: Read from a socket") {
			@Override
			public void execute(Block block) {
				int id = getSingleIntArg(this, block);
				AyaSocket sock = socket_manager.getSocket(id);
				block.push(List.fromString(sock.recv()));
			}
		});

	}
	
	private static int getSingleIntArg(NamedInstruction i, Block block) {
		final Obj obj_id = block.pop();
		if (obj_id.isa(Obj.NUM)) {
			return Casting.asNumber(obj_id).toInt();
		} else {
			throw new TypeError(i, "N");
		}
	}
}
