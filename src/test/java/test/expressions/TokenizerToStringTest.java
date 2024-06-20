package test.expressions;

import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.expressions.InvalidExpressionException;
import lu.itrust.business.expressions.TokenizerToString;

public class TokenizerToStringTest {

	@Test
	public void testLiterals() throws InvalidExpressionException {
		TokenizerToString tokenizerToString = new TokenizerToString("4+min(a,3.4,.125)");
		Assert.assertEquals("Not token","4.0+min(a,3.4,0.125)", tokenizerToString.toString());
	}

	@Test
	public void testNumbers() throws InvalidExpressionException {
		TokenizerToString tokenizerToString = new TokenizerToString("1+1+1-2*(a-5.5)/1000");
		Assert.assertEquals("Not token","1.0+1.0+1.0-2.0*(a-5.5)/1000.0", tokenizerToString.toString());
	}

	@Test
	public void testOperators() throws InvalidExpressionException {
		TokenizerToString tokenizerToString = new TokenizerToString("-a");
		Assert.assertEquals("Not token","-a", tokenizerToString.toString());
	}

	@Test
	public void testCombined() throws InvalidExpressionException {
		TokenizerToString tokenizerToString = new TokenizerToString("abc+a*(a-z)");
		Assert.assertEquals("Not token","abc+a*(a-z)", tokenizerToString.toString());
	}

}
