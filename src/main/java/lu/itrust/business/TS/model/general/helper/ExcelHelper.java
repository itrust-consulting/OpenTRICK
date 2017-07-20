package lu.itrust.business.TS.model.general.helper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;

public final class ExcelHelper {

	public static void setValue(Cell cell, double value) {
		cell.setT(STCellType.N);
		cell.setV(value + "");
	}

	public static void setValue(Cell cell, int value) {
		cell.setT(STCellType.N);
		cell.setV(value + "");
	}

	public static void setValue(Cell cell, String value) {
		if (value == null)
			value = "";
		CTXstringWhitespace ctXstringWhitespace = Context.getsmlObjectFactory().createCTXstringWhitespace();
		ctXstringWhitespace.setValue(value);
		CTRst ctRst = new CTRst();
		ctRst.setT(ctXstringWhitespace);
		cell.setIs(ctRst);
		cell.setT(STCellType.INLINE_STR);
	}

	/**
	 * Takes in a 0-based base-10 column and returns a ALPHA-26 representation.
	 * eg column #3 -> D
	 */
	public static String numToColString(int col) {
		// Excel counts column A as the 1st column, we
		// treat it as the 0th one
		int excelColNum = col + 1;

		StringBuilder colRef = new StringBuilder(2);
		int colRemain = excelColNum;

		while (colRemain > 0) {
			int thisPart = colRemain % 26;
			if (thisPart == 0) {
				thisPart = 26;
			}
			colRemain = (colRemain - thisPart) / 26;

			// The letter A is at 65
			char colChar = (char) (thisPart + 64);
			colRef.insert(0, colChar);
		}

		return colRef.toString();
	}

	/**
	 * 
	 * @param row1
	 *            >= 1
	 * @param col1
	 *            >= 0
	 * @param row2
	 *            >= 1
	 * @param col2
	 *            >= 0
	 * @return address
	 */
	public static String getAddress(int row1, int col1, int row2, int col2) {
		assert row1 >= 1;
		assert row2 >= 1;
		assert col1 >= 0;
		assert col2 >= 0;
		return String.format("%s%d:%s%d", numToColString(col1), row1, numToColString(col2), row2);
	}

	public static WorksheetPart createWorkSheetPart(SpreadsheetMLPackage mlPackage, String name) throws Exception {
		int index = mlPackage.getWorkbookPart().getContents().getSheets().getSheet().size() + 1;
		return mlPackage.createWorksheetPart(new PartName(String.format("/xl/worksheets/sheet%d.xml", index)), name, index);
	}

	public static Row createRow(SheetData sheetData) {
		Row row = Context.getsmlObjectFactory().createRow();
		sheetData.getRow().add(row);
		return row;
	}

	public static Row createRow(SheetData sheetData, int cells) {
		Row row = createRow(sheetData);
		for (int i = 0; i < cells; i++)
			row.getC().add(Context.getsmlObjectFactory().createCell());
		return row;
	}

	public static String getString(Cell cell, Map<String, String> sharedStrings) {
		switch (cell.getT()) {
		case INLINE_STR:
			return cell.getIs().getT().getValue();
		case S:
			return sharedStrings.get(cell.getV());
		case N:
		case B:
		case STR:
		case E:
			return cell.getV();
		}
		return null;
	}

	public static double getDouble(Cell cell) {
		try {
			String value = getString(cell, Collections.emptyMap());
			if (value == null)
				return 0;
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static int getInt(Cell cell) {
		try {
			String value = getString(cell, Collections.emptyMap());
			if (value == null)
				return 0;
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static SheetData findSheet(WorkbookPart workbookPart, String name) throws Exception {
		String id = workbookPart.getContents().getSheets().getSheet().parallelStream().filter(s -> s.getName().equals(name)).map(s -> s.getId()).findAny().orElse(null);
		if (id == null)
			return null;
		WorksheetPart worksheetPart = (WorksheetPart) workbookPart.getRelationshipsPart().getPart(id);
		return worksheetPart.getContents().getSheetData();
	}

	public static Map<String, String> getSharedStrings(WorkbookPart workbookPart) throws Exception {
		if (workbookPart.getSharedStrings() == null)
			return Collections.emptyMap();
		AtomicInteger integer = new AtomicInteger(0);
		return workbookPart.getSharedStrings().getContents().getSi().stream().collect(Collectors.toMap(v -> integer.getAndIncrement()+"", v -> v.getT().getValue()));
	}

}
