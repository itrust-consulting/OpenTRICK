package lu.itrust.ts.model;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.CellRef;

public class TestExcelCellRef {

	@Test
	public void testParseBasicRef() {

		CellRef coord = CellRef.parse("A1");

		Assert.assertEquals("Column", 0, coord.getCol());

		Assert.assertEquals("Row", 0, coord.getRow());

		Assert.assertEquals("Absolute", false, coord.isAbsolute());

		Assert.assertEquals("ToString", "A1", coord.toString());

	}

	@Test
	public void testParseAbosuluteRef() {

		CellRef coord = CellRef.parse("$A$1");

		Assert.assertEquals("Column", 0, coord.getCol());

		Assert.assertEquals("Row", 0, coord.getRow());

		Assert.assertEquals("Absolute", true, coord.isAbsolute());

		Assert.assertEquals("ToString", "$A$1", coord.toString());
	}

	@Test
	public void testParseAdvancedRef() {

		CellRef coord = CellRef.parse("$AA$65");

		Assert.assertEquals("Column", 26, coord.getCol());

		Assert.assertEquals("Row", 64, coord.getRow());

		Assert.assertEquals("Absolute", true, coord.isAbsolute());

		Assert.assertEquals("ToString", "$AA$65", coord.toString());
	}

	@Test
	public void testParseBasicAddress() {

		AddressRef addressRef = AddressRef.parse("Lol!A1:B3");

		Assert.assertEquals("Sheet name", "Lol", addressRef.getSheet());

		Assert.assertEquals("Begin column", 0, addressRef.getBegin().getCol());

		Assert.assertEquals("Begin row", 0, addressRef.getBegin().getRow());

		Assert.assertEquals("Begin absolute", false, addressRef.getBegin().isAbsolute());

		Assert.assertEquals("End column", 1, addressRef.getEnd().getCol());

		Assert.assertEquals("End row", 2, addressRef.getEnd().getRow());

		Assert.assertEquals("End absolute", false, addressRef.getEnd().isAbsolute());

		Assert.assertEquals("ToString", "Lol!A1:B3", addressRef.toString());

	}

	@Test
	public void testParseAbsoluteAddress() {

		AddressRef addressRef = AddressRef.parse("27001!$D$80:$G$12");

		Assert.assertEquals("Sheet name", "27001", addressRef.getSheet());

		Assert.assertEquals("Begin column", 3, addressRef.getBegin().getCol());

		Assert.assertEquals("Begin row", 79, addressRef.getBegin().getRow());

		Assert.assertEquals("Begin absolute", true, addressRef.getBegin().isAbsolute());

		Assert.assertEquals("End column", 6, addressRef.getEnd().getCol());

		Assert.assertEquals("End row", 11, addressRef.getEnd().getRow());

		Assert.assertEquals("End absolute", true, addressRef.getEnd().isAbsolute());

		Assert.assertEquals("ToString", "27001!$D$80:$G$12", addressRef.toString());

	}

	@Test
	public void replaceSingleExcelAt() {
		var value = "Risk_estimation[@Scenario]".replaceAll("\\[@(\\w+)\\]", "[[#This Row],[$1]]");
		Assert.assertEquals("ReplaceAll", "Risk_estimation[[#This Row],[Scenario]]", value);

	}

	@Test
	public void replaceMultiExcelAt() {
		var value = "Risk_estimation[@C]*Risk_estimation[@Pr]*Risk_estimation[@Qualif]".replaceAll("\\[@(\\w+)\\]",
				"[[#This Row],[$1]]");
		Assert.assertEquals("ReplaceAll",
				"Risk_estimation[[#This Row],[C]]*Risk_estimation[[#This Row],[Pr]]*Risk_estimation[[#This Row],[Qualif]]",
				value);
	}

	@Test
	public void matherReplaceMultiExcelAt() {
		Pattern pattern = Pattern.compile("\\[@(\\w+)\\]");
		var matcher = pattern.matcher("Risk_estimation[@C]*Risk_estimation[@Pr]*Risk_estimation[@Qualif]");
		var value = matcher.replaceAll(e -> "[[#This Row],[%s]]".formatted(e.group(1)));
		Assert.assertEquals("ReplaceAll",
				"Risk_estimation[[#This Row],[C]]*Risk_estimation[[#This Row],[Pr]]*Risk_estimation[[#This Row],[Qualif]]",
				value);
	}

	@Test
	public void matherReplaceNone() {
		Pattern pattern = Pattern.compile("\\[@(\\w+)\\]");
		var matcher = pattern.matcher("Risk_estimation");
		var value = matcher.replaceAll(e -> "[[#This Row],[%s]]".formatted(e.group(1)));
		Assert.assertEquals("ReplaceAll", "Risk_estimation", value);
	}

	@Test
	public void matherReplaceMultiExcelAtExtended() {
		Pattern pattern = Pattern.compile("\\[@(\\[\\w.*\\])");
		var matcher = pattern.matcher("MID(Risk_estimation[@[EXP Vulnerability]];2;1)");
		var value = matcher.replaceAll(e -> "[[#This Row],%s".formatted(e.group(1)));
		Assert.assertEquals("ReplaceAll", "MID(Risk_estimation[[#This Row],[EXP Vulnerability]];2;1)", value);
	}

	@Test
	public void matherReplaceMultiExcelMixed() {
		var value = Pattern.compile("\\[@(\\[\\w.*\\])")
				.matcher("Risk_estimation[@Max]/Risk_estimation[@Qualif]*Risk_estimation[@[Resid-V]]")
				.replaceAll(e -> "[[#This Row],%s".formatted(e.group(1)));

		value = Pattern.compile("\\[@(\\w+)\\]").matcher(value)
				.replaceAll(e -> "[[#This Row],[%s]]".formatted(e.group(1)));

		Assert.assertEquals("ReplaceAll",
				"Risk_estimation[[#This Row],[Max]]/Risk_estimation[[#This Row],[Qualif]]*Risk_estimation[[#This Row],[Resid-V]]",
				value);
	}

}
