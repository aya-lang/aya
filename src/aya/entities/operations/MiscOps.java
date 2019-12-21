package aya.entities.operations;

import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUM;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.SYMBOL;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import aya.Aya;
import aya.AyaPrefs;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.SyntaxError;
import aya.exceptions.TypeError;
import aya.instruction.op.OpDocReader;
import aya.instruction.op.OpInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.list.StrList;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.RationalNum;
import aya.obj.symbol.Symbol;
import aya.parser.CharacterParser;
import aya.util.CanvasInterface;
import aya.util.ChartParams;
import aya.util.FileUtils;
import aya.util.FreeChartInterface;
import aya.util.QuickDialog;
import aya.util.interfaces.JSONDecoder;

public class MiscOps {	

	
	public static char FIRST_OP = '!';
	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static OpInstruction[] MATH_OPS = {
		/* 33 !  */ new OP_Fact(),
		/* 34 "  */ null,
		/* 35 #  */ null,
		/* 36 $  */ new OP_SysTime(),
		/* 37 %  */ null,
		/* 38 &  */ null,
		/* 39 '  */ null,
		/* 40 (  */ null,
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ null,
		/* 46 .  */ null,
		/* 47 /  */ null,
		/* 48 0  */ null,
		/* 49 1  */ null,
		/* 50 2  */ null,
		/* 51 3  */ null,
		/* 52 4  */ null,
		/* 53 5  */ null,
		/* 54 6  */ null,
		/* 55 7  */ null,
		/* 56 8  */ null,
		/* 57 9  */ null,
		/* 58 :  */ null,
		/* 59 ;  */ null,
		/* 60 <  */ null, //new OP_ModSet(),
		/* 61 =  */ null,
		/* 62 >  */ null, //new OP_ModGet(),
		/* 63 ?  */ new OP_Help(),
		/* 64 @  */ null,
		/* 65 A  */ null, //new OP_Abs(),
		/* 66 B  */ null,
		/* 67 C  */ new OP_Acosine(),
		/* 68 D  */ new OP_MDate(),
		/* 69 E  */ null, //new OP_ScientificNotation(),
		/* 70 F  */ null,
		/* 71 G  */ new OP_Graphics(),
		/* 72 H  */ new OP_MParse_Date(),
		/* 73 I  */ null,
		/* 74 J  */ new OP_JSON(),
		/* 75 K  */ null,
		/* 76 L  */ new OP_Log(),
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ null,
		/* 80 P  */ null,//new OP_PrintColor(),
		/* 81 Q  */ null,
		/* 82 R  */ null,
		/* 83 S  */ new OP_Asine(),
		/* 84 T  */ new OP_Atangent(),
		/* 85 U  */ null,
		/* 86 V  */ new OP_Dialog(),
		/* 87 W  */ null,
		/* 88 X  */ new OP_AdvPlot(),
		/* 89 Y  */ null,
		/* 90 Z  */ new OP_SysConfig(),
		/* 91 [  */ null, //Matrix Literal
		/* 92 \  */ null,
		/* 93 ]  */ null, //No used for matrix literal, but ptobably shouldnt be used for anything
		/* 94 ^  */ null,
		/* 95 _  */ null,
		/* 96 `  */ null,
		/* 97 a  */ null,
		/* 98 b  */ null,
		/* 99 c  */ new OP_Cosine(),
		/* 100 d */ new OP_CastDouble(),
		/* 101 e */ new OP_Me(),
		/* 102 f */ null,
		/* 103 g */ null,
		/* 104 h */ new OP_MShow_Date(),
		/* 105 i */ null, //new OP_CastInt(),
		/* 106 j */ null,
		/* 107 k */ new OP_AddParserChar(),
		/* 108 l */ new OP_Ln(),
		/* 109 m */ new OP_HasMeta(),
		/* 110 n */ null,
		/* 111 o */ null,
		/* 112 p */ new OP_Primes(),
		/* 113 q */ new OP_SquareRoot(),
		/* 114 r */ new OP_To_Rat(),
		/* 115 s */ new OP_Sine(),
		/* 116 t */ new OP_Tangent(),
		/* 117 u */ null,
		/* 118 v */ null,
		/* 119 w */ null,
		/* 120 x */ null,
		/* 121 y */ null,
		/* 122 z */ null,
		/* 123 { */ null,
		/* 124 | */ null,
		/* 125 } */ null,
		/* 126 ~ */ null,
	};
	
