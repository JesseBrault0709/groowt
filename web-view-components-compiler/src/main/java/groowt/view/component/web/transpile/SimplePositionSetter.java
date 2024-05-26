package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.util.SourcePosition;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;
import org.codehaus.groovy.ast.ASTNode;

import static groowt.view.component.web.util.SourcePosition.fromEndOfToken;
import static groowt.view.component.web.util.SourcePosition.fromStartOfToken;

public class SimplePositionSetter implements PositionSetter {

    private final int lineOffset;
    private final int columnOffset;

    public SimplePositionSetter(int lineOffset, int columnOffset) {
        this.lineOffset = lineOffset;
        this.columnOffset = columnOffset;
    }

    public SimplePositionSetter() {
        this.lineOffset = 0;
        this.columnOffset = 0;
    }

    protected void set(ASTNode target, int startLine, int startColumn, int endLine, int endColumn) {
        target.setLineNumber(startLine + this.lineOffset);
        target.setColumnNumber(startColumn + this.columnOffset);
        target.setLastLineNumber(endLine + this.lineOffset);
        target.setLastColumnNumber(endColumn + this.columnOffset);
    }

    protected final void set(ASTNode target, SourcePosition start, SourcePosition end) {
        this.set(target, start.line(), start.column(), end.line(), end.column());
    }

    @Override
    public void setPositionOffsetInContainer(ASTNode target, Node container) {
        final SourcePosition containerStart = container.getTokenRange().getStartPosition();
        final SourcePosition startPosition = new SourcePosition(
                containerStart.line() + target.getLineNumber() - 1,
                target.getLineNumber() == 1
                        ? containerStart.column() + target.getColumnNumber() - 1
                        : target.getColumnNumber()
        );
        final SourcePosition endPosition = new SourcePosition(
                containerStart.line() + target.getLastLineNumber() - 1,
                target.getLastLineNumber() == 1
                        ? containerStart.column() + target.getLastColumnNumber() - 1
                        : target.getLastColumnNumber()
        );
        this.set(target, startPosition, endPosition);
    }

    @Override
    public void setPosition(ASTNode target, Token source) {
        this.set(target, fromStartOfToken(source), fromEndOfToken(source));
    }

    @Override
    public void setPosition(ASTNode target, TokenRange tokenRange) {
        this.set(target, tokenRange.getStartPosition(), tokenRange.getEndPosition());
    }

    @Override
    public void setPosition(ASTNode target, Node source) {
        this.setPosition(target, source.getTokenRange());
    }

    @Override
    public void setPosition(ASTNode target, Node start, Node end) {
        final var startPosition = start.getTokenRange().getStartPosition();
        final var endPosition = end.getTokenRange().getEndPosition();
        this.set(target, startPosition, endPosition);
    }

    @Override
    public void setToStartOf(ASTNode target, Node source) {
        final var tokenRange = source.getTokenRange();
        this.set(target, tokenRange.getStartPosition(), tokenRange.getStartPosition());
    }

    @Override
    public PositionSetter withOffset(int lineOffset, int columnOffset) {
        return new SimplePositionSetter(lineOffset, columnOffset);
    }

}
