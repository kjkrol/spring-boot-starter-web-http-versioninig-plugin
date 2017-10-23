package kjkrol.apiversioning;

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
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class ApiVersioningAnnotationParser {

    private static final String X_API_VERSION = "X-API-version";
    private static final String LATEST = "latest";

    Map<Method, List<RequestMappingInfo>> parseVersionAnnotation(Map<Method, RequestMappingInfo> originalMappings) {
        Map<Method, List<RequestMappingInfo>> result = new HashMap<>();
        originalMappings.entrySet().forEach(entry -> {
            RequestMappingInfo requestMappingInfo = entry.getValue();
            Method method = entry.getKey();
            registerVersions(requestMappingInfo, method)
                    .forEach(info -> createOrGetList(result, method).add(info));
            registerVersionLatestSupported(method, requestMappingInfo)
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

    private List<RequestMappingInfo> registerVersions(RequestMappingInfo requestMappingInfo, Method method) {
        ApiVersion apiVersion = findAnnotation(method, ApiVersion.class);
        ApiVersionLatest apiVersionLatest = findAnnotation(method, ApiVersionLatest.class);

        //        ProducesRequestCondition producesRequestCondition = requestMappingInfo.getProducesCondition();
        //        String mediaTypes = producesRequestCondition.getProducibleMediaTypes().stream()
        //                .map(MediaType::toString)
        //                .reduce("", String::concat);

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
                .map(version -> new RequestMappingInfo(
                        requestMappingInfo.getPatternsCondition(),
                        requestMappingInfo.getMethodsCondition(),
                        requestMappingInfo.getParamsCondition(),
                        new HeadersRequestCondition(X_API_VERSION + "=" + version), // requestMappingInfo.getHeadersCondition()
                        requestMappingInfo.getConsumesCondition(),
                        requestMappingInfo.getProducesCondition(), // new ProducesRequestCondition(mediaTypes + "; version=" + version),
                        null))
                .collect(toList());

    }

    private Optional<RequestMappingInfo> registerVersionLatestSupported(Method method, RequestMappingInfo requestMappingInfo) {
        ApiVersionLatestSupported annotation = findAnnotation(method, ApiVersionLatestSupported.class);
        if (nonNull(annotation)) {
            return Optional.of(requestMappingInfo);
        }
        return Optional.empty();
    }
}