	/** Returns the operation bound to the character */
	public static OpInstruction getOp(char op) {
		if(op >= 33 && op <= 126) {
			return MATH_OPS[op-FIRST_OP];
		} else {
			throw new SyntaxError("Misc. operator 'M" + op + "' does not exist");
		}
	}
	
}

// ! - 33
class OP_Fact extends OpInstruction {
	
	public OP_Fact() {
		init("M!");
		arg("N", "factorial");
		setOverload(1, "fact");
		vect();
	}
	
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)){
			block.push(((Number)n).factorial());
		} else if (n.isa(NUMBERLIST)) {
			block.push( ((NumberList)n).factorial() );
		} else {
			throw new TypeError(this, n);
		}
	}
}

// $ - 33
class OP_SysTime extends OpInstruction {
	
	public OP_SysTime() {
		init("M$");
		arg("-", "system time in milliseconds");
	}
	
	@Override
	public void execute(Block block) {
		block.push(new Num(System.currentTimeMillis()));
	}
}



// ? - 63
class OP_Help extends OpInstruction {
	
	public OP_Help() {
		init("M?");
		arg("N", "list op descriptions where N=[0:std, 1:dot, 2:colon, 3:misc]");
		arg("S", "search all help data");
	}

	@Override
	public void execute(Block block) {
		Obj s = block.pop();
		
		if(s.isa(STR)) {
			String str = s.str();
			ArrayList<Str> items;
			
			if (str.length() == 0) {
				String[] ss = Aya.getInstance().getHelpData().getAllItems();
				items = new ArrayList<Str>(ss.length);
				for (String a : ss) {
					items.add(new Str(a));
				}
			} else {
				ArrayList<String> ss = Aya.getInstance().getHelpData().staticSearch(s.str());
				items = new ArrayList<Str>(ss.size());
				for (String a : ss) {
					items.add(new Str(a));
				}
			}
			
			block.push(new StrList(items));
			
		} else if (s.isa(NUMBER)) {
			block.push(new GenericList(OpDocReader.getAllOpDicts()));
		}
		else {
			throw new TypeError(this, s);
		}
	}
}

// C - 67
class OP_Acosine extends OpInstruction {
	
	public OP_Acosine() {
		init("MC");
		arg("N", "inverse cosine");
		setOverload(1, "acos");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;

		if(n.isa(NUMBER)) {
			block.push(((Number)n).acos());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).acos());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// D - 68
class OP_MDate extends OpInstruction {

	private Calendar cal; 

	public OP_MDate() {
		init("MD");
		arg("N", "given time in ms, return date params [day_of_week, year, month, day_of_month, hour, min, s]");
		cal = Calendar.getInstance();
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();

		if (a.isa(NUMBER)) {
			long timeStamp = ((Number)a).toLong();
			cal.setTimeInMillis(timeStamp);
			
			ArrayList<Number> fields = new ArrayList<Number>();
			
			fields.add(new Num(cal.get(Calendar.DAY_OF_WEEK)));
			fields.add(new Num(cal.get(Calendar.YEAR)));
			fields.add(new Num(cal.get(Calendar.MONTH)));
			fields.add(new Num(cal.get(Calendar.DAY_OF_MONTH)));
			fields.add(new Num(cal.get(Calendar.HOUR)));
			fields.add(new Num(cal.get(Calendar.MINUTE)));
			fields.add(new Num(cal.get(Calendar.SECOND)));

			block.push(new NumberItemList(fields));
		} else {
			throw new TypeError(this, a);
		}
	}
}


// G - 67
class OP_Graphics extends OpInstruction {
	
	private CanvasInterface canvas;
	
	public OP_Graphics() {
		init("MG");
		arg("JDN", "2D graphics interface");

		this.canvas = new CanvasInterface();
	}
	
