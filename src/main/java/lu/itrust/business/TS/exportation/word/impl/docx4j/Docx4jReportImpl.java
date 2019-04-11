/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.dml.CTSRgbColor;
import org.docx4j.dml.CTShapeProperties;
import org.docx4j.dml.CTSolidColorFillProperties;
import org.docx4j.dml.chart.CTAxDataSource;
import org.docx4j.dml.chart.CTBarSer;
import org.docx4j.dml.chart.CTNumData;
import org.docx4j.dml.chart.CTNumDataSource;
import org.docx4j.dml.chart.CTNumRef;
import org.docx4j.dml.chart.CTRelId;
import org.docx4j.dml.chart.CTSerTx;
import org.docx4j.dml.chart.CTStrData;
import org.docx4j.dml.chart.CTStrRef;
import org.docx4j.dml.chart.CTStrVal;
import org.docx4j.dml.chart.CTUnsignedInt;
import org.docx4j.dml.chart.SerContent;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.docProps.custom.Properties.Property;
import org.docx4j.finders.RangeFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsCustomPart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.DrawingML.Chart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart.AddPartBehaviour;
import org.docx4j.relationships.Relationship;
import org.docx4j.relationships.Relationships;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.R.Tab;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Style;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.Docx4jReport;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.BookmarkClean;
import lu.itrust.business.TS.helper.Distribution;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.helper.Task;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisReportSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;

/**
 * @author eomar
 *
 */
public class Docx4jReportImpl implements Docx4jReport {

	private File file;

	private String path;

	private Locale locale;

	private String darkColor;

	private String lightColor;

	private Analysis analysis;

	private String defaultColor;

	private String zeroCostColor;

	private ReportTemplate template;

	private AtomicLong drawingIndex;

	private String defaultTableStyle;

	private ValueFactory valueFactory;

	private String currentParagraphId;

	private DecimalFormat numberFormat;

	private MessageSource messageSource;

	private AtomicInteger bookmarkMaxId;

	private AtomicLong bookmarkCounter;

	private DecimalFormat kiloNumberFormat;

	private org.docx4j.wml.Document document;

	private Map<String, CTBookmark> bookmarks;

	private org.docx4j.wml.ObjectFactory factory;

	private org.docx4j.dml.ObjectFactory dmlFactory;

	private org.docx4j.dml.chart.ObjectFactory chartFactory;

	private Map<String, Style> styles = Collections.emptyMap();

	private final AtomicBoolean mutex = new AtomicBoolean(false);

	private org.docx4j.dml.wordprocessingDrawing.ObjectFactory drawingFactory;

	private org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage;

	/**
	 * 
	 */
	public Docx4jReportImpl() {
	}

	public Docx4jReportImpl(String path, MessageSource messageSource) {
		setPath(path);
		setMessageSource(messageSource);
	}

	public P addBreak(P paragraph, STBrType type) {
		final R run = getFactory().createR();
		final Br br = getFactory().createBr();
		run.getContent().add(br);
		br.setType(type);
		paragraph.getContent().add(run);
		return paragraph;
	}

	public R addCellNumber(Tc cell, String number) {
		return addCellNumber(cell, number, false);
	}

	public R addCellNumber(Tc cell, String number, boolean isBold) {
		P paragraph = cell.getContent().size() == 1 ? (P) cell.getContent().get(0) : addCellParagraph(cell);
		setStyle(paragraph, getCurrentParagraphId());
		setText(paragraph, number, createAlignment("right"));
		R r = (R) paragraph.getContent().get(0);
		if (isBold) {
			if (r.getRPr() == null)
				r.setRPr(factory.createRPr());
			r.getRPr().setB(factory.createBooleanDefaultTrue());
		}
		return r;
	}

	public P addCellParagraph(Tc cell) {
		final P p = getFactory().createP();
		cell.getContent().add(p);
		return p;
	}

	public P addCellParagraph(Tc cell, String text) {
		return addCellParagraph(cell, text, false);
	}

