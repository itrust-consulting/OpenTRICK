package lu.itrust.business.expressions;

/**
 * Represents a class which continuously reads semantic tokens from a source.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public interface Tokenizer {
	public Token read() throws InvalidExpressionException;
}
