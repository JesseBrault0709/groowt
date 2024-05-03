package groowt.view.web.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AntlrUtil {

    private AntlrUtil() {}

    public static final class ParseErrorCollector {

        private final List<ParserRuleContext> nodesWithRecognitionException = new ArrayList<>();
        private final List<ErrorNode> errorNodes = new ArrayList<>();

        public void addNodeWithRecognitionException(ParserRuleContext parserRuleContext) {
            if (parserRuleContext.exception == null) {
                throw new IllegalArgumentException();
            }
            this.nodesWithRecognitionException.add(parserRuleContext);
        }

        public void addErrorNode(ErrorNode errorNode) {
            this.errorNodes.add(errorNode);
        }

        public boolean isEmpty() {
            return this.nodesWithRecognitionException.isEmpty() && this.errorNodes.isEmpty();
        }

        public int getErrorCount() {
            return this.nodesWithRecognitionException.size() + this.errorNodes.size();
        }

        public Collection<Tree> getAll() {
            final Collection<Tree> all = new ArrayList<>(this.nodesWithRecognitionException);
            all.addAll(this.errorNodes);
            return all;
        }

        public List<ParserRuleContext> getNodesWithRecognitionException() {
            return this.nodesWithRecognitionException;
        }

        public List<ErrorNode> getErrorNodes() {
            return this.errorNodes;
        }

    }

    private static void findErrorNodes(Tree tree, ParseErrorCollector parseErrorCollector) {
        switch (tree) {
            case ParserRuleContext parserRuleContext -> {
                if (parserRuleContext.exception != null) {
                    parseErrorCollector.addNodeWithRecognitionException(parserRuleContext);
                }
                parserRuleContext.children.forEach(child -> {
                    findErrorNodes(child, parseErrorCollector);
                });
            }
            case ErrorNode errorNode -> parseErrorCollector.addErrorNode(errorNode);
            default -> {} // ignore
        }
    }

    public static ParseErrorCollector findErrorNodes(ParserRuleContext ruleContext) {
        final var ec = new ParseErrorCollector();
        findErrorNodes(ruleContext, ec);
        return ec;
    }

}
