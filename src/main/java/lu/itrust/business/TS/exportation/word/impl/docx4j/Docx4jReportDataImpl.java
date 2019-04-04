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
import lu.itrust.business.TS.exportation.word.Docx4jReportData;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.BookmarkClean;
import lu.itrust.business.TS.helper.Distribution;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisReportSetting;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.AnalysisStandard;

/**
 * @author eomar
 *
 */
public class Docx4jReportDataImpl implements Docx4jReportData {

	private File file;

	private String path;

	private int progress;

	private Locale locale;

	private String darkColor;

	private String reportName;

	private String lightColor;

	private Analysis analysis;

	private String defaultColor;

	private String zeroCostColor;

	private ReportTemplate template;

	private AtomicLong drawingIndex;

	private ValueFactory valueFactory;

	private String currentParagraphId;

	private DecimalFormat numberFormat;

	private MessageSource messageSource;

	private AtomicInteger bookmarkMaxId;

	private String defaultParagraphStyle;

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

		setBookmarks(finder.getStarts().stream().filter(c -> c.getName().toLowerCase().startsWith("ts-"))
				.collect(Collectors.toMap(c -> c.getName().toLowerCase(), Function.identity(), (c1, c2) -> c1, LinkedHashMap::new)));
		return false;
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

		getWordMLPackage().getMainDocumentPart().getDocumentSettingsPart().getContents().setUpdateFields(factory.createBooleanDefaultTrue());
	}

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
		return file;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public int getProgess() {
		return progress;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public String getDarkColor() {
		return darkColor;
	}

	@Override
	public String getReportName() {
		return reportName;
	}

	@Override
	public String getLightColor() {
		return lightColor;
	}

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

	@Override
	public String getZeroCostColor() {
		return this.zeroCostColor;
	}

	@Override
	public String getDefaultColor() {
		return defaultColor;
	}

	@Override
	public ReportTemplate getTemplate() {
		return template;
	}

	@Override
	public String getDefaultTableStyle() {
		return defaultParagraphStyle;
	}

	@Override
	public ValueFactory getValueFactory() {
		return valueFactory;
	}

	@Override
	public DecimalFormat getNumberFormat() {
		return numberFormat;
	}

	@Override
	public MessageSource getMessageSource() {
		return messageSource;
	}

	@Override
	public AtomicInteger getBookmarkMaxId() {
		return bookmarkMaxId;
	}

	@Override
	public String getDefaultParagraphStyle() {
		return defaultParagraphStyle;
	}

	@Override
	public AtomicLong getBookmarkCounter() {
		return bookmarkCounter;
	}

	@Override
	public DecimalFormat getKiloNumberFormat() {
		return kiloNumberFormat;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public org.docx4j.wml.ObjectFactory getFactory() {
		return factory;
	}

	@Override
	public org.docx4j.dml.ObjectFactory getDmlFactory() {
		return dmlFactory;
	}

	@Override
	public org.docx4j.dml.chart.ObjectFactory getChartFactory() {
		return chartFactory;
	}

	@Override
	public org.docx4j.dml.wordprocessingDrawing.ObjectFactory getDrawingFactory() {
		return drawingFactory;
	}

	@Override
	public WordprocessingMLPackage getWordMLPackage() {
		return wordMLPackage;
	}

	@Override
	public AtomicLong getDrawingIndex() {
		return drawingIndex;
	}

	@Override
	public String getCurrentParagraphId() {
		return currentParagraphId;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param darkColor the darkColor to set
	 */
	public void setDarkColor(String darkColor) {
		this.darkColor = darkColor;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @param lightColor the lightColor to set
	 */
	public void setLightColor(String lightColor) {
		this.lightColor = lightColor;
	}

	/**
	 * @param analysis the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * @param defaultColor the defaultColor to set
	 */
	public void setDefaultColor(String defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * @param zeroCostColor
	 */
	public void setZeroCostColor(String zeroCostColor) {
		this.zeroCostColor = zeroCostColor;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(ReportTemplate template) {
		this.template = template;
	}

	/**
	 * @param drawingIndex the drawingIndex to set
	 */
	public void setDrawingIndex(AtomicLong drawingIndex) {
		this.drawingIndex = drawingIndex;
	}

	/**
	 * @param valueFactory the valueFactory to set
	 */
	public void setValueFactory(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	/**
	 * @param currentParagraphId the currentParagraphId to set
	 */
	public void setCurrentParagraphId(String currentParagraphId) {
		this.currentParagraphId = currentParagraphId;
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
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param bookmarkMaxId the bookmarkMaxId to set
	 */
	public void setBookmarkMaxId(AtomicInteger bookmarkMaxId) {
		this.bookmarkMaxId = bookmarkMaxId;
	}

	/**
	 * @param defaultParagraphStyle the defaultParagraphStyle to set
	 */
	public void setDefaultParagraphStyle(String defaultParagraphStyle) {
		this.defaultParagraphStyle = defaultParagraphStyle;
	}

	/**
	 * @param bookmarkCounter the bookmarkCounter to set
	 */
	public void setBookmarkCounter(AtomicLong bookmarkCounter) {
		this.bookmarkCounter = bookmarkCounter;
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
	 * @param document the document to set
	 */
	public void setDocument(org.docx4j.wml.Document document) {
		this.document = document;
	}

	/**
	 * @param factory the factory to set
	 */
	public void setFactory(org.docx4j.wml.ObjectFactory factory) {
		this.factory = factory;
	}

	/**
	 * @param dmlFactory the dmlFactory to set
	 */
	public void setDmlFactory(org.docx4j.dml.ObjectFactory dmlFactory) {
		this.dmlFactory = dmlFactory;
	}

	/**
	 * @param chartFactory the chartFactory to set
	 */
	public void setChartFactory(org.docx4j.dml.chart.ObjectFactory chartFactory) {
		this.chartFactory = chartFactory;
	}

	/**
	 * @param drawingFactory the drawingFactory to set
	 */
	public void setDrawingFactory(org.docx4j.dml.wordprocessingDrawing.ObjectFactory drawingFactory) {
		this.drawingFactory = drawingFactory;
	}

	/**
	 * @param wordMLPackage the wordMLPackage to set
	 */
	public void setWordMLPackage(org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage) {
		this.wordMLPackage = wordMLPackage;
	}

	public P addBreak(P paragraph, STBrType type) {
		final R run = getFactory().createR();
		final Br br = getFactory().createBr();
		run.getContent().add(br);
		br.setType(type);
		paragraph.getContent().add(run);
		return paragraph;
	}

	public P addCellParagraph(Tc cell) {
		final P p = getFactory().createP();
		cell.getContent().add(p);
		return p;
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

	public <T extends SerContent> T createChart(CTAxDataSource cat, String reference, long index, String phaseLabel, T ser) {

		setupTitle(reference, index, phaseLabel, ser);

		ser.setCat(cat);

		ser.setVal(new CTNumDataSource());

		ser.getVal().setNumRef(new CTNumRef());

		ser.getVal().getNumRef().setNumCache(new CTNumData());

		return ser;
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

	public String findChartId(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor(name);
		if (paragraph == null)
			return getDocument().getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream()).filter(r -> r instanceof R)
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

	public synchronized Long findDrawingId() throws XPathBinderAssociationIsPartialException, JAXBException {
		if (getDrawingIndex() == null) {
			setDrawingIndex(new AtomicLong(getDocument().getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream())
					.filter(r -> r instanceof R).flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream()).filter(i -> i instanceof Inline)
					.mapToLong(i -> ((Inline) i).getDocPr().getId()).max().orElse(0)));
		}
		return getDrawingIndex().incrementAndGet();
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
		R run = factory.createR();
		FldChar fldChar = factory.createFldChar();
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
			table.getTblPr().setJc(factory.createJc());
		table.getTblPr().getJc().setVal(JcEnumeration.CENTER);
		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(factory.createTblWidth());
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		return table;
	}

	public CTVerticalJc createVerticalAlignment(STVerticalJc alignment) {
		CTVerticalJc ctVerticalJc = factory.createCTVerticalJc();
		ctVerticalJc.setVal(alignment);
		return ctVerticalJc;
	}

	public List<Part> duplicateChart(int size, String chartName, String name) throws JAXBException, Docx4JException {
		int count = 1;
		if (size > Constant.CHAR_SINGLE_CONTENT_MAX_SIZE)
			count = Distribution.Distribut(size, Constant.CHAR_MULTI_CONTENT_SIZE, Constant.CHAR_MULTI_CONTENT_MAX_SIZE).getDivisor();
		Part part = findChart(chartName);
		if (part == null)
			return Collections.emptyList();
		List<Part> parts = new ArrayList<>(count);
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

	public ClonePartResult cloneChart(Chart part, String name, String description) throws Docx4JException, InvalidFormatException, JAXBException {
		Part copy = PartClone.clone(part, null);
		Relationship relationship = getWordMLPackage().getMainDocumentPart().addTargetPart(copy, AddPartBehaviour.RENAME_IF_NAME_EXISTS);
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

	public Part findChart(String name) throws InvalidFormatException, XPathBinderAssociationIsPartialException, JAXBException {
		String id = findChartId(name);
		if (id == null)
			return null;
		Relationship relationship = getWordMLPackage().getMainDocumentPart().getRelationshipsPart().getRelationships().getRelationship().parallelStream()
				.filter(part -> part.getId().equals(id)).findAny().orElse(null);
		if (relationship == null)
			return null;
		return getWordMLPackage().getParts().get(new PartName("/word/" + relationship.getTarget()));
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
		return (P) getDocument().getContent().parallelStream().filter(p -> (p instanceof P) && ((P) p).getContent().parallelStream().anyMatch(
				b -> (b instanceof JAXBElement) && ((JAXBElement<?>) b).getValue() instanceof CTBookmark && ((CTBookmark) ((JAXBElement<?>) b).getValue()).getName().equals(name)))
				.findAny().orElse(null);
	}

	public String formatLikelihood(String likelihood) {
		try {
			return getKiloNumberFormat().format(Double.parseDouble(likelihood));
		} catch (Exception e) {
			return likelihood;
		}
	}

	public TblPr getTableStyle(String id) {
		Style style = getStyles().get(id);
		return style == null ? null : (TblPr) style.getTblPr();
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
		CTMarkupRange markupRange = factory.createCTMarkupRange();
		CTBookmark bookmark = factory.createCTBookmark();
		markupRange.setId(id);
		bookmark.setId(id);
		bookmark.setName("_Tsr" + getBookmarkCounter().incrementAndGet());
		JAXBElement<CTMarkupRange> bookmarkEnd = factory.createBodyBookmarkEnd(markupRange);
		JAXBElement<CTBookmark> bookmarkStart = factory.createBodyBookmarkStart(bookmark);
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
			paragraph.setPPr(factory.createPPr());
		if (paragraph.getParent() instanceof Tc) {
			if (paragraph.getPPr().getJc() == null)
				paragraph.getPPr().setJc(factory.createJc());
			paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
		} else
			paragraph.getPPr().setTextAlignment(alignment);
		return paragraph;
	}

	public Tc setAlignment(Tc cell, TextAlignment alignment) {
		cell.getContent().parallelStream().filter(p -> p instanceof P).forEach(p -> setAlignment((P) p, alignment));
		return cell;
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

	public void setColor(CTBarSer ser, String color) {
		if (ser.getSpPr() == null) {
			ser.setSpPr(new CTShapeProperties());
			ser.getSpPr().setSolidFill(new CTSolidColorFillProperties());
			ser.getSpPr().getSolidFill().setSrgbClr(getColor(color));
		}
	}

	public CTSRgbColor getColor(String color) {
		CTSRgbColor rgbColor = new CTSRgbColor();
		rgbColor.setVal(color.substring(1));
		return rgbColor;
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

	public R setInstrText(R r, String content) {
		Text text = factory.createText();
		JAXBElement<Text> textWrapped = factory.createRInstrText(text);
		r.getContent().add(textWrapped);
		text.setValue(content);
		text.setSpace("preserve");
		return r;
	}

	public void setRepeatHeader(Tr row) {
		if (row.getTrPr() == null)
			row.setTrPr(factory.createTrPr());
		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(factory.createCTTrPrBaseTblHeader(factory.createBooleanDefaultTrue()));
	}

	public P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(factory.createPPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(factory.createPPrBasePStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	public P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

	public P setText(P paragraph, String content, boolean bold) {
		R r = factory.createR();
		Text text = factory.createText();
		r.getContent().add(text);
		text.setValue(content);
		if (r.getRPr() == null)
			r.setRPr(factory.createRPr());
		r.getRPr().setB(factory.createBooleanDefaultTrue());
		paragraph.getContent().add(r);
		return paragraph;
	}

	public P setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, getCurrentParagraphId());
			if (paragraph.getParent() instanceof Tc) {
				if (paragraph.getPPr().getJc() == null)
					paragraph.getPPr().setJc(factory.createJc());
				paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
			} else
				paragraph.getPPr().setTextAlignment(alignment);
		}
		paragraph.getContent().removeIf(r -> r instanceof R);
		R r = factory.createR();
		Text text = factory.createText();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);
		return paragraph;

	}

	public R setText(R r, String value) {
		Text text = factory.createText();
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

	public void setVerticalAlignment(Tc cell, CTVerticalJc alignment) {
		if (cell.getTcPr() == null)
			cell.setTcPr(factory.createTcPr());
		cell.getTcPr().setVAlign(alignment);
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

	private Map<String, Style> getStyles() {
		return styles;
	}

	private void setStyles(Map<String, Style> styles) {
		this.styles = styles;
	}

	protected Map<String, CTBookmark> getBookmarks() {
		return bookmarks;
	}

	protected void setBookmarks(Map<String, CTBookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	@Override
	public void export(Analysis analysis, String path, File file,  ServiceTaskFeedback serviceTaskFeedback, MessageSource messageSource ) {
		internalReportExport(analysis, path, null, file, serviceTaskFeedback, messageSource);
	}

	@Override
	public void export(Analysis analysis, String path, ReportTemplate template,  ServiceTaskFeedback serviceTaskFeedback, MessageSource messageSource) {
		internalReportExport(analysis, path, template, null,serviceTaskFeedback,messageSource);
	}

	protected synchronized void internalReportExport(Analysis analysis, String path, ReportTemplate template, File file, ServiceTaskFeedback serviceTaskFeedback, MessageSource messageSource) {
		try {
			if (getMutex().get())
				throw new TrickException("error.export.already.start", "Export is already started!");
			getMutex().set(true);
			setPath(path);
			setFile(file);
			setAnalysis(analysis);
			setTemplate(template);
			setMessageSource(messageSource);
			initialise();
			
			getBookmarks().forEach((key, value)-> {
				
			});
		} catch (Docx4JException | IOException e) {
			throw new TrickException("error.export.internal", "An error occurred while exporting word report!", e);
		}
	}

	private AtomicBoolean getMutex() {
		return mutex;
	}

}
