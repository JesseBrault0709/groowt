package groowt.view.web.ast.node;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import org.antlr.v4.runtime.Token;

import java.util.stream.Collectors;

public sealed abstract class ComponentTypeNode extends AbstractLeafNode permits
        ClassComponentTypeNode,
        StringComponentTypeNode {

    private final String identifier;

    public ComponentTypeNode(
            TokenList tokenList,
            TokenRange tokenRange,
            NodeExtensionContainer extensionContainer,
            TokenRange identifierTokenRange
    ) {
        super(tokenRange, extensionContainer);
        this.identifier = tokenList.getRange(identifierTokenRange).stream()
                .map(Token::getText)
                .collect(Collectors.joining());
    }

    public String getIdentifier() {
        return this.identifier;
    }

}
