package aya.entities.operations;

import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUM;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.STR;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import aya.AyaPrefs;
import aya.entities.Operation;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.list.StrList;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.parser.CharacterParser;
import aya.util.ChartParams;
import aya.util.FreeChartInterface;
import aya.util.QuickDialog;

public class MathOps {	

	
	public static char FIRST_OP = '!';
	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operation[] MATH_OPS = {
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
		/* 58    */ null,
		/* 59 ;  */ null,
		/* 60 <  */ null, //new OP_ModSet(),
		/* 61 =  */ null,
		/* 62 >  */ null, //new OP_ModGet(),
		/* 63 ?  */ null,
		/* 64 @  */ null,
		/* 65 A  */ null, //new OP_Abs(),
		/* 66 B  */ null,
		/* 67 C  */ new OP_Acosine(),
		/* 68 D  */ new OP_MDate(),
		/* 69 E  */ null, //new OP_ScientificNotation(),
		/* 70 F  */ null,
		/* 71 G  */ null,
		/* 72 H  */ new OP_MParse_Date(),
		/* 73 I  */ null,
		/* 74 J  */ null,
		/* 75 K  */ null,
		/* 76 L  */ new OP_Log(),
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ new OP_NewUserObject(),
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
		/* 109 m */ null,
		/* 110 n */ null,
		/* 111 o */ null,
		/* 112 p */ new OP_Primes(),
		/* 113 q */ new OP_SquareRoot(),
		/* 114 r */ null,
		/* 115 s */ new OP_Sine(),
		/* 116 t */ new OP_Tangent(),
		/* 117 u */ null,
		/* 118 v */ null,
		/* 119 w */ new OP_TypeStr(),
		/* 120 x */ null,
		/* 121 y */ null,
		/* 122 z */ null,
		/* 123 { */ null,
		/* 124 | */ new OP_Constants(),
		/* 125 } */ null,
		/* 126 ~ */ null,
	};
	
	/** Returns a list of all the op descriptions **/
	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (char i = 0; i <= 126-Ops.FIRST_OP; i++) {
			if(MATH_OPS[i] != null) {
				out.add(MATH_OPS[i].getDocStr() + "\n(misc. operator)");
			}
		}
		return out;
	}
	
	/** Returns the operation bound to the character */
	public static Operation getOp(char op) {
		if(op >= 33 && op <= 126) {
			return MATH_OPS[op-FIRST_OP];
		} else {
			throw new AyaRuntimeException("Misc. operator 'M" + op + "' does not exist");
		}
	}
	
}

// ! - 33
class OP_Fact extends Operation {
	public OP_Fact() {
		this.name = "M!";
		this.info = "factorial";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_FACT.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)){
			block.push(((Number)n).factorial());
		}
		
		else if (n.isa(NUMBERLIST)) {
			block.push( ((NumberList)n).factorial() );
		}
		
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_FACT);
		}
		
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

// ! - 33
class OP_SysTime extends Operation {
	public OP_SysTime() {
		this.name = "M$";
		this.info = "system time in milliseconds as a double";
		this.argTypes = "";
	}
	@Override
	public void execute(Block block) {
		block.push(new Num(System.currentTimeMillis()));
	}
}

// C - 67
class OP_Acosine extends Operation {
	public OP_Acosine() {
		this.name = "MC";
		this.info = "trigonometric inverse cosine";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_ACOS.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).acos());
		}
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).acos());
		}
		
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_ACOS);
		}
		
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

//D - 68
class OP_MDate extends Operation {
	private Calendar cal = Calendar.getInstance();

	public OP_MDate() {
		this.name = "MD";
		this.info = "input time in ms (M$) and return date params [day_of_week, year, month, day_of_month, hour, min, s]";
		this.argTypes = "N";
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
			throw new TypeError(this.name, this.argTypes, a);
		}
		
	}
}


//H - 68
class OP_MParse_Date extends Operation {

	public OP_MParse_Date() {
		this.name = "MH";
		this.info = "parse a date using a given format and return the time in ms";
		this.argTypes = "SS";
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
			throw new TypeError(this.name, this.argTypes, a, b);
		}
		
	}
}


//l - 76
class OP_Log extends Operation {

	public OP_Log() {
		this.name = "ML";
		this.info = "base-10 logarithm";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_LOG.name();
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		if(a.isa(NUMBER)) {
			block.push(((Number)a).log());
		}
		else if (a.isa(NUMBERLIST)) {
			block.push(((NumberList)a).log());
		}
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_LOG);
		}
		else {
			throw new TypeError(this, a);
		}
	}
}

//O - 79
class OP_NewUserObject extends Operation {
	public OP_NewUserObject() {
		this.name = "MO";
		this.info = "create a new user object using the first dict as the metatable";
		this.argTypes = "RR";
	}
	@Override
	public void execute(Block block) {
		final Obj meta = block.pop();
		final Obj dict = block.pop();


		if(dict.isa(DICT) && meta.isa(DICT)) {
			((Dict)dict).setMetaTable((Dict)meta);
			block.push(dict);
		} else {
			throw new TypeError(this.name, this.argTypes, meta, dict);
		}
	}
}

