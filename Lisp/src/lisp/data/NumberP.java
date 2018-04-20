package lisp.data;

import java.util.function.BinaryOperator;

import lisp.functions.Subr;

public abstract class NumberP extends Atom {
	

	public static final Atom ZERO = FixP.newAtom("0");
	public static final Atom ONE = FixP.newAtom("1");
	public static final Float almostZero = 3e-6f;

	public NumberP(String pName) {
		super(pName);
	}
	
	public static void init() {
		newAtom("plus").fsubr((x, a) -> numOpList(Subr.evlis(x, a), plusI, plusF));
		newAtom("times").fsubr((x, a) -> numOpList(Subr.evlis(x, a), timesI, timesF));
		newAtom("max").fsubr((x, a) -> numOpList(Subr.evlis(x, a), maxI, maxF));
		newAtom("min").fsubr((x, a) -> numOpList(Subr.evlis(x, a), minI, minF));
		newAtom("logor").fsubr((x, a) -> numOpList(Subr.evlis(x, a), logorI, logF));
		newAtom("logand").fsubr((x, a) -> numOpList(Subr.evlis(x, a), logandI, logF));
		newAtom("logxor").fsubr((x, a) -> numOpList(Subr.evlis(x, a), logxorI, logF));
		
		newAtom("difference").subr((x, a) -> numOp(car(x), cadr(x), differenceI, differenceF));
		newAtom("expt").subr((x, a) -> numOp(car(x), cadr(x), exptI, exptF));
		newAtom("leftshift").subr((x, a) -> numOp(car(x), cadr(x), leftshiftI, logF));
		
		Atom quot = newAtom("quotient");
		quot.subr((x, a) -> numOp(car(x), cadr(x), quotientI, quotientF));
		Atom rema = newAtom("remainder");
		rema.subr((x, a) -> numOp(car(x), cadr(x), remainderI, remainderF));
		newAtom("divide").subr((x, a) -> Subr.list(quot.apply(x, a), rema.apply(x, a)));
		
		newAtom("minus").subr((x, a) -> car(x).minus());
		newAtom("add1").subr((x, a) -> car(x).add1());
		newAtom("sub1").subr((x, a) -> car(x).sub1());
		newAtom("recip").subr((x, a) -> car(x).recip());
		
		newAtom("lessp").subr((x, a) -> car(x).floatValue() < cadr(x).floatValue() ? T8 : NIL);
		newAtom("greaterp").subr((x, a) -> car(x).floatValue() > cadr(x).floatValue() ? T8 : NIL);
		newAtom("zerop").subr((x, a) -> car(x).equals(ZERO) ? T8 : NIL);
		newAtom("onep").subr((x, a) -> car(x).equals(ONE) ? T8 : NIL);
		newAtom("minusp").subr((x, a) -> car(x).floatValue().compareTo(0f) < 0 ? T8 : NIL);
		newAtom("numberp").subr((x, a) -> car(x) instanceof NumberP ? T8 : NIL);
		newAtom("fixp").subr((x, a) -> car(x) instanceof FixP ? T8 : NIL);
		newAtom("floatp").subr((x, a) -> car(x) instanceof FloatP ? T8 : NIL);
	}
	
	public static Atom numOpList(Exp args, BinaryOperator<Integer> intOp, BinaryOperator<Float> floatOp) {
		if (car(args) instanceof FloatP) {
			return numOpFloat(cdr(args), floatOp, car(args).floatValue());
		}
		Integer acc = car(args).intValue();
		Exp m = cdr(args);
		while (m != NIL) {
			if (car(m) instanceof FloatP) {
				return numOpFloat(m, floatOp, acc.floatValue());
			} 
			acc = intOp.apply(acc, car(m).intValue());
			m = cdr(m);
		}
		return FixP.newAtom(acc);
	}
	
	public static Atom numOpFloat(Exp args, BinaryOperator<Float> floatOp, Float acc) {
		Exp m = args;
		Float accF = acc;
		while (m != NIL) {
			accF = floatOp.apply(accF, car(m).floatValue());
			m = cdr(m);
		}
		return FloatP.newAtom(accF);
	}
	
	public static Atom numOp(Exp x, Exp y, BinaryOperator<Integer> intOp, BinaryOperator<Float> floatOp) {
		if (x instanceof FloatP || y instanceof FloatP) {
			return FloatP.newAtom(floatOp.apply(x.floatValue(), y.floatValue()));
		} else {
			return FixP.newAtom(intOp.apply(x.intValue(), y.intValue()));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(floatValue());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NumberP))
			return false;
		NumberP other = (NumberP) obj;
		Float f = floatValue() - other.floatValue();
		if (f < 0) {
			f = -f;
		}
		if (f >= almostZero)
			return false;
		return true;
	}



	static BinaryOperator<Integer> plusI = (x, y) -> x + y;
	static BinaryOperator<Float> plusF = (x, y) -> x + y;
	
	static BinaryOperator<Integer> differenceI = (x, y) -> x - y;
	static BinaryOperator<Float> differenceF = (x, y) -> x - y;
	
	static BinaryOperator<Integer> timesI = (x, y) -> x * y;
	static BinaryOperator<Float> timesF = (x, y) -> x * y;
	
	static BinaryOperator<Integer> maxI = (x, y) -> Integer.max(x, y);
	static BinaryOperator<Float> maxF = (x, y) -> Float.max(x, y);
	
	static BinaryOperator<Integer> minI = (x, y) -> Integer.min(x, y);
	static BinaryOperator<Float> minF = (x, y) -> Float.min(x, y);
	
	static BinaryOperator<Integer> quotientI = (x, y) -> x / y;
	static BinaryOperator<Float> quotientF = (x, y) -> x / y;
	
	static BinaryOperator<Integer> remainderI = (x, y) -> x % y;
	static BinaryOperator<Float> remainderF = (x, y) -> x % y;
	
	static BinaryOperator<Integer> exptI = (x, y) -> (int) Math.pow(x, y);
	static BinaryOperator<Float> exptF = (x, y) -> (float) Math.pow(x, y);
	
	static BinaryOperator<Integer> logorI = (x, y) -> x | y;
	static BinaryOperator<Float> logF = (x, y) -> Subr.error(Subr.I4).floatValue();
	
	static BinaryOperator<Integer> logandI = (x, y) -> x & y;
	
	static BinaryOperator<Integer> logxorI = (x, y) -> x ^ y;
	
	static BinaryOperator<Integer> leftshiftI = (x, y) -> x << y;
}
