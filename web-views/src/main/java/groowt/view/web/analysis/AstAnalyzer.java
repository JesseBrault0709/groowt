package groowt.view.web.analysis;

import groowt.view.web.ast.node.Node;

@FunctionalInterface
public non-sealed interface AstAnalyzer<T extends Node, E extends AstError<T>> extends Analyzer<Node, E> {}