package com.nbh.edushare.common.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

        if (ex instanceof AppException appEx) {
            ErrorCode errorCode = appEx.getErrorCode();
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(errorCode.getMessage())
                    .extensions(Map.of("code", errorCode.name(), "status", errorCode.getStatus()))
                    .build();
        }

        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.UNAUTHORIZED)
                    .message("Bạn cần đăng nhập để thực hiện thao tác này")
                    .build();
        }

        if (ex instanceof PropertyReferenceException propEx) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message("Trường sắp xếp '" + propEx.getPropertyName() + "' không tồn tại trong hệ thống.")
                    .extensions(Map.of(
                            "code", "INVALID_SORT_PROPERTY",
                            "status", 400
                    ))
                    .build();
        }

        log.error("Unhandled exception in GraphQL field [{}]: ", env.getExecutionStepInfo().getPath(), ex);
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("Hệ thống đang gặp sự cố")
                .build();
    }
}