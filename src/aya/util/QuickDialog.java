package aya.util;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apfloat.Apfloat;

import aya.AyaPrefs;
import aya.obj.Obj;
import aya.obj.list.Str;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;


public class QuickDialog {
	
	//message types
	public static final int M_PLAIN = 1; //JOptionPane.PLAIN_MESSAGE;
	public static final int M_QUESTION = 2; //JOptionPane.QUESTION_MESSAGE;
	public static final int M_WARNING = 3; //JOptionPane.WARNING_MESSAGE;
	public static final int M_ERROR = 4; //JOptionPane.ERROR_MESSAGE;
	
	static int getMsgType(int msgType) {
		switch (msgType) {
		case 1:
			return JOptionPane.PLAIN_MESSAGE;
		case 2:
			return JOptionPane.QUESTION_MESSAGE;
		case 3:
			return JOptionPane.WARNING_MESSAGE;
		case 4:
			return JOptionPane.ERROR_MESSAGE;
		default:
			return JOptionPane.PLAIN_MESSAGE;
		}
	}
	
	//dialog types
	public static final int REQUEST_STRING = 1;
	public static final int REQUEST_NUMBER = 2;
	public static final int ALERT = 3;
	public static final int YES_OR_NO = 4;
	public static final int OPTION_BUTTONS = 5;
	public static final int OPTION_DROPDOWN = 6;
	public static final int CHOOSE_FILE = 7;
	
	//Minimum and maximum values of the above list of message types
	public static final int MIN_OPT = 1;
	public static final int MAX_OPT = 7;
	
	/** Requests a string input using JOptionPane  */
	public static String requestString(String title) {
		String s = JOptionPane.showInputDialog(title);
		if (s == null) 
			return "";
		return s;
	}
	
	/** Requests an input until a valid number is entered */
	public static Number numberInput(String title) {
		String error = "";
		Number out = null;
		do {
			String s = null;
			do {
				s = JOptionPane.showInputDialog(title, error);
			} while (s == null);
			
			try {
				out = new Num(Double.parseDouble(s));
			} catch (NumberFormatException e) {
				try {
					out = new BigNum(new Apfloat(s));
				} catch (NumberFormatException e2) {
					error = "Number Format Error";
				}
			}
			
		} while (out == null);
		return out;
	}
	
	/** A simple alert dialog */
	public static void alert(String title, String windowHdr, int msgType) {
		JOptionPane.showMessageDialog(null, title, windowHdr, msgType);
	}
	
	/** Returns true if yes is selected and false if no is selected */
	public static boolean yesOrNo(String title, String yes, String no, String windowHdr, int msgType) {
		String[] options = {yes,no};
		int n = JOptionPane.showOptionDialog(
				null,
				title,
				windowHdr,
				JOptionPane.YES_NO_OPTION,
				msgType,
				null,
				options,
				null);
		return n == 0;
	}
	
	public static String selectOptionButtons(String title, String[] options, String windowHdr, int msgType) {
		int choice = JOptionPane.showOptionDialog(
				null,
				title,
				windowHdr,
				JOptionPane.DEFAULT_OPTION,
				msgType,
				null,
				options,
				options[0]);
		if (choice == -1) {
			return "";
		}
		return options[choice];
	}
	
	public static String selectOptionDropdown(String title, String[] options, String windowHdr, int msgType) {
		String choice = (String)JOptionPane.showInputDialog(null,
				title, 
				windowHdr,
				msgType,
				null,
				options,
				options[0]);
		if (choice == null) {
			return "";
		}
		return choice;
	}
	
	public static String chooseFile() {
		 JFileChooser chooser = new JFileChooser(AyaPrefs.getWorkingDir());
		 int returVal = chooser.showOpenDialog(null);
		 if (returVal == JFileChooser.APPROVE_OPTION) {
			 return chooser.getSelectedFile().getAbsolutePath();
		 } else {
			 return "";
		 }
	}
	
	public static Obj showDialog(int dialogType, String title, String[] options, String windowHdr, int msgType) {
		msgType = getMsgType(msgType);
		switch (dialogType) {
		case REQUEST_STRING:
			return new Str(requestString(title));
		case REQUEST_NUMBER:
			return numberInput(title);
		case ALERT:
			alert(title, windowHdr, msgType);
			return null;
		case YES_OR_NO:
			if (options.length < 2) {
				throw new RuntimeException("Need two options for yes/no dialog.");
			}
			//TODO: Change Num to ByteNum
			return new Num(yesOrNo(title, options[0], options[1], windowHdr, msgType));
		case OPTION_BUTTONS:
			return new Str(selectOptionButtons(title, options, windowHdr, msgType));
		case OPTION_DROPDOWN:
			return new Str(selectOptionDropdown(title, options, windowHdr, msgType));
		case CHOOSE_FILE:
			return new Str(chooseFile());
		default:
			throw new RuntimeException("dialogType is invalid. Recieved " +  dialogType);
		}
	}
	
	public static void main(String[] args) {

	}
	
}
