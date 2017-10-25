package kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final ApiVersionAnnotationParser apiVersionAnnotationParser = new ApiVersionAnnotationParser();

    @Override
    /*
     * Before modify this method was copy-pasted from {@link AbstractHandlerMethodMapping}
     * Look for handler methods in a handler.
     * @param handler the bean name of a handler or a handler instance
     */
    protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = (handler instanceof String ? obtainApplicationContext().getType((String) handler) : handler.getClass());
        if (handlerType != null) {
            final Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<RequestMappingInfo>) method -> {
                        try {
                            return super.getMappingForMethod(method, handlerType);
                        } catch (Throwable ex) {
                            throw new IllegalStateException("Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
                        }
                    });
            Map<Method, List<RequestMappingInfo>> versionedMethods = apiVersionAnnotationParser.parseApiVersioningAnnotations(methods);
            if (versionedMethods.isEmpty()) {
                originalRegistrationProcess(handler, userType, methods);
            } else {
                customizedRegistrationProcess(handler, userType, versionedMethods);
            }
        }
    }

    private void originalRegistrationProcess(final Object handler, final Class<?> userType, Map<Method, RequestMappingInfo> methods) {
        logRequestMappingInfoCandidates(userType, methods);
        for (Map.Entry<Method, RequestMappingInfo> entry : methods.entrySet()) {
            Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
            RequestMappingInfo mapping = entry.getValue();
            registerHandlerMethod(handler, invocableMethod, mapping);
        }
    }

    private void customizedRegistrationProcess(final Object handler, final Class<?> userType, Map<Method, List<RequestMappingInfo>> methods) {
        logRequestMappingInfoCandidates(userType, methods);
        for (Map.Entry<Method, List<RequestMappingInfo>> entry : methods.entrySet()) {
            Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
            entry.getValue().forEach(mapping -> {
                registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }

    private void logRequestMappingInfoCandidates(final Class<?> userType, Map<Method, ?> methods) {
        if (logger.isDebugEnabled()) {
            logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
        }
    }

}
