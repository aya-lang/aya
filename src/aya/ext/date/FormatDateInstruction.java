package aya.ext.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.number.Number;

public class FormatDateInstruction extends NamedOperator {
	
	public FormatDateInstruction() {
		super("date.format");
		_doc = "convert the time in ms to a date string according to a given format";
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		if (a.isa(Obj.STR) && b.isa(Obj.NUMBER)) {
			String df_str = a.str();
			long time = ((Number)b).toLong();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new ValueError("Invalid date format: '" + df_str + "'");
			}
			
			Date date = new Date(time);
			String out;
			try {
				out = df.format(date);
			} catch (Exception e) {
				throw new ValueError("Cannot parse time: '" + time + "' as date '" + df_str + "'");
			}
			block.push(List.fromString(out));
		} else {
			throw new TypeError(this, "NS", a, b);
		}
	}

}
