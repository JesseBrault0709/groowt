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
