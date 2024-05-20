package groowt.view.component.web.antlr;

import groowt.view.component.web.util.RangeIterator;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class TokenList extends ArrayList<Token> {

    public TokenList(Collection<? extends Token> c) {
        super(c);
    }

    public TokenList(WebViewComponentsTokenStream tokenStream) {
        super(tokenStream.getAllTokens());
    }

    private @Nullable Token getNullable(int index) {
        if (index < this.size()) {
            return this.get(index);
        } else {
            return null;
        }
    }

    public List<Token> getRange(TokenRange range) {
        final List<Token> result = new ArrayList<>();
        final RangeIterator<Token> iter = range.rangeIterator(this::getNullable);
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    public MergedGroovyCodeToken getGroovyToken(int index) {
        return (MergedGroovyCodeToken) this.get(index);
    }

    @Override
    public boolean add(Token token) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public void add(int index, Token element) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public void addFirst(Token element) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public void addLast(Token element) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public Token remove(int index) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public Token removeFirst() {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public Token removeLast() {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public boolean addAll(Collection<? extends Token> c) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Token> c) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public boolean removeIf(Predicate<? super Token> predicate) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public void replaceAll(UnaryOperator<Token> operator) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

    @Override
    public void sort(Comparator<? super Token> comparator) {
        throw new UnsupportedOperationException("TokenList is immutable.");
    }

}
