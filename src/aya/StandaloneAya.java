package aya;

import java.io.PrintStream;
import java.util.ArrayList;

import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.parser.ParserException;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.ValueError;
import aya.io.fs.FilesystemIO;
import aya.io.http.HTTPDownloader;
import aya.io.stdin.ScannerInputWrapper;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;

public class StandaloneAya {
	/**
	 * A Bare-bones aya entry point that does not provide interactivity or preserve state
	 */

    public static void main(String[] args) {
		StaticData.IO = new AyaStdIO(System.out, System.err, System.in, new ScannerInputWrapper(System.in));
		StaticData.HTTP_DOWNLOADER = new HTTPDownloader();
		StaticData.FILESYSTEM = new FilesystemIO();
		
		if (args.length == 1) {
			runIsolated(args[0], StaticData.IO);
		} else {
			StaticData.IO.err().println("Please specify code to run as command line argument");
		}
    }
    
    // An extremely basic linter that catches syntax errors only
    // Currently the parser will stop after the first error, may need to update later
    public static ArrayList<ParserException> lint(String input) {
    	ArrayList<ParserException> errors = new ArrayList<ParserException>();
        try {
        	Parser.compile(new SourceString(input, "<main>"));
        } catch (ParserException err) {
        	errors.add(err);
        }
        return errors;
    }
    
    public static void runIsolated(String input, AyaStdIO io) {
        ExecutionContext ctx = ExecutionContext.createIsolatedContext();
        ctx.getVars().initGlobals();

        // Create request
        StaticBlock blk = Parser.compileSafeOrNull(new SourceString(input, "<main>"), io);

        if (blk != null) {
            ExecutionRequest request = new ExecutionRequest(0, blk);

            // Create evaluator and run
            BlockEvaluator b = ctx.createEvaluator();
            b.dump(request.getBlock());

            ExecutionResult result = null;
            try {
                b.eval();
                result = new ExecutionResultSuccess(request.id(), b.getStack());
            } catch (AyaRuntimeException ex) {
            	result = new ExecutionResultException(request.id(), ex, ctx.getCallStack());
            } catch (Exception e) {
				PrintStream err = StaticData.IO.err();
				err.println(DebugUtils.exToString(e));
				try {
					if (b.hasOutputState())
						err.println("stack:\n\t" + b.getPrintOutputState());
					if (b.getInstructions().size() > 0)
						err.println("just before:\n\t" + b.getInstructions().toString());
					if (!ctx.getCallStack().isEmpty())
						err.print(ctx.getCallStack().toString());
				} catch (Exception e2) {
					err.println("An additional error was thrown when attempting to print the stack state:");
					err.println(DebugUtils.exToString(e2));
					err.println("This is likely caused by an error in an overloaded __str__ or __repr__ blockEvaluator.");
				} 
				result = new ExecutionResultException(request.id(), new ValueError("TODO"), ctx.getCallStack());
            } finally {
                ctx.getVars().reset();
                ctx.getCallStack().reset();
            }
            
            if (result != null) {
                switch (result.getType()) {
                case ExecutionResult.TYPE_SUCCESS:
                    {
                        ExecutionResultSuccess res = (ExecutionResultSuccess)result;
                        ArrayList<Obj> data = res.getData();
                        if (data.size() > 0) {
                            for (int i = 0; i < data.size(); i++) {
                                io.out().print(data.get(i));
                                if (i < data.size() - 1) io.out().print(' ');
                            }
                            io.out().println();
                        }
                    }
                    break;
                case ExecutionResult.TYPE_EXCEPTION:
                    {
                        ExecutionResultException res = (ExecutionResultException)result;
                        res.ex().print(io.err());
                        if (!res.callstack().equals("")) {
                            io.err().print(res.callstack());
                        }
                    }
                    break;
                }
            }
        }
        
    }
}

