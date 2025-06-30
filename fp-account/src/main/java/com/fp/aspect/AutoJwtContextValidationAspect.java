package com.fp.aspect;

import com.fp.annotation.RevokeJwt;
import com.fp.auth.service.JwtService;
import com.fp.auth.service.RevokedJwtService;
import com.fp.constant.Messages;
import com.fp.exception.business.JwtContextException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;


/**
 * Automatic JWT context validation for write operations (POST, PUT, DELETE, PATCH).
 *
 * This aspect automatically validates JWT context for all controller methods
 * that perform write operations, eliminating the need to manually add annotations.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AutoJwtContextValidationAspect {
    private final JwtService jwtService;


    /**
     * Pointcut for all write operations in controllers.
     * Matches methods annotated with @PostMapping, @PutMapping, @DeleteMapping, @PatchMapping
     * in any class within the controller packages.
     */
    @Pointcut(
            """
            within(com.fp.controller.AccountController) && (
            @annotation(org.springframework.web.bind.annotation.PostMapping) ||
            @annotation(org.springframework.web.bind.annotation.PutMapping) ||
            @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
            @annotation(org.springframework.web.bind.annotation.PatchMapping)
            )
            """
    )
    public void writeOperations(){}

    /**
     * Intercepts methods annotated with @ValidateJwtContext and performs validation.
     * @param joinPoint
     */
    @Before("writeOperations()")
    public void validateJwtContext(JoinPoint joinPoint){
        log.debug("Validating JWT context for method: {}", joinPoint.getSignature().getName());

        //Get JWT from the context
        Jwt jwt = jwtService.getJwtFromAuthContext();
        Optional<String> jwtAccountIdOpt = jwtService.getAccountIdFromToken(jwt.getTokenValue());
        Optional<String> jwtEmailOpt = jwtService.getEmailFromToken(jwt.getTokenValue());

        // Check if JWT claims are present
        if (jwtAccountIdOpt.isEmpty()) {
            log.warn("Account ID not found in JWT context for method: {}", joinPoint.getSignature().getName());
            throw new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR);
        }

        if (jwtEmailOpt.isEmpty()) {
            log.warn("Email not found in JWT context for method: {}", joinPoint.getSignature().getName());
            throw new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR);
        }
        // Find and validate request body parameter
        validateRequestBodyParameters(joinPoint, jwtAccountIdOpt.get(), jwtEmailOpt.get());
    }

    @After("writeOperations()")
    public void handleAfterWriteOperation(JoinPoint joinPoint) {
        log.info("🔄 [@After] 写操作完成 - 方法: {}", joinPoint.getSignature().getName());

        // 检查是否有@RevokeJwt注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RevokeJwt revokeAnnotation = method.getAnnotation(RevokeJwt.class);

        if (revokeAnnotation != null && revokeAnnotation.revokeTokenAfter() == RevokeJwt.RevokeTokenAfter.SUCCESS) {
            log.info("✅ 发现@RevokeJwt注解，执行令牌撤销");
//            processTokenRevocation(joinPoint, revokeAnnotation);
        } else {
            log.info("ℹ️ 未发现@RevokeJwt注解或时机不匹配，跳过令牌撤销");
        }
    }

    /**
     * After retrieving the JWT context, this method validates the request body parameters
     * @param joinPoint The join point representing the method execution
     * @param jwtAccountId The account ID from the JWT context
     * @param jwtEmail The email from the JWT context
     */
    private void validateRequestBodyParameters(JoinPoint joinPoint, String jwtAccountId, String jwtEmail) {
        //Get method arguments
        Object[] args = joinPoint.getArgs();
        if(args.length == 0){
            log.warn("No arguments found in method for JWT context validation");
            throw new JwtContextException(Messages.Error.Auth.JWT_CONTEXT_REQUEST_BODY_ERROR);
        }
        //Find the @RequestBody parameter
        Object requestBody = findRequestBody(joinPoint);
        //Extract accountId and email from the request body
        if(requestBody == null){
            throw new JwtContextException(Messages.Error.Auth.JWT_CONTEXT_REQUEST_BODY_ERROR);
        }
        validateFieldIfExists(requestBody, "accountId", jwtAccountId, joinPoint);
        validateFieldIfExists(requestBody, "email", jwtEmail, joinPoint);
    }

    /**
     *
     * @param requestBody The method parameter with @RequestBody annotation
     * @param fieldName The name of the field to validate in the request body(e.g., "accountId", "email")
     * @param expectedValue The value from the JWT context to compare against
     * @param joinPoint The join point representing the method execution
     */
    private void validateFieldIfExists(Object requestBody, String fieldName, String expectedValue, JoinPoint joinPoint) {
        try {
            String fieldValue = extractFieldValue(requestBody, fieldName);
            if(fieldValue != null){
                if(!fieldValue.equals(expectedValue)){
                    log.warn("JWT {} '{}' does not match request {} '{}' for method: {}",
                            fieldName.toLowerCase(), expectedValue, fieldName.toLowerCase(),
                            fieldValue, joinPoint.getSignature().getName());
                    throw new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR);
                }
                log.debug("{} validation passed for method: {}", fieldName, joinPoint.getSignature().getName());
            }else{
                log.debug("{} field not found in request object for method: {}", fieldName, joinPoint.getSignature().getName());
                throw new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR);
            }
        }catch (JwtContextException e){
            throw e;
        } catch (Exception e) {
            log.debug("Error finding @RequestBody parameter: {}", e.getMessage());
        }
    }


    /**
     * Finds the @RequestBody parameter in the method arguments.
     */
    private Object findRequestBody(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for(Annotation annotation : parameterAnnotations[i]){
                if(annotation instanceof RequestBody){
                    return args[i];
                }
            }
        }
        return null;
    }


    private String extractFieldValue(Object requestObject, String fieldName) {
        try{
            Field field = requestObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(requestObject);
            return value != null ? value.toString() : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.debug("Field '{}' not found in object of type {}", fieldName, requestObject.getClass().getSimpleName());
            throw new JwtContextException(Messages.Error.Auth.JWT_CONTEXT_REQUEST_BODY_ERROR, e);
        }
    }
}
