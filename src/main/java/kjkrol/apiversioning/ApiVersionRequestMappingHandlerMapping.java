package kjkrol.apiversioning;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        ApiVersion methodAnnotation = findAnnotation(method, ApiVersion.class);
        if (methodAnnotation != null) {
            info = requestHeaderApiVersionInfos(methodAnnotation).combine(info);
        } else {
            ApiVersion typeAnnotation = findAnnotation(handlerType, ApiVersion.class);
            if (typeAnnotation != null) {
                info = requestHeaderApiVersionInfos(typeAnnotation).combine(info);
            }
        }
        return info;
    }

    private RequestMappingInfo requestHeaderApiVersionInfos(ApiVersion annotation) {
        return new RequestMappingInfo(null, null, null,
                createHeadersRequestCondition(annotation.value()),
                null, null, null);
    }

    private HeadersRequestCondition createHeadersRequestCondition(String[] versions) {
        String[] expression = Stream.of(versions)
                .filter(version -> !version.isEmpty())
                .map(version -> "X-API-version=" + version)
                .toArray(String[]::new);
        return new HeadersRequestCondition(expression);
    }

}
