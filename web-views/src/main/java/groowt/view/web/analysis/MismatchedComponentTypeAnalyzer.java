package groowt.view.web.analysis;

import groowt.view.web.antlr.WebViewComponentsParser.ComponentWithChildrenContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public final class MismatchedComponentTypeAnalyzer
        implements ParseTreeAnalyzer<ComponentWithChildrenContext, MismatchedComponentTypeError> {

    @Override
    public List<MismatchedComponentTypeError> analyze(ParseTree parseTree) {
        return MismatchedComponentTypeErrorAnalyzerKt.check(parseTree);
    }

}
