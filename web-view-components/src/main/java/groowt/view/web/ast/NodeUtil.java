package groowt.view.web.ast;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.NodeExtension;
import groowt.view.web.ast.node.LeafNode;
import groowt.view.web.ast.node.Node;
import groowt.view.web.ast.node.TreeNode;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class NodeUtil {

    public static boolean isAnyOfType(Node subject, Class<?>... nodeTypes) {
        Objects.requireNonNull(subject);
        for (final var type : nodeTypes) {
            if (type.isAssignableFrom(subject.getClass())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean hasExtensionOfType(Node subject, Class<?>... extensionTypes) {
        Objects.requireNonNull(subject);
        for (final var extensionType : extensionTypes) {
            if (subject.hasExtension((Class<? extends NodeExtension>) extensionType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyOfType(Node subject, List<Class<? extends Node>> nodeTypes) {
        Objects.requireNonNull(subject);
        for (final var type : nodeTypes) {
            if (type.isAssignableFrom(subject.getClass())) {
                return true;
            }
        }
        return false;
    }

    public static @Nullable LeafNode getDeepestLeftmostLeafNode(Node node) {
        return switch (node) {
            case LeafNode leafNode -> leafNode;
            case TreeNode treeNode when treeNode.getChildrenSize() > 0 ->
                    getDeepestLeftmostLeafNode(treeNode.getAt(0));
            default -> null;
        };
    }

    private static void doFormatAst(Node node, StringBuilder sb, int indentTimes, String indent, TokenList tokenList) {
        NodeUtilKt.formatSingleNode(node, sb, indentTimes, indent, tokenList);
        if (node instanceof TreeNode treeNode) {
            treeNode.getChildren().forEach(child -> doFormatAst(
                    child, sb, indentTimes + 1, indent, tokenList
            ));
        }
    }

    public static String formatAst(Node node, TokenList tokenList) {
        final var sb = new StringBuilder();
        doFormatAst(node, sb, 0, "  ", tokenList);
        return sb.toString();
    }

    private NodeUtil() {}

}
