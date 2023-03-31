package lu.itrust.business.ts.exportation.word.impl.docx4j;

import org.docx4j.convert.out.common.preprocess.PartialDeepCopy;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.parts.Part;

public class PartClone extends PartialDeepCopy {

	public static Part clone(Part part, OpcPackage targetPackage) throws Docx4JException{
		return PartialDeepCopy.copyPart(part, targetPackage, true);
	}
}
