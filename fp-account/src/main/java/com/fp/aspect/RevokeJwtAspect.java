package com.fp.aspect;

import com.fp.annotation.RevokeJwt;
import com.fp.auth.service.JwtService;
import com.fp.auth.service.RevokedJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RevokeJwtAspect {
    private final JwtService jwtService;
    private final RevokedJwtService revokedJwtService;
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("execution(* * (.., @(com.fp.annotation.RevokeJwt)(*),..))")
    public void parameterLevel(){}
    @Pointcut("@annotation(com.fp.annotation.RevokeJwt)")
    public void methodLevel(){}

    @Pointcut("parameterLevel() || methodLevel()")
    public void pointcut(){
    }

    /**
     * After advice that revokes the JWT token regardless of the method outcome.
     */
    @After("pointcut()")
    public void revokeTokenAfterAny(JoinPoint joinPoint) {
        processTokenRevocation(joinPoint, RevokeJwt.RevokeTokenAfter.ANY, "Method executed.");
    }


    /**
     * AfterThrowing advice that revokes the JWT token only if the operation failed.
     */
    @AfterThrowing(pointcut = "pointcut()", throwing = "ex")
    public void  revokeTokenAfterFailure(JoinPoint joinPoint, Exception ex) {
        processTokenRevocation(joinPoint, RevokeJwt.RevokeTokenAfter.FAILURE,
                "Method failed with exception: " + ex.getMessage());
    }

    /**
     * Revoke tokens after successful method execution.
     */
    @AfterReturning("pointcut()")
    public void revokeTokenAfterSuccess(JoinPoint joinPoint) {
        processTokenRevocation(joinPoint, RevokeJwt.RevokeTokenAfter.SUCCESS,  "Method executed successfully.");
    }

    /**
     * Process token revocation based on the RevokeJwt annotation on Method and Parameters.
     * @param joinPoint the join point representing the method execution
     * @param timing the timing of the revocation (success, failure, or any)
     * @param reason the reason for revocation, can be null or empty
     */
    private void processTokenRevocation(JoinPoint joinPoint, RevokeJwt.RevokeTokenAfter timing, String reason) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        //Collect all revocation tasks
        List<RevocationTask> tasks = new ArrayList<>();
        //1. Check method level revokation
        RevokeJwt methodAnnotation = method.getAnnotation(RevokeJwt.class);
        if(methodAnnotation != null && methodAnnotation.revokeTokenAfter().equals(timing)){
            // create task
            RevocationTask task = createMethodLevelTask(joinPoint, methodAnnotation, reason);
            tasks.add(task);
        }
        //2. Check parameter level revocation.
        List<RevocationTask> parameterTasks = createParameterLevelTasks(method, args, timing, reason, joinPoint);
        tasks.addAll(parameterTasks);

        //3. Execute all revocation tasks
        for(RevocationTask task : tasks) {
            executeRevocationTask(task);
        }
    }

    private void executeRevocationTask(RevocationTask task) {
        revokedJwtService.revokeJwt(task.jwt, task.reason);
    }

    private List<RevocationTask> createParameterLevelTasks(Method method, Object[] args, RevokeJwt.RevokeTokenAfter timing, String reason, JoinPoint joinPoint) {
        List<RevocationTask> parameterLevelTasks = new ArrayList<>();
        //1. find all parameters with @RevokeJwt annotation
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RevokeJwt parameterAnnotation = parameter.getAnnotation(RevokeJwt.class);
            if(parameterAnnotation != null && parameterAnnotation.revokeTokenAfter().equals(timing)) {
                Jwt jwt = null;
                if(args[i] instanceof Jwt){
                    // 2. If the parameter is a Jwt object, create a task directly
                    jwt = (Jwt) args[i];
                }else if(args[i] instanceof String){
                    //3. it should be a token string, decode it to Jwt object
                    jwt = jwtService.decode((String) args[i]);
                }
                if(jwt == null){
                    throw new JwtException("The parameter " + parameterNames[i] + " is not a valid Jwt or token string");
                }
                String parameterName = parameterNames[i] != null ? parameterNames[i] : "parameter[" + i + "]";
                String source = "parameter: '" + parameterName + "'";
                String fullReason = buildRevocationReason(parameterAnnotation.reason(), reason, method.getName(), source);
                RevocationTask task = new RevocationTask(jwt, fullReason, source);
                parameterLevelTasks.add(task);
            }
        }

        return  parameterLevelTasks;
    }

    private RevocationTask createMethodLevelTask(JoinPoint joinPoint, RevokeJwt methodAnnotation, String reason) {
        Jwt jwt;
        String source;
        if(methodAnnotation.tokenName() != null && !methodAnnotation.tokenName().isEmpty()){
            // Use the token from method parameter
            jwt = extractJwtByName(joinPoint, methodAnnotation.tokenName());
            source = "method parameter: " + methodAnnotation.tokenName();
        }else{
            jwt = jwtService.getJwtFromAuthContext();
            source = "security context";
        }
        String fullReason = buildRevocationReason(methodAnnotation.reason(), reason,
                joinPoint.getSignature().getName(), source);

        return new RevocationTask(jwt, fullReason, source);

    }

    private String buildRevocationReason(String customReason, String triggerReason, String methodName, String source) {
        StringBuilder reasonBuilder = new StringBuilder();

        if (customReason != null && !customReason.isEmpty()) {
            reasonBuilder.append(customReason);
        } else {
            reasonBuilder.append("Token revoked");
        }

        reasonBuilder.append(" after ").append(triggerReason)
                .append(" in method: ").append(methodName)
                .append(" (source: ").append(source).append(")");

        return reasonBuilder.toString();
    }

    private Jwt extractJwtByName(JoinPoint joinPoint, String tokenParameterName) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            Object[] args = joinPoint.getArgs();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            Object jwt = null;
            for(int i = 0; i < parameterNames.length; i++){
                if(parameterNames[i].equals(tokenParameterName)){
                    jwt = args[i];
                    break;
                }
            }
            if(jwt instanceof Jwt){
                return (Jwt) jwt;
            }
            if(jwt instanceof String){
                //jwt is a token string, decode it to Jwt object
                return jwtService.decode((String) jwt);
            }
            throw new JwtException(String.format("The token parameter %s is not a valid Jwt", tokenParameterName));
        }catch (JwtException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class RevocationTask {
        final Jwt jwt;
        final String reason;
        final String source;
        RevocationTask(Jwt jwt, String reason, String source) {
            this.jwt = jwt;
            this.reason = reason;
            this.source = source;
        }
    }
}
