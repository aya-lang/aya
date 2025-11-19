package web;

import java.io.PrintStream;
import java.util.ArrayList;

import aya.ext.xml.XmlInstructionStore;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

import aya.AyaStdIO;
import aya.StandaloneAya;
import aya.StaticData;
import aya.exceptions.parser.ParserException;
import aya.ext.color.ColorInstructionStore;
import aya.ext.date.DateInstructionStore;
import aya.ext.json.JSONInstructionStore;
import aya.ext.la.LinearAlgebraInstructionStore;
import aya.io.StringOut;
import aya.io.stdin.EmptyInputWrapper;

public class AyaWeb {

	private static final StringOut output = new StringOut();
	
    public static void main(String[] args) {
    	
    	//
    	// Set up StaticData
    	//
		StaticData sd = StaticData.getInstance();

		StaticData.IO = new AyaStdIO(
				new PrintStream(output.getOutStream()),
				new PrintStream(output.getErrStream()),
				null,
				new EmptyInputWrapper());
		
		WebFilesystemIO fs = new WebFilesystemIO();
		StaticData.FILESYSTEM = fs;
		
		//
		// Named Instructions
		// Web build only supports a limited set of named instructions
		// We do not use the full initNamedInstructions method for web
		//
		WebAvailableNamedInstructionStore wsi = new WebAvailableNamedInstructionStore();
		sd.addNamedInstructionStore(wsi);
		sd.addNamedInstructionStore(new JSONInstructionStore());
		sd.addNamedInstructionStore(new XmlInstructionStore());
		sd.addNamedInstructionStore(new DateInstructionStore());
		sd.addNamedInstructionStore(new ColorInstructionStore());
		sd.addNamedInstructionStore(new LinearAlgebraInstructionStore());

		//
		// Exported Functions Implementation
		//
    	exportRunIsolated(s -> {
			StandaloneAya.runIsolated(s, StaticData.IO);
			return output.flushOut() + output.flushErr();
		});
    	
    	exportAddFile((path, content) -> ((WebFilesystemIO)(StaticData.FILESYSTEM)).addFile(path, content));
    	
    	exportListFiles(() -> String.join(",", fs.listFiles()));
    	
    	exportLint(source -> {
			ArrayList<ParserException> errors = StandaloneAya.lint(source);
			if (errors.size() > 0) {
				// TODO: The compile function stops after the first error
				// if we update the parser to catch multiple errors, we will need to update this
				ParserException err = errors.get(0);
				return err.getSource().getIndex() + ":" + err.getSimpleMessage();
			} else {
				return "";
			}
		});
    	
    }
    
    //
    // Exported Functions
    //
    
    @JSBody(params = "runIsolated", script = "main.runIsolated = runIsolated;")
    private static native void exportRunIsolated(ExportFunctionRunIsolated fn);

    @JSBody(params = "addFile", script = "main.addFile = addFile;")
    private static native void exportAddFile(ExportFunctionAddFile fn);

    @JSBody(params = "listFiles", script = "main.listFiles = listFiles;")
    private static native void exportListFiles(ExportFunctionListFiles fn);

    @JSBody(params = "lint", script = "main.lint = lint;")
    private static native void exportLint(ExportFunctionLint fn);

}

@JSFunctor
interface ExportFunctionRunIsolated extends JSObject {
	String call(String s);
}

@JSFunctor
interface ExportFunctionAddFile extends JSObject {
	void call(String path, String content);
}

@JSFunctor
interface ExportFunctionListFiles extends JSObject {
	String call();
}

@JSFunctor
interface ExportFunctionLint extends JSObject {
	String call(String source);
}