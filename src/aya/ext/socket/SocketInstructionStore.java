package aya.ext.socket;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.util.Casting;

public class SocketInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		SocketManager socket_manager = new SocketManager();

		return Arrays.asList(
				new NamedOperator("socket.open_server", "ip::str port::int: Open a socket server") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						final Obj obj_port = blockEvaluator.pop();
						final Obj obj_ip = blockEvaluator.pop();
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

							blockEvaluator.push(Num.fromInt(result));
						} else {
							throw new TypeError(this, "SN");
						}
					}
				},


				new NamedOperator("socket.accept", "server_id::int: Open a connection on the server") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						int id = getSingleIntArg(this, blockEvaluator);
						int sock_id = socket_manager.accept(id);
						blockEvaluator.push(Num.fromInt(sock_id));
					}
				},

				new NamedOperator("socket.open_client", "ip::str port::int: Open a socket client") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						final Obj obj_port = blockEvaluator.pop();
						final Obj obj_ip = blockEvaluator.pop();
						if (obj_port.isa(Obj.NUM)) {
							int port = Casting.asNumber(obj_port).toInt();
							String ip = obj_ip.str();
							try {
								int result = socket_manager.openClient(ip, port);
								blockEvaluator.push(Num.fromInt(result));
							} catch (IOException e) {
								throw new IOError(opName(), ip + ":" + port, e);
							}

						} else {
							throw new TypeError(this, "SN");
						}
					}
				},


				new NamedOperator("socket.send", "data::str id::int: Send data on a socket") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						final Obj obj_id = blockEvaluator.pop();
						final Obj obj_data = blockEvaluator.pop();
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
				},


				new NamedOperator("socket.close", "id::num: Close a socket or server") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						int id = getSingleIntArg(this, blockEvaluator);
						// May be either a socket or a server
						socket_manager.closeSocket(id);
						socket_manager.closeSocketServer(id);
					}
				},


				new NamedOperator("socket.recv", "id::num: Read from a socket") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						int id = getSingleIntArg(this, blockEvaluator);
						AyaSocket sock = socket_manager.getSocket(id);
						blockEvaluator.push(List.fromString(sock.recv()));
					}
				},

				new NamedOperator("socket.get_addr", "id::num: Get the socket's connection addr") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						int id = getSingleIntArg(this, blockEvaluator);
						blockEvaluator.push(List.fromString(socket_manager.getIP(id).toString()));
					}
				},

				new NamedOperator("socket.get_port", "id::num: Get the socket's connection port") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						int id = getSingleIntArg(this, blockEvaluator);
						blockEvaluator.push(Num.fromInt(socket_manager.getPort(id)));
					}
				}
		);

	}

	private static int getSingleIntArg(NamedOperator i, BlockEvaluator blockEvaluator) {
		final Obj obj_id = blockEvaluator.pop();
		if (obj_id.isa(Obj.NUM)) {
			return Casting.asNumber(obj_id).toInt();
		} else {
			throw new TypeError(i, "N");
		}
	}
}
