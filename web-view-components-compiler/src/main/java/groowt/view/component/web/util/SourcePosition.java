package groowt.view.component.web.util;

import org.antlr.v4.runtime.Token;

public record SourcePosition(int line, int column) {

    public static final SourcePosition UNKNOWN = new SourcePosition(-1, -1);

    public static String formatStartOfTokenShort(Token token) {
        return fromStartOfToken(token).toStringShort();
    }

    public static String formatStartOfTokenLong(Token token) {
        return fromStartOfToken(token).toStringLong();
    }

    public static SourcePosition fromStartOfToken(Token token) {
        return new SourcePosition(token.getLine(), token.getCharPositionInLine() + 1);
    }

    public static SourcePosition fromEndOfToken(Token token) {
        final var text = token.getText();
        if (token.getType() == Token.EOF || text.length() == 1) {
            return new SourcePosition(token.getLine(), token.getCharPositionInLine() + 1);
        } else {
            int line = token.getLine();
            int col = token.getCharPositionInLine() + 1;
            int i = 0;
            while (i < text.length()) {
                final char c0 = text.charAt(i);
                if (c0 == '\r') {
                    line++;
                    col = 1;
                    final char c1 = text.charAt(i + 1);
                    if (c1 == '\n') {
                        i += 2;
                    } else {
                        i++;
                    }
                } else if (c0 == '\n') {
                    line++;
                    col = 1;
                    i++;
                } else {
                    col++;
                    i++;
                }
            }
            return new SourcePosition(line, col);
        }
    }

    public String toStringShort() {
        return this.line + "," + this.column;
    }

    public String toStringLong() {
        return "line " + this.line + ", column " + this.column;
    }

}
