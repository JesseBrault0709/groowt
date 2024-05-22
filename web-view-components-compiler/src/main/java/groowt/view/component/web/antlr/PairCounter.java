package groowt.view.component.web.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.IntegerStack;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;

public interface PairCounter {

    @FunctionalInterface
    interface OnPop {
        void after();
    }

    interface StackEntry {
        IntegerStack getModes();
        @Nullable OnPop getOnPop();
        int get();
        int increment();
        int decrement();
    }

    Deque<StackEntry> getStack();
    void setLexer(Lexer lexer);

    void push();
    void push(OnPop onPop);
    void pop();
    void increment();
    void decrement();
    boolean isCounting();
    boolean isLast();
    int getCurrentCount();
    int getStackSize();
    void clear();

}
