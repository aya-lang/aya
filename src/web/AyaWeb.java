package web;

import java.io.PrintStream;
import java.util.ArrayList;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
//import org.teavm.jso.dom.html.HTMLDocument;

import aya.AyaStdIO;
import aya.StandaloneAya;
import aya.StaticData;
import aya.exceptions.parser.ParserException;
import aya.ext.color.ColorInstructionStore;
import aya.ext.date.DateInstructionStore;
import aya.ext.graphics.GraphicsInstructionStore;
import aya.ext.json.JSONInstructionStore;
import aya.ext.la.LinearAlgebraInstructionStore;
import aya.io.StringOut;
import aya.io.stdin.EmptyInputWrapper;

public class AyaWeb {

	private static StringOut output = new StringOut();
	
    public static void main(String[] args) {
		StaticData sd = StaticData.getInstance();

		StaticData.IO = new AyaStdIO(
				new PrintStream(output.getOutStream()),
				new PrintStream(output.getErrStream()),
				null,
				new EmptyInputWrapper());
		
		WebFilesystemIO fs = new WebFilesystemIO();
		StaticData.FILESYSTEM = fs;
	
		WebAvailableNamedInstructionStore wsi = new WebAvailableNamedInstructionStore();
		sd.addNamedInstructionStore(wsi);

		//sd.addNamedInstructionStore(new DebugInstructionStore());
		sd.addNamedInstructionStore(new JSONInstructionStore());
		//sd.addNamedInstructionStore(new ImageInstructionStore());
		//sd.addNamedInstructionStore(new GraphicsInstructionStore());
		//sd.addNamedInstructionStore(new FStreamInstructionStore());
		//sd.addNamedInstructionStore(new SystemInstructionStore());
		//sd.addNamedInstructionStore(new DialogInstructionStore());
		//sd.addNamedInstructionStore(new PlotInstructionStore());
		sd.addNamedInstructionStore(new DateInstructionStore());
		//sd.addNamedInstructionStore(new SocketInstructionStore());
		sd.addNamedInstructionStore(new ColorInstructionStore());
		sd.addNamedInstructionStore(new LinearAlgebraInstructionStore());
		//sd.addNamedInstructionStore(new ThreadInstructionStore());

    	exportSayHello(new ExportFunctionSayHello() {	
			@Override
			public String call(String s) {
				return sayHello(s);
			}
		});

    	exportRunIsolated(new ExportFunctionRunIsolated() {
			@Override
			public String call(String s) {
				return runIsolated(s);
			}
		});
    	
    	exportAddFile(new ExportFunctionAddFile() {
			@Override
			public void call(String path, String content) {
				((WebFilesystemIO)(StaticData.FILESYSTEM)).addFile(path, content);
			}
		});
    	
    	exportListFiles(new ExportFunctionListFiles() {
			@Override
			public String call() {
				ArrayList<String> files = fs.listFiles();
				String out = "";
				for (String s : files) {
					out += s + ",";
				}
				return out;
			}
		});
    	
    	exportLint(new ExportFunctionLint() {
    		@Override
    		public String call(String source) {
    			ArrayList<ParserException> errors = StandaloneAya.lint(source);
    			if (errors.size() > 0) {
    				// TODO: The compile function stops after the first error
    				// if we update the parser to catch multiple errors, we will need to update this
    				ParserException err = errors.get(0);
    				return err.getSource().getIndex() + ":" + err.getSimpleMessage();
    			} else {
    				return "";
    			}
    		}
    	});
    	
    }
    
    public static String runIsolated(String input) {
    	StandaloneAya.runIsolated(input, StaticData.IO);
        return output.flushOut() + output.flushErr();
    }
    
    //public static void example() {
    //	String outstr = runIsolated("1 1 +");
    //    var document = HTMLDocument.current();
    //    var div = document.createElement("div");
    //    //div.appendChild(document.createTextNode("Hello world"));
    //    div.appendChild(document.createTextNode(outstr));
    //    document.getBody().appendChild(div);
    //}
    
    private static String sayHello(String s) {
    	return "Hello, " + s + "!";
    }
    
    @JSBody(params = "sayHello", script = "main.sayHello = sayHello;")
    private static native void exportSayHello(ExportFunctionSayHello fn);

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
interface ExportFunctionSayHello extends JSObject {
	String call(String s);
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

