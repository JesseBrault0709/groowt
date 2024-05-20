package groowt.view.web.ast.node;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.NodeExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractLeafNode implements LeafNode {

    private final TokenRange tokenRange;
    private final NodeExtensionContainer extensionContainer;

    public AbstractLeafNode(TokenRange tokenRange, NodeExtensionContainer extensionContainer) {
        this.tokenRange = tokenRange;
        this.extensionContainer = extensionContainer;
    }

    protected static TokenRange getTokenRangeFromIndex(TokenList tokenList, int tokenIndex) {
        return TokenRange.of(tokenList.get(tokenIndex));
    }

    @Override
    public TokenRange getTokenRange() {
        return this.tokenRange;
    }

    @Override
    public <T extends NodeExtension> T createExtension(Class<T> extensionClass, Object... constructorArgs) {
        return this.extensionContainer.createExtension(extensionClass, this, constructorArgs);
    }

    @Override
    public NodeExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public <T extends NodeExtension> @Nullable T findExtension(Class<T> extensionClass) {
        return this.extensionContainer.findExtension(extensionClass);
    }

    @Override
    public <T extends NodeExtension> void configureExtension(Class<T> extensionClass, Consumer<? super T> configure) {
        this.extensionContainer.configureExtension(extensionClass, configure);
    }

    @Override
    public <T extends NodeExtension> T getExtension(Class<T> extensionClass) {
        return this.extensionContainer.getExtension(extensionClass);
    }

    @Override
    public boolean hasExtension(Class<? extends NodeExtension> extensionClass) {
        return this.extensionContainer.hasExtension(extensionClass);
    }

}
