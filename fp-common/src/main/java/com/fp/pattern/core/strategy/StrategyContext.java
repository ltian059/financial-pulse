package com.fp.pattern.core.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

///
/// Strategy Pattern Context Base class.
///
/// This abstract class offers the selection of strategies and common logic for strategy execution.
///
@Slf4j
public abstract class StrategyContext<T, R> {
    /**
     * Spring managed list of strategies.
     *
     * Every strategy implementation should be annotated with @StrategyComponent or similar to be picked up by Spring.
     */
    @Autowired
    public List<Strategy<T, R>> strategies;

    public R executeStrategy(T input) {
        Strategy<T, R> strategy = selectStrategy(input);
        if(strategy == null) {
            throw new IllegalArgumentException("No strategy found for input: " + input);
        }
        logStrategyExecution(strategy, input);

        return strategy.execute(input);
    }

    /**
     * Selects the appropriate strategy based on the input and priorities of the strategies.
     * @param input the input for which a strategy is needed
     * @return the selected strategy, or null if no suitable strategy is found
     */
    protected Strategy<T,R> selectStrategy(T input) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(input))
                .min(Comparator.comparingInt(Strategy::getPriority))
                .orElse(null);
    }

    protected void logStrategyExecution(Strategy<T,R> strategy, T input) {
        log.debug("Executing Strategy: " + strategy.getStrategyName() +
                " for input: " + input);
    }

    public List<Strategy<T, R>> getAvailableStrategies() {
        return strategies;
    }
}
