package lisp.data;

import static lisp.data.Atom.*;

import java.util.function.BinaryOperator;

import lisp.functions.Subr;

public class Exp implements BinaryOperator<Exp>{
	Exp left;
	Exp right;

	public Exp() {
		this.left = NIL;
		this.right = NIL;
	}
	
	public Exp(Exp left, Exp right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String toString() {
		return "(" + left + " . " + right + ")";
	}
	
	public boolean bool() {
		return this != NIL;
	}

	public static Exp cons(Exp left, Exp right) {
		return new Exp(left, right);
	}
	
	public static Exp car(Exp exp) {
		return exp.left;
	}
	
	public static Exp cdr(Exp exp) {
		return exp.right;
	}
	
	public static Exp rplaca(Exp x, Exp y) {
		x.left = y;
		return x;
	}
	
	public static Exp rplacd(Exp x, Exp y) {
		x.right = y;
		return x;
	}
	
	public static Atom eq(Exp atom1, Exp atom2) {
		return atom1 == atom2 ? T8 : NIL;
	}
	
	public static Atom atom(Exp exp) {
		return exp instanceof Atom ? T8 : NIL;
	}
	
	public static Atom numberp(Exp exp) {
		return exp instanceof NumberP ? T8 : NIL;
	}
	
	public static Exp caar(Exp exp) {
		return car(car(exp));
	}
	public static Exp cdar(Exp exp) {
		return cdr(car(exp));
	}
	public static Exp cadr(Exp exp) {
		return car(cdr(exp));
	}
	public static Exp caddr(Exp exp) {
		return car(cdr(cdr(exp)));
	}
	public static Exp cadar(Exp exp) {
		return car(cdr(car(exp)));
	}

	@Override
	public Exp apply(Exp args, Exp a) {
		return Subr.apply(this, args, NIL);
	}
	
	public Integer intValue() {
		Subr.error(Subr.I3);
		return null;
	}
	public Float floatValue() {
		Subr.error(Subr.I3);
		return null;
	}
	public Atom minus() {
		Subr.error(Subr.I3);
		return null;
	}
	
	public Atom add1() {
		Subr.error(Subr.I3);
		return null;
	}
	
	public Atom sub1() {
		Subr.error(Subr.I3);
		return null;
	}
	
	public Atom recip() {
		Subr.error(Subr.I3);
		return null;
	}
}