	public P addCellParagraph(Tc cell, String text, boolean add) {
		P p = (P) (!add && cell.getContent().size() == 1 ? cell.getContent().get(0) : addCellParagraph(cell));
		if (text == null)
			text = "";
		String[] texts = text.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			if (i > 0)
				p = addCellParagraph(cell);
			setStyle(p, getCurrentParagraphId());
			setText(p, texts[i], null);
		}
		return p;
	}

	public P addFigureCaption(String value) {
		P paragraph = setStyle(factory.createP(), "Caption");
		R run = setText(factory.createR(), getMessage("report.caption.figure.name", null, "Figure", locale));
		run.getContent().stream().filter(text -> text instanceof Text).map(text -> (Text) text).forEach(text -> text.setSpace("preserve"));
		paragraph.getContent().add(run);
		paragraph.getContent().add(createSpecialRun(STFldCharType.BEGIN));
		paragraph.getContent().add(setInstrText(factory.createR(), "SEQ Figure \\* ARABIC "));
		paragraph.getContent().add(createSpecialRun(STFldCharType.SEPARATE));
		paragraph.getContent().add(setText(factory.createR(), "1"));
		paragraph.getContent().add(createSpecialRun(STFldCharType.END));
		paragraph.getContent().add(setText(factory.createR(), (getLocale().getLanguage().equalsIgnoreCase("fr") ? "\u00A0:\u00A0" : ": ") + value));
		return paragraph;

	}

	public void addTab(P paragraph) {
		Tab tab = factory.createRTab();
		R run = factory.createR();
		run.getContent().add(tab);
		paragraph.getContent().add(run);
	}

	public P addTableCaption(String value) {
		P paragraph = setStyle(factory.createP(), "Caption");
		R run = setText(factory.createR(), getMessage("report.caption.table.name", null, "Table", locale));
		run.getContent().stream().filter(text -> text instanceof Text).map(text -> (Text) text).forEach(text -> text.setSpace("preserve"));
		paragraph.getContent().add(run);
		paragraph.getContent().add(createSpecialRun(STFldCharType.BEGIN));
		paragraph.getContent().add(setInstrText(factory.createR(), "SEQ Table \\* ARABIC "));
		paragraph.getContent().add(createSpecialRun(STFldCharType.SEPARATE));
		paragraph.getContent().add(setText(factory.createR(), "1"));
		paragraph.getContent().add(createSpecialRun(STFldCharType.END));
		paragraph.getContent().add(setText(factory.createR(), (getLocale().getLanguage().equalsIgnoreCase("fr") ? "\u00A0:\u00A0" : ": ") + value));
		return paragraph;
	}

	public P addText(P paragraph, String value) {
		R run = factory.createR();
		Text text = factory.createText();
		run.getContent().add(text);
		text.setValue(value);
		paragraph.getContent().add(run);
		return paragraph;
	}

	public PartName chartDependancyPartName(Relationship relationship) throws InvalidFormatException {
		final String name = relationship.getTarget();
		return name.startsWith("..") ? new PartName("/word" + name.replace("..", "")) : new PartName("/word/charts/" + name);
	}

	public void cleanup(RangeFinder finder) throws Docx4JException {
		final List<CTRelId> refs = new LinkedList<>();
		final Map<BigInteger, BookmarkClean> bookmarks = new LinkedHashMap<>();

		finder.getStarts().stream().filter(c -> c.getName().startsWith("_Tsr")).forEach(c -> bookmarks.put(c.getId(), new BookmarkClean(c)));
		finder.getEnds().stream().filter(c -> bookmarks.containsKey(c.getId())).forEach(c -> bookmarks.get(c.getId()).update(c));

		bookmarks.values().stream().forEach(c -> {
			if (c.hasContent()) {

				int startIndex = findIndexLoop(c.getStart()), endIndex = findIndexLoop(c.getEnd());

				if (!(startIndex == -1 || endIndex == -1)) {
					final List<Object> contents = getDocument().getContent().subList(startIndex + (c.getStartParent() instanceof P ? 1 : 0),
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
			final Relationships mainRelationships = getWordMLPackage().getMainDocumentPart().getRelationshipsPart().getContents();
			for (CTRelId ctRelId : refs) {
				final Relationship relationship = mainRelationships.getRelationship().stream().filter(p -> p.getId().equals(ctRelId.getId())).findAny().orElse(null);
				if (relationship == null)
					continue;
				final Part chart = getWordMLPackage().getParts().get(new PartName("/word/" + relationship.getTarget()));
				if (chart == null)
					continue;

				final List<Relationship> relationships = chart.getRelationshipsPart().getContents().getRelationship();
				while (!relationships.isEmpty()) {
					final Part part = getWordMLPackage().getParts().get(chartDependancyPartName(relationships.remove(0)));
					if (part != null)
						part.remove();
				}
				mainRelationships.getRelationship().remove(relationship);
				chart.remove();
			}
		}

	}

	public ClonePartResult cloneChart(Chart part, String name, String description) throws Docx4JException, InvalidFormatException, JAXBException {
		final Part copy = PartClone.clone(part, null);
		final Relationship relationship = getWordMLPackage().getMainDocumentPart().addTargetPart(copy, AddPartBehaviour.RENAME_IF_NAME_EXISTS);
		part.getRelationshipsPart().getContents().getRelationship().stream().sorted((r1, r2) -> NaturalOrderComparator.compareTo(r1.getId(), r2.getId())).forEach(re -> {
			try {
				if (re.getTarget().startsWith(".."))
					copy.addTargetPart(PartClone.clone(getWordMLPackage().getParts().get(new PartName("/word" + re.getTarget().replace("..", ""))), null),
							AddPartBehaviour.RENAME_IF_NAME_EXISTS);
				else
					copy.addTargetPart(PartClone.clone(getWordMLPackage().getParts().get(new PartName("/word/charts/" + re.getTarget())), null),
							AddPartBehaviour.RENAME_IF_NAME_EXISTS);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return new ClonePartResult(copy, relationship, createGraphic(name, description, relationship.getId()));
	}

	@Override
	public void close() {
	}

	public int computeSommuryLength(final List<String> collectionNames) {
		if (collectionNames.contains(Constant.STANDARD_27001)) {
			if (collectionNames.contains(Constant.STANDARD_27002))
				return 4 + collectionNames.size() - 2;
			return 2 + (collectionNames.size() - 1);
		} else if (collectionNames.contains(Constant.STANDARD_27002))
			return 2 + (collectionNames.size() - 1);
		else
			return collectionNames.size();
	}
	
	public List<String> getStandardNames() {
		return analysis.findStandards().stream()
				.map(c -> c.is(Constant.STANDARD_27001) ? Constant.STANDARD_27001 : c.is(Constant.STANDARD_27002) ? Constant.STANDARD_27002 : c.getLabel())
				.sorted(NaturalOrderComparator::compareTo).collect(Collectors.toList());
	}

	public TextAlignment createAlignment(String value) {
		final TextAlignment alignment = factory.createPPrBaseTextAlignment();
		alignment.setVal(value);
		return alignment;
	}

	public <T extends SerContent> T createChart(CTAxDataSource cat, String reference, long index, String phaseLabel, T ser) {

		setupTitle(reference, index, phaseLabel, ser);

		ser.setCat(cat);

		ser.setVal(new CTNumDataSource());

		ser.getVal().setNumRef(new CTNumRef());

		ser.getVal().getNumRef().setNumCache(new CTNumData());

		return ser;
	}

	public <T extends SerContent> T createChart(String reference, long index, String title, T ser) {

		CTAxDataSource ctAxDataSource = new CTAxDataSource();

		ctAxDataSource.setStrRef(new CTStrRef());

		ctAxDataSource.getStrRef().setStrCache(new CTStrData());

		return createChart(ctAxDataSource, reference, index, title, ser);

	}

	public P createGraphic(String name, String description, String refId) throws XPathBinderAssociationIsPartialException, JAXBException {
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

	public Property createProperty(String name) throws Docx4JException {
		org.docx4j.docProps.custom.ObjectFactory factory = new org.docx4j.docProps.custom.ObjectFactory();
		Property property = factory.createPropertiesProperty();
		property.setName(name);
		getWordMLPackage().getDocPropsCustomPart();
		property.setFmtid(DocPropsCustomPart.fmtidValLpwstr);
		property.setPid(getWordMLPackage().getDocPropsCustomPart().getNextPid());
		getWordMLPackage().getDocPropsCustomPart().getContents().getProperty().add(property);
		return property;
	}

	public Property createProperty(String name, boolean resued) throws Docx4JException {
		Property property = getWordMLPackage().getDocPropsCustomPart().getProperty(name);
		if (!(property == null || resued)) {
			getWordMLPackage().getDocPropsCustomPart().getContents().getProperty().remove(property);
			property = createProperty(name);
		} else if (property == null)
			property = createProperty(name);
		return property;
	}

	public R createSpecialRun(STFldCharType type) {
		R run = getFactory().createR();
		FldChar fldChar = getFactory().createFldChar();
		fldChar.setFldCharType(type);
		run.getContent().add(fldChar);
		return run;
	}

	public Tbl createTable(String styleId, int rows, int cols) {
		Tbl table = TblFactory.createTable(rows, cols, 1);
		Style value = getStyles().get(styleId);
		if (value != null)
			table.getTblPr().getTblStyle().setVal(value.getName().getVal());
		else
			table.getTblPr().getTblStyle().setVal(styleId);
		if (table.getTblPr().getJc() == null)
			table.getTblPr().setJc(getFactory().createJc());
		table.getTblPr().getJc().setVal(JcEnumeration.CENTER);
		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(getFactory().createTblWidth());
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		return table;
	}

	public CTVerticalJc createVerticalAlignment(STVerticalJc alignment) {
		CTVerticalJc ctVerticalJc = getFactory().createCTVerticalJc();
		ctVerticalJc.setVal(alignment);
		return ctVerticalJc;
	}

	public List<Part> duplicateChart(int size, String chartName, String name) throws JAXBException, Docx4JException {
		int count = 1;
		if (size > Constant.CHAR_SINGLE_CONTENT_MAX_SIZE)
			count = Distribution.Distribut(size, Constant.CHAR_MULTI_CONTENT_SIZE, Constant.CHAR_MULTI_CONTENT_MAX_SIZE).getDivisor();
		final Part part = findChart(chartName);
		if (part == null)
			return Collections.emptyList();
		final List<Part> parts = new ArrayList<>(count);
		parts.add(part);
		if (count > 1) {
			List<Object> contents = new LinkedList<>();
			for (int i = 1; i < count; i++) {
				ClonePartResult result = cloneChart((Chart) part, name + i, name + i);
				contents.add(result.getP());
				parts.add(result.getPart());
			}
			insertAllAfter(findTableAnchor(chartName), contents);
		}
		return parts;
	}

	@Override
	public void export(ReportTemplate template, Task task, Analysis analysis, ServiceTaskFeedback serviceTaskFeedback) {
		internalReportExport(task, analysis, template, serviceTaskFeedback);
	}

	public Part findChart(CTBookmark bookmark) throws InvalidFormatException {
		return findChart(findP(bookmark));
	}

	public Part findChart(P p) throws InvalidFormatException {
		if (p == null)
			return null;
		final String id = findChartId(p);
		if (id == null)
			return null;
		return findChartById(id);
	}

	public Part findChart(String name) throws InvalidFormatException, XPathBinderAssociationIsPartialException, JAXBException {
		final String id = findChartId(name);
		if (id == null)
			return null;
		return findChartById(id);
	}

	public Part findChartById(final String id) throws InvalidFormatException {
		final Relationship relationship = getWordMLPackage().getMainDocumentPart().getRelationshipsPart().getRelationships().getRelationship().parallelStream()
				.filter(part -> part.getId().equalsIgnoreCase(id)).findAny().orElse(null);
		if (relationship == null)
			return null;
		return getWordMLPackage().getParts().get(new PartName("/word/" + relationship.getTarget()));
	}

	public String findChartId(P p) {
		return p.getContent().parallelStream().filter(r -> r instanceof R).flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement)
				.map(d -> ((JAXBElement<?>) d).getValue()).filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream())
				.filter(i -> (i instanceof Inline)).flatMap(i -> ((Inline) i).getGraphic().getGraphicData().getAny().parallelStream())
				.map(v -> ((CTRelId) ((JAXBElement<?>) v).getValue()).getId()).findAny().orElse(null);
	}

	public String findChartId(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor(name);
		if (paragraph == null)
			return getDocument().getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream()).filter(r -> r instanceof R)
					.flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream())
					.filter(i -> (i instanceof Inline) && ((Inline) i).getDocPr() != null && name.equalsIgnoreCase(((Inline) i).getDocPr().getDescr()))
					.flatMap(i -> ((Inline) i).getGraphic().getGraphicData().getAny().parallelStream()).map(v -> ((CTRelId) ((JAXBElement<?>) v).getValue()).getId()).findAny()
					.orElse(null);
		else
			return findChartId(paragraph);
	}

	public synchronized Long findDrawingId() throws XPathBinderAssociationIsPartialException, JAXBException {
		if (getDrawingIndex() == null) {
			setDrawingIndex(new AtomicLong(getDocument().getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream())
					.filter(r -> r instanceof R).flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream()).filter(i -> i instanceof Inline)
					.mapToLong(i -> ((Inline) i).getDocPr().getId()).max().orElse(0)));
		}
		return getDrawingIndex().incrementAndGet();
	}

	public int findIndex(Object reference) {
		int index = -1;
		if (reference != null) {
			index = getDocument().getContent().indexOf(reference);
			if (index == -1 && !(reference instanceof P)) {
				for (int i = 0; i < getDocument().getContent().size(); i++) {
					if (XmlUtils.unwrap(reference).equals(XmlUtils.unwrap(getDocument().getContent().get(i))))
						return i;
				}
			}
		}
		return index;
	}

	public int findIndexLoop(Object reference) {
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

	public <T> T findLastAnignable(List<Object> elements, Class<T> assignable) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (assignable.isAssignableFrom(elements.get(i).getClass()))
				return assignable.cast(elements.get(i));
		}
		return null;
	}

	public Object findNext(Object reference) {
		int index = findIndex(reference);
		if (index < 0 || index >= (getDocument().getContent().size() - 1))
			return null;
		return getDocument().getContent().get(index + 1);
	}

	public P findNext(P p) {
		Object reference = p;
		while (true) {
			Object element = findNext((Object) reference);
			if (element == null || element instanceof P)
				return (P) element;
			else
				reference = element;
		}
	}

	public P findNextP(int index) {
		for (int i = index + 1; i < getDocument().getContent().size(); i++) {
			Object object = getDocument().getContent().get(i);
			if (object instanceof P)
				return (P) object;
		}
		return null;
	}

	public P findP(CTBookmark bookmark) {
		Child element = bookmark;
		while (element.getParent() != null) {
			Object parent = element.getParent();
			if (parent instanceof P)
				return (P) parent;
			if (parent instanceof Child)
				element = (Child) parent;
			else
				break;
		}
		return null;

	}

	public Object findPrevious(Object reference) {
		int index = findIndex(reference);
		if (index < 1)
			return null;
		return getDocument().getContent().get(index - 1);
	}

	public P findPrevious(P p) {
		Object reference = p;
		while (true) {
			Object element = findPrevious((Object) reference);
			if (element == null || element instanceof P)
				return (P) element;
			else
				reference = element;
		}
	}

	public P findPreviousP(int index) {
		for (int i = Math.min(index, getDocument().getContent().size()) - 1; i >= 0; i--) {
			Object object = getDocument().getContent().get(i);
			if (object instanceof P)
				return (P) object;
		}
		return null;
	}

	public P findTableAnchor(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		return (P) getDocument().getContent().parallelStream()
				.filter(p -> (p instanceof P) && ((P) p).getContent().parallelStream().anyMatch(b -> (b instanceof JAXBElement)
						&& ((JAXBElement<?>) b).getValue() instanceof CTBookmark && ((CTBookmark) ((JAXBElement<?>) b).getValue()).getName().equalsIgnoreCase(name)))
				.findAny().orElse(null);
	}

	public String formatLikelihood(String likelihood) {
		try {
			return getKiloNumberFormat().format(Double.parseDouble(likelihood));
		} catch (Exception e) {
			return likelihood;
		}
	}

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

	@Override
	public AtomicLong getBookmarkCounter() {
		return bookmarkCounter;
	}

	@Override
	public AtomicInteger getBookmarkMaxId() {
		return bookmarkMaxId;
	}

	@Override
	public org.docx4j.dml.chart.ObjectFactory getChartFactory() {
		return chartFactory;
	}

	public CTSRgbColor getColor(String color) {
		CTSRgbColor rgbColor = new CTSRgbColor();
		rgbColor.setVal(color.substring(1));
		return rgbColor;
	}

	@Override
	public String getCurrentParagraphId() {
		return currentParagraphId;
	}

	@Override
	public String getDarkColor() {
		return darkColor;
	}

	@Override
	public String getDefaultColor() {
		return defaultColor;
	}

	@Override
	public Object getDefaultTableStyle() {
		return styles == null || defaultTableStyle == null ? defaultTableStyle : styles.get(defaultTableStyle);
	}

	public String getDisplayName(AssetType type) {
		return getMessage("label.asset_type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	public String getDisplayName(ScenarioType type) {
		return getMessage("label.scenario.type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	@Override
	public org.docx4j.dml.ObjectFactory getDmlFactory() {
		return dmlFactory;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public org.docx4j.dml.wordprocessingDrawing.ObjectFactory getDrawingFactory() {
		return drawingFactory;
	}

	@Override
	public AtomicLong getDrawingIndex() {
		return drawingIndex;
	}

	@Override
	public org.docx4j.wml.ObjectFactory getFactory() {
		return factory;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public DecimalFormat getKiloNumberFormat() {
		return kiloNumberFormat;
	}

	@Override
	public String getLightColor() {
		return lightColor;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public MessageSource getMessageSource() {
		return messageSource;
	}

	@Override
	public DecimalFormat getNumberFormat() {
		return numberFormat;
	}

	@Override
	public String getPath() {
		return path;
	}

	public TblPr getTableStyle(String id) {
		Style style = getStyles().get(id);
		return style == null ? null : (TblPr) style.getTblPr();
	}

	@Override
	public ReportTemplate getTemplate() {
		return template;
	}

	@Override
	public ValueFactory getValueFactory() {
		return valueFactory;
	}

	@Override
	public WordprocessingMLPackage getWordMLPackage() {
		return wordMLPackage;
	}

	@Override
	public String getZeroCostColor() {
		return this.zeroCostColor;
	}

	public boolean insertAfter(Object reference, Object element) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		if (reference instanceof P) {
			P next = findNextP(index);
			if (next != null)
				putCustomerContentMarker((P) reference, next);
		}
		getDocument().getContent().add(index + 1, element);
		return true;
	}

	public boolean insertAllAfter(Object reference, List<Object> elements) {
		int index = findIndex(reference);
		if (index == -1 || elements.isEmpty())
			return false;

		if ((reference instanceof P)) {
			ContentAccessor next = findNextP(index);
			if (next == null && index == getDocument().getContent().size() - 1)
				next = findLastAnignable(elements, ContentAccessor.class);
			if (next != null)
				putCustomerContentMarker((P) reference, next);
		}

		getDocument().getContent().addAll(index + 1, elements);
		return true;
	}

	public boolean insertAllBefore(Object reference, List<Object> elements) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		if (!elements.isEmpty() && (reference instanceof P)) {
			P previous = findPreviousP(index);
			if (previous != null)
				putCustomerContentMarker(previous, (P) reference);
		}
		getDocument().getContent().addAll(index, elements);
		return true;
	}

	public boolean insertBefore(Object reference, Object element) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		if (reference instanceof P) {
			P previous = findPreviousP(index);
			if (previous != null)
				putCustomerContentMarker(previous, (P) reference);
		}
		getDocument().getContent().add(index, element);
		return true;
	}

	public void putCustomerContentMarker(ContentAccessor begin, ContentAccessor end) {
		BigInteger id = BigInteger.valueOf(getBookmarkMaxId().incrementAndGet());
		CTMarkupRange markupRange = getFactory().createCTMarkupRange();
		CTBookmark bookmark = getFactory().createCTBookmark();
		markupRange.setId(id);
		bookmark.setId(id);
		bookmark.setName("_Tsr" + getBookmarkCounter().incrementAndGet());
		JAXBElement<CTMarkupRange> bookmarkEnd = getFactory().createBodyBookmarkEnd(markupRange);
		JAXBElement<CTBookmark> bookmarkStart = getFactory().createBodyBookmarkStart(bookmark);
		begin.getContent().add(bookmarkStart);
		end.getContent().add(bookmarkEnd);
	}

	public boolean replace(Object reference, Object element) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		getDocument().getContent().set(index, element);
		return true;
	}

	public P setAlignment(P paragraph, TextAlignment alignment) {
		if (paragraph.getPPr() == null)
			paragraph.setPPr(getFactory().createPPr());
		if (paragraph.getParent() instanceof Tc) {
			if (paragraph.getPPr().getJc() == null)
				paragraph.getPPr().setJc(getFactory().createJc());
			paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
		} else
			paragraph.getPPr().setTextAlignment(alignment);
		return paragraph;
	}

	public Tc setAlignment(Tc cell, TextAlignment alignment) {
		cell.getContent().parallelStream().filter(p -> p instanceof P).forEach(p -> setAlignment((P) p, alignment));
		return cell;
	}

	/**
	 * @param analysis the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * @param bookmarkCounter the bookmarkCounter to set
	 */
	public void setBookmarkCounter(AtomicLong bookmarkCounter) {
		this.bookmarkCounter = bookmarkCounter;
	}

	/**
	 * @param bookmarkMaxId the bookmarkMaxId to set
	 */
	public void setBookmarkMaxId(AtomicInteger bookmarkMaxId) {
		this.bookmarkMaxId = bookmarkMaxId;
	}

	public void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	public void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p).forEach(p -> setStyle(p, getCurrentParagraphId()));
		setText(paragraph, text, alignment);
	}

	/**
	 * @param chartFactory the chartFactory to set
	 */
	public void setChartFactory(org.docx4j.dml.chart.ObjectFactory chartFactory) {
		this.chartFactory = chartFactory;
	}

	public void setColor(CTBarSer ser, String color) {
		if (ser.getSpPr() == null) {
			ser.setSpPr(new CTShapeProperties());
			ser.getSpPr().setSolidFill(new CTSolidColorFillProperties());
			ser.getSpPr().getSolidFill().setSrgbClr(getColor(color));
		}
	}

	/**
	 * @param currentParagraphId the currentParagraphId to set
	 */
	public void setCurrentParagraphId(String currentParagraphId) {
		this.currentParagraphId = currentParagraphId;
	}

	public void setCustomProperty(String name, Object value) throws Docx4JException {
		if (value instanceof Number) {
			if (value instanceof Double)
				createProperty(name, false).setR8(Double.isNaN((double) value) ? 0 : ((Number) value).doubleValue());
			else
				createProperty(name, false).setI4(((Number) value).intValue());
		} else if (value instanceof Boolean)
			createProperty(name, false).setBool((Boolean) value);
		else
			createProperty(name, false).setLpwstr(value.toString());
	}

	/**
	 * @param darkColor the darkColor to set
	 */
	public void setDarkColor(String darkColor) {
		this.darkColor = darkColor;
	}

	/**
	 * @param defaultColor the defaultColor to set
	 */
	public void setDefaultColor(String defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * @param dmlFactory the dmlFactory to set
	 */
	public void setDmlFactory(org.docx4j.dml.ObjectFactory dmlFactory) {
		this.dmlFactory = dmlFactory;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(org.docx4j.wml.Document document) {
		this.document = document;
	}

	/**
	 * @param drawingFactory the drawingFactory to set
	 */
	public void setDrawingFactory(org.docx4j.dml.wordprocessingDrawing.ObjectFactory drawingFactory) {
		this.drawingFactory = drawingFactory;
	}

	/**
	 * @param drawingIndex the drawingIndex to set
	 */
	public void setDrawingIndex(AtomicLong drawingIndex) {
		this.drawingIndex = drawingIndex;
	}

	/**
	 * @param factory the factory to set
	 */
	public void setFactory(org.docx4j.wml.ObjectFactory factory) {
		this.factory = factory;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	public R setInstrText(R r, String content) {
		Text text = getFactory().createText();
		JAXBElement<Text> textWrapped = getFactory().createRInstrText(text);
		r.getContent().add(textWrapped);
		text.setValue(content);
		text.setSpace("preserve");
		return r;
	}

	/**
	 * @param kiloNumberFormat the kiloNumberFormat to set
	 */
	public void setKiloNumberFormat(DecimalFormat kiloNumberFormat) {
		if (kiloNumberFormat != null)
			kiloNumberFormat.setMaximumFractionDigits(1);
		this.kiloNumberFormat = kiloNumberFormat;
	}

	/**
	 * @param lightColor the lightColor to set
	 */
	public void setLightColor(String lightColor) {
		this.lightColor = lightColor;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param numberFormat the numberFormat to set
	 */
	public void setNumberFormat(DecimalFormat numberFormat) {
		if (numberFormat != null)
			numberFormat.setMaximumFractionDigits(0);
		this.numberFormat = numberFormat;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public void setRepeatHeader(Tr row) {
		if (row.getTrPr() == null)
			row.setTrPr(getFactory().createTrPr());
		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(getFactory().createCTTrPrBaseTblHeader(getFactory().createBooleanDefaultTrue()));
	}

	public P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(getFactory().createPPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(getFactory().createPPrBasePStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(ReportTemplate template) {
		this.template = template;
	}

	public P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

	public P setText(P paragraph, String content, boolean bold) {
		R r = getFactory().createR();
		Text text = getFactory().createText();
		r.getContent().add(text);
		text.setValue(content);
		if (r.getRPr() == null)
			r.setRPr(getFactory().createRPr());
		r.getRPr().setB(getFactory().createBooleanDefaultTrue());
		paragraph.getContent().add(r);
		return paragraph;
	}

	public P setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, getCurrentParagraphId());
			if (paragraph.getParent() instanceof Tc) {
				if (paragraph.getPPr().getJc() == null)
					paragraph.getPPr().setJc(getFactory().createJc());
				paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
			} else
				paragraph.getPPr().setTextAlignment(alignment);
		}
		paragraph.getContent().removeIf(r -> r instanceof R);
		R r = getFactory().createR();
		Text text = getFactory().createText();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);
		return paragraph;

	}

	public R setText(R r, String value) {
		Text text = getFactory().createText();
		text.setValue(value == null ? "" : value);
		r.getContent().add(text);
		return r;
	}

	public <T extends SerContent> void setupTitle(String reference, long index, String title, T ser) {
		ser.setOrder(new CTUnsignedInt());
		ser.getOrder().setVal(index);
		ser.setIdx(new CTUnsignedInt());
		ser.getIdx().setVal(index);
		ser.setTx(new CTSerTx());
		ser.getTx().setStrRef(new CTStrRef());
		ser.getTx().getStrRef().setStrCache(new CTStrData());
		ser.getTx().getStrRef().setF(reference);
		CTStrVal valTitle = new CTStrVal();
		valTitle.setIdx(0);
		valTitle.setV(title);
		ser.getTx().getStrRef().getStrCache().getPt().add(valTitle);
		ser.getTx().getStrRef().getStrCache().setPtCount(new CTUnsignedInt());
		ser.getTx().getStrRef().getStrCache().getPtCount().setVal(ser.getTx().getStrRef().getStrCache().getPt().size());
	}

	/**
	 * @param valueFactory the valueFactory to set
	 */
	public void setValueFactory(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	public void setVerticalAlignment(Tc cell, CTVerticalJc alignment) {
		if (cell.getTcPr() == null)
			cell.setTcPr(getFactory().createTcPr());
		cell.getTcPr().setVAlign(alignment);
	}

	/**
	 * @param wordMLPackage the wordMLPackage to set
	 */
	public void setWordMLPackage(org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage) {
		this.wordMLPackage = wordMLPackage;
	}

	/**
	 * @param zeroCostColor
	 */
	public void setZeroCostColor(String zeroCostColor) {
		this.zeroCostColor = zeroCostColor;
	}

	protected Map<String, CTBookmark> getBookmarks() {
		return bookmarks;
	}

	protected String getPropertyString(String name) {
		Property property = wordMLPackage.getDocPropsCustomPart().getProperty(name);
		return property == null ? null : property.getLpwstr();
	}

	public String getValueOrEmpty(String value) {
		return value == null ? "" : value;
	}

	protected boolean initialise() throws Docx4JException, IOException {
		if (getTemplate() != null) {
			setFile(new File(String.format("%s/WEB-INF/tmp/STA_%d_%s_v%s.docx", getPath(), System.nanoTime(), getAnalysis().getLabel().replaceAll("/|-|:|.|&", "_"),
					getAnalysis().getVersion())));
			if (!getFile().exists())
				getFile().createNewFile();
			Files.write(getFile().toPath(), getTemplate().getFile());
		}

		if (!getFile().exists())
			throw new TrickException("error.export.report.no.template", "No template file");

		setFactory(Context.getWmlObjectFactory());

		setDmlFactory(new org.docx4j.dml.ObjectFactory());

		setChartFactory(new org.docx4j.dml.chart.ObjectFactory());

		setWordMLPackage(WordprocessingMLPackage.load(getFile()));

		setValueFactory(new ValueFactory(getAnalysis().getParameters()));

		setDocument(getWordMLPackage().getMainDocumentPart().getContents());

		setDrawingFactory(new org.docx4j.dml.wordprocessingDrawing.ObjectFactory());

		if (getWordMLPackage().getDocPropsCustomPart() == null)
			getWordMLPackage().addDocPropsCustomPart();

		final RangeFinder finder = new RangeFinder("CTBookmark", "CTMarkupRange");

		new TraversalUtil(getWordMLPackage().getMainDocumentPart().getContent(), finder);

		if (getTemplate() == null)
			cleanup(finder);

		setDefaultTableStyle("TableTS");

		final AnalysisType type = getType();

		setBookmarkCounter(new AtomicLong(System.currentTimeMillis()));

		setNumberFormat((DecimalFormat) DecimalFormat.getInstance(Locale.FRENCH));

		setDarkColor(getAnalysis().findSetting(AnalysisReportSetting.DARK_COLOR));

		setLightColor(getAnalysis().findSetting(AnalysisReportSetting.LIGHT_COLOR));

		setKiloNumberFormat((DecimalFormat) DecimalFormat.getInstance(Locale.FRENCH));

		setDefaultColor(getAnalysis().findSetting(AnalysisReportSetting.DEFAULT_COLOR));

		setZeroCostColor(getAnalysis().findSetting(AnalysisReportSetting.ZERO_COST_COLOR));

		setLocale(getAnalysis().getLanguage().getAlpha2().equalsIgnoreCase(Locale.FRENCH.getLanguage()) ? Locale.FRENCH : Locale.ENGLISH);

		setBookmarkMaxId(new AtomicInteger(finder.getStarts().parallelStream().mapToInt(p -> p.getId().intValue()).max().orElse(1)));

		setStyles(getWordMLPackage().getMainDocumentPart().getStyleDefinitionsPart().getContents().getStyle().parallelStream()
				.collect(Collectors.toMap(Style::getStyleId, Function.identity())));

		setBookmarks(finder.getStarts().stream().filter(c -> internalName(c.getName(), type).startsWith("ts_"))
				.collect(Collectors.toMap(c -> internalName(c.getName(), type), Function.identity(), (c1, c2) -> c1, LinkedHashMap::new)));
		return false;
	}

	protected synchronized void internalReportExport(final Task task, final Analysis analysis, final ReportTemplate template, final ServiceTaskFeedback serviceTaskFeedback) {
		try {
			if (getMutex().get())
				throw new TrickException("error.export.already.start", "Export is already started!");
			getMutex().set(true);
			setAnalysis(analysis);
			setTemplate(template);
			initialise();
			final int[] progressing = { 0, getBookmarks().size() };
			getBookmarks().forEach((key, value) -> {
				serviceTaskFeedback.send(task.getId(), new MessageHandler("info.exporting.report.task." + extractName(key), null, task.update(progressing[0]++, progressing[1])));
				DocxChainFactory.build(new Docx4jData(key, value, this));
			});
			updateProperties();
			getWordMLPackage().save(getFile());
		} catch (Docx4JException | IOException e) {
			throw new TrickException("error.export.internal", "An error occurred while exporting word report!", e);
		}
	}

	protected void setBookmarks(Map<String, CTBookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	protected void setDefaultTableStyle(String defaultTableStyle) {
		this.defaultTableStyle = defaultTableStyle;
	}

	protected void updateProperties() throws Docx4JException {

		final String currentTime = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT).replaceAll("\\.\\d*", "");

		for (AnalysisStandard analysisStandard : getAnalysis().getAnalysisStandards()) {
			final long count = analysisStandard.getMeasures().stream().filter(m -> m.getStatus().equalsIgnoreCase(Constant.MEASURE_STATUS_NOT_APPLICABLE)).count();
			if (count == 0)
				continue;
			if (analysisStandard.getStandard().is(Constant.STANDARD_27001))
				setCustomProperty(NA_MEASURES_27001, count);
			else if (analysisStandard.getStandard().is(Constant.STANDARD_27002))
				setCustomProperty(NA_MEASURES_27002, count);
			else
				setCustomProperty(analysisStandard.getStandard().getLabel().toUpperCase() + NA_MEASURES, count);
		}

		setCustomProperty(MAX_IMPL, getAnalysis().getSimpleParameters().stream().filter(p -> p.getDescription().equals(Constant.SOA_THRESHOLD)).map(p -> p.getValue().doubleValue())
				.findAny().orElse(0D));

		setCustomProperty(EXTERNAL_WL_VAL, getAnalysis().getSimpleParameters().stream().filter(p -> p.getDescription().equals(Constant.PARAMETER_EXTERNAL_SETUP_RATE))
				.map(p -> p.getValue().doubleValue()).findAny().orElse(0D));

		setCustomProperty(INTERNAL_WL_VAL, getAnalysis().getSimpleParameters().stream().filter(p -> p.getDescription().equals(Constant.PARAMETER_INTERNAL_SETUP_RATE))
				.map(p -> p.getValue().doubleValue()).findAny().orElse(0D));

		setCustomProperty(NUMBER_MEASURES_ALL_PHASES,
				getAnalysis().getAnalysisStandards().stream().flatMap(e -> e.getMeasures().stream()).filter(m -> m.getMeasureDescription().isComputable()
						&& !(m.getImplementationRateValue(getValueFactory()) >= 100 || Constant.MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(m.getStatus()))).count());

		setCustomProperty(CLIENT_NAME, getAnalysis().getCustomer().getOrganisation());

		getWordMLPackage().getDocPropsCorePart().getContents().setLastPrinted(null);

		getWordMLPackage().getDocPropsCorePart().getContents().getCreated().getContent().clear();

		getWordMLPackage().getDocPropsCorePart().getContents().getCreator().getContent().clear();

		getWordMLPackage().getDocPropsCorePart().getContents().getModified().getContent().clear();

		getWordMLPackage().getDocPropsExtendedPart().getContents().setCompany(getAnalysis().getCustomer().getOrganisation());

		getWordMLPackage().getDocPropsExtendedPart().getContents().setManager(getAnalysis().getCustomer().getContactPerson());

		getWordMLPackage().getDocPropsCorePart().getContents().getCreator().getContent()
				.add(String.format("%s %s", getAnalysis().getOwner().getFirstName(), getAnalysis().getOwner().getLastName()));

		getWordMLPackage().getDocPropsCorePart().getContents().getCreated().getContent().add(currentTime);

		getWordMLPackage().getDocPropsCorePart().getContents().getModified().getContent().add(currentTime);

		getWordMLPackage().getDocPropsCorePart().getContents().setLastModifiedBy(getMessage("report.export.from.ts", "Exported from TRICK Service"));

		getWordMLPackage().getMainDocumentPart().getDocumentSettingsPart().getContents().setUpdateFields(getFactory().createBooleanDefaultTrue());

		updateALEAndAssetTypeProperties();

		if (template != null)
			setCustomProperty(PROPERTY_REPORT_TYPE, template.getType());
	}

	private void updateALEAndAssetTypeProperties() throws Docx4JException {
		if (!(getAnalysis().isQuantitative()
				|| getBookmarks().keySet().parallelStream().anyMatch(e -> e.equalsIgnoreCase("ts_qt_asset") || e.equalsIgnoreCase("ts_qt_assessment"))))
			return;
		updateAssetValueProperties();
		updateAssessmentALEProperties();
	}

	private void updateAssessmentALEProperties() throws Docx4JException {
		final List<Assessment> assessments = analysis.findSelectedAssessments();

		final Map<String, Double> aleByAssetTypes = new LinkedHashMap<>();

		double totalale = 0;

		for (Assessment assessment : assessments) {
			final String assetType = assessment.getAsset().getAssetType().getName().toUpperCase();
			aleByAssetTypes.put(assetType, aleByAssetTypes.getOrDefault(assetType, 0D) + assessment.getALE());
			totalale += assessment.getALE();
		}

		for (Entry<String, Double> entry : aleByAssetTypes.entrySet())
			setCustomProperty(entry.getKey() + "_Rsk", (long) (entry.getValue() * 0.001));

		setCustomProperty("TOTAL_ALE_VAL", (long) (totalale * 0.001));
	}

	private void updateAssetValueProperties() throws Docx4JException {
		final DecimalFormat assetDecimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);
		final Map<String, Double> assetTypeValues = analysis.findSelectedAssets().stream()
				.collect(Collectors.groupingBy(asset -> asset.getAssetType().getName(), Collectors.summingDouble(Asset::getValue)));
		final List<SummaryStage> summaries = getSummaryStage(ActionPlanMode.APPN);
		final List<Phase> phases = analysis.findUsablePhase();

		double assetTotalValue = 0;

		for (Entry<String, Double> entry : assetTypeValues.entrySet()) {
			assetTotalValue += entry.getValue();
			setCustomProperty(entry.getKey().toUpperCase() + "_Val", (long) (entry.getValue() * 0.001));
		}

		double time = 0, sumRosi = 0, sumDRosi = 0, sumCost = 0;
		for (SummaryStage stage : summaries) {
			final Phase phase = phases.stream().filter(p -> stage.getStage().equals("Phase " + p.getNumber())).findAny().orElse(null);
			if (phase == null)
				continue;
			time += phase.getTime();
			sumRosi += stage.getROSI();
			sumDRosi += stage.getRelativeROSI() * stage.getCostOfMeasures();
			sumCost += stage.getCostOfMeasures();
		}

		if (time == 0)
			time = 1.0;
		if (sumCost == 0)
			sumCost = 1.0;

		final double avRosi = sumRosi / (time * 1000.0), avDRosi = sumDRosi / (time * sumCost);

		assetDecimalFormat.setMaximumFractionDigits(1);

		setCustomProperty("TOTAL_ASSET_VAL", assetDecimalFormat.format(assetTotalValue * 0.001));

		setCustomProperty("AV_ROSI_VAL", assetDecimalFormat.format(avRosi));

		assetDecimalFormat.setMaximumFractionDigits(2);
		assetDecimalFormat.setMinimumFractionDigits(1);
		setCustomProperty("AV_DROSI_VAL", (long) avDRosi);
		setCustomProperty("GAIN_VAL", assetDecimalFormat.format(1 + avDRosi * 0.01));
		if (!summaries.isEmpty())
			setCustomProperty("FINAL_ALE_VAL", (long) (summaries.get(summaries.size() - 1).getTotalALE() * 0.001));
	}

	public List<SummaryStage> getSummaryStage(ActionPlanMode planMode) {
		return getAnalysis().findSummary(planMode);
	}

	private String extractName(String key) {
		return key.toLowerCase().replaceAll("ts_qt_|ts_ql_|ts_", "").trim();
	}

	private AtomicBoolean getMutex() {
		return mutex;
	}

	private Map<String, Style> getStyles() {
		return styles;
	}

	private AnalysisType getType() {
		try {
			if (!(getTemplate() == null || getTemplate().getType() == null))
				return getTemplate().getType();
			String type = getPropertyString(PROPERTY_REPORT_TYPE);
			return type == null ? getAnalysis().getType() : AnalysisType.valueOf(type);
		} catch (Exception e) {
			return null;
		}
	}

	private String internalName(String name, AnalysisType type) {
		final String tmp = name.toLowerCase().trim();
		if (name.startsWith("ts_") || type == null || type.isHybrid())
			return tmp;
		final String prefix = type == AnalysisType.QUALITATIVE ? "ts_ql_" : "ts_qt_";
		switch (tmp) {
		case "additionalcollection":
			return (type == AnalysisType.HYBRID ? "ts_hy_" : prefix) + tmp;
		case "actionplan":
		case "assessment":
		case "asset":
		case "assetnotselected":
		case "impact":
		case "proba":
		case "summary":
		case "chartcompliance27001":
		case "chartcompliance27002":
			return prefix + tmp;
		case "chartalebyasset":
		case "chartalebyassettype":
		case "chartalebyscenario":
		case "chartalebyscenariotype":
		case "chartrentability":
		case "chartriskbyasset":
		case "chartriskbyassettype":
		case "chartriskbyscenario":
		case "chartriskbyscenariotype":
		case "currentsecuritylevel":
		case "impactlist":
		case "listcollection":
		case "measurescollection":
		case "phase":
		case "riskacceptance":
		case "riskheatmap":
		case "riskheatmapsummary":
		case "scenario":
		case "scope":
		case "risk":
		case "threat":
			return "ts_" + tmp;
		case "vul":
			return "ts_vulnerability";
		default:
			return tmp;
		}
	}

	private void setStyles(Map<String, Style> styles) {
		this.styles = styles;
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
