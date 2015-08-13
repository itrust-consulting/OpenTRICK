package test.expressions;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import lu.itrust.business.expressions.InvalidExpressionException;
import lu.itrust.business.expressions.StringExpressionParser;

public class StringExpressionParserTest extends TestCase {
	public void testEvalNegativeNumbers() throws IllegalArgumentException, InvalidExpressionException {
		Map<String, Double> variableValueMap = new HashMap<>();
		variableValueMap.put("a", 1.5);
		
		StringExpressionParser p = new StringExpressionParser("-a");
		assertEquals(-1.5, p.evaluate(variableValueMap));		
	}
	
	public void testEval() throws IllegalArgumentException, InvalidExpressionException {
		Map<String, Double> variableValueMap = new HashMap<>();
		variableValueMap.put("a", 1.5);
		
		StringExpressionParser p = new StringExpressionParser("1+1+1-2*(a-5.5)/1000");
		assertEquals(3.008, p.evaluate(variableValueMap));
	}
	
	public void testEvalFunction() throws IllegalArgumentException, InvalidExpressionException {
		Map<String, Double> variableValueMap = new HashMap<>();
		variableValueMap.put("a", 1.5);
		
		StringExpressionParser p = new StringExpressionParser("4+min(a,3.4,.125)");
		assertEquals(4.125, p.evaluate(variableValueMap));
	}

	public void testExtract() throws IllegalArgumentException, InvalidExpressionException {
		StringExpressionParser p = new StringExpressionParser("abc+a*(a-z)");
		String[] vars = p.getInvolvedVariables().toArray(new String[0]);
		assertEquals(3, vars.length);
		// the order of the variables is the order of detection in the algorithm
		// it is NOT the alphabetical order, nor the natural order of occurrence in the expression
		assertEquals("a", vars[0]);
		assertEquals("abc", vars[1]);
		assertEquals("z", vars[2]);
	}
}
