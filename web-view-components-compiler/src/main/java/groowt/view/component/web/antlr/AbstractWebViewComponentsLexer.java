package groowt.view.component.web.antlr;

import groovyjarjarantlr4.runtime.Token;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static groowt.view.component.web.antlr.LexerSemanticPredicates.isAnyOf;
import static groowt.view.component.web.antlr.TokenUtil.escapeChars;

public abstract class AbstractWebViewComponentsLexer extends Lexer {

    protected static final class PositionAdjustingLexerATNSimulator extends LexerATNSimulator {

        public PositionAdjustingLexerATNSimulator(
                Lexer recognizer,
                ATN atn,
                DFA[] decisionToDFA,
                PredictionContextCache sharedContextCache
        ) {
            super(recognizer, atn, decisionToDFA, sharedContextCache);
        }

        public void resetAcceptPosition(CharStream input, int index, int line, int charPositionInLine, boolean consume) {
            input.seek(index);
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            if (consume) {
                this.consume(input);
            }
        }

    }

    protected sealed interface GStringPathEndSpec permits StringContinueEndSpec, StringClosingEndSpec {}

    protected record StringContinueEndSpec() implements GStringPathEndSpec {}

    protected record StringClosingEndSpec(int type, int popCount) implements GStringPathEndSpec {}

    protected PairCounter curlies = new SimplePairCounter();
    protected PairCounter parentheses = new SimplePairCounter();

    private final Logger logger;

    private boolean canPreamble = true;
    private boolean inPreamble;
    private boolean inConstructor;

    public AbstractWebViewComponentsLexer(CharStream input) {
        super(input);
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.curlies.setLexer(this);
        this.parentheses.setLexer(this);
    }

