package lu.itrust.business.expressions;

/**
 * Enumerates all token types that may occur in an expression.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public enum TokenType {
	Variable,
	Number,
	PlusOperator,
	MinusOperator,
	TimesOperator,
	DivideOperator,
	LeftBracket,
	RightBracket,
	Comma,
	End
}
