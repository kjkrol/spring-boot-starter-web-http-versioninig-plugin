package tool

import kjkrol.apiversioning.springframework.web.servlet.mvc.config.ApiVersionConfiguration
import kjkrol.apiversioning.springframework.web.servlet.mvc.config.ApiVersionResponseHeaderModifierAdvice
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackageClasses = [ApiVersionConfiguration, ExampleController, ApiVersionResponseHeaderModifierAdvice])
class ApiVersioningApplication {

    static void main(String[] args) {
        SpringApplication.run(ApiVersioningApplication.class, args)
    }

}