    public AbstractWebViewComponentsLexer() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.curlies.setLexer(this);
        this.parentheses.setLexer(this);
    }

    public PairCounter getCurlies() {
        return this.curlies;
    }

    public void setCurlies(PairCounter curlies) {
        this.curlies = curlies;
    }

    public PairCounter getParentheses() {
        return this.parentheses;
    }

    public void setParentheses(PairCounter parentheses) {
        this.parentheses = parentheses;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public boolean canPreamble() {
        return this.canPreamble;
    }

    public void setCanPreamble(boolean canPreamble) {
        this.canPreamble = canPreamble;
    }

    public boolean inPreamble() {
        return this.inPreamble;
    }

    public void setInPreamble(boolean inPreamble) {
        this.inPreamble = inPreamble;
    }

    public boolean inConstructor() {
        return this.inConstructor;
    }

    public void setInConstructor(boolean inConstructor) {
        this.inConstructor = inConstructor;
    }

    @Override
    public void reset() {
        this.curlies.clear();
        this.parentheses.clear();
        this.canPreamble = true;
        this.inPreamble = false;
        this.inConstructor = false;
        super.reset();
    }

    protected String getModeName(int m) {
        return this.getModeNames()[m];
    }

    @Override
    public void pushMode(int m) {
        if (logger.isTraceEnabled()) {
            final var old = this._mode;
            super.pushMode(m);
            final var delta = this._mode;
            logger.trace("pushMode: target {} to {}", this.getModeName(old), this.getModeName(delta));
        } else {
            super.pushMode(m);
        }
    }

    @Override
    public int popMode() {
        if (logger.isTraceEnabled()) {
            final var popped = this._mode;
            final var delta = super.popMode();
            logger.trace("popMode: to {} target {}", this.getModeName(popped), this.getModeName(delta));
            return popped;
        } else {
            return super.popMode();
        }
    }

    protected int peekMode() {
        return this._mode;
    }

    protected int peekMode(int index) {
        if (index == 0) {
            return this._mode;
        } else {
            final Deque<Integer> tempStack = new LinkedList<>();
            Integer result = null;
            for (int i = 0; i < index; i++) {
                final var cur = this._modeStack.pop();
                tempStack.push(cur);
                if (i + 1 == index) {
                    result = cur;
                }
            }
            if (result == null) {
                throw new IllegalStateException("did not find result in peek mode");
            }
            for (int i = 0; i < tempStack.size(); i++) {
                this._modeStack.push(tempStack.pop());
            }
            return result;
        }
    }

    protected void enterPreamble() {
        this.inPreamble = true;
        this.canPreamble = false;
    }

    protected void exitPreamble() {
        this.inPreamble = false;
    }

    protected void enterConstructor() {
        this.parentheses.push(this::popMode);
        this.parentheses.increment();
        this.inConstructor = true;
    }

    protected boolean canExitConstructor() {
        return this.inConstructor && this.parentheses.getStackSize() == 1 && this.parentheses.isLast();
    }

    protected void exitConstructor() {
        this.parentheses.pop();
        this.inConstructor = false;
    }

    protected String getNextCharsAsString(int numberOfChars) {
        final var b = new StringBuilder();
        for (int i = 1; i <= numberOfChars; i++) {
            b.append((char) this._input.LA(i));
        }
        return b.toString();
    }

    protected String getNextCharAsString() {
        return Character.toString((char) this.getNextChar());
    }

    protected int getCurrentChar() {
        return this._input.LA(-1);
    }

    protected int getNextChar() {
        return this._input.LA(1);
    }

    protected boolean isNext(char test) {
        return this._input.LA(1) == test;
    }

    protected boolean isNext(String test) {
        return this.getNextCharsAsString(test.length()).equals(test);
    }

    protected boolean isNextIgnoreNlws(char test) {
        for (int i = 1; this._input.LA(i) != Token.EOF; i++) {
            final char subject = (char) this._input.LA(i);
            if (!isAnyOf(subject, ' ', '\t', '\n', '\r')) {
                return subject == test;
            }
        }
        return false;
    }

    @Override
    public final LexerATNSimulator getInterpreter() {
        return this._interp;
    }

    protected abstract PositionAdjustingLexerATNSimulator getPositionAdjustingInterpreter();

    protected void rollbackOne() {
        this.rollbackOne(false);
    }

    protected void rollbackOne(boolean consume) {
        this.getPositionAdjustingInterpreter().resetAcceptPosition(
                this._input,
                Math.max(this._tokenStartCharIndex - 1, 0),
                this._tokenStartLine,
                Math.max(this._tokenStartCharIndex - 1, 0),
                consume
        );
    }

    // For debugging purposes
    protected record CurrentInfo(String currentText, int line, int col, int index, int mode, int[] modeStack) {}

    // For debugging purposes
    protected final Function<CurrentInfo, String> dumpCurrentInfo = currentInfo -> {
        return new StringBuilder("CurrentInfo(text: ")
                .append(escapeChars(currentInfo.currentText))
                .append(", line: ")
                .append(currentInfo.line)
                .append(", col: ")
                .append(currentInfo.col)
                .append(", index: ")
                .append(currentInfo.index)
                .append(", mode: ")
                .append(this.getModeName(currentInfo.mode))
                .append(", modeStack: ")
                .append(Arrays.stream(currentInfo.modeStack)
                        .mapToObj(this::getModeName)
                        .collect(Collectors.joining(", ", "[", "]"))
                )
                .append(")")
                .toString();
    };

    // For debugging purposes
    private CurrentInfo getCurrentInfo() {
        final LexerATNSimulator interpreter = this.getInterpreter();
        return new CurrentInfo(
                this.getText(),
                interpreter.getLine(),
                interpreter.getCharPositionInLine(),
                this.getInputStream().index(),
                this._mode,
                this._modeStack.toArray()
        );
    }

    // For debugging purposes
    protected void debugHook(String name) {
        logger.debug("hooked: {}", name);
    }

    // For debugging purposes
    protected void debugHook(String name, Function<CurrentInfo, String> msgFunction) {
        if (logger.isDebugEnabled() && msgFunction != null) {
            logger.debug("hooked: {}; msg: {}", name, msgFunction.apply(this.getCurrentInfo()));
        } else {
            logger.debug("hooked: {}", name);
        }
    }

    // For debugging purposes
    protected <T> T debugHookReturning(String name, T returnValue) {
        this.debugHook(name);
        return returnValue;
    }

    // For debugging purposes
    protected <T> T debugHookReturning(String name, Function<CurrentInfo, String> msgFunction, T returnValue) {
        this.debugHook(name, msgFunction);
        return returnValue;
    }

    // For debugging purposes
    protected boolean debugHookSemPred(String name) {
        return this.debugHookReturning(name, true);
    }

    // For debugging purposes
    protected boolean debugHookSemPred(String name, Function<CurrentInfo, String> msgFunction) {
        return this.debugHookReturning(name, msgFunction, true);
    }

}
