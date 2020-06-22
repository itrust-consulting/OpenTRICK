/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import java.util.List;

import org.docx4j.dml.CTShapeProperties;
import org.docx4j.dml.chart.CTAxDataSource;
import org.docx4j.dml.chart.CTBarSer;
import org.docx4j.dml.chart.CTBoolean;
import org.docx4j.dml.chart.CTDLbls;
import org.docx4j.dml.chart.CTDPt;
import org.docx4j.dml.chart.CTExtensionList;
import org.docx4j.dml.chart.CTNumDataSource;
import org.docx4j.dml.chart.CTPictureOptions;
import org.docx4j.dml.chart.CTSerTx;
import org.docx4j.dml.chart.CTShape;
import org.docx4j.dml.chart.CTTrendline;
import org.docx4j.dml.chart.CTUnsignedInt;

/**
 * @author eomar
 *
 */
public class CTBarSerProxy implements CTChartSer {
	
	private CTBarSer proxy;

	/**
	 * 
	 */
	public CTBarSerProxy() {
		this(new CTBarSer());
	}

	/**
	 * @param barSer
	 */
	public CTBarSerProxy(CTBarSer proxy) {
		this.proxy = proxy;
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getIdx()
	 */
	public CTUnsignedInt getIdx() {
		return proxy.getIdx();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setIdx(org.docx4j.dml.chart.CTUnsignedInt)
	 */
	public void setIdx(CTUnsignedInt value) {
		proxy.setIdx(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getOrder()
	 */
	public CTUnsignedInt getOrder() {
		return proxy.getOrder();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setOrder(org.docx4j.dml.chart.CTUnsignedInt)
	 */
	public void setOrder(CTUnsignedInt value) {
		proxy.setOrder(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getTx()
	 */
	public CTSerTx getTx() {
		return proxy.getTx();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setTx(org.docx4j.dml.chart.CTSerTx)
	 */
	public void setTx(CTSerTx value) {
		proxy.setTx(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getSpPr()
	 */
	public CTShapeProperties getSpPr() {
		return proxy.getSpPr();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setSpPr(org.docx4j.dml.CTShapeProperties)
	 */
	public void setSpPr(CTShapeProperties value) {
		proxy.setSpPr(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getInvertIfNegative()
	 */
	public CTBoolean getInvertIfNegative() {
		return proxy.getInvertIfNegative();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setInvertIfNegative(org.docx4j.dml.chart.CTBoolean)
	 */
	public void setInvertIfNegative(CTBoolean value) {
		proxy.setInvertIfNegative(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getPictureOptions()
	 */
	public CTPictureOptions getPictureOptions() {
		return proxy.getPictureOptions();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setPictureOptions(org.docx4j.dml.chart.CTPictureOptions)
	 */
	public void setPictureOptions(CTPictureOptions value) {
		proxy.setPictureOptions(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getDPt()
	 */
	public List<CTDPt> getDPt() {
		return proxy.getDPt();
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getDLbls()
	 */
	public CTDLbls getDLbls() {
		return proxy.getDLbls();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setDLbls(org.docx4j.dml.chart.CTDLbls)
	 */
	public void setDLbls(CTDLbls value) {
		proxy.setDLbls(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getTrendline()
	 */
	public List<CTTrendline> getTrendline() {
		return proxy.getTrendline();
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getCat()
	 */
	public CTAxDataSource getCat() {
		return proxy.getCat();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setCat(org.docx4j.dml.chart.CTAxDataSource)
	 */
	public void setCat(CTAxDataSource value) {
		proxy.setCat(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getVal()
	 */
	public CTNumDataSource getVal() {
		return proxy.getVal();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setVal(org.docx4j.dml.chart.CTNumDataSource)
	 */
	public void setVal(CTNumDataSource value) {
		proxy.setVal(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getShape()
	 */
	public CTShape getShape() {
		return proxy.getShape();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setShape(org.docx4j.dml.chart.CTShape)
	 */
	public void setShape(CTShape value) {
		proxy.setShape(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTBarSer#getExtLst()
	 */
	public CTExtensionList getExtLst() {
		return proxy.getExtLst();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTBarSer#setExtLst(org.docx4j.dml.chart.CTExtensionList)
	 */
	public void setExtLst(CTExtensionList value) {
		proxy.setExtLst(value);
	}

	/**
	 * @return the proxy
	 */
	public CTBarSer getProxy() {
		return proxy;
	}
}
