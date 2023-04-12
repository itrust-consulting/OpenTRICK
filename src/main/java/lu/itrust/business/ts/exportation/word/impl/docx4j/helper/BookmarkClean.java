package lu.itrust.business.ts.exportation.word.impl.docx4j.helper;

import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ContentAccessor;

public class BookmarkClean {

	private CTBookmark start;

	private CTMarkupRange end;

	public BookmarkClean(CTBookmark startBookmark) {
		update(startBookmark);
	}

	public BookmarkClean(CTMarkupRange endBookmark) {
		update(endBookmark);
	}

	public BookmarkClean(CTBookmark start, CTMarkupRange end) {
		this(start);
		update(end);
	}

	public CTBookmark getStart() {
		return start;
	}

	public void setStart(CTBookmark start) {
		this.start = start;
	}

	public CTMarkupRange getEnd() {
		return end;
	}

	public void setEnd(CTMarkupRange end) {
		this.end = end;
	}

	public void update(CTBookmark start) {
		setStart(start);
	}

	public void update(CTMarkupRange end) {
		setEnd(end);
	}

	public boolean hasContent() {
		return !(start == null || end == null || getStartParent() == null || start.getParent().equals(end.getParent()));
	}

	public ContentAccessor getStartParent() {
		return start == null ? null : (ContentAccessor) start.getParent();
	}

	public ContentAccessor getEndParent() {
		return end == null ? null : (ContentAccessor) end.getParent();
	}

}
