//package element.parser.tokens;
//
//import element.obj.number.Num;
//
//public class BoolToken extends StdToken {
//		
//	public BoolToken(String data) {
//		super(data, Token.BOOL);
//	}
//
//	
//	@Override
//	public Object getElementObj() {
//		//return data.charAt(0) == 'T';
//		if (data.charAt(0) == 'T') {
//			return Num.ONE;
//		} else {
//			return Num.ZERO;
//		}
//	}
//
//	@Override
//	public String typeString() {
//		return "bool";
//	}
//}
