package tool

import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersion
import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionLatest
import kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionSupported
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import static java.util.Arrays.asList
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

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
