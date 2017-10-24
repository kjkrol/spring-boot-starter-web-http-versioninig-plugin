package kjkrol.apiversioning.springframework.web.servlet.mvc.config;

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionRequestMappingHandlerMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class ApiVersionConfiguration extends WebMvcConfigurationSupport {

    //TODO: to response with custom header read this ->
    // https://mtyurt.net/2015/07/20/spring-modify-response-headers-after-processing/
    // https://stackoverflow.com/questions/46227751/spring-controllers-adding-a-response-header-parameter-called-elapsed-time

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}
