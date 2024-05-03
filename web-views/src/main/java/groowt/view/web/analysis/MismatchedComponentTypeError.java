package groowt.view.web.analysis;

import groowt.view.web.antlr.WebViewComponentsParser.ComponentWithChildrenContext;

public record MismatchedComponentTypeError(ComponentWithChildrenContext subject, String message)
        implements ParseTreeAnalysisError<ComponentWithChildrenContext> {}