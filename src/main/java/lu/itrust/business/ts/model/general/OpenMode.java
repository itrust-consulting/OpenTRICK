/**
 * 
 */
package lu.itrust.business.ts.model.general;

/**
 * @author eomar
 *
 */
public enum OpenMode {
	READ("read-only"), EDIT("edit");

	private String value;

	/**
	 * @param value
	 */
	private OpenMode(String value) {
		this.value = value;
	}
	
	public boolean isReadOnly() {
		return value.startsWith("read-only");
	}

	public static OpenMode defaultValue() {
		return READ;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public static OpenMode parse(Object data) {
		return parse(data, null);
	}

	protected static OpenMode parse(String data, OpenMode defaultValue) {
		data = data.trim().replace("_", "-").toLowerCase();
		for (OpenMode mode : values()) {
			if (mode.value.equals(data))
				return mode;
		}
		return defaultValue;
	}

	public static OpenMode parse(Object data, OpenMode defaultValue) {
		if (data == null)
			return null;
		else if (data instanceof OpenMode)
			return (OpenMode) data;
		else
			return parse(data.toString(), defaultValue);
	}

	public static OpenMode parseOrDefault(Object open) {
		return parse(open, defaultValue());
	}

	public static Boolean isReadOnly(OpenMode mode) {
		return mode == null || mode.isReadOnly();
	}

}
