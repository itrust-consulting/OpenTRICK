package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

public class TabRef {
	
	private String sheet;
	
	private CellCoord begin;
	
	private CellCoord end;

	public TabRef() {
	}

	/**
	 * @return the begin
	 */
	public CellCoord getBegin() {
		return begin;
	}

	/**
	 * @param begin the begin to set
	 */
	public void setBegin(CellCoord begin) {
		this.begin = begin;
	}

	/**
	 * @return the end
	 */
	public CellCoord getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(CellCoord end) {
		this.end = end;
	}

	/**
	 * @return the sheet
	 */
	public String getSheet() {
		return sheet;
	}

	/**
	 * @param sheet the sheet to set
	 */
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}
	
	public static TabRef parse(String value) {
		TabRef tabRef = new TabRef();
		int index = value.lastIndexOf("!");
		if(index!=-1) {
			tabRef.sheet = value.substring(0, index);
			value = value.substring(index+1);
		}
		
		return tabRef;
	}

}
