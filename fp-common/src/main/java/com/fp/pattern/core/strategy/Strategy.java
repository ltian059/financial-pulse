package com.fp.pattern.core.strategy;

///
/// Strategy Design Pattern Base Interface
///
/// All strategy implementations should extend this interface.
///
public interface Strategy<T, R> {
    boolean supports(T type);

    /**
     * Executes the strategy with the given input.
     * @param input the input for the strategy
     * @return the result of the strategy execution
     */
    R execute(T input);

    String getStrategyName();

    /**
     * Returns the priority of the strategy. (smaller number means higher priority)
     * @return the priority of the strategy
     */
    default int getPriority(){
        return Integer.MAX_VALUE;
    }
}
