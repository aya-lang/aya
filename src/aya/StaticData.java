package aya;

import aya.ext.color.ColorInstructionStore;
import aya.ext.date.DateInstructionStore;
import aya.ext.debug.DebugInstructionStore;
import aya.ext.dialog.DialogInstructionStore;
import aya.ext.fstream.FStreamInstructionStore;
import aya.ext.graphics.GraphicsInstructionStore;
import aya.ext.image.ImageInstructionStore;
import aya.ext.json.JSONInstructionStore;
import aya.ext.la.LinearAlgebraInstructionStore;
import aya.ext.plot.PlotInstructionStore;
import aya.ext.socket.SocketInstructionStore;
import aya.ext.sys.SystemInstructionStore;
import aya.ext.thread.ThreadInstructionStore;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.instruction.op.ColonOps;
import aya.instruction.op.DotOps;
import aya.instruction.op.MiscOps;
import aya.instruction.op.OpDocReader;
import aya.instruction.op.Operator;
import aya.instruction.op.Ops;
import aya.parser.SpecialNumberParser;
import aya.util.StringSearch;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StaticData {

	public static final boolean DEBUG = true;
	public static final String VERSION_NAME = "v0.4.0";
	public static final String ayarcPath = "ayarc.aya";
	public static final boolean PRINT_LARGE_ERRORS = true;
	public static final String QUIT = "\\Q";


	public static final AyaStdIO IO = new AyaStdIO(System.out, System.err, System.in);

	//
	// All calls to modify this data will need to be thread safe
	//
	private static StaticData _instance;


	//
	// Data loaded in the parser
	//
	private StringSearch _helpData;


	//
	// Data Loaded on Start-up
	//
	private final Map<String, NamedOperator> _namedInstructions = new HashMap<>();


	private StaticData() {
		_helpData = null; // initHelpData will create
	}

	public static StaticData getInstance() {
		if (_instance == null) {
			_instance = new StaticData();
		}
		return _instance;
	}

	public void init() {
		initHelpData();
		initNamedInstructions();
	}


	///////////////
	// Help Data //
	///////////////

	private void initHelpData() {
		if (_helpData == null) {

			//Make sure all classes are loaded
			try {
				loadOps(Ops.OPS);
				loadOps(Ops.EXTRA_OPS);
				loadOps(MiscOps.MATH_OPS);
				loadOps(ColonOps.COLON_OPS);
				loadOps(DotOps.DOT_OPS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.addAll(OpDocReader.getAllOpDescriptions());
			// Add additional help data
			searchList.add(AyaPrefs.CONSTANTS_HELP);
			searchList.add(SpecialNumberParser.STR_CONSTANTS_HELP);
			searchList.toArray(new String[searchList.size()]);
			_helpData = new StringSearch(searchList);
		}
	}

	public StringSearch getHelpData() {
		initHelpData();
		return _helpData;
	}

	public void addHelpText(String in) {
		getHelpData().addUnique(in);
	}

	public String[] getQuickSearchData() {
		return getHelpData().getAllItems();
	}

	/* This function does nothing but force java to load
	 * the operators and call the static blocks
	 */
	private void loadOps(Operator[] ops) {
		for (Operator o : ops) {
			if (o != null) o.getClass();
		}
	}

	////////////////////////
	// Named Instructions //
	////////////////////////

	private void initNamedInstructions() {
		addNamedInstructionStore(new DebugInstructionStore());
		addNamedInstructionStore(new JSONInstructionStore());
		addNamedInstructionStore(new ImageInstructionStore());
		addNamedInstructionStore(new GraphicsInstructionStore());
		addNamedInstructionStore(new FStreamInstructionStore());
		addNamedInstructionStore(new SystemInstructionStore());
		addNamedInstructionStore(new DialogInstructionStore());
		addNamedInstructionStore(new PlotInstructionStore());
		addNamedInstructionStore(new DateInstructionStore());
		addNamedInstructionStore(new SocketInstructionStore());
		addNamedInstructionStore(new ColorInstructionStore());
		addNamedInstructionStore(new LinearAlgebraInstructionStore());
		addNamedInstructionStore(new ThreadInstructionStore());

		/*
			Unfortunately, classpath wildcards are not supported in MANIFEST files. https://docs.oracle.com/javase/7/docs/technotes/tools/windows/classpath.html
			So the user-libraries need to be loaded manually.
		 */
		try {
			File libsDir = new File(AyaPrefs.getAyaRootDirectory(), "libs");
			final URL[] libUrls;
			try (Stream<Path> libPaths = Files.list(libsDir.toPath())) {
				libUrls = libPaths.map(path -> {
					try {
						return path.toUri().toURL();
					} catch (Exception e) {
						return null;
					}
				}).filter(Objects::nonNull).toArray(URL[]::new);
			}

			try (URLClassLoader libClassLoader = new URLClassLoader(libUrls)) {
				StreamSupport.stream(
						ServiceLoader.load(NamedInstructionStore.class, libClassLoader).spliterator(),
						false
				).forEach(store -> {
					IO.out().println("found store: " + store.getClass().getName());
					addNamedInstructionStore(store);
				});
			}
		} catch (Exception e) {
			IO.err().println("Failed to load libraries due to exception: " + e.getMessage());
			e.printStackTrace(IO.err());
		}
	}

	private void addNamedInstructionStore(NamedInstructionStore store) {
		for (NamedOperator instruction : store.getNamedInstructions()) {
			String iName = instruction.getName();
			NamedOperator previous = _namedInstructions.put(iName, instruction);
			if (previous != null) {
				IO.err().println("NamedInstruction '" + iName + "' has multiple implementations:\n"
						+ "  " + previous.getClass().getName() + "\n"
						+ "  " + instruction.getClass().getName()
				);
			}

			String doc = instruction.getDoc();
			if (doc != null && !doc.isEmpty()) {
				addHelpText(instruction.opName() + "\n  " + doc);
			}
		}
	}

	public NamedOperator getNamedInstruction(String name) {
		return _namedInstructions.get(name);
	}

}
