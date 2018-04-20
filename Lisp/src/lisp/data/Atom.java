package lisp.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import lisp.Parser;
import lisp.functions.Subr;

public class Atom extends Exp {

	public static final Map<String, Atom> oblist = new HashMap<>();

	public static final Atom NIL = newAtom("nil");
	public static final Atom T = newAtom("T");
	public static final Atom T8 = newAtom("*T*");
	public static final Atom F = newAtom("F");

	public static final Atom APVAL = newAtom("apval");
	public static final Atom SUBR = newAtom("subr");
	public static final Atom FSUBR = newAtom("fsubr");
	public static final Atom EXPR = newAtom("expr");
	public static final Atom FEXPR = newAtom("fexpr");

	public static final Atom LABEL = newAtom("label");
	public static final Atom FUNARG = newAtom("funarg");
	public static final Atom LAMBDA = newAtom("lambda");
	public static final Atom COND = newAtom("cond");

	public static void init() {

		T.apval(T8);
		T8.apval(T8);
		F.apval(NIL);
		NIL.apval(NIL);

		newAtom("car").subr((x, a) -> caar(x));
		newAtom("cdr").subr((x, a) -> cdar(x));
		newAtom("cons").subr((x, a) -> cons(car(x), cadr(x)));
		newAtom("atom").subr((x, a) -> atom(car(x)));
		newAtom("eq").subr((x, a) -> eq(car(x), cadr(x)));

		newAtom("quote").fsubr((x, a) -> car(x));
		newAtom("function").fsubr((x, a) -> Subr.list(FUNARG, car(x), a));

		COND.fsubr((x, a) -> Subr.evcon(x, a));
		newAtom("list").fsubr((x, a) -> Subr.evlis(x, a));
		newAtom("or").fsubr((x, a) -> Subr.or(x, a));
		newAtom("and").fsubr((x, a) -> Subr.and(x, a));

		newAtom("equal").subr((x, a) -> Subr.equal(car(x), cadr(x)));
		newAtom("subst").subr((x, a) -> Subr.subst(car(x), cadr(x), caddr(x)));
		newAtom("null").subr((x, a) -> Subr.not(car(x)));
		newAtom("not").subr((x, a) -> Subr.not(car(x)));
		newAtom("append").subr((x, a) -> Subr.append(car(x), cadr(x)));
		newAtom("sublis").subr((x, a) -> Subr.sublis(car(x), cadr(x)));

		newAtom("member").subr((x, a) -> Subr.member(car(x), cadr(x)));

		newAtom("attrib").subr((x, a) -> Subr.attrib(car(x), cadr(x)));
		newAtom("prop").subr((x, a) -> Subr.prop(car(x), cadr(x), () -> caddr(x).apply(NIL, NIL)));
		newAtom("rplaca").subr((x, a) -> rplaca(car(x), cadr(x)));
		newAtom("rplacd").subr((x, a) -> rplacd(car(x), cadr(x)));
		newAtom("maplist").subr((x, a) -> Subr.maplist(car(x), (j) -> cadr(x).apply(cons(j, NIL), NIL)));
		

		newAtom("deflist").expr(Parser.parse(
				"(lambda (x ind) (cond ((null x) nil) (T (cons ((lambda (x e p) ((lambda (x y) y) (rplaca (prop x p (function (lambda () (cdr (attrib x (list p nil)))))) e) x)) (caar x) (cadar x) ind) (deflist (cdr x) ind)))))"));
		newAtom("define").expr(Parser.parse("(lambda (x) (deflist x (quote expr)))"));
		newAtom("cset").expr(Parser.parse("(lambda (x e) (car (rplaca (prop x (quote apval) (function (lambda () (cdr (attrib x (list (quote apval) nil)))))) (cons e nil))))"));
		
		NumberP.init();
		
	}

	String pName;

	BinaryOperator<Exp> subr;

	public Atom(String pName) {
		this.pName = pName;
	}

	public static Atom newAtom(String name) {
		Atom a = oblist.get(name);
		if (a == null) {
			a = new Atom(name);
			oblist.put(name, a);
		}
		return a;
	}

	public void apval(Atom apval) {
		this.left = NIL;
		this.right = cons(APVAL, cons(cons(apval, NIL), NIL));
	}

	public void expr(Exp expr) {
		this.left = NIL;
		this.right = cons(EXPR, cons(expr, NIL));
	}

	public void fexpr(Exp fexpr) {
		this.left = NIL;
		this.right = cons(FEXPR, cons(fexpr, NIL));
	}

	public void subr(BinaryOperator<Exp> subr) {
		this.left = NIL;
		this.right = cons(SUBR, cons(this, NIL));
		this.subr = subr;
	}

	public void fsubr(BinaryOperator<Exp> fsubr) {
		this.left = NIL;
		this.right = cons(FSUBR, cons(this, NIL));
		this.subr = fsubr;
	}

	@Override
	public String toString() {
		return pName;
	}

	@Override
	public Exp apply(Exp args, Exp a) {
		if (subr == null) {
			return super.apply(args, a);
		}
		return subr.apply(args, a);
	}

}
