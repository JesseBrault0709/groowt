package groowt.view.web.ast.node;

import groowt.view.web.ast.extension.NodeExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractTreeNode implements TreeNode {

    protected static List<Node> filterNulls(Iterable<? extends Node> nodes) {
        final List<Node> nonNulls = new ArrayList<>();
        for (final Node node : nodes) {
            if (node != null) {
                nonNulls.add(node);
            }
        }
        return nonNulls;
    }

    protected static List<Node> filterNulls(Node... nodes) {
        return filterNulls(Arrays.asList(nodes));
    }

    protected static List<Node> checkForNulls(List<? extends Node> children) {
        for (final Node child : children) {
            if (child == null) {
                throw new NullPointerException("children may not contain null elements!");
            }
        }
        return new ArrayList<>(children);
    }

    protected static List<Node> checkForNulls(Node... nodes) {
        return checkForNulls(Arrays.asList(nodes));
    }

    private final TokenRange tokenRange;
    private final NodeExtensionContainer extensionContainer;
    private final List<Node> children = new ArrayList<>();

    public AbstractTreeNode(
            TokenRange tokenRange,
            NodeExtensionContainer extensionContainer,
            List<? extends Node> children
    ) {
        this.tokenRange = tokenRange;
        this.extensionContainer = extensionContainer;
        this.children.addAll(checkForNulls(children));
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

    /**
     * @return a <strong>copy</strong> of the children.
     */
    @Override
    public List<Node> getChildren() {
        return new ArrayList<>(this.children);
    }

    @Override
    public Node getAt(int index) {
        return this.children.get(index);
    }

    @Override
    public <C extends Node> C getAt(int index, Class<C> childClass) {
        return childClass.cast(this.children.get(index));
    }

    @Override
    public int indexOf(Node child) {
        return this.children.indexOf(child);
    }

    @Override
    public int getChildrenSize() {
        return this.children.size();
    }

}
