package aya.ext.dialog;

import javax.swing.JOptionPane;

import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.variable.EncodedVars;

public class DialogInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {

		addInstruction(new NamedInstruction("dialog.getstr", "message::str: popup window with a a text input field") {
			@Override
			public void execute(Block block) {
				final Obj title = block.pop();
				block.push(new Str(QuickDialog.requestString(title.str())));
			}
		});

		addInstruction(new NamedInstruction("dialog.getnum", "message::str: popup window with a number input field") {
			@Override
			public void execute(Block block) {
				final Obj title = block.pop();
				block.push(QuickDialog.numberInput(title.str()));
			}
		});
		
		addInstruction(new NamedInstruction("dialog.alert", "message::str title::str type::sym show an alert dialog") {
			@Override
			public void execute(Block block) {
				final Obj type_obj = block.pop();
				final Obj title_obj = block.pop();
				final Obj message_obj = block.pop();
				
				if (!type_obj.isa(Obj.SYMBOL)) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);
				QuickDialog.alert(message_obj.str(), title_obj.str(), type);
			}
		});

		addInstruction(new NamedInstruction("dialog.confirm", "message::str options::list title::str type::sym show an alert dialog") {
			@Override
			public void execute(Block block) {
				final Obj type_obj = block.pop();
				final Obj title_obj = block.pop();
				final Obj options_obj = block.pop();
				final Obj message_obj = block.pop();
				
				if (!(type_obj.isa(Obj.SYMBOL) && options_obj.isa(Obj.LIST))) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);
				List options = (List)options_obj;
				
				if (options.length() != 2) throw new AyaRuntimeException(":{dialog.confirm} : Expected options list of length 2. Got " + options.repr());
				
				boolean val = QuickDialog.confirm(
						message_obj.str(),
						options.get(0).str(),
						options.get(1).str(),
						title_obj.str(),
						type);
						
				block.push(Num.fromBool(val));
			}
		});
		
		addInstruction(new NamedInstruction("dialog.buttons", "message::str options::list title::str type::sym: show a dialog with several option buttons") {
			@Override
			public void execute(Block block) {
				final Obj type_obj = block.pop();
				final Obj title_obj = block.pop();
				final Obj options_obj = block.pop();
				final Obj message_obj = block.pop();
				
				if (!(type_obj.isa(Obj.SYMBOL) && options_obj.isa(Obj.LIST))) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);

				List options_list = (List)options_obj;
				if (options_list.length() <= 0) throw new AyaRuntimeException(":{dialog.buttons} : Expected non-empty options. Got " + options_list.repr());

				String[] options = new String[options_list.length()];
				for (int i = 0; i < options_list.length(); i++) {
					options[i] = options_list.get(i).str();
				}

				String selected = QuickDialog.selectOptionButtons(
						message_obj.str(),
						options,
						title_obj.str(),
						type);
						
				block.push(new Str(selected));
			}
		});

		addInstruction(new NamedInstruction("dialog.dropdown", "message::str options::list title::str type::sym: show a dialog with several options as a dropdown") {
			@Override
			public void execute(Block block) {
				final Obj type_obj = block.pop();
				final Obj title_obj = block.pop();
				final Obj options_obj = block.pop();
				final Obj message_obj = block.pop();
				
				if (!(type_obj.isa(Obj.SYMBOL) && options_obj.isa(Obj.LIST))) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);

				List options_list = (List)options_obj;
				if (options_list.length() <= 0) throw new AyaRuntimeException(":{dialog.buttons} : Expected non-empty options. Got " + options_list.repr());

				String[] options = new String[options_list.length()];
				for (int i = 0; i < options_list.length(); i++) {
					options[i] = options_list.get(i).str();
				}

				String selected = QuickDialog.selectOptionDropdown(
						message_obj.str(),
						options,
						title_obj.str(),
						type);
						
				block.push(new Str(selected));
				
			}
		});
		
		addInstruction(new NamedInstruction("dialog.choosefile") {
			@Override
			public void execute(Block block) {
				block.push(new Str(QuickDialog.chooseFile()));
			}
		});
	}
	
	private int symToDialogType(Symbol sym) {
		final long id = sym.id();
		if (id == EncodedVars.PLAIN) return JOptionPane.PLAIN_MESSAGE;
		if (id == EncodedVars.QUESTION) return JOptionPane.QUESTION_MESSAGE;
		if (id == EncodedVars.WARN) return JOptionPane.WARNING_MESSAGE;
		if (id == EncodedVars.ERROR) return JOptionPane.ERROR_MESSAGE;
		else return JOptionPane.PLAIN_MESSAGE;
	}
}
