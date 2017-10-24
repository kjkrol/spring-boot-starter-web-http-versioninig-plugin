package kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.LATEST;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.X_API_VERSION;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class ApiVersionAnnotationParser {

    Map<Method, List<RequestMappingInfo>> parseVersionAnnotation(Map<Method, RequestMappingInfo> originalMappings) {
        Map<Method, List<RequestMappingInfo>> result = new HashMap<>();
        originalMappings.entrySet().forEach(entry -> {
            RequestMappingInfo requestMappingInfo = entry.getValue();
            Method method = entry.getKey();
            createMappingForEachVersion(requestMappingInfo, method)
                    .forEach(info -> createOrGetList(result, method).add(info));
            createMappingForVersionSupported(method, requestMappingInfo)
                    .ifPresent(info -> createOrGetList(result, method).add(info));
        });
        return result;
    }

    private List<RequestMappingInfo> createOrGetList(Map<Method, List<RequestMappingInfo>> map, Method method) {
        List<RequestMappingInfo> list = map.get(method);
        if (isNull(list)) {
            list = new ArrayList<>();
            map.put(method, list);
        }
        return list;
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
            return Collections.singletonList(requestMappingInfo);
        }
        return versions.stream()
                .map(version -> X_API_VERSION + "=" + version)
                .map(HeadersRequestCondition::new)
                .map(headersRequestCondition -> addCustomHeaderToRequestMappingInfo(requestMappingInfo, headersRequestCondition))
                .collect(toList());

    }

    private RequestMappingInfo addCustomHeaderToRequestMappingInfo(RequestMappingInfo requestMappingInfo, HeadersRequestCondition headersRequestCondition) {
        return new RequestMappingInfo(null, null, null, headersRequestCondition, null, null, null)
                .combine(requestMappingInfo);
    }

    private Optional<RequestMappingInfo> createMappingForVersionSupported(Method method, RequestMappingInfo requestMappingInfo) {
        ApiVersionSupported annotation = findAnnotation(method, ApiVersionSupported.class);
        if (nonNull(annotation)) {
            return Optional.of(requestMappingInfo);
        }
        return Optional.empty();
    }
}
