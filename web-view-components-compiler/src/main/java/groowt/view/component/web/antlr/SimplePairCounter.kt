package groowt.view.component.web.antlr

import groowt.view.component.web.antlr.PairCounter.StackEntry
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.misc.IntegerStack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class SimplePairCounter : PairCounter {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SimplePairCounter::class.java)
    }

    class SimpleStackEntry(private val modes: IntegerStack, private val onPop: PairCounter.OnPop?) : StackEntry {

        private var count: Int = 0

        override fun getModes() = modes
        override fun getOnPop() = onPop
        override fun get() = this.count
        override fun increment(): Int = ++this.count
        override fun decrement(): Int = --this.count

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StackEntry) return false
            return modes == other.modes
                    && onPop == other.onPop
                    && get() == other.get()
        }

        override fun hashCode() = Objects.hash(modes, onPop, count)

        override fun toString() = "SimpleStackEntry($count)"

    }

    private val stack: Deque<SimpleStackEntry> = LinkedList()
    private var lexer: Lexer? = null

    override fun getStack(): Deque<StackEntry> = LinkedList(stack)

    override fun setLexer(lexer: Lexer?) {
        this.lexer = lexer
    }

    private fun currentEntry(): SimpleStackEntry {
        if (this.stack.isEmpty()) {
            throw IllegalStateException("Cannot currentEntry() when stack is empty!")
        }
        return this.stack.peek()
    }

    private fun getLexerModes(): IntegerStack {
        if (lexer == null) {
            throw IllegalStateException("lexer is null")
        }
        return IntegerStack(lexer!!._modeStack).also { it.push(lexer!!._mode) }
    }

    override fun push() {
        this.stack.push(SimpleStackEntry(this.getLexerModes(), null))
    }

    override fun push(onPop: PairCounter.OnPop?) {
        this.stack.push(SimpleStackEntry(this.getLexerModes(), onPop))
    }

    override fun pop() {
        val entry = this.stack.pop()
        entry.onPop?.after()
        if (logger.isWarnEnabled) {
            val currentModes = this.getLexerModes()
            if (entry.modes != currentModes) {
                logger.warn(
                    "popped counter entry's modes differ target current modes; "
                            + "old modes: {}, new modes: {}; "
                            + "did you pop the counter target a different mode?",
                    entry.modes,
                    currentModes
                )
            }
        }
    }

    override fun increment() {
        this.currentEntry().increment()
    }

    override fun decrement() {
        val newCount = this.currentEntry().decrement()
        if (newCount < 1) {
            throw IllegalStateException("Should never decrement below 1!")
        }
    }

    override fun isCounting() = this.stack.isNotEmpty()

    override fun isLast() = this.currentCount == 1

    override fun getCurrentCount() = this.currentEntry().get()

    override fun getStackSize() = this.stack.size

    override fun clear() {
        this.stack.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PairCounter) return false
        return stack == other.stack
    }

    override fun hashCode(): Int = Objects.hash(stack)

}
