package test.expressions;

import junit.framework.TestCase;
import lu.itrust.business.expressions.InvalidExpressionException;
import lu.itrust.business.expressions.StringTokenizer;
import lu.itrust.business.expressions.Token;
import lu.itrust.business.expressions.TokenType;

public class StringTokenizerTest extends TestCase {
	public void testLiterals() throws InvalidExpressionException {
		StringTokenizer s = new StringTokenizer("a23234____bc_def__4 _a _1 a1 ABC");
		Token token;

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());
		assertEquals("a23234____bc_def__4", token.getParameter());

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());
		assertEquals("_a", token.getParameter());

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());
		assertEquals("_1", token.getParameter());

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());
		assertEquals("a1", token.getParameter());

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());
		assertEquals("ABC", token.getParameter());

		token = s.read();
		assertEquals(TokenType.End, token.getType());
	}

	public void testNumbers() throws InvalidExpressionException {
		StringTokenizer s = new StringTokenizer("   1    5.0    1.    .2  ");
		Token token;

		token = s.read();
		assertEquals(TokenType.Number, token.getType());
		assertEquals(1.0, token.getParameter());
		
		token = s.read();
		assertEquals(TokenType.Number, token.getType());
		assertEquals(5.0, token.getParameter());
		
		token = s.read();
		assertEquals(TokenType.Number, token.getType());
		assertEquals(1.0, token.getParameter());
		
		token = s.read();
		assertEquals(TokenType.Number, token.getType());
		assertEquals(0.2, token.getParameter());

		token = s.read();
		assertEquals(TokenType.End, token.getType());
	}
	
	public void testOperators() throws InvalidExpressionException {
		StringTokenizer s = new StringTokenizer("  + -//*)(     ,, ");
		Token token;

		token = s.read();
		assertEquals(TokenType.PlusOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.MinusOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.DivideOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.DivideOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.TimesOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.RightBracket, token.getType());

		token = s.read();
		assertEquals(TokenType.LeftBracket, token.getType());

		token = s.read();
		assertEquals(TokenType.Comma, token.getType());

		token = s.read();
		assertEquals(TokenType.Comma, token.getType());

		token = s.read();
		assertEquals(TokenType.End, token.getType());
	}

	public void testCombined() throws InvalidExpressionException {
		StringTokenizer s = new StringTokenizer("1+3.*(a-b,1)");
		Token token;

		token = s.read();
		assertEquals(TokenType.Number, token.getType());

		token = s.read();
		assertEquals(TokenType.PlusOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.Number, token.getType());

		token = s.read();
		assertEquals(TokenType.TimesOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.LeftBracket, token.getType());

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());

		token = s.read();
		assertEquals(TokenType.MinusOperator, token.getType());

		token = s.read();
		assertEquals(TokenType.Variable, token.getType());

		token = s.read();
		assertEquals(TokenType.Comma, token.getType());

		token = s.read();
		assertEquals(TokenType.Number, token.getType());

		token = s.read();
		assertEquals(TokenType.RightBracket, token.getType());

		token = s.read();
		assertEquals(TokenType.End, token.getType());
	}

}
