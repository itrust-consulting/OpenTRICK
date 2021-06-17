package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import java.util.List;

import org.docx4j.dml.CTShapeProperties;
import org.docx4j.dml.chart.CTAxDataSource;
import org.docx4j.dml.chart.CTDLbls;
import org.docx4j.dml.chart.CTDPt;
import org.docx4j.dml.chart.CTExtensionList;
import org.docx4j.dml.chart.CTMarker;
import org.docx4j.dml.chart.CTNumDataSource;
import org.docx4j.dml.chart.CTRadarSer;
import org.docx4j.dml.chart.CTSerTx;
import org.docx4j.dml.chart.CTUnsignedInt;

public class CTRadarSerProxy implements CTChartSer {

	private CTRadarSer proxy;

	/**
	 * 
	 */
	public CTRadarSerProxy() {
		this(new CTRadarSer());
	}

	/**
	 * @param data
	 */
	public CTRadarSerProxy(CTRadarSer proxy) {
		this.proxy = proxy;
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getIdx()
	 */
	public CTUnsignedInt getIdx() {
		return proxy.getIdx();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setIdx(org.docx4j.dml.chart.CTUnsignedInt)
	 */
	public void setIdx(CTUnsignedInt value) {
		proxy.setIdx(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getOrder()
	 */
	public CTUnsignedInt getOrder() {
		return proxy.getOrder();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setOrder(org.docx4j.dml.chart.CTUnsignedInt)
	 */
	public void setOrder(CTUnsignedInt value) {
		proxy.setOrder(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getTx()
	 */
	public CTSerTx getTx() {
		return proxy.getTx();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setTx(org.docx4j.dml.chart.CTSerTx)
	 */
	public void setTx(CTSerTx value) {
		proxy.setTx(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getSpPr()
	 */
	public CTShapeProperties getSpPr() {
		return proxy.getSpPr();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setSpPr(org.docx4j.dml.CTShapeProperties)
	 */
	public void setSpPr(CTShapeProperties value) {
		proxy.setSpPr(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getMarker()
	 */
	public CTMarker getMarker() {
		return proxy.getMarker();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setMarker(org.docx4j.dml.chart.CTMarker)
	 */
	public void setMarker(CTMarker value) {
		proxy.setMarker(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getDPt()
	 */
	public List<CTDPt> getDPt() {
		return proxy.getDPt();
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getDLbls()
	 */
	public CTDLbls getDLbls() {
		return proxy.getDLbls();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setDLbls(org.docx4j.dml.chart.CTDLbls)
	 */
	public void setDLbls(CTDLbls value) {
		proxy.setDLbls(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getCat()
	 */
	public CTAxDataSource getCat() {
		return proxy.getCat();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setCat(org.docx4j.dml.chart.CTAxDataSource)
	 */
	public void setCat(CTAxDataSource value) {
		proxy.setCat(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getVal()
	 */
	public CTNumDataSource getVal() {
		return proxy.getVal();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setVal(org.docx4j.dml.chart.CTNumDataSource)
	 */
	public void setVal(CTNumDataSource value) {
		proxy.setVal(value);
	}

	/**
	 * @return
	 * @see org.docx4j.dml.chart.CTRadarSer#getExtLst()
	 */
	public CTExtensionList getExtLst() {
		return proxy.getExtLst();
	}

	/**
	 * @param value
	 * @see org.docx4j.dml.chart.CTRadarSer#setExtLst(org.docx4j.dml.chart.CTExtensionList)
	 */
	public void setExtLst(CTExtensionList value) {
		proxy.setExtLst(value);
	}

	/**
	 * @return the proxy
	 */
	public CTRadarSer getProxy() {
		return proxy;
	}
}
