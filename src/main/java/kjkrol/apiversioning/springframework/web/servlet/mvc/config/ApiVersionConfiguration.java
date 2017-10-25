package kjkrol.apiversioning.springframework.web.servlet.mvc.config;

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionRequestMappingHandlerMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
class ApiVersionConfiguration extends WebMvcConfigurationSupport {

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}
