package groowt.view.component.web.antlr

import groowt.view.component.web.antlr.WebViewComponentsLexer.DEFAULT_TOKEN_CHANNEL
import groowt.view.component.web.antlr.WebViewComponentsLexer.GroovyCode
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenSource

open class MergedGroovyCodeToken(
    private val originals: List<Token>,
    private val index: Int,
    private val tokenSource: TokenSource,
    private val inputStream: CharStream
) : Token {

    private val myText: String by lazy { this.originals.joinToString("", transform = Token::getText) }

    private val myLine: Int by lazy { this.originals.first().line }

    private val myCharPositionInLine: Int by lazy { this.originals.first().charPositionInLine }

    private val myStartIndex: Int by lazy { this.originals.first().startIndex }

    private val myStopIndex: Int by lazy { this.originals.last().stopIndex }

    fun getOriginals() = this.originals

    override fun getText() = this.myText

    override fun getType() = GroovyCode

    override fun getLine() = this.myLine

    override fun getCharPositionInLine() = this.myCharPositionInLine

    override fun getChannel() = DEFAULT_TOKEN_CHANNEL

    override fun getTokenIndex() = this.index

    override fun getStartIndex() = this.myStartIndex

    override fun getStopIndex() = this.myStopIndex

    override fun getTokenSource() = this.tokenSource

    override fun getInputStream() = this.inputStream

}
