/**
 * 
 */
package lu.itrust.business.TS.exportation.word;

/**
 * @author eomar
 *
 */
public interface IDocxBuilder {
	
	IDocxBuilder getNext();
	
	boolean build(IBuildData data);
}
