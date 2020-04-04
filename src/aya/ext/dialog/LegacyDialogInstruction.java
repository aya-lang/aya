package aya.ext.dialog;

import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.number.Number;

public class LegacyDialogInstruction extends NamedInstruction {

	public LegacyDialogInstruction() {
		super("dialog.legacy");
		_doc = ("options title windowhdr msgtype dialogtype MV\n"
				+ "  msgtype:\n"
				+ "    1: plain\n"
				+ "    2: question\n"
				+ "    3: warning\n"
				+ "    4: error"
				+ "  dialogtype:\n"
				+ "    1: request string\n"
				+ "    2: request number\n"
				+ "    3: alert\n"
				+ "    4: yes or no\n"
				+ "    5: option buttons\n"
				+ "    6: option dropdown\n"
				+ "    7: choose file\n");
	}

	@Override
	public void execute(Block block) {
		final Obj _dialogType = block.pop();
		final Obj _msgType = block.pop();
		final Obj _windowHdr = block.pop();
		final Obj _title = block.pop();
		final Obj _options = block.pop();

		//Check types
		if(!(	_dialogType.isa(Obj.NUMBER)
				&& _msgType.isa(Obj.NUMBER)
				&& _windowHdr.isa(Obj.STR)
				&& _title.isa(Obj.STR)
				&& _options.isa(Obj.LIST)
				)) {
			throw new TypeError(this, "LSSNN", _dialogType, _msgType, _windowHdr, _title, _options);
		}

		//Cast values
		final int dialogType = ((Number)_dialogType).toInt();
		final int msgType = ((Number)_msgType).toInt();
		final String windowHdr = _windowHdr.str();
		final String title = _title.str();
		final List options = ((List)_options);

		//Error checking
		if (dialogType < QuickDialog.MIN_OPT || dialogType > QuickDialog.MAX_OPT) {
			throw new AyaRuntimeException(":{dialog.legacy}: invalid dialog type: " + dialogType);
		}
		if (msgType < 1 || msgType > 4) {
			throw new AyaRuntimeException(":{dialog.legacy}: invalid message type: " + msgType);
		}
		if ((dialogType == QuickDialog.OPTION_BUTTONS || dialogType == QuickDialog.OPTION_DROPDOWN)
				&& options.length() <= 0) {
			throw new AyaRuntimeException(":{dialog.legacy}: options list must not be empty");
		}
		if (dialogType == QuickDialog.YES_OR_NO && options.length() != 2) {
			throw new AyaRuntimeException(":{dialog.legacy}: yes or no dialog options list length must be 2");
		}

		//Convert arraylist to string array
		String[] optionsArr = new String[options.length()];
		for (int i = 0; i < options.length(); i++) {
			optionsArr[i] = options.get(i).str();
		}

		//show dialog
		final Obj out = QuickDialog.showDialog(dialogType, title, optionsArr, windowHdr, msgType);
		if (out != null) {
			block.push(out);
		}
	}

}
