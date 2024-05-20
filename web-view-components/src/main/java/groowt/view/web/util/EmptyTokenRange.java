package groowt.view.web.util;

import org.antlr.v4.runtime.Token;

final class EmptyTokenRange extends EmptyRange<Token> implements TokenRange {

    private static final EmptyTokenRange instance = new EmptyTokenRange();

    public static EmptyTokenRange getInstance() {
        return instance;
    }

    private EmptyTokenRange() {}

    @Override
    public Token getEnd() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInclusiveStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInclusiveEnd() {
        throw new UnsupportedOperationException();
    }

}
