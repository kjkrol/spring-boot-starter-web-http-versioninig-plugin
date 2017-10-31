spring-webmvc-api-versioninig-plugin
=============

API versioning for org.springframework.boot:spring-boot-starter-web:5.x using HTTP header

This tiny spring-boot-starter-web plugin allows to versioning exposed REST API and accessing them using customized **X-API-version** HTTP headers.

Server side
-----------

Configuration:
Two provided configuration classes:
- ApiVersionConfiguration
- ApiVersionResponseHeaderModifierAdvice

should be visible for spring **ComponentScan**. It can be done like that:
```
@SpringBootApplication(scanBasePackageClasses = {ApiVersionWebMvcRegistrations.class, ApiVersionResponseHeaderModifierAdvice.class})
```

To enable versioning for exposed Rest controller methods the following annotation should be used:

annotation           | description
-------------------- | ------------------------------------------------------
@ApiVersion          | collects set of versions supported by annotted method
@ApiVersionLatest    | mark method as a latest (newest) version
@ApiVersionSupported | mark method as supported by defult; allows to access method without using **X-API-version** header

Usage example:
```
@RestController
class ExampleController {

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.0")
    List<String> getNames1() {
        return asList("Adam", "Noah");
    }

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion(["1.0.1", "1.0.2"])
    @ApiVersionSupported
    List<String> getNames2() {
        return asList("Abraham", "Joseph", "Moses");
    }

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.3")
    @ApiVersionLatest
    List<String> getNames3() {
        return asList("Solomon", "David");
    }

}
```

Client side
-----------

- To execute specific method version user should add X-API-version header to HTTP request
```
X-API-version: 1.0.2
```
- Server will provide latest alias for the latest supported version
```
X-API-version: latest
```
- If no X-API-version header is specified, server will execute default method (marked as @ApiVersionSupported)
- Each HTTP response provides X-API-version header with information about executed method version
- Specifying invalid (or unsupported) version will result in HTTP status 404 Not Found
