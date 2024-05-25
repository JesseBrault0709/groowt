package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.util.SourcePosition;
import groowt.view.component.web.util.TokenRange;
import org.codehaus.groovy.ast.ASTNode;

public class SimplePositionSetter implements PositionSetter {

    protected void set(ASTNode target, int startLine, int startColumn, int endLine, int endColumn) {
        target.setLineNumber(startLine);
        target.setColumnNumber(startColumn);
        target.setLastLineNumber(endLine);
        target.setLastColumnNumber(endColumn);
    }

    protected void set(ASTNode target, SourcePosition start, SourcePosition end) {
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
        target.setLineNumber(startPosition.line());
        target.setColumnNumber(startPosition.column());
        final var endPosition = end.getTokenRange().getEndPosition();
        target.setLastLineNumber(endPosition.line());
        target.setLastColumnNumber(endPosition.column());
    }

    @Override
    public void setToStartOf(ASTNode target, Node source) {
        final var tokenRange = source.getTokenRange();
        this.set(target, tokenRange.getStartPosition(), tokenRange.getStartPosition());
    }

}
