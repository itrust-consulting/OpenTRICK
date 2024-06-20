/**
 * 
 */
package lu.itrust.business.ts.model.general;

/**
 * The OpenMode enum represents the different modes in which a file can be opened.
 */
public enum OpenMode {
	READ("read-only"), // Represents the read-only mode
	EDIT("edit"); // Represents the edit mode

	private String value;

	/**
	 * Constructs a new OpenMode with the specified value.
	 *
	 * @param value the value of the OpenMode
	 */
	private OpenMode(String value) {
		this.value = value;
	}

	/**
	 * Checks if the OpenMode is read-only.
	 *
	 * @return true if the OpenMode is read-only, false otherwise
	 */
	public boolean isReadOnly() {
		return value.startsWith("read-only");
	}

	/**
	 * Returns the default OpenMode, which is READ.
	 *
	 * @return the default OpenMode
	 */
	public static OpenMode defaultValue() {
		return READ;
	}

	/**
	 * Returns the value of the OpenMode.
	 *
	 * @return the value of the OpenMode
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the OpenMode.
	 *
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Parses the specified data into an OpenMode.
	 *
	 * @param data the data to parse
	 * @return the parsed OpenMode, or null if the data is null
	 */
	public static OpenMode parse(Object data) {
		return parse(data, null);
	}

	/**
	 * Parses the specified data into an OpenMode, using the specified defaultValue if the parsing fails.
	 *
	 * @param data         the data to parse
	 * @param defaultValue the default value to use if the parsing fails
	 * @return the parsed OpenMode, or the defaultValue if the parsing fails
	 */
	protected static OpenMode parse(String data, OpenMode defaultValue) {
		data = data.trim().replace("_", "-").toLowerCase();
		for (OpenMode mode : values()) {
			if (mode.value.equals(data))
				return mode;
		}
		return defaultValue;
	}

	/**
	 * Parses the specified data into an OpenMode, using the default value if the parsing fails.
	 *
	 * @param data the data to parse
	 * @return the parsed OpenMode, or the default value if the parsing fails
	 */
	public static OpenMode parse(Object data, OpenMode defaultValue) {
		if (data == null)
			return null;
		else if (data instanceof OpenMode)
			return (OpenMode) data;
		else
			return parse(data.toString(), defaultValue);
	}

	/**
	 * Parses the specified data into an OpenMode, using the default value if the parsing fails.
	 *
	 * @param open the data to parse
	 * @return the parsed OpenMode, or the default value if the parsing fails
	 */
	public static OpenMode parseOrDefault(Object open) {
		return parse(open, defaultValue());
	}

	/**
	 * Checks if the specified OpenMode is read-only.
	 *
	 * @param mode the OpenMode to check
	 * @return true if the OpenMode is read-only or null, false otherwise
	 */
	public static Boolean isReadOnly(OpenMode mode) {
		return mode == null || mode.isReadOnly();
	}
}
