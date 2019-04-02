/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.io.File;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.dml.chart.CTAxDataSource;
import org.docx4j.dml.chart.CTNumData;
import org.docx4j.dml.chart.CTNumDataSource;
import org.docx4j.dml.chart.CTNumRef;
import org.docx4j.dml.chart.CTRelId;
import org.docx4j.dml.chart.SerContent;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.finders.RangeFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.relationships.Relationship;
import org.docx4j.relationships.Relationships;
import org.docx4j.wml.Br;
import org.docx4j.wml.Document;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.springframework.context.MessageSource;

import com.atlassian.util.concurrent.atomic.AtomicInteger;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.Docx4jReportData;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.BookmarkClean;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;

/**
 * @author eomar
 *
 */
public class Docx4jReportDataImpl implements Docx4jReportData {

	private Analysis analysis;

	private AtomicLong drawingIndex;

	private org.docx4j.wml.Document document;

	private org.docx4j.wml.ObjectFactory factory;

	private org.docx4j.dml.ObjectFactory dmlFactory;

	private org.docx4j.dml.chart.ObjectFactory chartFactory;

	private org.docx4j.dml.wordprocessingDrawing.ObjectFactory drawingFactory;

	private org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage;

	/**
	 * 
	 */
	public Docx4jReportDataImpl() {
	}

