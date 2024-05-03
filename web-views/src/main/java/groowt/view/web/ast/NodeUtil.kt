package groowt.view.web.ast

import groowt.view.web.antlr.TokenList
import groowt.view.web.antlr.formatToken
import groowt.view.web.ast.node.LeafNode
import groowt.view.web.ast.node.Node

fun formatSingleNode(node: Node, sb: StringBuilder, indentTimes: Int, indent: String, tokenList: TokenList) {
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
