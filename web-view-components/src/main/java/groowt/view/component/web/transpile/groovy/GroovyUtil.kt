package groowt.view.component.web.transpile.groovy

import org.codehaus.groovy.ast.ASTNode

fun formatGroovyPosition(astNode: ASTNode): String =
    "[${astNode.lineNumber},${astNode.columnNumber}..${astNode.lastLineNumber},${astNode.lastColumnNumber}]"
