/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import org.docx4j.openpackaging.parts.Part;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P;

/**
 * @author eomar
 *
 */
public class ClonePartResult {
	
	private P p;
	
	private Relationship relationship;
	
	private Part part;

	/**
	 * @param part
	 * @param relationship
	 * @param p
	 */
	public ClonePartResult(Part part, Relationship relationship, P p) {
		this.setP(p);
		this.setRelationship(relationship);
		this.setPart(part);
	}

	/**
	 * @return the p
	 */
	public P getP() {
		return p;
	}

	/**
	 * @param p the p to set
	 */
	public void setP(P p) {
		this.p = p;
	}

	/**
	 * @return the relationship
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}

	/**
	 * @return the part
	 */
	public Part getPart() {
		return part;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(Part part) {
		this.part = part;
	}

	

}