	@Override
	public void close() {
	}

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public int getProgess() {
		return 0;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public String getDarkColor() {
		return null;
	}

	@Override
	public String getReportName() {
		return null;
	}

	@Override
	public String getLightColor() {
		return null;
	}

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

	@Override
	public String getDefaultColor() {
		return null;
	}

	@Override
	public ReportTemplate getTemplate() {
		return null;
	}

	@Override
	public String getDefaultTableStyle() {
		return null;
	}

	@Override
	public ValueFactory getValueFactory() {
		return null;
	}

	@Override
	public DecimalFormat getNumberFormat() {
		return null;
	}

	@Override
	public MessageSource getMessageSource() {
		return null;
	}

	@Override
	public AtomicInteger getBookmarkMaxId() {
		return null;
	}

	@Override
	public String getDefaultParagraphStyle() {
		return null;
	}

	@Override
	public AtomicInteger getBookmarkCounter() {
		return null;
	}

	@Override
	public DecimalFormat getKiloNumberFormat() {
		return null;
	}

	@Override
	public Document getDocument() {
		return null;
	}

	@Override
	public org.docx4j.wml.ObjectFactory getFactory() {
		return null;
	}

	@Override
	public org.docx4j.dml.ObjectFactory getDmlFactory() {
		return null;
	}

	@Override
	public org.docx4j.dml.chart.ObjectFactory getChartFactory() {
		return null;
	}

	@Override
	public org.docx4j.dml.wordprocessingDrawing.ObjectFactory getDrawingFactory() {
		return null;
	}

	@Override
	public WordprocessingMLPackage getWordMLPackage() {
		return null;
	}

	@Override
	public AtomicLong getDrawingIndex() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	private P addBreak(P paragraph, STBrType type) {
		R run = getFactory().createR();
		Br br = getFactory().createBr();
		run.getContent().add(br);
		br.setType(type);
		paragraph.getContent().add(run);
		return paragraph;
	}

	private P addCellParagraph(Tc cell) {
		P p = getFactory().createP();
		cell.getContent().add(p);
		return p;
	}

	private PartName chartDependancyPartName(Relationship relationship) throws InvalidFormatException {
		String name = relationship.getTarget();
		return name.startsWith("..") ? new PartName("/word" + name.replace("..", "")) : new PartName("/word/charts/" + name);
	}

	private void cleanup(RangeFinder finder) throws Docx4JException {
		final List<CTRelId> refs = new LinkedList<>();
		final Map<BigInteger, BookmarkClean> bookmarks = new LinkedHashMap<>();

		// if (loadTypeFromDocument() != getType())
		// throw new TrickException("error.report.type.not.compatible", "Report and
		// analysis are not compatible");

		finder.getStarts().stream().filter(c -> c.getName().startsWith("_Tsr")).forEach(c -> bookmarks.put(c.getId(), new BookmarkClean(c)));
		finder.getEnds().stream().filter(c -> bookmarks.containsKey(c.getId())).forEach(c -> bookmarks.get(c.getId()).update(c));

		bookmarks.values().stream().forEach(c -> {
			if (c.hasContent()) {

				int startIndex = findIndexLoop(c.getStart()), endIndex = findIndexLoop(c.getEnd());

				if (!(startIndex == -1 || endIndex == -1)) {
					List<Object> contents = getDocument().getContent().subList(startIndex + (c.getStartParent() instanceof P ? 1 : 0),
							Math.min(endIndex + (c.getEndParent() instanceof P ? 0 : 1), getDocument().getContent().size()));

					contents.stream().filter(i -> XmlUtils.unwrap(i) instanceof P).map(i -> (P) XmlUtils.unwrap(i)).flatMap(p -> p.getContent().stream())
							.filter(r -> XmlUtils.unwrap(r) instanceof R).flatMap(r -> ((R) XmlUtils.unwrap(r)).getContent().stream())
							.filter(i -> XmlUtils.unwrap(i) instanceof Drawing).map(i -> (Drawing) XmlUtils.unwrap(i)).flatMap(d -> d.getAnchorOrInline().stream())
							.filter(i -> XmlUtils.unwrap(i) instanceof Inline).map(i -> (Inline) XmlUtils.unwrap(i))
							.filter(i -> !(i.getGraphic() == null || i.getGraphic().getGraphicData() == null || i.getGraphic().getGraphicData().getAny().isEmpty())
									&& i.getGraphic().getGraphicData().getUri().equals(HTTP_SCHEMAS_OPENXMLFORMATS_ORG_DRAWINGML_2006_CHART))
							.flatMap(i -> i.getGraphic().getGraphicData().getAny().stream()).filter(i -> XmlUtils.unwrap(i) instanceof CTRelId)
							.map(i -> (CTRelId) XmlUtils.unwrap(i)).filter(i -> i != null).forEach(ref -> refs.add(ref));

					contents.clear();
				}

			}

			if (c.getStartParent() != null)
				c.getStartParent().getContent().removeIf(ct -> XmlUtils.unwrap(ct).equals(c.getStart()));
			if (c.getEndParent() != null)
				c.getEndParent().getContent().removeIf(ct -> XmlUtils.unwrap(ct).equals(c.getEnd()));
		});

		if (!refs.isEmpty()) {
			Relationships mainRelationships = getWordMLPackage().getMainDocumentPart().getRelationshipsPart().getContents();
			for (CTRelId ctRelId : refs) {
				Relationship relationship = mainRelationships.getRelationship().stream().filter(p -> p.getId().equals(ctRelId.getId())).findAny().orElse(null);
				if (relationship == null)
					continue;
				Part chart = getWordMLPackage().getParts().get(new PartName("/word/" + relationship.getTarget()));
				if (chart == null)
					continue;

				List<Relationship> relationships = chart.getRelationshipsPart().getContents().getRelationship();
				while (!relationships.isEmpty()) {
					Part part = getWordMLPackage().getParts().get(chartDependancyPartName(relationships.remove(0)));
					if (part != null)
						part.remove();
				}
				mainRelationships.getRelationship().remove(relationship);
				chart.remove();
			}
		}

	}

	private <T extends SerContent> T createChart(CTAxDataSource cat, String reference, long index, String phaseLabel, T ser) {

		setupTitle(reference, index, phaseLabel, ser);

		ser.setCat(cat);

		ser.setVal(new CTNumDataSource());

		ser.getVal().setNumRef(new CTNumRef());

		ser.getVal().getNumRef().setNumCache(new CTNumData());

		return ser;
	}

	private P createGraphic(String name, String description, String refId) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = setStyle(getFactory().createP(), "FigurewithCaption");
		R run = getFactory().createR();
		run.setRPr(getFactory().createRPr());
		run.getRPr().setNoProof(getFactory().createBooleanDefaultTrue());
		Drawing drawing = getFactory().createDrawing();
		paragraph.getContent().add(run);
		run.getContent().add(getFactory().createRDrawing(drawing));
		Inline inline = getDrawingFactory().createInline();
		drawing.getAnchorOrInline().add(inline);
		inline.setDocPr(dmlFactory.createCTNonVisualDrawingProps());
		inline.getDocPr().setDescr(description);
		inline.getDocPr().setName(name);
		inline.getDocPr().setId(findDrawingId());
		inline.setGraphic(dmlFactory.createGraphic());
		inline.setExtent(dmlFactory.createCTPositiveSize2D());
		inline.getExtent().setCx(5486400);
		inline.getExtent().setCy(3200400);
		inline.setEffectExtent(drawingFactory.createCTEffectExtent());
		inline.getEffectExtent().setB(0);
		inline.getEffectExtent().setL(0);
		inline.getEffectExtent().setR(0);
		inline.getEffectExtent().setT(0);
		inline.setDistB(0L);
		inline.setDistL(0L);
		inline.setDistT(0L);
		inline.setDistR(0L);
		inline.setCNvGraphicFramePr(dmlFactory.createCTNonVisualGraphicFrameProperties());
		inline.getGraphic().setGraphicData(dmlFactory.createGraphicData());
		inline.getGraphic().getGraphicData().setUri(HTTP_SCHEMAS_OPENXMLFORMATS_ORG_DRAWINGML_2006_CHART);
		CTRelId relId = chartFactory.createCTRelId();
		relId.setId(refId);
		inline.getGraphic().getGraphicData().getAny().add(chartFactory.createChart(relId));
		return paragraph;
	}

