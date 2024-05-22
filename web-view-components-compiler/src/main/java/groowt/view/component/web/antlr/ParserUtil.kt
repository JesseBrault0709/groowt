@file:JvmName("ParserUtil")
package groowt.view.component.web.antlr

import groowt.view.component.web.antlr.WebViewComponentsParser.CompilationUnitContext
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.Tree
import org.fusesource.jansi.Ansi.ansi
import java.io.File
import java.io.Reader
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

data class CompilationUnitParseResult(
    val lexer: WebViewComponentsLexer,
    val tokenStream: WebViewComponentsTokenStream,
    val parser: WebViewComponentsParser,
    val lexerErrors: List<LexerError>,
    val parserErrors: List<ParserError>,
    val compilationUnitContext: CompilationUnitContext
)

fun parseCompilationUnit(file: File): CompilationUnitParseResult =
    parseCompilationUnit(CharStreams.fromFileName(file.toString()))

fun parseCompilationUnit(source: String): CompilationUnitParseResult =
    parseCompilationUnit(CharStreams.fromString(source))

fun parseCompilationUnit(reader: Reader): CompilationUnitParseResult =
    parseCompilationUnit(CharStreams.fromReader(reader))

fun parseCompilationUnit(charStream: CharStream): CompilationUnitParseResult {
    val lexer = WebViewComponentsLexer(charStream)
    val tokenStream = WebViewComponentsTokenStream(lexer)

    val parser = WebViewComponentsParser(tokenStream)
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
    val parserErrorListener = ParserErrorListener()
    parser.addErrorListener(parserErrorListener)

    val cu = parser.compilationUnit()
    return CompilationUnitParseResult(
        lexer,
        tokenStream,
        parser,
        parserErrorListener.getLexerErrors(),
        parserErrorListener.getParserErrors(),
        cu
    )
}

fun parseCompilationUnit(
    tokenStream: TokenStream,
    onResult: BiConsumer<CompilationUnitContext, WebViewComponentsParser>
) = parse(tokenStream, WebViewComponentsParser::compilationUnit, onResult)

fun <C : ParserRuleContext> parse(tokenStream: TokenStream, resultFunction: Function<WebViewComponentsParser, C>): C =
    resultFunction.apply(WebViewComponentsParser(tokenStream))

fun <C : ParserRuleContext> parse(
    tokenStream: TokenStream,
    resultFunction: Function<WebViewComponentsParser, C>,
    onResult: BiConsumer<C, WebViewComponentsParser>
) {
    val parser = WebViewComponentsParser(tokenStream)
    val c = resultFunction.apply(parser)
    onResult.accept(c, parser)
}

private fun formatForError(parser: Parser, tree: ParseTree): String {
    val name: String = when (tree) {
        is ParserRuleContext -> parser.ruleNames[tree.ruleIndex]
        is TerminalNode -> parser.vocabulary.getDisplayName(tree.symbol.type)
        else -> throw IllegalArgumentException("Unable to determine name for ParseTree: $tree")
    }
    val (line, col) = when (tree) {
        is ParserRuleContext -> Pair(tree.start.line, tree.start.charPositionInLine)
        is TerminalNode -> Pair(tree.symbol.line, tree.symbol.charPositionInLine)
        else -> throw IllegalArgumentException("Unable to determine line/col for ParseTree: $tree")
    }
    return "$name($line,$col)"
}

fun formatTree(parser: Parser, tree: Tree, colors: Boolean, consumer: Consumer<String>): Unit =
    consumer.accept(formatTree(parser, tree, colors).toString())

fun formatTree(parser: Parser, tree: Tree, colors: Boolean = true): String =
    doFormatTree(parser, tree, colors, 0, "  ", StringBuilder()).toString()

fun formatTree(parser: Parser, tree: Tree, colors: Boolean, indentTimes: Int): String =
    doFormatTree(parser, tree, colors, indentTimes, "  ", StringBuilder()).toString()

fun formatTree(parser: Parser, tree: Tree, colors: Boolean, indentTimes: Int, indentText: String): String =
    doFormatTree(parser, tree, colors, indentTimes, indentText, StringBuilder()).toString()

fun formatTree(
    parser: Parser,
    tree: Tree,
    colors: Boolean,
    indentTimes: Int,
    indentText: String,
    sb: StringBuilder
): String = doFormatTree(parser, tree, colors, indentTimes, indentText, sb).toString()

private fun formatBasicInfo(parser: Parser, tree: Tree, sb: StringBuilder) {
    when (tree) {
        is ParserRuleContext -> {
            sb.append(parser.ruleNames[tree.ruleIndex])
                .append(
                    "[${tree.start.line},${tree.start.charPositionInLine + 1}.."
                        + "${tree.stop.line},${tree.stop.charPositionInLine + 1}]"
                )
        }

        is TerminalNode -> {
            sb.append(parser.vocabulary.getDisplayName(tree.symbol.type))
                .append("[${tree.symbol.line},${tree.symbol.charPositionInLine + 1}]")
        }
    }
}

private fun doFormatTree(
    parser: Parser,
    tree: Tree,
    colors: Boolean,
    indentTimes: Int,
    indentText: String,
    sb: StringBuilder
): StringBuilder {
    sb.repeat(indentText, indentTimes)
    if (tree is RuleContext) {
        var e: RecognitionException? = null
        if (tree is ParserRuleContext && tree.exception != null) {
            e = tree.exception
            if (colors) {
                sb.append(ansi().fgRed())
            }
        }
        formatBasicInfo (parser, tree, sb)
        if (e != null) {
            sb.append(": Exception: ${e.javaClass.simpleName}(${escapeChars(tree.text)})")
            if (colors) {
                sb.append(ansi().reset())
            }
        }
        sb . append ("\n")
        var i = 0
        while (i < tree.childCount) {
            doFormatTree(parser, tree.getChild(i), colors, indentTimes + 1, indentText, sb)
            i++
        }
    } else if (tree is TerminalNode) {
        if (colors && tree is ErrorNode) {
            sb.append(ansi().fgRed())
        }
        formatBasicInfo (parser, tree, sb)
        sb.append("(${escapeChars(tree.text)})")
        if (colors) {
            sb.append(
                ansi().reset()
            )
        }
        sb.append ("\n")
    } else {
        if (colors) {
            sb.append(ansi().fgYellow())
        }
        sb.append ("Unknown node: ${tree.toStringTree()}")
        if (colors) {
            sb.append(ansi().reset())
        }
        sb.append ("\n")
    }
    return sb
}
