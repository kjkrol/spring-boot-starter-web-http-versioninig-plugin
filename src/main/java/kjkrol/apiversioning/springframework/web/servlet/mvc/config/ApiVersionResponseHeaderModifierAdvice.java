package kjkrol.apiversioning.springframework.web.servlet.mvc.config;

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersion;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.LATEST;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.X_API_VERSION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
class ApiVersionResponseHeaderModifierAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String[] requestMappingVersions = null;
        for (Annotation annotation : returnType.getMethod().getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(ApiVersion.class)) {
                requestMappingVersions = ((ApiVersion) annotation).value();
                String xApiVersionHeaderStr = String.join(", ", requestMappingVersions);
                response.getHeaders().add(X_API_VERSION, xApiVersionHeaderStr);
            }
        }
        String version = request.getHeaders().getFirst("X-API-version");

        if (!LATEST.equals(version)) {
            System.out.println(version + " " + LATEST);
            response.setStatusCode(BAD_REQUEST);
        } else if (!Arrays.stream(requestMappingVersions).peek(System.out::println).anyMatch(s -> s.equals(version))) {
            response.setStatusCode(BAD_REQUEST);
        }
        try {
            response.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
}

