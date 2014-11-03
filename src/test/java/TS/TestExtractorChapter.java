/**
 * 
 */
package TS;

import junit.framework.TestCase;
import lu.itrust.business.TS.data.actionplan.ActionPlanComputation;

/**
 * @author oensuifudine
 *
 */
public class TestExtractorChapter extends TestCase {
	
	public void testExtractor(){
		
		assertEquals("A.10", "A.10", ActionPlanComputation.extractMainChapter("A.10.10.1"));
		
		assertEquals("A.10", "A.10", ActionPlanComputation.extractMainChapter("A.10"));
		
		assertEquals("10", "10", ActionPlanComputation.extractMainChapter("10.1.10"));
		
		assertEquals("10", "10", ActionPlanComputation.extractMainChapter("10.1"));
		
		assertEquals("10", "10", ActionPlanComputation.extractMainChapter("10"));
	}

}
