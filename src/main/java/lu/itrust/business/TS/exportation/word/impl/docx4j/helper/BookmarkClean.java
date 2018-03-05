package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ContentAccessor;

public class BookmarkClean {

	private CTBookmark startBookmark;

	private CTMarkupRange endBookmark;

	private ContentAccessor start;

	private ContentAccessor end;

	public BookmarkClean(ContentAccessor start, CTBookmark startBookmark) {
		update(start, startBookmark);
	}

	public BookmarkClean(ContentAccessor end, CTMarkupRange endBookmark) {
		update(end, endBookmark);
	}

	public BookmarkClean(ContentAccessor start, CTBookmark startBookmark, ContentAccessor end, CTMarkupRange endBookmark) {
		this(start, startBookmark);
		update(end, endBookmark);
	}

	public CTBookmark getStartBookmark() {
		return startBookmark;
	}

	public void setStartBookmark(CTBookmark startBookmark) {
		this.startBookmark = startBookmark;
	}

	public CTMarkupRange getEndBookmark() {
		return endBookmark;
	}

	public void setEndBookmark(CTMarkupRange endBookmark) {
		this.endBookmark = endBookmark;
	}

	public void update(ContentAccessor start, CTBookmark startBookmark) {
		setStart(start);
		setStartBookmark(startBookmark);
	}

	public void update(ContentAccessor end, CTMarkupRange endBookmark) {
		setEnd(end);
		setEndBookmark(endBookmark);
	}

	public boolean hasContent() {
		return !(start == null || end == null || start.equals(end));
	}

	public void setStart(ContentAccessor start) {
		this.start = start;
	}

	public void setEnd(ContentAccessor end) {
		this.end = end;
	}

	public ContentAccessor getStart() {
		return start;
	}

	public ContentAccessor getEnd() {
		return end;
	}

}
