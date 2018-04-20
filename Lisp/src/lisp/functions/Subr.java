package lisp.functions;

import lisp.data.Exp;

import static lisp.data.Atom.NIL;
import static lisp.data.Atom.T8;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lisp.LispError;
import lisp.Parser;
import lisp.data.Atom;

public class Subr extends Atom {

	static final Atom A2 = new Atom("Function object has no definition - Apply");
	static final Atom A3 = new Atom("Conditional unsatisfied - Evcon");
	static final Atom A8 = new Atom("Unbound variable - Eval");
	static final Atom A9 = new Atom("Function object has no definition - Eval");
	static final Atom F2 = new Atom("First argument list too short - Pair");
	static final Atom F3 = new Atom("Second argument list too short - Pair");
	public static final Atom I3 = new Atom("Bad argument - Numval");
	public static final Atom I4 = new Atom("Bad argument - Fixval");

	public Subr(String pName) {
		super(pName);
		// TODO Auto-generated constructor stub
	}

	public static Exp evalquote(Exp fn, Exp args) {
		if (get(fn, FEXPR).bool() || get(fn, FSUBR).bool()) {
			return eval(cons(fn, args), NIL);
		} else {
			return apply(fn, args, NIL);
		}

	}

	public static Exp apply(Exp fn, Exp args, Exp a) {
		if (not(fn).bool()) {
			return NIL;
		} else if (atom(fn).bool()) {
			try {
				Exp temp;
				if ((temp = get(fn, EXPR)).bool()) {
					return apply(temp, args, a);
				} else if ((temp = get(fn, SUBR)).bool()) {
					return temp.apply(args, a);
				} else {
					return apply(cdr(sassoc(fn, a, () -> error(A2))), args, a);
				}
			} catch (LispError le) {
				return error(fn);
			}
		} else if (eq(car(fn), LABEL).bool()) {
			return apply(caddr(fn), args, cons(cons(cadr(fn), caddr(fn)), a));
		} else if (eq(car(fn), FUNARG).bool()) {
			return apply(cadr(fn), args, caddr(fn));
		} else if (eq(car(fn), LAMBDA).bool()) {
			return eval(caddr(fn), nconc(pair(cadr(fn), args), a));
		} else {
			return apply(eval(fn, a), args, a);
		}
	}

	public static Exp eval(Exp form, Exp a) {
		if (not(form).bool()) {
			return NIL;
		} else if (numberp(form).bool()) {
			return form;
		} else if (atom(form).bool()) {
			try {
				Exp apval;
				if ((apval = get(form, APVAL)).bool()) {
					return car(apval);
				} else {
					return cdr(sassoc(form, a, () -> error(A8)));
				}
			} catch (LispError le) {
				return error(form);
			}
		} else if (eq(car(form), COND).bool()) {
			return evcon(cdr(form), a);
		} else if (atom(car(form)).bool()) {
			try {
				Exp temp;
				if ((temp = get(car(form), EXPR)).bool()) {
					return apply(temp, evlis(cdr(form), a), a);
				} else if ((temp = get(car(form), FEXPR)).bool()) {
					return apply(temp, list(cdr(form), a), a);
				} else if ((temp = get(car(form), SUBR)).bool()) {
					return temp.apply(evlis(cdr(form), a), a);
				} else if ((temp = get(car(form), FSUBR)).bool()) {
					return temp.apply(cdr(form), a);
				} else {
					return eval(cons(cdr(sassoc(car(form), a, () -> error(A9))), cdr(form)), a);
				}
			} catch (LispError le) {
				return error(car(form));
			}
		} else {
			return apply(car(form), evlis(cdr(form), a), a);
		}
	}

	public static Exp evcon(Exp c, Exp a) {
		if (not(c).bool()) {
			return error(A3);
		} else if (eval(caar(c), a).bool()) {
			return eval(cadar(c), a);
		} else {
			return evcon(cdr(c), a);
		}
	}

	public static Exp evlis(Exp m, Exp a) {
		return maplist(m, j -> eval(car(j), a));
	}

	public static Exp maplist(Exp x, UnaryOperator<Exp> fn) {
		if (not(x).bool()) {
			return NIL;
		} else {
			return cons(fn.apply(x), maplist(cdr(x), fn));
		}
	}

