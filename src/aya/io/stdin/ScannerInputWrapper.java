package aya.io.stdin;

import java.io.InputStream;
import java.util.Scanner;

public class ScannerInputWrapper extends InputWrapper {
	private Scanner _scanner;
	
	public ScannerInputWrapper(Scanner scanner) {
		_scanner = scanner;
	}

	public ScannerInputWrapper(InputStream in) {
		_scanner = new Scanner(in, "UTF-8");
	}
	
	@Override
	public String nextLine() {
		return _scanner.nextLine();
	}
	
	public Scanner getScanner() {
		return _scanner;
	}
}
