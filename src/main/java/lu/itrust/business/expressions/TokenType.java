package lu.itrust.business.expressions;

/**
 * Enumerates all token types that may occur in an expression.
 * 
 * @author  itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public enum TokenType {
	Variable, Number, PlusOperator('+'), MinusOperator('-'), TimesOperator('*'), DivideOperator('/'), LeftBracket('('), RightBracket(')'), Comma(','), End;

	private char symbole;

	private TokenType() {
	}

	private TokenType(char symbole) {
		this.symbole = symbole;
	}

	public char getSymbole() {
		return symbole;
	}

	public void setSymbole(char symbole) {
		this.symbole = symbole;
	}

}