	@Override
	public void execute(Block block) {
		final Obj o_id = block.pop();
		final Obj o_params = block.pop();
		final Obj o_command = block.pop();
		
		
		if (o_id.isa(NUM) && o_params.isa(DICT) && o_command.isa(SYMBOL)) {
			int id = ((Number)o_id).toInt();
			Dict params = (Dict)o_params;
			Symbol command = (Symbol)o_command;
			
			int result = this.canvas.doCommand(id, command, params);
			block.push(new Num(result));
		} else {
			throw new TypeError(this, o_id, o_params, o_command);
		}
		
	}
}



// H - 68
class OP_MParse_Date extends OpInstruction {
	
	public OP_MParse_Date() {
		init("MH");
		arg("SS", "parse a date using a given format and return the time in ms");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		
		if (a.isa(STR) && b.isa(STR)) {
			String df_str = a.str();
			String date_str = b.str();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new AyaRuntimeException("Invalid date format: '" + df_str + "'");
			}
			
			Date date;
			try {
				date = df.parse(date_str);
			} catch (ParseException e) {
				throw new AyaRuntimeException("Cannot parse date: '" + date_str + "' as '" + df_str + "'");
			}
			block.push(new Num(date.getTime()));
		} else {
			throw new TypeError(this, a, b);
		}
		
	}
}


// J - 74
class OP_JSON extends OpInstruction {

	public OP_JSON() {
		init("MJ");
		arg("AA", "json interface");
	}

	@Override
	public void execute(Block block) {
		Obj cmd_obj = block.pop();
		Obj obj = block.pop();
		
		String cmd = getCmd(cmd_obj);

		if (cmd.equals("decode") || cmd.equals("decode_json")) {
			if (obj.isa(STR)) {
				block.push(JSONDecoder.decodeJSON(obj.str()));
			} else {
				throw new TypeError(this, cmd_obj, obj);
			}
		} else if (cmd.equals("decode_xml")) {
			if (obj.isa(STR)) {
				block.push(JSONDecoder.decodeXML(obj.str()));
			} else {
				throw new TypeError(this, cmd_obj, obj);
			}
		} else {
			throw new AyaRuntimeException("MJ: " + cmd + " is not a valid command");
		}
	}
	
	private String getCmd(Obj o) {
		if (o.isa(STR)) {
			return o.str();
		} else if (o.isa(SYMBOL)) {
			return ((Symbol)o).name();
		} else {
			throw new AyaRuntimeException("MJ: " + o.repr() + " is not a valid command");
		}
	}
	
}


// L - 76
class OP_Log extends OpInstruction {
	
	public OP_Log() {
		init("ML");
		arg("N", "base-10 logarithm");
		setOverload(1, "log");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (overload().execute(block, a)) return;
		
		if(a.isa(NUMBER)) {
			block.push(((Number)a).log());
		} else if (a.isa(NUMBERLIST)) {
			block.push(((NumberList)a).log());
		} else {
			throw new TypeError(this, a);
		}
	}
}


// S - 83
class OP_Asine extends OpInstruction {
	
	public OP_Asine() {
		init("MS");
		arg("N", "inverse sine");
		vect();
		setOverload(1, "asin");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;

		if(n.isa(NUMBER)) {
			block.push(((Number)n).asin());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).asin());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// T - 84
class OP_Atangent extends OpInstruction {
	
	public OP_Atangent() {
		init("MT");
		arg("N", "inverse tangent");
		vect();
		setOverload(1, "atan");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
	
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).atan());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).atan());
		} else {
			throw new TypeError(this, n);
		}
	}
}


//V - 86
class OP_Dialog extends OpInstruction {
	
	public OP_Dialog() {
		init("MV");
		arg("LSSNN", "options title windowhdr msgtype dialogtype MV\n"
				+ "  msgtype:\n"
				+ "    1: plain\n"
				+ "    2: question\n"
				+ "    3: warning\n"
				+ "    4: error"
				+ "  dialogtype:\n"
				+ "    1: request string\n"
				+ "    2: request number\n"
				+ "    3: alert\n"
				+ "    4: yes or no\n"
				+ "    5: option buttons\n"
				+ "    6: option dropdown\n"
				+ "    7: choose file\n"
);
	}

