@file:JvmName("MismatchedComponentTypeAnalysis")
package groowt.view.component.web.analysis

import groowt.view.component.web.WebViewComponentBugError
import groowt.view.component.web.antlr.WebViewComponentsParser.ComponentTypeContext
import groowt.view.component.web.antlr.WebViewComponentsParser.ComponentWithChildrenContext
import groowt.view.component.web.util.SourcePosition
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTree

private fun getIdentifiers(ctx: ComponentTypeContext): Token {
    val typedIdentifier = ctx.TypedIdentifier()
    if (typedIdentifier != null) {
        return typedIdentifier.symbol
    }
    val stringIdentifier = ctx.StringIdentifier()
    if (stringIdentifier != null) {
        return stringIdentifier.symbol
    }
    throw WebViewComponentBugError("Could not determine identifier type: $ctx")
}

private fun getErrorMessage(
    openType: ComponentTypeContext,
    closingType: ComponentTypeContext
) = "The component's opening and closing tags' types must match exactly. " +
        "Found '${openType.text}' at ${SourcePosition.formatStartOfTokenLong(openType.start)} " +
        "and '${closingType.text}' at ${SourcePosition.formatStartOfTokenLong(closingType.start)}."

private fun test(openIdentifiers: Token, closingIdentifiers: Token): Boolean =
    openIdentifiers.text.equals(closingIdentifiers.text)

private fun doCheck(tree: ParseTree, destination: MutableList<MismatchedComponentTypeError>) {
    if (tree is ParserRuleContext) {
        for (child in tree.children) {
            doCheck(child, destination)
        }
        if (tree is ComponentWithChildrenContext) {
            val openType: ComponentTypeContext = tree.openComponent().componentArgs().componentType()
            val closingType: ComponentTypeContext = tree.closingComponent().componentType()
            val openIdentifier = getIdentifiers(openType)
            val closingIdentifier = getIdentifiers(closingType)
            if (!test(openIdentifier, closingIdentifier)) {
                destination.add(MismatchedComponentTypeError(tree, getErrorMessage(openType, closingType)))
            }
        }
    }
}

data class MismatchedComponentTypeError(val component: ComponentWithChildrenContext, val message: String)

fun checkForMismatchedComponentTypeErrors(tree: ParseTree): List<MismatchedComponentTypeError> {
    val result: MutableList<MismatchedComponentTypeError> = ArrayList()
    doCheck(tree, result)
    return result
}
