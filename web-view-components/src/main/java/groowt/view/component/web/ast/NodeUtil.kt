package groowt.view.component.web.ast

import groowt.view.component.web.antlr.TokenList
import groowt.view.component.web.antlr.formatToken
import groowt.view.component.web.ast.node.LeafNode
import groowt.view.component.web.ast.node.Node

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
