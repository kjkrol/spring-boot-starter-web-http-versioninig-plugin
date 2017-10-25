package kjkrol.apiversioning.springframework.web.servlet.mvc.config;

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersion;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;

import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.X_API_VERSION;

@ControllerAdvice
class ApiVersionResponseHeaderModifierAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        for (Annotation annotation : returnType.getMethod().getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(ApiVersion.class)) {
                String[] array = ((ApiVersion) annotation).value();
                String xApiVersionHeaderStr = String.join(", ", array);
                response.getHeaders().add(X_API_VERSION, xApiVersionHeaderStr);
            }
        }
        return body;
    }
}

