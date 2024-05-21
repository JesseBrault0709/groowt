package groowt.view.component.web.antlr

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.Interval

private operator fun Interval.component1(): Int = this.a

private operator fun Interval.component2(): Int = this.b

class WebViewComponentsTokenStream(private val tokenSource: TokenSource) : TokenStream {

    private val tokens: MutableList<Token> = ArrayList()

    private var initialized = false
    private var currentIndex = 0

    private fun atEOF(): Boolean {
        if (this.tokens.isEmpty()) {
            throw IllegalStateException("Must initialize first!")
        } else if (this.currentIndex >= this.tokens.size) {
            throw IllegalStateException(
                "this.currentIndex is greater than or equal to this.tokens.size: "
                        + "currentIndex: ${this.currentIndex}, tokens.size: ${this.tokens.size}"
            )
        }
        return this.tokens[this.currentIndex].type == Token.EOF
    }

    private fun hasEOF() = this.tokens.isNotEmpty() && this.tokens.last().type == Token.EOF

    private fun initialize() {
        if (!this.initialized) {
            this.currentIndex = 0
            val syncResult = this.sync(this.currentIndex)
            if (!syncResult) {
                throw IllegalStateException("Could not sync during initializing!")
            }
            this.initialized = true
        }
    }

    private fun sync(index: Int): Boolean {
        val needed = index - tokens.size + 1 // how many do we need?
        if (needed > 0) {
            val fetchedCount = this.fetch(needed)
            return fetchedCount >= needed
        }
        return true
    }

    private fun syncNextAndIncrementIndex() {
        if (this.sync(this.currentIndex + 1)) {
            this.currentIndex++
        }
    }

    private fun fetch(count: Int): Int {
        if (hasEOF()) {
            return 0
        }
        var fetched = 0
        while (fetched < count) {
            val (groovyTokens, followingToken) = this.fetchGroovyTokens()
            if (groovyTokens.isNotEmpty()) {
                fetched++
                this.tokens.add(
                    MergedGroovyCodeToken(groovyTokens, this.tokens.size, this.tokenSource, this.tokenSource.inputStream)
                )
            }
            if (followingToken != null) {
                fetched++
                if (followingToken is WritableToken) {
                    followingToken.tokenIndex = this.tokens.size
                }
                this.tokens.add(followingToken)
                if (followingToken.type == Token.EOF) {
                    break
                }
            }
        }
        return fetched
    }

    private fun fetchGroovyTokens(): Pair<List<Token>, Token?> {
        val groovyTokens: MutableList<Token> = ArrayList()
        for (next in GroovyTokenSourceIterable(this.tokenSource)) {
            when (next) {
                is GroovyNextToken -> groovyTokens.add(next.token)
                is NonGroovyNextToken -> return Pair(groovyTokens, next.token)
            }
        }
        return Pair(groovyTokens, null)
    }

    override fun consume() {
        if (this.initialized && (this.atEOF() || this.LA(1) == Token.EOF)) {
            throw IllegalStateException("Cannot consume when at EOF!")
        }
        this.syncNextAndIncrementIndex()
    }

    override fun LA(i: Int): Int = when (i) {
        0 -> throw IllegalArgumentException("Cannot LA to index 0.")
        else -> this.LT(i)!!.type
    }

    override fun mark() = 0

    override fun release(marker: Int) = Unit // Do nothing

    override fun index() = this.currentIndex

    override fun seek(index: Int) {
        this.initialize()
        this.currentIndex = index
    }

    override fun size(): Int {
        throw UnsupportedOperationException("Cannot get size() on this stream!")
    }

    override fun getSourceName(): String = this.tokenSource.sourceName

    @Suppress("FunctionName")
    private fun LB(target: Int): Token? {
        val delta = this.currentIndex - target
        return when {
            delta < 0 -> null
            else -> this.tokens.get(delta)
        }
    }

    override fun LT(target: Int): Token? {
        this.initialize()
        return when {
            target == 0 -> null
            target < 0 -> this.LB(target * -1)
            else -> {
                val index = this.currentIndex + target - 1
                this.sync(index)
                if (index >= tokens.size) {
                    return this.tokens.last()
                } else {
                    return this.tokens[index]
                }
            }
        }
    }

    override fun get(index: Int): Token {
        if (index < 0 || index >= tokens.size) {
            throw IndexOutOfBoundsException("Token index $index is out of bounds 0..${this.tokens.size}!")
        }
        return this.tokens[index]
    }

    override fun getTokenSource() = this.tokenSource

    override fun getText(interval: Interval): String {
        val (start, stop) = interval
        if (start < 0 || stop < 0) {
            return ""
        }
        this.sync(stop)
        val target = if (stop >= this.tokens.size) this.tokens.size - 1 else stop
        val b = StringBuilder()
        for (i in start..target) {
            val token = this.tokens[i]
            if (token.type != Token.EOF) {
                b.append(token.text)
            }
        }
        return b.toString()
    }

    override fun getText(): String = this.getText(Interval.of(0, this.size() - 1))

    override fun getText(ctx: RuleContext): String = this.getText(ctx.sourceInterval)

    override fun getText(start: Token?, stop: Token?): String {
        return if (start != null && stop != null) {
            this.getText(Interval.of(start.tokenIndex, stop.tokenIndex))
        } else {
            ""
        }
    }

    fun getAllTokens(): List<Token> {
        this.fill()
        return this.tokens
    }

    fun getAllTokensSkipEOF(): List<Token> {
        this.fill()
        return this.tokens.filter { it.type != Token.EOF }
    }

    private fun fill() {
        val oldIndex = this.currentIndex
        this.initialize()
        if (!this.hasEOF()) {
            while (!this.atEOF()) {
                this.syncNextAndIncrementIndex()
            }
        }
        this.seek(oldIndex)
    }

}
