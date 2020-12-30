package aya.ext.date;

import java.util.Calendar;

import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.symbol.SymbolConstants;

public class DescribeDateInstruction extends NamedInstruction {

	private Calendar cal; 
	
	public DescribeDateInstruction() {
		super("date.desc");
		_doc = "given time in ms, return date params";
		cal = Calendar.getInstance();
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();

		if (a.isa(Obj.NUMBER)) {
			long timeStamp = ((Number)a).toLong();
			cal.setTimeInMillis(timeStamp);
			
			Dict out = new Dict();
			
			out.set(SymbolConstants.DAY_OF_WEEK,  Num.fromInt(cal.get(Calendar.DAY_OF_WEEK)));
			out.set(SymbolConstants.YEAR,         Num.fromInt(cal.get(Calendar.YEAR)));
			out.set(SymbolConstants.MONTH,        Num.fromInt(cal.get(Calendar.MONTH)));
			out.set(SymbolConstants.DAY_OF_MONTH, Num.fromInt(cal.get(Calendar.DAY_OF_MONTH)));
			out.set(SymbolConstants.HOUR,         Num.fromInt(cal.get(Calendar.HOUR)));
			out.set(SymbolConstants.MINUTE,       Num.fromInt(cal.get(Calendar.MINUTE)));
			out.set(SymbolConstants.SECOND,       Num.fromInt(cal.get(Calendar.SECOND)));
			out.set(SymbolConstants.MS, 		  Num.fromInt(cal.get(Calendar.MILLISECOND)));
			out.set(SymbolConstants.STAMP, 		  a);

			block.push(out);

		} else {
			throw new TypeError(this,"N", a);
		}
	}

}
