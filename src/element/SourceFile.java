package element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import element.obj.block.Block;
import element.parser.Parser;

/**
 * A Class used for managing comma separated data text files
 * @author Nicholas Paul
 */
public class SourceFile {
	private String rawText;
	private String codeText;
	private Block code;
	File file;
	private Element elem;

	/** New source file using a file 
	 * @throws FileNotFoundException */
	public SourceFile(File file, Element elem) throws FileNotFoundException {
		this.file = file;
		this.elem = elem;
		
		
		try {
			file.createNewFile(); //Only creates a new file if the file does not exist
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		BufferedReader br = null;
		try {
			//str = new Scanner( this.file ).useDelimiter("\\A").next();
			FileInputStream fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			StringBuilder codeTxtBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append('\n');
				//if(line.length() > 0 && line.charAt(0) == '\t') {
					codeTxtBuilder.append(line).append('\n');
				//}
			}
			this.rawText = sb.toString();
			this.codeText = codeTxtBuilder.toString();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File '" + file.getPath() + "' cannot be found");
		} catch (NoSuchElementException e2) {
			//File exists, but is empty
			this.rawText = "";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Returns the compiled instructions from the source file */
	public Block getBlock() {
		return code;
	}
	
	/** Parses the data file 
	 * @throws SyntaxException */
	public void compile() {
		this.code = Parser.compile(this.codeText, this.elem);
	}
	

	/** Returns true if the file is empty */
	public boolean isEmpty() {
		return this.rawText.trim().equals("");
	}

	/** Appends a string to the data file */
	public void print(String str) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(this.file);
		this.rawText += str;
		writer.print(this.rawText);
		writer.close();
	}

	/** prints the input string over the current file. Erases all previous file contents */
	public void printOver(String text) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(this.file);
		writer.print(text);
		this.rawText = text;
		writer.close();
	}

	/** returns the rawText for the file */
	public String getRawText() {
		return rawText;
	}
	
	/** returns the codeText for the file */
	public String getCodeText() {
		return codeText;
	}

	/** returns false if the file does not exist */
	public boolean exists() {
		return this.rawText != null;
	}
	
	static String readFile(String path, Charset encoding)  throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	/** returns the rawtext of the file */
	@Override public String toString() {
		return this.rawText;
	}
}
