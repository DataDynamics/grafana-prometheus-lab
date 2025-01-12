package io.datadynamics.prometheus.micrometer.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SchedulingAspect {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        System.out.println("Executed method: " + className + "." + methodName + " in " + executionTime + "ms");
        return proceed;
    }
}