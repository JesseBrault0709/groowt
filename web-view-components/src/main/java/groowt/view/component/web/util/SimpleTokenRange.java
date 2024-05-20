package groowt.view.component.web.util;

import org.antlr.v4.runtime.Token;

import java.util.Comparator;

final class SimpleTokenRange extends ComparatorClosedRange<Token> implements TokenRange {

    private static final Comparator<Token> TOKEN_COMPARATOR = (left, right) -> {
        if (left.equals(right)) {
            return 0;
        } else {
            return left.getTokenIndex() - right.getTokenIndex();
        }
    };

    public SimpleTokenRange(Token start, Token end) {
        super(start, end, true, true, TOKEN_COMPARATOR, Token::getTokenIndex);
    }

}
