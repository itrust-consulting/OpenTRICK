/**
 * 
 */
package lu.itrust.business.component.helper;

import java.io.IOException;

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

	public ReportExcelSheet() {
	}

	public ReportExcelSheet(PackagePart packagePart) throws IOException {
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

	public void setPackagePart(PackagePart packagePart) throws IOException {
		this.packagePart = packagePart;
		if (this.packagePart != null)
			setXssfWorkbook(new XSSFWorkbook(this.packagePart.getInputStream()));

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
		if (this.xssfWorkbook == null || this.packagePart == null)
			return false;
		this.xssfWorkbook.write(this.packagePart.getOutputStream());
		this.xssfWorkbook.getPackage().close();
		return true;
	}

}