	private static Exp nconc(Exp x, Exp y) {
		if (not(x).bool())
			return y;
		Exp m = x;
		while (!not(cdr(m)).bool()) {
			m = cdr(m);
		}
		rplacd(m, y);
		return x;
	}

	private static Exp pair(Exp x, Exp y) {
		Exp u = x;
		Exp v = y;
		Exp m = NIL;

		while (true) {
			if (not(u).bool()) {
				if (not(v).bool()) {
					return m;
				} else {
					try {
						error(F2);
					} catch (LispError le) {
						error(v);
					}
				}
			} else if (not(v).bool()) {
				try {
					error(F3);
				} catch (LispError le) {
					error(u);
				}
			}
			m = cons(cons(car(u), car(v)), m);
			u = cdr(u);
			v = cdr(v);
		}
	}

	public static Exp get(Exp x, Exp y) {
		if (not(x).bool()) {
			return NIL;
		} else if (eq(car(x), y).bool()) {
			return cadr(x);
		} else {
			return get(cdr(x), y);
		}
	}

	public static Atom not(Exp x) {
		return x == NIL ? T8 : NIL;
	}

	public static Exp sassoc(Exp x, Exp y, Supplier<Exp> u) {
		if (not(y).bool()) {
			return u.get();
		} else if (eq(caar(y), x).bool()) {
			return car(y);
		} else {
			return sassoc(x, cdr(y), u);
		}
	}

	public static Atom error(Exp exp) throws LispError {
		Parser.printExp(exp);
		throw new LispError();
	}

	public static Exp list(Exp... exps) {
		Exp r = NIL;
		for (int i = exps.length - 1; i >= 0; i--) {
			r = cons(exps[i], r);
		}
		return r;
	}

	public static Atom equal(Exp x, Exp y) {
		if (atom(x).bool()) {
			if (atom(y).bool()) {
				return x.equals(y) ? T8 : NIL;
			} else {
				return NIL;
			}
		} else if (equal(car(x), car(y)).bool()) {
			return equal(cdr(x), cdr(y));
		} else {
			return NIL;
		}
	}

	public static Exp subst(Exp x, Exp y, Exp z) {
		if (equal(y, z).bool()) {
			return x;
		} else if (atom(z).bool()) {
			return z;
		} else {
			return cons(subst(x, y, car(z)), subst(x, y, cdr(z)));
		}
	}

	public static Exp append(Exp x, Exp y) {
		if (not(x).bool()) {
			return y;
		} else {
			return cons(car(x), append(cdr(x), y));
		}
	}

	public static Atom member(Exp x, Exp y) {
		if (not(y).bool()) {
			return NIL;
		} else if (equal(x, car(y)).bool()) {
			return T8;
		} else {
			return member(x, cdr(y));
		}
	}

	public static Exp search(Exp x, UnaryOperator<Exp> p, UnaryOperator<Exp> f, UnaryOperator<Exp> u) {
		if (not(x).bool()) {
			return u.apply(x);
		} else if (p.apply(x).bool()) {
			return f.apply(x);
		} else {
			return search(cdr(x), p, f, u);
		}
	}

	public static Exp sublis(Exp x, Exp y) {
		if (not(x).bool()) {
			return y;
		} else if (not(y).bool()) {
			return y;
		} else {
			return search(x, j -> equal(y, caar(j)), j -> cdar(j), j -> {
				if (atom(y).bool()) {
					return y;
				} else {
					return cons(sublis(x, car(y)), sublis(x, cdr(y)));
				}
			});
		}
	}
	
	public static Exp prop(Exp x, Exp y, Supplier<Exp> u) {
		if (not(x).bool()) {
			return u.get();
		} else if (eq(car(x), y).bool()) {
			return cdr(x);
		} else {
			return prop(cdr(x), y, u);
		}
	}
	
	public static Exp attrib(Exp x, Exp y) {
		if (not(x).bool())
			return y;
		Exp m = x;
		while (!not(cdr(m)).bool()) {
			m = cdr(m);
		}
		rplacd(m, y);
		return y;
	}
	
	public static Exp or(Exp x, Exp a) {
		Exp u = x;
		while (u != NIL) {
			if (eval(car(u), a).bool())
				return T8;
			u = cdr(u);
		}
		return NIL;
	}
	
	public static Exp and(Exp x, Exp a) {
		Exp u = x;
		while (u != NIL) {
			if (!eval(car(u), a).bool())
				return NIL;
			u = cdr(u);
		}
		return T8;
	}
}
