package aya.obj.number;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.util.Casting;

public class BaseConversion {
	
	public static Obj convertBase(int from_base, int to_base, Obj num) throws NumberFormatException, ValueError, TypeError {
		BigInteger out_bi = BigInteger.ZERO;
		
		//Check Radix Ranges
		if ((Character.MIN_RADIX > from_base 
				|| Character.MIN_RADIX > to_base
				|| Character.MAX_RADIX < from_base
				|| Character.MAX_RADIX < to_base) && (
						from_base != 0
						&& to_base != 0
						)){
			throw new ValueError("list conversion: base out of range (" + from_base + ", " + to_base + ")");
		} if(num.isa(Obj.STR)) {
			out_bi = new BigInteger(num.str(), from_base);
			
		} else if(num.isa(Obj.NUMBER)) {
			//Always base ten
			out_bi = Casting.asNumber(num).toBigDecimal().setScale(0, RoundingMode.FLOOR).toBigInteger();
		} else if (num.isa(Obj.NUMBERLIST)) {
			//Assume base 2
			if (from_base == 2) {
				NumberList bin_list = Casting.asNumberList(num);
				StringBuilder sb = new StringBuilder(bin_list.length());

					for (int i = 0; i < bin_list.length(); i++) {
						int c = bin_list.get(i).toInt();
						//Check for binary only
						if (c == 1 || c == 0) {
							sb.append(c);
						} else {
							throw new ValueError("base conversion: list must be base 2");
						}
						
					}
				out_bi = new BigInteger(sb.toString(), 2);
			} else if (from_base == 0) {
				NumberList nums = Casting.asNumberList(num);
				byte[] in_bytes = new byte[nums.length()];
				for (int i = 0; i < nums.length(); i++) {
					int c = nums.get(i).toInt();
					in_bytes[i] = (byte)c;
				}
				out_bi = new BigInteger(in_bytes);
			} else {
				throw new ValueError("base conversion: list must be base 2 or bytes (base 0)");
			}
		}
		
		else {
			throw new TypeError("invalid base conversion type");
		}
		
		//OUTPUT
		
		//Convert to best use
		if (to_base == 10) {
			//Larger than an int, return BigDeciaml
			if (out_bi.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
				return new BigNum(new BigDecimal(out_bi));
			}
			//Smaller than int 
			else {
				return new Num(out_bi.doubleValue());
			}
		} else if (to_base == 2) {
			String bin_str = out_bi.toString(2);
			char[] cs = bin_str.toCharArray();
			double[] out = new double[cs.length];
			for (int i = 0; i < out.length; i++) {
				out[i] = (double)(cs[i] - '0');
			}
			return new List(new DoubleList(out));
		} else if (to_base == 0) {
			// Special case: byte list
			return new List(NumberList.fromBytes(out_bi.toByteArray()));
		} else {
			return List.fromString(out_bi.toString(to_base));
		}
	}
}
