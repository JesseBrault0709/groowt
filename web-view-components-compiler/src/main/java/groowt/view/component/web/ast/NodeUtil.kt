@file:JvmName("NodeUtil")
package groowt.view.component.web.ast

import groowt.view.component.web.antlr.TokenList
import groowt.view.component.web.antlr.formatToken
import groowt.view.component.web.ast.node.LeafNode
import groowt.view.component.web.ast.node.Node
import groowt.view.component.web.ast.node.TreeNode

fun getDeepestLeftmost(node: Node): LeafNode? {
    return when (node) {
        is LeafNode -> node
        is TreeNode -> {
            if (node.children.isNotEmpty()) {
                getDeepestLeftmost(node.children.first())
            } else {
                null
            }
        }
    }
}

fun formatAst(node: Node, tokenList: TokenList): String {
    val sb = StringBuilder()
    formatAst(node, tokenList, sb, indentTimes = 0, indent = "  ")
    return sb.toString()
}

fun formatAst(
    node: Node,
    tokenList: TokenList,
    sb: StringBuilder,
    indentTimes: Int = 0,
    indent: String = "  ",
) {
    formatSingleNode(node, sb, indentTimes, indent, tokenList)
    if (node is TreeNode) {
        node.children.forEach { formatAst(it, tokenList, sb, indentTimes + 1, indent) }
    }
}

private fun formatSingleNode(node: Node, sb: StringBuilder, indentTimes: Int, indent: String, tokenList: TokenList) {
    sb.append(indent.repeat(indentTimes))
    sb.append(
        "${node.javaClass.simpleName}(${node.tokenRange.startPosition.toStringShort()}.."
                + "${node.tokenRange.endPosition.toStringShort()})\n"
    )
    if (node is LeafNode) {
        val tokens = tokenList.getRange(node.tokenRange)
        tokens.forEach {
            sb.append(indent.repeat(indentTimes + 1))
            sb.append(formatToken(it))
            sb.append("\n")
        }
    }
}
