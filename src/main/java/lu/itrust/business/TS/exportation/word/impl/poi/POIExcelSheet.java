/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.poi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lu.itrust.business.TS.exportation.word.IExcelSheet;

/**
 * @author eomar
 *
 */
@Deprecated
public class POIExcelSheet implements IExcelSheet {

	private String name;

	private PackagePart packagePart;

	private XSSFWorkbook xssfWorkbook;

	private String tempPath = null;

	private File file = null;

	public POIExcelSheet() {
	}

	public POIExcelSheet(PackagePart packagePart, String tempPath) throws IOException, InvalidFormatException {
		setTempPath(tempPath);
		setPackagePart(packagePart);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getPackagePart()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getXssfWorkbook()
	 */
	@Override
	public XSSFWorkbook getWorkbook() {
		return xssfWorkbook;
	}

	public void setXssfWorkbook(XSSFWorkbook xssfWorkbook) {
		this.xssfWorkbook = xssfWorkbook;
		if (this.xssfWorkbook != null)
			setName(this.xssfWorkbook.getSheetAt(0).getSheetName());
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#save()
	 */
	@Override
	public boolean save() throws OpenXML4JException, IOException {
		try {
			if (this.xssfWorkbook == null || this.packagePart == null)
				return false;
			this.xssfWorkbook.write(this.packagePart.getOutputStream());
		} finally {
			if (file != null && file.exists()){
				if(!file.delete())
					file.deleteOnExit();
			}
			if(this.xssfWorkbook!=null)
				this.xssfWorkbook.close();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getTempPath()
	 */
	@Override
	public String getTempPath() {
		return tempPath;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setTempPath(java.lang.String)
	 */
	@Override
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setFile(java.io.File)
	 */
	@Override
	public void setFile(File file) {
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setPackagePart(org.apache.poi.openxml4j.opc.PackagePart)
	 */
	@Override
	public void setPackagePart(Object packagePart) throws Exception {
		setPackagePart((PackagePart)packagePart);
	}


	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setXssfWorkbook(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	public void setWorkbook(Object workbook) {
		setXssfWorkbook((XSSFWorkbook) workbook);
	}
	
	

}
