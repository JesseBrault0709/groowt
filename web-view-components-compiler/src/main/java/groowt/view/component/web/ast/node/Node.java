package groowt.view.component.web.ast.node;

import groowt.util.extensible.Extensible;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.extension.NodeExtension;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.ast.extension.NodeExtensionFactory;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;

import java.util.stream.Collectors;

public sealed interface Node extends Extensible<NodeExtension, NodeExtensionFactory, NodeExtensionContainer>
        permits TreeNode, LeafNode {

    TokenRange getTokenRange();

    default String getText(TokenList tokenList) {
        return tokenList.getRange(this.getTokenRange()).stream().map(Token::getText).collect(Collectors.joining());
    }

}
