package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.Styles;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.relationships.Relationship;
import org.springframework.util.StringUtils;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.CTMergeCell;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSheetDimension;
import org.xlsx4j.sml.CTTable;
import org.xlsx4j.sml.CTTableColumn;
import org.xlsx4j.sml.CTTablePart;
import org.xlsx4j.sml.CTTableStyleInfo;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.Sheet;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Worksheet;

public final class ExcelHelper {

	private static final char ABSOLUTE_REFERENCE_MARKER = '$';

	public static final Pattern CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Z]+)?(\\$?[0-9]+)?", Pattern.CASE_INSENSITIVE);

	public static final Pattern STRICTLY_CELL_REF_PATTERN = Pattern.compile("\\$?([A-Z]+)\\$?([0-9]+)", Pattern.CASE_INSENSITIVE);

	public static Cell setValue(Row row, int cellIndex, Object value) {
		Cell cell2 = getCell(row, cellIndex);
		if (value instanceof Double)
			setValue(cell2, (Double) value);
		else if (value instanceof Integer)
			setValue(cell2, (Integer) value);
		else if (value instanceof Boolean)
			setValue(cell2, (Boolean) value);
		else
			setValue(cell2, value == null ? "" : String.valueOf(value));
		return cell2;
	}

	public static void setValuePercent(Row row, int cellIndex, double value) {
		Cell cell = getCell(row, cellIndex);
		cell.setS(1L);
		cell.setV((value >= 1 ? value * 0.01 : value) + "");
	}

	public static void setValue(Cell cell, double value) {
		cell.setT(STCellType.N);
		cell.setV(value + "");
	}

	public static void setValue(Cell cell, int value) {
		cell.setT(STCellType.N);
		cell.setV(value + "");
	}

	public static void setValue(Cell cell, Boolean value) {
		cell.setT(STCellType.B);
		cell.setV(value ? "1" : "0");
	}

	public static Cell setFormula(Cell cell, String formula) {
		cell.setF(Context.getsmlObjectFactory().createCTCellFormula());
		cell.getF().setValue(formula);
		return cell;
	}

	public static Row getRow(SheetData sheet, int index, int colSize) {
		Row sheetRow;
		while (sheet.getRow().size() <= index) {
			sheetRow = Context.getsmlObjectFactory().createRow();
			createCell(sheetRow, colSize);
			sheet.getRow().add(sheetRow);
		}
		sheetRow = sheet.getRow().get(index);
		createCell(sheetRow, colSize);
		return sheetRow;
	}

	public static Cell getCell(Row row, int index) {
		return createCell(row, index);
	}

	public static Cell createCell(Row row, int index) {
		while (row.getC().size() <= index)
			row.getC().add(Context.getsmlObjectFactory().createCell());
		return row.getC().get(index);
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

	public static Styles createStylesPart(WorksheetPart worksheetPart) throws InvalidFormatException {
		Styles part = new Styles();
		Relationship relationship = worksheetPart.getWorkbookPart().addTargetPart(part);
		part.setContents(Context.getsmlObjectFactory().createCTStylesheet());
		worksheetPart.getWorkbookPart().setPartShortcut(part, relationship.getType());
		return part;
	}

	/**
	 * Takes in a 0-based base-10 column and returns a ALPHA-26 representation. eg
	 * column #3 -> D
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
	 * takes in a column reference portion of a CellRef and converts it from
	 * ALPHA-26 number format to 0-based base 10. 'A' -> 0 'Z' -> 25 'AA' -> 26 'IV'
	 * -> 255
	 * 
	 * @return zero based column index
	 */
	public static int colStringToIndex(String ref) {
		int retval = 0;
		char[] refs = ref.toUpperCase(Locale.ROOT).toCharArray();
		for (int k = 0; k < refs.length; k++) {
			char thechar = refs[k];
			if (thechar == ABSOLUTE_REFERENCE_MARKER) {
				if (k != 0)
					throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
				continue;
			} else if (Character.isDigit(thechar))
				break;
			else
				// Character is uppercase letter, find relative value to A
				retval = (retval * 26) + (thechar - 'A' + 1);
		}
		return retval - 1;
	}

	/**
	 * 
	 * @param row1 >= 0
	 * @param col1 >= 0
	 * @param row2 >= 0
	 * @param col2 >= 0
	 * @return address
	 */
	public static String getAddress(int row1, int col1, int row2, int col2) {
		assert row1 >= 0;
		assert row2 >= 0;
		assert col1 >= 0;
		assert col2 >= 0;
		return String.format("%s%d:%s%d", numToColString(col1), (row1 + 1), numToColString(col2), (row2 + 1));
	}

	public static void mergeCell(Worksheet worksheet, int rowStart, int colStart, int rowEnd, int colEnd) {
		CTMergeCell mergeCell = Context.getsmlObjectFactory().createCTMergeCell();
		if (worksheet.getMergeCells() == null)
			worksheet.setMergeCells(Context.getsmlObjectFactory().createCTMergeCells());
		worksheet.getMergeCells().getMergeCell().add(mergeCell);
		mergeCell.setRef(getAddress(rowStart, colStart, rowEnd, colEnd));
	}

	public static WorksheetPart createWorkSheetPart(SpreadsheetMLPackage mlPackage, String name) throws Exception {
		final int[] indexes = findNextSheetNumberAndId(mlPackage);
		final WorksheetPart part = mlPackage.createWorksheetPart(new PartName(String.format("/xl/worksheets/sheet%d.xml", indexes[0])), name, indexes[1]);
		part.getContents().getSheetData().setParent(part.getContents());
		part.getContents().setParent(part);
		return part;
	}

	public static TablePart createTablePart(WorksheetPart worksheetPart) throws Exception {
		long id = worksheetPart.getPackage().getContentTypeManager().getOverrideContentType().values().parallelStream().filter(r -> r.getPartName().startsWith("/xl/tables/"))
				.count() + 1;
		return createTablePart(worksheetPart, new PartName(String.format("/xl/tables/table%d.xml", id)), String.format("Table%d", id), id);
	}

	private static TablePart createTablePart(WorksheetPart worksheetPart, PartName partName, String name, long id) throws Exception {
		CTTablePart tablePart = new CTTablePart();
		TablePart part = new TablePart(partName);
		Relationship r = worksheetPart.addTargetPart(part);
		CTTable table = Context.getsmlObjectFactory().createCTTable();
		table.setId(id);
		table.setName(name);
		table.setDisplayName(name);
		part.setContents(table);
		tablePart.setId(r.getId());
		table.setTotalsRowShown(true);
		table.setAutoFilter(Context.getsmlObjectFactory().createCTAutoFilter());
		table.setTableColumns(Context.getsmlObjectFactory().createCTTableColumns());
		table.setTableStyleInfo(Context.getsmlObjectFactory().createCTTableStyleInfo());
		table.getTableStyleInfo().setName("TableStyleMedium2");
		table.getTableStyleInfo().setShowRowStripes(true);
		worksheetPart.getContents().setTableParts(Context.getsmlObjectFactory().createCTTableParts());
		worksheetPart.getContents().getTableParts().getTablePart().add(tablePart);
		return part;
	}

	public static CTTable createHeader(WorksheetPart worksheetPart, String name, String style, String[] columns, int length) throws Exception {
		TablePart tablePart = createTablePart(worksheetPart);
		CTTable table = tablePart.getContents();
		if (style != null) {
			if (table.getTableStyleInfo() == null)
				table.setTableStyleInfo(new CTTableStyleInfo());
			table.getTableStyleInfo().setName(style);
		}

		Row row = getRow(worksheetPart.getContents().getSheetData(), 0, columns.length);
		for (int i = 0; i < columns.length; i++) {
			CTTableColumn column = new CTTableColumn();
			column.setId(i + 1);
			column.setName(columns[i]);
			table.getTableColumns().getTableColumn().add(column);
			setValue(row, i, columns[i]);
		}
		table.getTableColumns().setCount((long) columns.length);
		table.setRef(new AddressRef(new CellRef(0, 0), new CellRef(length == 0 ? 1 : length, columns.length - 1)).toString());
		table.getAutoFilter().setRef(table.getRef());
		worksheetPart.getContents().setDimension(new CTSheetDimension());
		worksheetPart.getContents().getDimension().setRef(table.getRef());
		if (!isEmpty(name))
			table.setDisplayName(name);
		return table;
	}

	public static boolean isEmpty(String name) {
		return name == null || name.length() == 0;
	}

	public static Row createRow(SheetData sheetData) {
		Row row = Context.getsmlObjectFactory().createRow();
		sheetData.getRow().add(row);
		row.setR((long) sheetData.getRow().size());
		return row;
	}

	public static Row createRow(SheetData sheetData, int cells) {
		Row row = createRow(sheetData);
		for (int i = 0; i < cells; i++)
			row.getC().add(Context.getsmlObjectFactory().createCell());
		return row;
	}

	public static WorksheetPart getWorksheetPart(Worksheet worksheet) {
		return (WorksheetPart) worksheet.getParent();
	}

	public static WorksheetPart getWorksheetPart(final SheetData sheetData) {
		return getWorksheetPart((Worksheet) sheetData.getParent());
	}

	public static WorksheetPart getWorksheetPart(final Row row) {
		return getWorksheetPart((SheetData) row.getParent());
	}

	public static WorksheetPart getWorksheetPart(final Cell cell) {
		return getWorksheetPart((Row) cell.getParent());
	}

	public static String getString(Cell cell, DataFormatter formatter) {
		if (cell == null)
			return "";
		switch (cell.getT()) {
		case INLINE_STR:
			if (cell.getIs() != null)
				return cell.getIs().getT().getValue();
			break;
		case S:
		case STR:
			if (cell.getV() == null)
				return null;
			break;
		case N:
			if (getWorksheetPart(cell).getWorkbookPart().getStylesPart() != null)
				break;
		case B:
			return cell.getV();
		case E:
			return "";
		}
		return formatter.formatCellValue(cell);
	}

	public static String getString(Row row, int cell, DataFormatter formatter) {
		Cell c = getCellAt(row, cell);
		return c == null ? null : getString(c, formatter);
	}

	public static String getString(SheetData sheet, int row, int cell, DataFormatter formatter) {
		return sheet.getRow().size() > row && row >= 0 ? getString(sheet.getRow().get(row), cell, formatter) : null;
	}

	public static Cell getCellAt(Row row, int index) {
		for (int i = Math.min(index, row.getC().size() - 1); i >= 0; i--) {
			Cell cell = row.getC().get(i);
			if (colToIndex(cell.getR(), index) == index)
				return cell;
		}
		return null;
	}

	public static int colToIndex(String r, int index) {
		return StringUtils.hasText(r) ? colStringToIndex(r): index ;
	}

	public static double getDouble(Cell cell, DataFormatter formatter) {
		try {
			String value = getString(cell, formatter);
			if (isEmpty(value))
				return 0;
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static double getDouble(Row row, int index, DataFormatter formatter) {
		try {
			String value = getString(row, index, formatter);
			if (isEmpty(value))
				return 0;
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static int getInt(Cell cell, DataFormatter formatter) {
		try {
			String value = getString(cell, formatter);
			if (isEmpty(value))
				return 0;
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static int getInt(Row row, int index, DataFormatter formatter) {
		try {
			String value = getString(row, index, formatter);
			if (isEmpty(value))
				return 0;
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static boolean getBoolean(Cell cell, DataFormatter formatter) {
		String value = getString(cell, formatter);
		try {
			return !(isEmpty(value) || Integer.parseInt(value) == 0);
		} catch (NumberFormatException e) {
			return Boolean.valueOf(value);
		}
	}

	public static boolean getBoolean(Row row, int index) {
		String value = getString(row, index, new DataFormatter());
		try {
			return !(isEmpty(value) || Integer.parseInt(value) == 0);
		} catch (NumberFormatException e) {
			return Boolean.valueOf(value);
		}
	}

	public static SheetData findSheet(WorkbookPart workbookPart, String name) throws Exception {
		String id = workbookPart.getContents().getSheets().getSheet().parallelStream().filter(s -> s.getName().equals(name)).map(s -> s.getId()).findAny().orElse(null);
		if (isEmpty(id))
			return null;
		WorksheetPart worksheetPart = (WorksheetPart) workbookPart.getRelationshipsPart().getPart(id);
		return worksheetPart.getContents().getSheetData();
	}

	public static SheetData findSheet(WorkbookPart workbookPart, Sheet sheet) throws Exception {
		WorksheetPart worksheetPart = (WorksheetPart) workbookPart.getRelationshipsPart().getPart(sheet.getId());
		if (worksheetPart == null)
			return null;
		return worksheetPart.getContents().getSheetData();
	}

	public static Map<String, String> getSharedStrings(WorkbookPart workbookPart) throws Exception {
		if (workbookPart.getSharedStrings() == null)
			return Collections.emptyMap();
		AtomicInteger integer = new AtomicInteger(0);
		return workbookPart.getSharedStrings().getContents().getSi().stream().collect(Collectors.toMap(v -> integer.getAndIncrement() + "", v -> {
			if (v.getT() != null)
				return v.getT().getValue();
			else if (!v.getR().isEmpty())
				return v.getR().stream().map(r -> r.getT().getValue()).collect(Collectors.joining(""));
			else
				return v.getRPh().stream().map(r -> r.getT().getValue()).collect(Collectors.joining(""));
		}));
	}

	public static TablePart findTable(WorksheetPart worksheetPart, String name) throws Exception {
		for (CTTablePart ctTablePart : worksheetPart.getContents().getTableParts().getTablePart()) {
			TablePart table = (TablePart) worksheetPart.getRelationshipsPart().getPart(ctTablePart.getId());
			if (table.getContents().getName().equals(name) || table.getContents().getDisplayName().equals(name))
				return table;
		}
		return null;
	}

	public static TablePart findTable(SheetData sheetData, String name) throws Exception {
		final Worksheet worksheet = (Worksheet) sheetData.getParent();
		final WorksheetPart worksheetPart = (WorksheetPart) worksheet.getParent();
		for (CTTablePart ctTablePart : worksheet.getTableParts().getTablePart()) {
			TablePart table = (TablePart) worksheetPart.getRelationshipsPart().getPart(ctTablePart.getId());
			if (table.getContents().getName().equals(name) || table.getContents().getDisplayName().equals(name))
				return table;
		}

		return null;
	}

	public static TablePart findTableNameStartWith(WorksheetPart worksheetPart, String name) throws Exception {
		for (CTTablePart ctTablePart : worksheetPart.getContents().getTableParts().getTablePart()) {
			TablePart table = (TablePart) worksheetPart.getRelationshipsPart().getPart(ctTablePart.getId());
			if (table.getContents().getName().startsWith(name) || table.getContents().getDisplayName().startsWith(name))
				return table;
		}
		return null;
	}

	/**
	 * It will retrieve an array of int size 2, int[2]<br>
	 * [0] Next sheet file name number: /xl/worksheets/sheet[0].xml<br>
	 * [1] The id of the sheet : rid[1]
	 * 
	 * @param mlPackage
	 * @return
	 */
	public static int[] findNextSheetNumberAndId(SpreadsheetMLPackage mlPackage) {
		final int result[] = { mlPackage.getParts().getParts().values().parallelStream().filter(p -> p.getPartName().getName().startsWith("/xl/worksheets/sheet")).mapToInt(p -> {
			try {
				return Integer.parseInt(p.getPartName().getName().replaceAll("[/xl/worksheets/sheet]|[.xml]", ""));
			} catch (NumberFormatException e) {
				return 0;
			}
		}).max().orElse(0) + 1, 1 };

		try {
			result[1] = Integer.parseInt(mlPackage.getWorkbookPart().getRelationshipsPart().getNextId().replace("rId", ""));
		} catch (NumberFormatException e) {
		}
		return result;

	}

	public static boolean isEmptyOrWhiteSpace(String value) {
		return isEmpty(value) || value.trim().length() == 0;
	}

}
