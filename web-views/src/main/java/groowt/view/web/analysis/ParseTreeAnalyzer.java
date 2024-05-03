package groowt.view.web.analysis;

import org.antlr.v4.runtime.tree.ParseTree;

@FunctionalInterface
public non-sealed interface ParseTreeAnalyzer<T extends ParseTree, E extends ParseTreeAnalysisError<T>>
        extends Analyzer<ParseTree, E> {}
