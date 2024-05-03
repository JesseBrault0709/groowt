package groowt.util.di;

import org.jetbrains.annotations.Contract;

import java.util.function.Function;

/**
 * An {@link ObjectFactory} is an object that can construct objects of given types.
 */
@FunctionalInterface
public interface ObjectFactory {

    /**
     * Create a new instance of the given {@code instanceType} with the given constructor args.
     *
     * @apiNote An implementation may provide a subclass of the given instance type,
     * or it may directly instantiate the given type, if it is a class
     * and it can determine the correct constructor from the given arguments.
     * See individual implementation documentation for exact behavior.
     *
     * @implSpec It is up to individual implementations of {@link ObjectFactory} to determine how to
     * select the appropriate constructor for the given type. The returned
     * instance must be new and in a valid state.
     *
     * @param instanceType the {@link Class} of the desired type
     * @param constructorArgs any arguments to pass to the constructor(s) of the class.
     * @return the new instance
     * @param <T> the desired type
     */
    @Contract("_, _-> new")
    <T> T createInstance(Class<T> instanceType, Object... constructorArgs);

    /**
     * Very similar to {@link #createInstance(Class, Object...)}, but catches any {@link RuntimeException}
     * thrown by {@link #createInstance} and subsequently passes it to the given {@link Function}, returning
     * instead the return value of the {@link Function}.
     *
     * @param instanceType the desired type of the created instance
     * @param onException a {@link Function} to handle when an exception occurs and return a value nonetheless
     * @param constructorArgs arguments to pass to the constructor
     * @return the created instance
     * @param <T> the desired type
     *
     * @throws RuntimeException if the given {@link Function} itself throws a RuntimeException
     *
     * @see #createInstance(Class, Object...)
     */
    @Contract("_, _, _ -> new")
    default <T> T createInstanceCatching(
            Class<T> instanceType,
            Function<? super RuntimeException, ? extends T> onException,
            Object... constructorArgs
    ) {
        try {
            return this.createInstance(instanceType, constructorArgs);
        } catch (RuntimeException runtimeException) {
            return onException.apply(runtimeException);
        }
    }

}