	@Override
	public void execute(Block block) {
		final Obj _dialogType = block.pop();
		final Obj _msgType = block.pop();
		final Obj _windowHdr = block.pop();
		final Obj _title = block.pop();
		final Obj _options = block.pop();
		
		//Check types
		if(!(	_dialogType.isa(NUMBER)
				&& _msgType.isa(NUMBER)
				&& _windowHdr.isa(STR)
				&& _title.isa(STR)
				&& _options.isa(LIST)
				)) {
			throw new TypeError(this, _dialogType, _msgType, _windowHdr, _title, _options);
		}
		
		//Cast values
		final int dialogType = ((Number)_dialogType).toInt();
		final int msgType = ((Number)_msgType).toInt();
		final String windowHdr = _windowHdr.str();
		final String title = _title.str();
		final List options = ((List)_options);
		
		//Error checking
		if (dialogType < QuickDialog.MIN_OPT || dialogType > QuickDialog.MAX_OPT) {
			throw new AyaRuntimeException("MV: invalid dialog type: " + dialogType);
		}
		if (msgType < 1 || msgType > 4) {
			throw new AyaRuntimeException("MV: invalid message type: " + msgType);
		}
		if ((dialogType == QuickDialog.OPTION_BUTTONS || dialogType == QuickDialog.OPTION_DROPDOWN)
				&& options.length() <= 0) {
			throw new AyaRuntimeException("MV: options list must not be empty");
		}
		if (dialogType == QuickDialog.YES_OR_NO && options.length() != 2) {
			throw new AyaRuntimeException("MV: yes or no dialog options list length must be 2");
		}

		//Convert arraylist to string array
		String[] optionsArr = new String[options.length()];
		for (int i = 0; i < options.length(); i++) {
			optionsArr[i] = options.get(i).str();
		}
		
		//show dialog
		final Obj out = QuickDialog.showDialog(dialogType, title, optionsArr, windowHdr, msgType);
		if (out != null) {
			block.push(out);
		}
	}
}



//X - 88
class OP_AdvPlot extends OpInstruction {
	
	public OP_AdvPlot() {
		init("MX");
		arg("D", "plot\n"
				+ "  parameters:\n"
				+ "    plottype (::line ::scatter)\n"
				+ "    title S\n"
				+ "    xlabel S\n"
				+ "    ylabel S\n"
				+ "    height D\n"
				+ "    width D\n"
				+ "    xaxis [minD maxD]\n"
				+ "    yaxis [minD maxD]\n"
				+ "    x L<N>\n"
				+ "    y [[nameS strokeD color[r g b] dataL], ..]\n"
				+ "    show B\n"
				+ "    legend B\n"
				+ "    horizontal B\n"
				+ "    filename S\n");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		if (a.isa(DICT)) {
			ChartParams cp = ChartParams.parseParams((Dict)a);
			FreeChartInterface.drawChart(cp);
		}
	}
}

//Z - 9
class OP_SysConfig extends OpInstruction {
	
	public OP_SysConfig() {
		init("MZ");
		arg("AN",  "system functions\n"
				+ "  S1: change prompt text\n"
				+ "  A2: get working dir\n"
				+ "  S3: set working dir\n"
				+ "  \"\"3: reset working dir\n"
				+ "  S4: list files in working dir + S\n"
				+ "  S5: create dir in working dir + S\n"
				+ "  S6: delete file or dir"
				+ "  S7: get home dir + S");
	}

	@Override
	public void execute(Block block) {
		Obj cmd = block.pop();
		Obj arg = block.pop();
		
		if(cmd.isa(NUMBER)) {
			doCommand(((Number)cmd).toInt(), arg, block);
		} else {	
			throw new TypeError(this, cmd, arg);
		}
	}
	
