package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.*;

import java.util.regex.Matcher;

public class CellCoord {

	private boolean absolute;

	private int row;

	private int col;

	public CellCoord() {
	}

	/**
	 * @return the absolute
	 */
	public boolean isAbsolute() {
		return absolute;
	}

	/**
	 * @param absolute
	 *            the absolute to set
	 */
	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row
	 *            the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the col
	 */
	public int getCol() {
		return col;
	}

	/**
	 * @param col
	 *            the col to set
	 */
	public void setCol(int col) {
		this.col = col;
	}

	public static final CellCoord parse(String value) {
		CellCoord coord = new CellCoord();
		if ((coord.absolute = value.contains("$")))
			value = value.replaceAll("\\$", "");
		Matcher matcher = CELL_REF_PATTERN.matcher(value);
		if (!matcher.find())
			throw new IllegalArgumentException("No address found");
		coord.col = colStringToIndex(matcher.group(1));
		coord.row = Integer.parseInt(matcher.group(2))-1;
		return coord;
	}

}
