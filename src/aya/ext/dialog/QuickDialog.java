package aya.ext.dialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apfloat.Apfloat;

import aya.AyaPrefs;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;


public class QuickDialog {
	
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
	public static boolean confirm(String title, String yes, String no, String windowHdr, int msgType) {
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
	
}
