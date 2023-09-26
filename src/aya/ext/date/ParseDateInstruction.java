package aya.ext.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.number.Num;

public class ParseDateInstruction extends NamedOperator {
	
	public ParseDateInstruction() {
		super("date.parse");
		_doc = "parse a date using a given format and return the time in ms";
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		
		if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			String df_str = a.str();
			String date_str = b.str();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new ValueError("Invalid date format: '" + df_str + "'");
			}
			
			Date date;
			try {
				date = df.parse(date_str);
			} catch (ParseException e) {
				throw new ValueError("Cannot parse date: '" + date_str + "' as '" + df_str + "'");
			}
			block.push(new Num(date.getTime()));
		} else {
			throw new TypeError(this, "SS", a, b);
		}
	}

}
