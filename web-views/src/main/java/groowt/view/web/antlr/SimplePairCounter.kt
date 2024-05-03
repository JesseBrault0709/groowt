package groowt.view.web.antlr

import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.misc.IntegerStack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class SimplePairCounter(private val lexer: Lexer) : PairCounter {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SimplePairCounter::class.java)
    }

    private class StackEntry(val modes: IntegerStack, val onPop: PairCounter.OnPop?) {
        private var count: Int = 0
        fun get() = this.count
        fun increment(): Int = ++this.count
        fun decrement(): Int = --this.count
    }

    private val stack: Deque<StackEntry> = LinkedList()

    private fun currentEntry(): StackEntry {
        if (this.stack.isEmpty()) {
            throw IllegalStateException("Cannot currentEntry() when stack is empty!")
        }
        return this.stack.peek()
    }

    private fun getLexerModes() = IntegerStack(this.lexer._modeStack).also { it.push(this.lexer._mode) }

    override fun push() {
        this.stack.push(StackEntry(this.getLexerModes(), null))
    }

    override fun push(onPop: PairCounter.OnPop?) {
        this.stack.push(StackEntry(this.getLexerModes(), onPop))
    }

    override fun pop() {
        val entry = this.stack.pop()
        if (entry.onPop != null) {
            entry.onPop.after()
        }
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

}
