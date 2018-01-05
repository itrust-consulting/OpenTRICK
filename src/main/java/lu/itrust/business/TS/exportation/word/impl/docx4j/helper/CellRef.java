package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.*;

import java.util.regex.Matcher;

public class CellRef {

	private boolean absolute;

	private int row;

	private int col;

	public CellRef() {
	}

	public CellRef(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public CellRef(int row, int col, boolean absolute) {
		this(row, col);
		this.absolute = absolute;
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

	public static final CellRef parse(String value) {
		CellRef coord = new CellRef();
		if ((coord.absolute = value.contains("$")))
			value = value.replaceAll("\\$", "");
		Matcher matcher = CELL_REF_PATTERN.matcher(value);
		if (!matcher.find())
			throw new IllegalArgumentException("No address found");
		coord.col = colStringToIndex(matcher.group(1));
		coord.row = Integer.parseInt(matcher.group(2)) - 1;
		return coord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(isAbsolute() ? 4 : 2);
		if (isAbsolute())
			builder.append("$");
		builder.append(numToColString(col));
		if (isAbsolute())
			builder.append("$");
		builder.append(row + 1);
		return builder.toString();
	}

}
