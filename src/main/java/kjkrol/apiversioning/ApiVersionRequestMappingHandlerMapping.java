package kjkrol.apiversioning;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) {
            return null;
        }
        ApiVersion methodAnnotation = findAnnotation(method, ApiVersion.class);
        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            // Concatenate our ApiVersion with the usual request mapping
            return decorateRequestMappingInfoWithApiVersion(info, methodAnnotation, methodCondition);
        } else {
            ApiVersion typeAnnotation = findAnnotation(handlerType, ApiVersion.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                // Concatenate our ApiVersion with the usual request mapping
                return decorateRequestMappingInfoWithApiVersion(info, typeAnnotation, typeCondition);
            }
            return info;
        }
    }

    private RequestMappingInfo decorateRequestMappingInfoWithApiVersion(RequestMappingInfo originalRequestMappingInfo,
            ApiVersion apiVersionAnnotation, RequestCondition<?> methodCondition) {
        String apiVersions = String.join(",", apiVersionAnnotation.value());
        System.out.println("############ apiVersions = " + apiVersions);
        ProducesRequestCondition producesRequestCondition = originalRequestMappingInfo.getProducesCondition();
        String mediaTypes = producesRequestCondition.getProducibleMediaTypes().stream().map(MediaType::toString).reduce("", String::concat);
        System.out.println("############ mediaTypes = " + mediaTypes);
        RequestMappingInfo result = new RequestMappingInfo(
                originalRequestMappingInfo.getPatternsCondition(),
                originalRequestMappingInfo.getMethodsCondition(),
                originalRequestMappingInfo.getParamsCondition(),
                originalRequestMappingInfo.getHeadersCondition(),
                originalRequestMappingInfo.getConsumesCondition(),
                new ProducesRequestCondition(mediaTypes + "; version=" + apiVersions),
                null);
        System.out.println("############ RequestMappingInfo = " + result);
        return result;
    }

}
