package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import org.springframework.util.StringUtils;

public class AddressRef {

	private String sheet;

	private CellRef begin;

	private CellRef end;

	public AddressRef() {
	}

	public AddressRef(CellRef begin, CellRef end) {
		this.begin = begin;
		this.end = end;
	}


	

	public AddressRef(String sheet, CellRef begin, CellRef end) {
		this(begin, end);
		this.sheet = sheet;
	}

	/**
	 * @return the begin
	 */
	public CellRef getBegin() {
		return begin;
	}

	/**
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(CellRef begin) {
		this.begin = begin;
	}

	/**
	 * @return the end
	 */
	public CellRef getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(CellRef end) {
		this.end = end;
	}

	/**
	 * @return the sheet
	 */
	public String getSheet() {
		return sheet;
	}

	/**
	 * @param sheet
	 *            the sheet to set
	 */
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	public static AddressRef parse(String value) {
		AddressRef addressRef = new AddressRef();
		int index = value.lastIndexOf("!");
		if (index != -1) {
			addressRef.sheet = value.substring(0, index);
			value = value.substring(index + 1);
		}
		String[] address = value.split(":");
		addressRef.begin = CellRef.parse(address[0]);
		if (address.length == 2)
			addressRef.end = CellRef.parse(address[1]);
		else if (address.length > 2)
			throw new IllegalArgumentException("Bad address.");
		return addressRef;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(3);
		if(!StringUtils.isEmpty(sheet))
			builder.append(sheet+"!");
		builder.append(begin.toString());
		if(end!=null)
			builder.append(":"+end.toString());
		return builder.toString();	
	}
	
	

}
