package kjkrol.apiversioning.springframework.web.servlet.mvc.config;

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersion;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.HEADER_NAME;

@ControllerAdvice
public class ApiVersionResponseHeaderModifierAdvice implements ResponseBodyAdvice<Object> {

    private static final String NOT_VERSIONED = "not versioned";
    private final Map<Integer, String> methodVersions = new HashMap<>();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        response.getHeaders().add(HEADER_NAME, methodVersions.computeIfAbsent(returnType.hashCode(), key -> scanApiVersionAnnotation(returnType)));
        return body;
    }

    private String scanApiVersionAnnotation(MethodParameter returnType) {
        ApiVersion apiVersion = returnType.getMethodAnnotation(ApiVersion.class);
        if (nonNull(apiVersion)) {
            return String.join(", ", apiVersion.value());

        }
        return NOT_VERSIONED;
    }
}

