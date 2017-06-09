package lu.itrust.business.TS.exportation.helper;

import java.io.File;

public interface IExcelSheet {

	String getName();

	void setName(String name);

	Object getPackagePart();

	void setPackagePart(Object packagePart) throws Exception;

	Object getWorkbook();

	void setWorkbook(Object workbook);

	boolean save() throws Exception;

	String getTempPath();

	void setTempPath(String tempPath);

	File getFile();

	void setFile(File file);

}