package org.matheclipse.parser.test;

import junit.framework.TestCase;

import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Tests parser function for SimpleParserFactory
 */
public class ParserTestCase extends TestCase {

	public ParserTestCase(String name) {
		super(name);
	}

	public void testParser() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-a-b*c!!+d");
			assertEquals(obj.toString(), "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser0() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("(#^3)&[x][y,z].{a,b,c}");
			assertEquals(obj.toString(), "Dot[Function[Power[Slot[1], 3]][x][y, z], List[a, b, c]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser1() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("Integrate[Sin[x]^2+3*x^4, x]");
			assertEquals(obj.toString(), "Integrate[Plus[Power[Sin[x], 2], Times[3, Power[x, 4]]], x]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser2() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a[][0][1]f[[x]]");
			assertEquals(obj.toString(), "Times[a[][0][1], Part[f, x]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser3() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f[y,z](a+b+c)");
			assertEquals(obj.toString(), "Times[f[y, z], Plus[Plus[a, b], c]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser4() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("$a=2");
			assertEquals(obj.toString(), "Set[$a, 2]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser5() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("4.7942553860420304E-1");
			assertEquals(obj.toString(), "4.7942553860420304E-1");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser6() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a+%%%+%3*4!");
			assertEquals(obj.toString(), "Plus[Plus[a, Out[-3]], Times[Out[3], Factorial[4]]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser7() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a+%%%+%3*:=4!");
			fail("A SyntaxError exception should occur here");
		} catch (Exception e) {
			assertEquals("Syntax error in line: 1 - Operator: := is no prefix operator.\n" + "a+%%%+%3*:=4!\n" + "          ^", e
					.getMessage());
		}
	}

	public void testParser8() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-42424242424242424242");
			assertEquals(obj.toString(), "-42424242424242424242");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser9() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-42424242424242424242.125");
			assertEquals(obj.toString(), "-42424242424242424242.125");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser10() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-3/4");
			assertEquals(obj.toString(), "-3/4");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser11() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-(3/4)");
			assertEquals(obj.toString(), "-3/4");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser12() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-(Pi/4)");
			assertEquals(obj.toString(), "Times[-1, Times[Pi, 1/4]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser13() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a*b*c*d");
			assertEquals(obj.toString(), "Times[Times[Times[a, b], c], d]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser14() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-a-b*c!!+d");
			assertEquals(obj.dependsOn("d"), true);
			assertEquals(obj.dependsOn("x"), false);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser15() {
		try {
			Parser p = new Parser();
			ASTNode obj = p
					.parse("Integrate[Sin[a_.*x_]^n_IntegerQ, x_Symbol]:= -Sin[a*x]^(n-1)*Cos[a*x]/(n*a)+(n-1)/n*Integrate[Sin[a*x]^(n-2),x]/;Positive[n]&&FreeQ[a,x]");
			assertEquals(
					obj.toString(),
					"SetDelayed[Integrate[Power[Sin[Times[a_., x_]], n_IntegerQ], x_Symbol], Condition[Plus[Times[Times[-1, Power[Sin[Times[a, x]], Plus[n, Times[-1, 1]]]], Times[Cos[Times[a, x]], Power[Times[n, a], -1]]], Times[Times[Plus[n, Times[-1, 1]], Power[n, -1]], Integrate[Power[Sin[Times[a, x]], Plus[n, Times[-1, 2]]], x]]], And[Positive[n], FreeQ[a, x]]]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser16() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f[[1,2]]");
			assertEquals(obj.toString(), "Part[f, 1, 2]");
			obj = p.parse("f[[1]][[2]]");
			assertEquals(obj.toString(), "Part[Part[f, 1], 2]");
			obj = p.parse("f[[1,2,f[x]]]");
			assertEquals(obj.toString(), "Part[f, 1, 2, f[x]]");
			obj = p.parse("f[[1]][[2]][[f[x]]]");
			assertEquals(obj.toString(), "Part[Part[Part[f, 1], 2], f[x]]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}
}