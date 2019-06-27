/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;

import lu.itrust.business.TS.exportation.word.IExcelSheet;
import lu.itrust.business.TS.helper.InstanceManager;

/**
 * @author eomar
 *
 */
public class Docx4jExcelSheet implements IExcelSheet {

	private String name;

	private EmbeddedPackagePart packagePart;

	private SpreadsheetMLPackage mlPackage;

	private File file = null;

	public Docx4jExcelSheet() {
	}

	public Docx4jExcelSheet(EmbeddedPackagePart packagePart) throws Exception {
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
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getPackagePart()
	 */
	@Override
	public EmbeddedPackagePart getPackagePart() {
		return packagePart;
	}

	public void setPackagePart(EmbeddedPackagePart packagePart) throws Exception {
		this.packagePart = packagePart;
		if (this.packagePart != null) {
			setFile(InstanceManager.getServiceStorage().createTmpFile());
			InstanceManager.getServiceStorage().store(this.packagePart.getBytes(), getFile().getName());
			setMlPackage(SpreadsheetMLPackage.load(getFile()));
			setName(getWorkbook().getContents().getSheets().getSheet().get(0).getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#getXssfWorkbook()
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
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			if (this.packagePart == null || this.mlPackage == null)
				return false;
			this.mlPackage.save(outputStream);
			outputStream.flush();
			this.packagePart.setBinaryData(outputStream.toByteArray());
		} finally {
			if (file != null)
				InstanceManager.getServiceStorage().delete(file.getName());
		}

		return true;
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
	 * @see lu.itrust.business.TS.exportation.helper.IExcelSheet#setFile(java.io.
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
	 * @param mlPackage the mlPackage to set
	 */
	public void setMlPackage(SpreadsheetMLPackage mlPackage) {
		this.mlPackage = mlPackage;
	}

	@Override
	public void setPackagePart(Object packagePart) throws Exception {
		setPackagePart((EmbeddedPackagePart) packagePart);
	}

}
