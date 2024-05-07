@file:JvmName("MismatchedComponentTypeAnalysis")
package groowt.view.web.analysis

import groowt.view.web.antlr.WebViewComponentsParser.ComponentTypeContext
import groowt.view.web.antlr.WebViewComponentsParser.ComponentWithChildrenContext
import groowt.view.web.util.SourcePosition
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

private fun getIdentifiers(
    componentTypeContext: ComponentTypeContext
): List<Token> = componentTypeContext.Identifier().map(TerminalNode::getSymbol)

private fun getErrorMessage(
    openType: ComponentTypeContext,
    closingType: ComponentTypeContext
) = "The component's opening and closing tags' types must match exactly. " +
        "Found ${openType.text} at [${SourcePosition.formatStartOfToken(openType.start)}] " +
        "and ${closingType.text} at [${SourcePosition.formatStartOfToken(closingType.start)}]."

private fun test(
    openIdentifiers: List<Token>,
    closingIdentifiers: List<Token>
): Boolean {
    if (openIdentifiers.size != closingIdentifiers.size) {
        return false
    }
    openIdentifiers.zip(closingIdentifiers).forEach { (openIdentifier, closingIdentifier) ->
        if (!openIdentifier.text.equals(closingIdentifier.text)) {
            return false
        }
    }
    return true
}

private fun doCheck(tree: ParseTree, destination: MutableList<MismatchedComponentTypeError>) {
    if (tree is ParserRuleContext) {
        for (child in tree.children) {
            doCheck(child, destination)
        }
        if (tree is ComponentWithChildrenContext) {
            val openType: ComponentTypeContext = tree.openComponent().componentArgs().componentType()
            val closingType: ComponentTypeContext = tree.closingComponent().componentType()
            val openTypeIdentifiers = getIdentifiers(openType)
            val closingTypeIdentifiers = getIdentifiers(closingType)
            if (!test(openTypeIdentifiers, closingTypeIdentifiers)) {
                destination.add(MismatchedComponentTypeError(tree, getErrorMessage(openType, closingType)))
            }
        }
    }
}

data class MismatchedComponentTypeError(val tree: ParseTree, val message: String)

fun check(tree: ParseTree): List<MismatchedComponentTypeError> {
    val result: MutableList<MismatchedComponentTypeError> = ArrayList()
    doCheck(tree, result)
    return result
}
