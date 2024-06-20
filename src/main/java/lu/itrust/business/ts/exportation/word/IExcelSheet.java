package lu.itrust.business.ts.exportation.word;

import java.io.File;

public interface IExcelSheet {

	String getName();

	void setName(String name);

	Object getPackagePart();

	void setPackagePart(Object packagePart) throws Exception;

	Object getWorkbook();

	boolean save() throws Exception;

	File getFile();

	void setFile(File file);

}