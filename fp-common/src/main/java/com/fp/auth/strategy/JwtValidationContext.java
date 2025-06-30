package com.fp.auth.strategy;

import com.fp.constant.JwtClaimsKey;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.core.strategy.Strategy;
import com.fp.pattern.core.strategy.StrategyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Comparator;

///
/// # Context for JWT validation strategies.
///
/// This class extends the `StrategyContext` to provide a context for JWT validation strategies.
///
/// Use for managing the execution of different JWT validation strategies based on the request.
@Component
@Slf4j
public class JwtValidationContext extends StrategyContext<JwtValidationRequest, JwtValidationResult> {

    /**
     * <h1>The super method can only select the highest priority strategy that supports the input.</h1>
     * <p>
     *     However, in the case of the post-validation, the highest priority we set is the revoked token strategy.
     *     This means that if the token is not revoked, it will not be validated by the other strategies.
     *     Therefore, we need to override this method to select the strategy based on the JWT type and the request URI.
     * </p>
     *
     * <p>
     *     Execute all applicable JWT validation strategies in priority order.
     *     Stop at first failure.
     * </p>
     * @param input the input for which a strategy is needed
     */
    @Override
    public JwtValidationResult executeStrategy(JwtValidationRequest input) {
        Jwt jwt = input.getJwt();
        String jwtType = jwt.getClaimAsString(JwtClaimsKey.TYPE);
        var applicableStrategies = strategies.stream()
                .filter(s -> s.supports(input))
                .sorted(Comparator.comparingInt(Strategy::getPriority))
                .toList();

        //Use the sorted and all applicable strategies to validate the JWT
        for(var strategy : applicableStrategies) {
            log.debug("Executing JWT validation strategy: {} (priority: {})",
                    strategy.getStrategyName(), strategy.getPriority());
            JwtValidationResult result = strategy.execute(input);
            if (!result.isValid()) {
                return result; // Stop at first failure
            }
            log.debug("Strategy {} passed validation", strategy.getStrategyName());
        }
        return JwtValidationResult.success();
    }

    public JwtValidationResult executeValidationStrategy(JwtValidationRequest input) {
        return executeStrategy(input);
    }
}
