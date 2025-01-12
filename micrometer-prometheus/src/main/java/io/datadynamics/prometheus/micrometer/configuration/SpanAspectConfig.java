package io.datadynamics.prometheus.micrometer.configuration;

import io.micrometer.common.annotation.NoOpValueResolver;
import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

@Configuration
public class SpanAspectConfig {

    @Bean
    NewSpanParser newSpanParser() {
        return new DefaultNewSpanParser();
    }

    // You can provide your own resolvers - here we go with a noop example.
    @Bean
    ValueResolver valueResolver() {
        return new NoOpValueResolver();
    }

    // Example of a SpEL resolver
    @Bean
    ValueExpressionResolver valueExpressionResolver() {
        return new SpelTagValueExpressionResolver();
    }

    @Bean
    MethodInvocationProcessor methodInvocationProcessor(NewSpanParser newSpanParser, Tracer tracer, BeanFactory beanFactory) {
        return new ImperativeMethodInvocationProcessor(newSpanParser, tracer, beanFactory::getBean, beanFactory::getBean);
    }

    @Bean
    SpanAspect spanAspect(MethodInvocationProcessor methodInvocationProcessor) {
        return new SpanAspect(methodInvocationProcessor);
    }

    // Example of using SpEL to resolve expressions in @SpanTag
    static class SpelTagValueExpressionResolver implements ValueExpressionResolver {

        private Logger log = LoggerFactory.getLogger(SpelTagValueExpressionResolver.class);

        @Override
        public String resolve(String expression, Object parameter) {
            try {
                SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
                ExpressionParser expressionParser = new SpelExpressionParser();
                Expression expressionToEvaluate = expressionParser.parseExpression(expression);
                return expressionToEvaluate.getValue(context, parameter, String.class);
            } catch (Exception ex) {
                log.error("Exception occurred while tying to evaluate the SpEL expression [" + expression + "]", ex);
            }
            return parameter.toString();
        }

    }
}

