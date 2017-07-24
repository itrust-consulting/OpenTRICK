package lu.itrust.business.TS.exportation.word;

import java.io.File;

public interface IExcelSheet {

	String getName();

	void setName(String name);

	Object getPackagePart();

	void setPackagePart(Object packagePart) throws Exception;

	Object getWorkbook();

	boolean save() throws Exception;

	String getTempPath();

	void setTempPath(String tempPath);

	File getFile();

	void setFile(File file);

}