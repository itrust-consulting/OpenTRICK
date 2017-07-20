package lu.itrust.TS.model;

import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.CellCoord;

public class TestExcelCellRef {

	@Test
	public void testParseBasicRef() {

		CellCoord coord = CellCoord.parse("A1");

		Assert.assertEquals("Column", 0, coord.getCol());
		
		Assert.assertEquals("Row", 0, coord.getRow());
		
		Assert.assertEquals("Absolute", false, coord.isAbsolute());
	}
	
	@Test
	public void testParseAbosuluteRef() {

		CellCoord coord = CellCoord.parse("$A$1");

		Assert.assertEquals("Column", 0, coord.getCol());
		
		Assert.assertEquals("Row", 0, coord.getRow());
		
		Assert.assertEquals("Absolute", true, coord.isAbsolute());
	}
	
	@Test
	public void testParseAdvancedRef() {

		CellCoord coord = CellCoord.parse("$AA$65");

		Assert.assertEquals("Column", 26, coord.getCol());
		
		Assert.assertEquals("Row", 64, coord.getRow());
		
		Assert.assertEquals("Absolute", true, coord.isAbsolute());
	}



}
