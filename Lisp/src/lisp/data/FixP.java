package lisp.data;

public class FixP extends NumberP {

	Integer val;
	
	public FixP(String pName, Integer val) {
		super(pName);
		this.val = val;
	}
	
	public static Atom newAtom(String name) {
		return newAtom(Integer.decode(name));
	}
	
	public static Atom newAtom(Integer i) {
		String name = i.toString();
		Atom a = oblist.get(name);
		if (a == null) {
			a = new FixP(name, i);
			oblist.put(name, a);
		}
		return a;
	}
	
	public Integer intValue() {
		return val;
	}
	public Float floatValue() {
		return val.floatValue();
	}
	
	public Atom minus() {
		return newAtom(-val);
	}
	
	public Atom add1() {
		return newAtom(val + 1);
	}
	
	public Atom sub1() {
		return newAtom(val - 1);
	}
	
	public Atom recip() {
		return newAtom(0);
	}
}
