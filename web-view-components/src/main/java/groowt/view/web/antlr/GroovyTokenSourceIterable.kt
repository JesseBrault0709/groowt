package groowt.view.web.antlr

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenSource

internal sealed interface NextToken {
    val token: Token
}

internal data class GroovyNextToken(override val token: Token) : NextToken

internal data class NonGroovyNextToken(override val token: Token) : NextToken

internal class GroovyTokenSourceIterable(private val tokenSource: TokenSource) : Iterable<NextToken> {
    override fun iterator() = GroovyTokenSourceIterator(this.tokenSource)
}

internal class GroovyTokenSourceIterator(private val tokenSource: TokenSource) : Iterator<NextToken> {

    private var done: Boolean = false

    override fun hasNext() = !done

    override fun next(): NextToken {
        if (this.done) throw IllegalStateException("Cannot next() when hasNext() == false")
        val next = this.tokenSource.nextToken()
        if (isGroovyTokenType(next)) {
            return GroovyNextToken(next)
        } else {
            this.done = true
            return NonGroovyNextToken(next)
        }
    }

}
