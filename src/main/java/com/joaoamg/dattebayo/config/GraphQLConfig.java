package com.joaoamg.dattebayo.config;

import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Configuration
public class GraphQLConfig {

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {


        return (Throwable ex, DataFetchingEnvironment environment) -> {


            SourceLocation location = environment.getField().getSourceLocation();

            if (ex instanceof ResourceNotFoundException) {

                GraphQLError error = createError(
                        ex.getMessage(),
                        "NOT_FOUND",
                        location
                );
                return Mono.just(List.of(error));
            }

            if (ex instanceof BusinessRuleException) {

                GraphQLError error = createError(
                        ex.getMessage(),
                        "BUSINESS_RULE_VIOLATION",
                        location
                );
                return Mono.just(List.of(error));
            }


            return Mono.empty();
        };
    }

    private GraphQLError createError(String message, String errorCode, SourceLocation location) {
        return GraphQLError.newError()
                .message(message)
                .location(location)
                .extensions(Map.of("code", errorCode))
                .build();
    }
}