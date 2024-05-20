package groowt.view.component.web.util;

import groowt.view.component.web.antlr.TokenList;
import org.antlr.v4.runtime.Token;

import java.util.stream.Collectors;

public interface TokenRange extends ClosedRange<Token> {

    static TokenRange fromIndex(TokenList tokenList, int index) {
        return of(tokenList.get(index));
    }

    static TokenRange of(Token startAndEnd) {
        return new SimpleTokenRange(startAndEnd, startAndEnd);
    }

    static TokenRange of(Token start, Token end) {
        return new SimpleTokenRange(start, end);
    }

    static TokenRange empty() {
        return EmptyTokenRange.getInstance();
    }

    default String getText(TokenList tokenList) {
        return tokenList.getRange(this).stream().map(Token::getText).collect(Collectors.joining());
    }

    default SourcePosition getStartPosition() {
        return SourcePosition.fromStartOfToken(this.getStart());
    }

    default SourcePosition getEndPosition() {
        return SourcePosition.fromEndOfToken(this.getEnd());
    }

}
