package lisp;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lisp.data.Atom;
import lisp.data.Exp;
import lisp.data.FixP;
import lisp.data.FloatP;
import lisp.functions.Subr;

import static lisp.data.Exp.*;

public class Parser {
	
	static Pattern adad = Pattern.compile("c[a,d]{2,}r");

	public static void main(String[] args) throws Exception {
		Atom.init();
		String prompt;
		
		Scanner reader = new Scanner(System.in);
		System.out.print("evalquote> ");
		prompt = reader.nextLine();
		
		while (!"stop".equals(prompt)) {
			List<String> tokens = tokenize(prompt);
			try {
				Exp fn = readFromTokens(tokens);
				Exp a = readFromTokens(tokens);
				printExp(Subr.evalquote(fn, a));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			System.out.print("evalquote> ");
			prompt = reader.nextLine();
		}
		
	}
	
	public static Exp parse(String program) {
		try {
			return readFromTokens(tokenize(program));
		} catch (Exception e) {
			e.printStackTrace();
			return Atom.NIL;
		}
	}
	
	static List<String> tokenize(String str) {
		return Arrays.stream(str.replace("(", " ( ").replace(")", " ) ").split("[,\\s]+"))
				.filter(a -> !"".equals(a))
				.collect(Collectors.toList());
	}

	static Exp readFromTokens(List<String> tokens) throws Exception {
		if (tokens.isEmpty())
			throw new Exception("Unexpected EOF");
		String token = tokens.remove(0);
		if("(".equals(token)) {
			if (")".equals(tokens.get(0))) {
				tokens.remove(0);
				return Atom.NIL;
			}
			Exp left;
			
			if (adad.matcher(tokens.get(0)).matches()) {
				if ('a' == tokens.get(0).charAt(1)) {
					tokens.set(0, tokens.get(0).replaceFirst("a", ""));
					tokens.add(0, "(");
					left = readFromTokens(tokens);
					return Exp.cons(newAtom("car"), Exp.cons(left, Atom.NIL));
				} else { // 'd' == tokens.get(0).charAt(1)
					tokens.set(0, tokens.get(0).replaceFirst("d", ""));
					tokens.add(0, "(");
					left = readFromTokens(tokens);
					return Exp.cons(newAtom("cdr"), Exp.cons(left, Atom.NIL));
				}
			}
			
			left = readFromTokens(tokens);
			if (")".equals(tokens.get(0))) {
				tokens.remove(0);
				return Exp.cons(left, Atom.NIL);
				
			} else if (".".equals(tokens.get(0))) {
				tokens.remove(0);
				Exp r = Exp.cons(left, readFromTokens(tokens));
				if (!")".equals(tokens.remove(0)))
					throw new Exception("expected )");
				return r;
			} else { // using list structure
				tokens.add(0, "(");
				return Exp.cons(left, readFromTokens(tokens));
			}
		} else if (")".equals(token)) {
			throw new Exception("unexpected )");
		} else {
			return newAtom(token);
		}
	}
	
	static Atom newAtom(String token) {
		try {
			return FixP.newAtom(token);
		} catch (NumberFormatException nfe) {
			try {
				return FloatP.newAtom(token);
			} catch (NumberFormatException nfe2) {
				return Atom.newAtom(token);
			}
		}
	}
	
	public static void printExp(Exp exp) {
		if (exp instanceof Atom) {
			System.out.println(exp);
		} else {
			System.out.println("(" + expStr(exp) + ")");
		}
	}

	private static String expStr(Exp exp) {
		Exp left = car(exp);
		Exp right = cdr(exp);
		StringBuilder str = new StringBuilder();
		if (left instanceof Atom) {
			str.append(left);
		} else {
			str.append("(").append(expStr(left)).append(")");
		}
		
		if (!(Atom.NIL == right)) {
			if (right instanceof Atom) {
				str.append(" . ").append(right);
			} else {
				str.append(" ").append(expStr(right));
			}
		}
		
		return str.toString();
	}
}
