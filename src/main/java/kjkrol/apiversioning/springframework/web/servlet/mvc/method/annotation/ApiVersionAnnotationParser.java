package kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.LATEST;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.HEADER_NAME;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class ApiVersionAnnotationParser {

    private static final String X_API_VERSION_HEADER_PATTERN = HEADER_NAME + "=%s";

    Map<Method, List<RequestMappingInfo>> parseApiVersioningAnnotations(Map<Method, RequestMappingInfo> originalMappings) {
        Map<Method, List<RequestMappingInfo>> result = new HashMap<>();
        originalMappings.forEach((method, requestMappingInfo) -> {
            createMappingForEachVersion(requestMappingInfo, method)
                    .forEach(info -> result
                            .computeIfAbsent(method, x -> new ArrayList<>())
                            .add(info));
            createMappingForVersionSupported(method, requestMappingInfo)
                    .ifPresent(info -> result
                            .computeIfAbsent(method, x -> new ArrayList<>())
                            .add(info));
        });
        return result;
    }

    private List<RequestMappingInfo> createMappingForEachVersion(RequestMappingInfo requestMappingInfo, Method method) {
        ApiVersion apiVersion = findAnnotation(method, ApiVersion.class);
        ApiVersionLatest apiVersionLatest = findAnnotation(method, ApiVersionLatest.class);
        List<String> versions = new ArrayList<>();
        if (nonNull(apiVersionLatest)) {
            versions.add(LATEST);
        }
        if (nonNull(apiVersion)) {
            versions.addAll(asList(apiVersion.value()));
        }
        if (versions.isEmpty()) {
            return singletonList(requestMappingInfo);
        }
        return versions.stream()
                .map(version -> format(X_API_VERSION_HEADER_PATTERN, version))
                .map(ApiVersionHeadersRequestCondition::new)
                .map(headersRequestCondition -> new RequestMappingInfo(requestMappingInfo, headersRequestCondition))
                .collect(toList());
    }

    private Optional<RequestMappingInfo> createMappingForVersionSupported(Method method, RequestMappingInfo requestMappingInfo) {
        ApiVersionSupported annotation = findAnnotation(method, ApiVersionSupported.class);
        if (nonNull(annotation)) {
            return of(new RequestMappingInfo(requestMappingInfo, new ApiVersionHeadersRequestCondition()));
        }
        return empty();
    }
}
