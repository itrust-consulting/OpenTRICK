/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder;

import java.util.HashSet;
import java.util.Set;

import lu.itrust.business.TS.exportation.word.IBuildData;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;

/**
 * @author eomar
 *
 */
public abstract class Docx4jBuilder implements IDocxBuilder {

	private IDocxBuilder next;

	private Set<String> supported;

	/**
	 * @param next
	 */
	protected Docx4jBuilder(IDocxBuilder next) {
		this(next, "");
	}

	protected Docx4jBuilder(IDocxBuilder next, String... supports) {
		this.next = next;
		if (!(supports == null || supports.length == 0)) {
			final Set<String> tmp = new HashSet<>(supports.length);
			for (String support : supports) {
				if (!(support == null || support.isEmpty()))
					tmp.add(support);
			}
			if (!tmp.isEmpty())
				this.supported = tmp;
		}
	}

	@Override
	public IDocxBuilder getNext() {
		return next;
	}

	protected Set<String> getSupported() {
		return supported;
	}

	protected void setSupported(Set<String> supported) {
		this.supported = supported;
	}

	@Override
	public boolean build(IBuildData data) {
		if (data instanceof Docx4jData) {
			if (tryToBuild((Docx4jData) data))
				return true;
			else if (getNext() != null)
				return getNext().build(data);
		}
		return false;
	}

	protected boolean tryToBuild(Docx4jData data) {
		if (isSupported(data.getAnchor()))
			return internalBuild(data);
		return false;
	}

	protected boolean isSupported(String anchor) {
		return getSupported() != null && getSupported().contains(anchor);
	}

	protected abstract boolean internalBuild(Docx4jData data);

}
