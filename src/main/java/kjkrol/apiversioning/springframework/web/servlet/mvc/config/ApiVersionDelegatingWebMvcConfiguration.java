package kjkrol.apiversioning.springframework.web.servlet.mvc.config;

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionRequestMappingHandlerMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class ApiVersionDelegatingWebMvcConfiguration extends DelegatingWebMvcConfiguration {

    public RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}
