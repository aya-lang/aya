package element.variable;

public class MemberVariable {
	public long id;
	
	public MemberVariable(String s) {
		id = Variable.encodeString(s);
	}
	
	public String toString() {
		return "." + Variable.decodeLong(id);
	}
}
