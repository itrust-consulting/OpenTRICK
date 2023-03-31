/**
 * 
 */
package lu.itrust.business.ts.exportation.word;

/**
 * @author eomar
 *
 */
public interface IDocxBuilder {
	
	IDocxBuilder getNext();
	
	boolean build(IBuildData data);
}