	private String findChartId(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor(name);
		if (paragraph == null)
			return document.getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream()).filter(r -> r instanceof R)
					.flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream())
					.filter(i -> (i instanceof Inline) && ((Inline) i).getDocPr() != null && name.equals(((Inline) i).getDocPr().getDescr()))
					.flatMap(i -> ((Inline) i).getGraphic().getGraphicData().getAny().parallelStream()).map(v -> ((CTRelId) ((JAXBElement<?>) v).getValue()).getId()).findAny()
					.orElse(null);
		else
			return paragraph.getContent().parallelStream().filter(r -> r instanceof R).flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement)
					.map(d -> ((JAXBElement<?>) d).getValue()).filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream())
					.filter(i -> (i instanceof Inline)).flatMap(i -> ((Inline) i).getGraphic().getGraphicData().getAny().parallelStream())
					.map(v -> ((CTRelId) ((JAXBElement<?>) v).getValue()).getId()).findAny().orElse(null);
	}

	private synchronized Long findDrawingId() throws XPathBinderAssociationIsPartialException, JAXBException {
		if (drawingIndex == null) {
			drawingIndex = new AtomicLong(document.getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream())
					.filter(r -> r instanceof R).flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream()).filter(i -> i instanceof Inline)
					.mapToLong(i -> ((Inline) i).getDocPr().getId()).max().orElse(0));
		}
		return drawingIndex.incrementAndGet();
	}

	private int findIndexLoop(Object reference) {
		Object ctp = reference;
		while (true) {
			int index = findIndex(ctp);
			if (index != -1)
				return index;
			else if (ctp instanceof Child && !(ctp instanceof Document))
				ctp = ((Child) ctp).getParent();
			else
				return -1;
		}
	}

	private <T> T findLastAnignable(List<Object> elements, Class<T> assignable) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (assignable.isAssignableFrom(elements.get(i).getClass()))
				return assignable.cast(elements.get(i));
		}
		return null;
	}

	public static void mergeCell(Tr row, int begin, int size, String color) {
		int length = begin + size;
		for (int i = 0; i < length; i++) {
			Tc cell = (Tc) row.getContent().get(i);
			if (color != null)
				setColor(cell, color);
			if (i < begin)
				continue;
			else {
				if (cell.getTcPr() == null)
					cell.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (i == begin) {
					cell.getTcPr().setHMerge(Context.getWmlObjectFactory().createTcPrInnerHMerge());
					cell.getTcPr().getHMerge().setVal("restart");
				} else {
					cell.getTcPr().setHMerge(Context.getWmlObjectFactory().createTcPrInnerHMerge());
					cell.getTcPr().getHMerge().setVal("continue");
				}
			}
		}
	}

	public static Tc setColor(Tc tc, String color) {
		if (tc.getTcPr() == null)
			tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
		if (tc.getTcPr().getShd() == null)
			tc.getTcPr().setShd(Context.getWmlObjectFactory().createCTShd());
		tc.getTcPr().getShd().setFill(color);
		return tc;
	}

	public static void verticalMergeCell(List<?> rows, int col, int begin, int size, String color) {
		int length = begin + size;
		for (int i = 0; i < length; i++) {
			Tc cell = (Tc) ((Tr) rows.get(i)).getContent().get(col);
			if (color != null)
				setColor(cell, color);
			if (i < begin)
				continue;
			else {
				if (cell.getTcPr() == null)
					cell.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (i == begin) {
					cell.getTcPr().setVMerge(Context.getWmlObjectFactory().createTcPrInnerVMerge());
					cell.getTcPr().getVMerge().setVal("restart");
				} else {
					cell.getTcPr().setVMerge(Context.getWmlObjectFactory().createTcPrInnerVMerge());
					cell.getTcPr().getVMerge().setVal("continue");
				}
			}
		}
	}

}
