package lu.itrust.TS.model;

import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;

/**
 * @author oensuifudine
 *
 */
public class TestExtractorChapter {
	
	@Test
	public void testExtractor(){
		
		Assert.assertEquals("A.10", "A.10", ActionPlanComputation.extractMainChapter("A.10.10.1"));
		
		Assert.assertEquals("A.10", "A.10", ActionPlanComputation.extractMainChapter("A.10"));
		
		Assert.assertEquals("10", "10", ActionPlanComputation.extractMainChapter("10.1.10"));
		
		Assert.assertEquals("10", "10", ActionPlanComputation.extractMainChapter("10.1"));
		
		Assert.assertEquals("10", "10", ActionPlanComputation.extractMainChapter("10"));
	}

}