	private void doCommand(int cmdID, Obj arg, Block b) {
		switch(cmdID) {
		
		//Change the prompt
		case 1:
			if(arg.isa(STR)) {
				AyaPrefs.setPrompt(arg.str());
			} else {
				throw new AyaRuntimeException("arg 1 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
		
		//Return working directory
		case 2:
			b.push(new Str(AyaPrefs.getWorkingDir()));
			break;
			
		//Set working directory
		case 3:
			if (arg.isa(STR)) {
				String dir = arg.str();
				if(dir.equals("")) {
					AyaPrefs.resetWorkingDir();
				} else {
					if (!AyaPrefs.setWorkingDir(arg.str())) {
						throw new AyaRuntimeException("arg 3 MZ: arg is not a valid path."
								+ " Did you include a '/' or '\' at the end? Received:\n" + arg.repr());
					}
				}
			}else {
				throw new AyaRuntimeException("arg 3 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
		
		//List files in working directory
		case 4:
			if (arg.isa(STR)) {
				String fstr = AyaPrefs.getWorkingDir() + arg.str();
				try {
					ArrayList<String> dirs = AyaPrefs.listFilesAndDirsForFolder(new File(fstr));
					ArrayList<Str> obj_dirs = new ArrayList<Str>(dirs.size());
					for (String s : dirs) {
						obj_dirs.add(new Str(s));
					}
					b.push(new StrList(obj_dirs));
				} catch (NullPointerException e) {
					throw new AyaRuntimeException("arg 4 MZ: arg is not a valid location. Received:\n" + fstr);
				}
			} else {
				throw new AyaRuntimeException("arg 4 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
			
		//Create dir
		case 5:
			if(arg.isa(STR)) {
				String fstr = AyaPrefs.getWorkingDir() + arg.str();
				if(!AyaPrefs.mkDir(fstr)) {
					throw new AyaRuntimeException("arg 5 MZ: arg must be a valid name. Received:\n" + fstr);
				}
			} else {
				throw new AyaRuntimeException("arg 5 MZ: arg must be a string. Received:\n" + arg.repr());
			}

		break;
		
		//Delete
		case 6:
			if(arg.isa(STR)) {
				String arg_str = arg.str();
				if(arg_str.equals("")) {
					throw new AyaRuntimeException("arg 5 MZ: arg must be a valid name. Received:\n" + arg_str);
				}
				String fstr = AyaPrefs.getWorkingDir() + arg.str();
				if(!AyaPrefs.deleteFile(fstr)) {
					throw new AyaRuntimeException("arg 5 MZ: arg must be a valid name. Received:\n" + fstr);
				}
			} else {
				throw new AyaRuntimeException("arg 5 MZ: arg must be a string. Received:\n" + arg.repr());
			}

		break;
		
		case 7:
			b.push(new Str(FileUtils.pathAppend(AyaPrefs.getHomeDir(), arg.str())));
			break;
			

		
		default:
			throw new AyaRuntimeException("arg " + cmdID + " MZ: is not a valid command ID");

		}
	}
}


// c - 99
class OP_Cosine extends OpInstruction {
	
	public OP_Cosine() {
		init("Mc");
		arg("N", "cosine");
		vect();
		setOverload(1, "cos");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if(overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).cos());
			return;
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).cos());
		} else {
			throw new TypeError(this, n);
		}
	}
}

//d - 100
class OP_CastDouble extends OpInstruction {
	
	public OP_CastDouble() {
		init("Md");
		arg("N", "cast to double");
		arg("S", "parse double, if invalid, return 0.0");
		setOverload(1, "float");
		vect();
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		if (overload().execute(block, a)) return;
		
		if(a.isa(STR)) {
			try {
				block.push(new Num(Double.parseDouble(a.str())));
			} catch (NumberFormatException e) {
				throw new AyaRuntimeException("Cannot cast string \""+ a.repr() + "\" to a double.");
			}
		} else if (a.isa(NUM)) {
			block.push(a); //Already a double
		} else if (a.isa(NUMBER)){
			block.push(new Num(((Number)a).toDouble()));
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_FLOAT);
		}
		else {
			throw new TypeError(this, a);
		}
	}
}

// e - 100
class OP_Me extends OpInstruction {
	
	public OP_Me() {
		init("Me");
		arg("N", "exponential function");
		vect();
		setOverload(1, "exp");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
	
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).exp());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).exp());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// h - 104
class OP_MShow_Date extends OpInstruction {
	