////P - 80
//class OP_PrintColor extends Operation {
//	public OP_PrintColor() {
//		this.name = "MP";
//		this.info = "print a string to the console with the given color";
//		this.argTypes = "SIII";
//	}
//	@Override
//	public void execute(Block block) {
//		final Obj a = block.pop();
//		final Obj b = block.pop();
//		final Obj c = block.pop();
//		final Obj d = block.pop();
//		
//		if(a.isa(NUMBER) && b.isa(NUMBER) && c.isa(NUMBER)) {
//			int ai = ((Number)a).toInt();
//			int bi = ((Number)b).toInt();
//			int ci = ((Number)c).toInt();
//			
//			try {
//				//Aya.getInstance().getOut().printColor(d.str(), new Color(ci, bi, ai));
//			} catch (IllegalArgumentException e) {
//				throw new AyaRuntimeException("Cannot print using color (" + ci + ", " + bi + ", " + ai + ")" );
//			}
//			return;
//		}
//		
//		throw new TypeError(this.name, this.argTypes, a,b,c,d);
//	}
//}

// S - 83
class OP_Asine extends Operation {
	public OP_Asine() {
		this.name = "MS";
		this.info = "trigonometric inverse sine";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_ASIN.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).asin());
		}		
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).asin());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_ASIN);
		}
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

// T - 84
class OP_Atangent extends Operation {
	public OP_Atangent() {
		this.name = "MT";
		this.info = "trigonometric inverse tangent";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_ATAN.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).atan());
		}
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).atan());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_ATAN);
		}
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}


//V - 86
class OP_Dialog extends Operation {
	public OP_Dialog() {
		this.name = "MV";
		this.info = "options title windowhdr msgtype dialogtype MV\n"
				+ "  dialogtype:\n"
				+ "    1: request string\n"
				+ "    2: request number\n"
				+ "    3: alert\n"
				+ "    4: yes or no\n"
				+ "    5: option buttons\n"
				+ "    6: option dropdown\n"
				+ "    7: choose file\n"
				+ "  msgtype:\n"
				+ "    1: plain\n"
				+ "    2: question\n"
				+ "    3: warning\n"
				+ "    4: error";
		this.argTypes = "LSSII";
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
class OP_AdvPlot extends Operation {
	public OP_AdvPlot() {
		this.name = "MX";
		this.info = "plot\n"
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
				+ "    filename S\n";
		this.argTypes = "L";
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
class OP_SysConfig extends Operation {
	public OP_SysConfig() {
		this.name = "MZ";
		this.info = "system functions\n"
				+ "  S1: change prompt text\n"
				+ "  A2: get working dir\n"
				+ "  S3: set working dir\n"
				+ "  \"\"3: reset working dir\n"
				+ "  S4: list files in working dir + S\n"
				+ "  S5: create dir in working dir + S\n"
				+ "  S6: delete file or dir";
		this.argTypes = "AI";
	}
	@Override
	public void execute(Block block) {
		Obj cmd = block.pop();
		Obj arg = block.pop();
		
		if(cmd.isa(NUMBER)) {
			doCommand(((Number)cmd).toInt(), arg, block);
		} else {	
			throw new TypeError(this.name, this.argTypes, cmd, arg);
		}
	}
	
	private void doCommand(int cmdID, Obj arg, Block b) {
		switch(cmdID) {
		
		//Change the prompt
		case 1:
			if(arg.isa(STR)) {
				AyaPrefs.setPrompt(arg.str());
			} else {
				throw new AyaRuntimeException("arg 1 MZ: arg must be a string. Recieved:\n" + arg.repr());
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
								+ " Did you include a '/' or '\' at the end? Recieved:\n" + arg.repr());
					}
				}
			}else {
				throw new AyaRuntimeException("arg 3 MZ: arg must be a string. Recieved:\n" + arg.repr());
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
					throw new AyaRuntimeException("arg 4 MZ: arg is not a valid location. Recieved:\n" + fstr);
				}
			} else {
				throw new AyaRuntimeException("arg 4 MZ: arg must be a string. Recieved:\n" + arg.repr());
			}
			break;
			
		//Create dir
		case 5:
			if(arg.isa(STR)) {
				String fstr = AyaPrefs.getWorkingDir() + arg.str();
				if(!AyaPrefs.mkDir(fstr)) {
					throw new AyaRuntimeException("arg 5 MZ: arg must be a valid name. Recieved:\n" + fstr);
				}
			} else {
				throw new AyaRuntimeException("arg 5 MZ: arg must be a string. Recieved:\n" + arg.repr());
			}

		break;
		
		//Delete
		case 6:
			if(arg.isa(STR)) {
				String arg_str = arg.str();
				if(arg_str.equals("")) {
					throw new AyaRuntimeException("arg 5 MZ: arg must be a valid name. Recieved:\n" + arg_str);
				}
				String fstr = AyaPrefs.getWorkingDir() + arg.str();
				if(!AyaPrefs.deleteFile(fstr)) {
					throw new AyaRuntimeException("arg 5 MZ: arg must be a valid name. Recieved:\n" + fstr);
				}
			} else {
				throw new AyaRuntimeException("arg 5 MZ: arg must be a string. Recieved:\n" + arg.repr());
			}

		break;
		
		default:
			throw new AyaRuntimeException("arg " + cmdID + " MZ: is not a valid command ID");

		}
	}
}


