package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import java.util.List;

import org.docx4j.dml.CTShapeProperties;
import org.docx4j.dml.chart.CTAxDataSource;
import org.docx4j.dml.chart.CTDLbls;
import org.docx4j.dml.chart.CTDPt;
import org.docx4j.dml.chart.CTExtensionList;
import org.docx4j.dml.chart.CTNumDataSource;
import org.docx4j.dml.chart.CTSerTx;
import org.docx4j.dml.chart.CTUnsignedInt;

public interface CTChartSer {

	/**
	 * Gets the value of the idx property.
	 * 
	 * @return possible object is {@link CTUnsignedInt }
	 * 
	 */
	public CTUnsignedInt getIdx();

	/**
	 * Sets the value of the idx property.
	 * 
	 * @param value allowed object is {@link CTUnsignedInt }
	 * 
	 */
	public void setIdx(CTUnsignedInt value);

	/**
	 * Gets the value of the order property.
	 * 
	 * @return possible object is {@link CTUnsignedInt }
	 * 
	 */
	public CTUnsignedInt getOrder();

	/**
	 * Sets the value of the order property.
	 * 
	 * @param value allowed object is {@link CTUnsignedInt }
	 * 
	 */
	public void setOrder(CTUnsignedInt value);

	/**
	 * Gets the value of the tx property.
	 * 
	 * @return possible object is {@link CTSerTx }
	 * 
	 */
	public CTSerTx getTx();

	/**
	 * Sets the value of the tx property.
	 * 
	 * @param value allowed object is {@link CTSerTx }
	 * 
	 */
	public void setTx(CTSerTx value);

	/**
	 * Gets the value of the spPr property.
	 * 
	 * @return possible object is {@link CTShapeProperties }
	 * 
	 */
	public CTShapeProperties getSpPr();

	/**
	 * Sets the value of the spPr property.
	 * 
	 * @param value allowed object is {@link CTShapeProperties }
	 * 
	 */
	public void setSpPr(CTShapeProperties value);


	/**
	 * Gets the value of the dPt property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the dPt property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDPt().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link CTDPt }
	 * 
	 * 
	 */
	public List<CTDPt> getDPt();

	/**
	 * Gets the value of the dLbls property.
	 * 
	 * @return possible object is {@link CTDLbls }
	 * 
	 */
	public CTDLbls getDLbls();

	/**
	 * Sets the value of the dLbls property.
	 * 
	 * @param value allowed object is {@link CTDLbls }
	 * 
	 */
	public void setDLbls(CTDLbls value);


	/**
	 * Gets the value of the cat property.
	 * 
	 * @return possible object is {@link CTAxDataSource }
	 * 
	 */
	public CTAxDataSource getCat();

	/**
	 * Sets the value of the cat property.
	 * 
	 * @param value allowed object is {@link CTAxDataSource }
	 * 
	 */
	public void setCat(CTAxDataSource value);

	/**
	 * Gets the value of the val property.
	 * 
	 * @return possible object is {@link CTNumDataSource }
	 * 
	 */
	public CTNumDataSource getVal();

	/**
	 * Sets the value of the val property.
	 * 
	 * @param value allowed object is {@link CTNumDataSource }
	 * 
	 */
	public void setVal(CTNumDataSource value);

	/**
	 * Gets the value of the extLst property.
	 * 
	 * @return possible object is {@link CTExtensionList }
	 * 
	 */
	public CTExtensionList getExtLst();

	/**
	 * Sets the value of the extLst property.
	 * 
	 * @param value allowed object is {@link CTExtensionList }
	 * 
	 */
	public void setExtLst(CTExtensionList value);

}