	public OP_MShow_Date() {
		init("Mh");
		arg("NS", "convert the time in ms to a date string according to a given format");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		if (a.isa(STR) && b.isa(NUMBER)) {
			String df_str = a.str();
			long time = ((Number)b).toLong();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new AyaRuntimeException("Invalid date format: '" + df_str + "'");
			}
			
			Date date = new Date(time);
			String out;
			try {
				out = df.format(date);
			} catch (Exception e) {
				throw new AyaRuntimeException("Cannot parse time: '" + time + "' as date '" + df_str + "'");
			}
			block.push(new Str(out));
		} else {
			throw new TypeError(this, a, b);
		}
		
	}
}


// k - 107
class OP_AddParserChar extends OpInstruction {
	
	public OP_AddParserChar() {
		init("Mk");
		arg("CS", "add special character");
	}

	@Override
	public void execute(Block block) {
		final Obj obj_name = block.pop();
		final Obj obj_char = block.pop();
		
		if (obj_name.isa(STR) && obj_char.isa(CHAR)) {
			String str = obj_name.str();
			if (str.length() > 0 && CharacterParser.lalpha(str)) {
				CharacterParser.add_char(str, ((Char)obj_char).charValue());
			} else {
				throw new AyaRuntimeException("Cannot create special character using " + str);
			}
		} else {
			throw new TypeError(this, obj_char, obj_name);
		}
	}
}


// l - 108
class OP_Ln extends OpInstruction {
	
	public OP_Ln() {
		init("Ml");
		arg("N", "natural logarithm");
		vect();
		setOverload(1, "ln");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).ln());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).ln());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// m - 109
class OP_HasMeta extends OpInstruction {
	
	public OP_HasMeta() {
		init("Mm");
		arg("D", "true if the dict has a metatable, leave D on stack");
	}

	@Override
	public void execute(Block block) {
		final Obj d = block.pop();

		if (d.isa(DICT)) {
			block.push(((Dict)d).hasMetaTable() ? Num.ONE : Num.ZERO);
		} else {
			throw new TypeError(this, d);
		}
	}
}

// p - 112
class OP_Primes extends OpInstruction {
	
	public OP_Primes() {
		init("Mp");
		arg("N", "list primes up to N");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();

		if (a.isa(NUMBER)) {
			int i = ((Number)a).toInt();
			if (i < 0) {
				throw new AyaRuntimeException("Mp: Input must be positive");
			}
			block.push(NumberItemList.primes(i));
		} else {
			throw new TypeError(this, a);
		}
	}

}

// q - 113
class OP_SquareRoot extends OpInstruction {
	
	public OP_SquareRoot() {
		init("Mq");
		arg("N", "square root");
		setOverload(1, "sqrt");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).sqrt());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).sqrt());
		} else {
			throw new TypeError(this, n);
		}
	}
}


// r - 114
class OP_To_Rat extends OpInstruction {
	
	public OP_To_Rat() {
		init("Mr");
		arg("N", "convert to rational number");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if(n.isa(NUMBER)) {
			if (n.isa(Obj.RATIONAL_NUMBER)) {
				block.push(n);
			} else {
				block.push( new RationalNum(((Number)n).toDouble()) );
			}
		} else if (n.isa(NUMBERLIST)) {
			ArrayList<Number> nl = ((NumberList)n).toArrayList();
			ArrayList<Number> ns = new ArrayList<Number>(nl.size());
			for (Number j : nl) {
				if (j.isa(Obj.RATIONAL_NUMBER)) {
					ns.add(j);
				} else {
					ns.add( new RationalNum(((Number)j).toDouble()) );
				}
			}
			block.push(new NumberItemList(ns));
		} else {
			throw new TypeError(this, n);
		}
	}
}


// s - 115
class OP_Sine extends OpInstruction {
	
	public OP_Sine() {
		init("Ms");
		arg("N", "sine");
		vect();
		setOverload(1, "sin");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).sin());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).sin());
		} else {
			throw new TypeError(this, n);
		}
	}
}




// t - 116
class OP_Tangent extends OpInstruction {
	
	public OP_Tangent() {
		init("Mt");
		arg("N", "tangent");
		vect();
		setOverload(1, "tan");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if (n.isa(NUMBER)) {
			block.push(((Number)n).tan());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).tan());
		} else {
			throw new TypeError(this, n);
		}
	}
}





