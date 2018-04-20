package lisp.data;

public class FloatP extends NumberP {

	Float val;
	
	public FloatP(String pName, Float val) {
		super(pName);
		this.val = val;
	}
	
	public static Atom newAtom(String name) {
		return newAtom(Float.parseFloat(name));
	}
	
	public static Atom newAtom(Float f) {
		String name = f.toString();
		Atom a = oblist.get(name);
		if (a == null) {
			a = new FloatP(name, f);
			oblist.put(name, a);
		}
		return a;
	}

	public Integer intValue() {
		return val.intValue();
	}
	public Float floatValue() {
		return val;
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
		return newAtom(1 / val);
	}
}
