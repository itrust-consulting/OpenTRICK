/**
 * 
 */
package lu.itrust.business.TS.component.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author eomar
 *
 */
public class ReportExcelSheet {

	private String name;

	private PackagePart packagePart;

	private XSSFWorkbook xssfWorkbook;

	private String tempPath = null;

	private File file = null;

	public ReportExcelSheet() {
	}

	public ReportExcelSheet(PackagePart packagePart, String tempPath) throws IOException, InvalidFormatException {
		setTempPath(tempPath);
		setPackagePart(packagePart);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PackagePart getPackagePart() {
		return packagePart;
	}

	public void setPackagePart(PackagePart packagePart) throws IOException, InvalidFormatException {
		this.packagePart = packagePart;
		if (this.packagePart != null) {
			file = new File(String.format("%s/%d.xslx", tempPath, System.nanoTime()));
			Files.copy(this.packagePart.getInputStream(), file.toPath());
			setXssfWorkbook(new XSSFWorkbook(file.getCanonicalFile()));
		}
	}

	public XSSFWorkbook getXssfWorkbook() {
		return xssfWorkbook;
	}

	public void setXssfWorkbook(XSSFWorkbook xssfWorkbook) {
		this.xssfWorkbook = xssfWorkbook;
		if (this.xssfWorkbook != null)
			setName(this.xssfWorkbook.getSheetAt(0).getSheetName());
	}

	public boolean save() throws OpenXML4JException, IOException {
		try {
			if (this.xssfWorkbook == null || this.packagePart == null)
				return false;
			this.xssfWorkbook.write(this.packagePart.getOutputStream());
		} finally {
			if (file != null && file.exists())
				file.delete();
		}

		return true;
	}

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
