package groowt.view.web.antlr;

public interface PairCounter {

    @FunctionalInterface
    interface OnPop {
        void after();
    }

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