/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.TS.exportation.word.IExcelSheet;

/**
 * @author eomar
 *
 */
public class Docx4jExcelSheet implements IExcelSheet {

	private String name;

	private EmbeddedPackagePart packagePart;

	private SpreadsheetMLPackage mlPackage;

	private String tempPath = null;

	private File file = null;

	public Docx4jExcelSheet() {
	}

	public Docx4jExcelSheet(EmbeddedPackagePart packagePart, String tempPath) throws Exception {
		setTempPath(tempPath);
		setPackagePart(packagePart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.helper.IExcelSheet#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.helper.IExcelSheet#getPackagePart()
	 */
	@Override
	public EmbeddedPackagePart getPackagePart() {
		return packagePart;
	}

	public void setPackagePart(EmbeddedPackagePart packagePart) throws Exception {
		this.packagePart = packagePart;
		if (this.packagePart != null) {
			file = new File(String.format("%s/%d.xslx", tempPath, System.nanoTime()));
			FileCopyUtils.copy(this.packagePart.getBytes(), file);
			setMlPackage(SpreadsheetMLPackage.load(file));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.helper.IExcelSheet#getXssfWorkbook()
	 */
	@Override
	public WorkbookPart getWorkbook() {
		return mlPackage.getWorkbookPart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#save()
	 */
	@Override
	public boolean save() throws Exception {
		ByteArrayOutputStream outputStream = null;
		try {
			if (this.packagePart == null || this.mlPackage == null)
				return false;
			this.mlPackage.save(outputStream = new ByteArrayOutputStream());
			outputStream.flush();
			this.packagePart.setBinaryData(outputStream.toByteArray());
		} finally {
			if (file != null && file.exists()) {
				if (!file.delete())
					file.deleteOnExit();
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
				}
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getTempPath()
	 */
	@Override
	public String getTempPath() {
		return tempPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.helper.IExcelSheet#setTempPath(java.
	 * lang.String)
	 */
	@Override
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.helper.IExcelSheet#setFile(java.io.
	 * File)
	 */
	@Override
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the mlPackage
	 */
	public SpreadsheetMLPackage getMlPackage() {
		return mlPackage;
	}

	/**
	 * @param mlPackage
	 *            the mlPackage to set
	 */
	public void setMlPackage(SpreadsheetMLPackage mlPackage) {
		this.mlPackage = mlPackage;
	}

	@Override
	public void setPackagePart(Object packagePart) throws Exception {
		setPackagePart((EmbeddedPackagePart) packagePart);
	}

}
