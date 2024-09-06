package aya.ext.dialog;

import static aya.util.Casting.asList;

import javax.swing.JOptionPane;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.instruction.named.NamedInstructionStore;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class DialogInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {

		addInstruction(new NamedOperator("dialog.getstr", "message::str: popup window with a a text input field") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj title = blockEvaluator.pop();
				blockEvaluator.push(List.fromString(QuickDialog.requestString(title.str())));
			}
		});

		addInstruction(new NamedOperator("dialog.getnum", "message::str: popup window with a number input field") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj title = blockEvaluator.pop();
				blockEvaluator.push(QuickDialog.numberInput(title.str()));
			}
		});
		
		addInstruction(new NamedOperator("dialog.alert", "message::str title::str type::sym show an alert dialog") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj type_obj = blockEvaluator.pop();
				final Obj title_obj = blockEvaluator.pop();
				final Obj message_obj = blockEvaluator.pop();
				
				if (!type_obj.isa(Obj.SYMBOL)) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);
				QuickDialog.alert(message_obj.str(), title_obj.str(), type);
			}
		});

		addInstruction(new NamedOperator("dialog.confirm", "message::str options::list title::str type::sym show an alert dialog") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj type_obj = blockEvaluator.pop();
				final Obj title_obj = blockEvaluator.pop();
				final Obj options_obj = blockEvaluator.pop();
				final Obj message_obj = blockEvaluator.pop();
				
				if (!(type_obj.isa(Obj.SYMBOL) && options_obj.isa(Obj.LIST))) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);
				List options = asList(options_obj);
				
				if (options.length() != 2) throw new ValueError(":{dialog.confirm} : Expected options list of length 2. Got " + options.repr());
				
				boolean val = QuickDialog.confirm(
						message_obj.str(),
						options.getExact(0).str(),
						options.getExact(1).str(),
						title_obj.str(),
						type);
						
				blockEvaluator.push(Num.fromBool(val));
			}
		});
		
		addInstruction(new NamedOperator("dialog.buttons", "message::str options::list title::str type::sym: show a dialog with several option buttons") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj type_obj = blockEvaluator.pop();
				final Obj title_obj = blockEvaluator.pop();
				final Obj options_obj = blockEvaluator.pop();
				final Obj message_obj = blockEvaluator.pop();
				
				if (!(type_obj.isa(Obj.SYMBOL) && options_obj.isa(Obj.LIST))) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);

				List options_list = asList(options_obj);
				if (options_list.length() <= 0) throw new ValueError(":{dialog.buttons} : Expected non-empty options. Got " + options_list.repr());

				String[] options = new String[options_list.length()];
				for (int i = 0; i < options_list.length(); i++) {
					options[i] = options_list.getExact(i).str();
				}

				String selected = QuickDialog.selectOptionButtons(
						message_obj.str(),
						options,
						title_obj.str(),
						type);
						
				blockEvaluator.push(List.fromString(selected));
			}
		});

		addInstruction(new NamedOperator("dialog.dropdown", "message::str options::list title::str type::sym: show a dialog with several options as a dropdown") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj type_obj = blockEvaluator.pop();
				final Obj title_obj = blockEvaluator.pop();
				final Obj options_obj = blockEvaluator.pop();
				final Obj message_obj = blockEvaluator.pop();
				
				if (!(type_obj.isa(Obj.SYMBOL) && options_obj.isa(Obj.LIST))) throw new TypeError(this, "SSJ", type_obj, title_obj, message_obj);

				int type = symToDialogType((Symbol)type_obj);

				List options_list = asList(options_obj);
				if (options_list.length() <= 0) throw new ValueError(":{dialog.buttons} : Expected non-empty options. Got " + options_list.repr());

				String[] options = new String[options_list.length()];
				for (int i = 0; i < options_list.length(); i++) {
					options[i] = options_list.getExact(i).str();
				}

				String selected = QuickDialog.selectOptionDropdown(
						message_obj.str(),
						options,
						title_obj.str(),
						type);
						
				blockEvaluator.push(List.fromString(selected));
				
			}
		});
		
		addInstruction(new NamedOperator("dialog.choosefile") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				blockEvaluator.push(List.fromString(QuickDialog.chooseFile()));
			}
		});
	}
	
	private int symToDialogType(Symbol sym) {
		final long id = sym.id();
		if (id == SymbolConstants.PLAIN.id()) return JOptionPane.PLAIN_MESSAGE;
		if (id == SymbolConstants.QUESTION.id()) return JOptionPane.QUESTION_MESSAGE;
		if (id == SymbolConstants.WARN.id()) return JOptionPane.WARNING_MESSAGE;
		if (id == SymbolConstants.ERROR.id()) return JOptionPane.ERROR_MESSAGE;
		else return JOptionPane.PLAIN_MESSAGE;
	}
}
