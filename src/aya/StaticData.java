package aya;

import java.util.ArrayList;

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
import aya.io.fs.AbstractFilesystemIO;
import aya.io.fs.UnimplementedFilesystemIO;
import aya.io.http.AbstractHTTPDownloader;
import aya.io.http.UnimplementedHTTPDownloader;
import aya.parser.SpecialNumberParser;
import aya.util.StringSearch;

public class StaticData {

	public static final boolean DEBUG = true;
	public static final String VERSION_NAME = "v0.4.0";
	public static final String ayarcPath = "ayarc.aya";
	public static final boolean PRINT_LARGE_ERRORS = true;
	public static final String QUIT = "\\Q";

	
	// Must me initialized in main
	public static AyaStdIO IO = null;
	public static AbstractHTTPDownloader HTTP_DOWNLOADER = new UnimplementedHTTPDownloader();
	public static AbstractFilesystemIO FILESYSTEM = new UnimplementedFilesystemIO();
	
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
	private ArrayList<NamedInstructionStore> _namedInstructionStores;
	
	
	private StaticData() {
		_helpData = null; // initHelpData will create
		_namedInstructionStores = new ArrayList<NamedInstructionStore>();
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
		if(_helpData == null) {
			
			//Make sure all classes are loaded
			try
			{
			  loadOps(Ops.OPS);
			  loadOps(Ops.EXTRA_OPS);
			  loadOps(MiscOps.MATH_OPS);
			  loadOps(ColonOps.COLON_OPS);
			  loadOps(DotOps.DOT_OPS);
			}
			catch(Exception e)
			{
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
		_namedInstructionStores.add(new DebugInstructionStore());
		_namedInstructionStores.add(new JSONInstructionStore());
		_namedInstructionStores.add(new ImageInstructionStore());
		_namedInstructionStores.add(new GraphicsInstructionStore());
		_namedInstructionStores.add(new FStreamInstructionStore());
		_namedInstructionStores.add(new SystemInstructionStore());
		_namedInstructionStores.add(new DialogInstructionStore());
		_namedInstructionStores.add(new PlotInstructionStore());
		_namedInstructionStores.add(new DateInstructionStore());
		_namedInstructionStores.add(new SocketInstructionStore());
		_namedInstructionStores.add(new ColorInstructionStore());
		_namedInstructionStores.add(new LinearAlgebraInstructionStore());
		_namedInstructionStores.add(new ThreadInstructionStore());
		
		for (NamedInstructionStore x : _namedInstructionStores) {
			x.initHelpData(this);
		}
		
	}
	public NamedOperator getNamedInstruction(String name) {
		for (NamedInstructionStore x : _namedInstructionStores) {
			NamedOperator i = x.getInstruction(name);
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	

}