// c - 99
class OP_Cosine extends Operation {
	public OP_Cosine() {
		this.name = "Mc";
		this.info = "trigonometric cosine";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_COS.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).cos());
			return;
		}
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).cos());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_COS);
		}
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

//d - 100
class OP_CastDouble extends Operation {
	public OP_CastDouble() {
		this.name = "Md";
		this.info = "cast number to double. if input not number, return 0.0";
		this.argTypes = "SN";
		this.overload = Ops.KEYVAR_FLOAT.name();
	}
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
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
class OP_Me extends Operation {
	public OP_Me() {
		this.name = "Me";
		this.info = "exponential";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_EXP.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).exp());
			return;
		}
		
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).exp());
		}
		
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_EXP);
		}
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

// h - 104
class OP_MShow_Date extends Operation {

	public OP_MShow_Date() {
		this.name = "Mh";
		this.info = "convert the time in ms to a date string according to a given format";
		this.argTypes = "NS";
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
			throw new TypeError(this.name, this.argTypes, a, b);
		}
		
	}
}


// k - 107
class OP_AddParserChar extends Operation {
	public OP_AddParserChar() {
		this.name = "Mk";
		this.info = "add a special character";
		this.argTypes = "CS";
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
class OP_Ln extends Operation {
	public OP_Ln() {
		this.name = "Ml";
		this.info = "natural logarithm";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_LN.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).ln());
		} 
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).ln());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_LN);
		}
		else {
			throw new TypeError(this, n);
		}
	}
}

// p - 112
class OP_Primes extends Operation {
	public OP_Primes() {
		this.name = "Mp";
		this.info = "N list all primes up to N";
		this.argTypes = "N";
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
class OP_SquareRoot extends Operation {
	public OP_SquareRoot() {
		this.name = "Mq";
		this.info = "square root function";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_SQRT.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		if(n.isa(NUMBER)) {
			block.push(((Number)n).sqrt());
		}
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).sqrt());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_SQRT);
		}
		else {
			throw new TypeError(this, n);
		}
	}
}

// s - 115
class OP_Sine extends Operation {
	public OP_Sine() {
		this.name = "Ms";
		this.info = "trigonometric sine";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_SIN.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).sin());
		}
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).sin());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_SIN);
		}
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}




// t - 116
class OP_Tangent extends Operation {
	public OP_Tangent() {
		this.name = "Mt";
		this.info = "trigonometric tangent";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_TAN.name();
	}
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).tan());
		}
		else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).tan());
		}
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_TAN);
		}
		else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

//w - 119
class OP_TypeStr extends Operation {
	public OP_TypeStr() {
		this.name = "Mw";
		this.info = "return string representation of the type\n  modules begin with a ':' and user types begin with a '.'";
		this.argTypes = "A";
	}
	@Override
	public void execute(Block block) {
		block.push(new Str(Obj.typeName(block.pop().type())));
	}
}



// L - 108
class OP_Constants extends Operation {
	public OP_Constants() {
		this.name = "M|";
		this.info = "constants:\n"
				+ "  0: pi\n"
				+ "  1: e\n"
				+ "  2: double max\n"
				+ "  3: double min\n"
				+ "  4: NaN\n"
				+ "  5: +inf\n"
				+ "  6: -inf\n"
				+ "  7: int max\n"
				+ "  8: int min\n"
				+ "  9: system file separator\n"
				+ "  10: system path separator\n"
				+ "  11: char max value\n"
				+ "  12: system line separator";
		this.argTypes = "N";
	}
	
	public static final Str FILE_SEPARATOR = new Str(File.separator);
	public static final Str FILE_PATH_SEPARATOR = new Str(File.pathSeparator);
	public static final Str SYS_LINE_SEPARATOR = new Str(System.lineSeparator());
	
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		if(a.isa(NUMBER)) {
			final int i = ((Number)a).toInt();
			switch (i) {
			case 0: block.push(Num.PI); break;
			case 1: block.push(Num.E); break;
			case 2: block.push(Num.DOUBLE_MAX); break;
			case 3: block.push(Num.DOUBLE_MIN); break;
			case 4: block.push(Num.DOUBLE_NAN); break; 
			case 5: block.push(Num.DOUBLE_INF); break;
			case 6: block.push(Num.DOUBLE_NINF); break;
			case 7: block.push(Num.INT_MAX); break;
			case 8: block.push(Num.INT_MIN); break;
			case 9: block.push(FILE_SEPARATOR); break;
			case 10: block.push(FILE_PATH_SEPARATOR); break;
			case 11: block.push(Char.MAX_VALUE); break;
			case 12: block.push(SYS_LINE_SEPARATOR); break;
			default:
				throw new AyaRuntimeException("M|: (" + i + ") is not a valid constant id.");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}



