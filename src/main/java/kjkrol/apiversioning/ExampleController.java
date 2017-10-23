package kjkrol.apiversioning;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ExampleController {

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.0")
    public List<String> getNames() {
        return asList("Adam", "Noe");
    }

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion({ "1.0.1", "1.0.2" })
    @ApiVersionLatestSupported
    public List<String> getNames1() {
        return asList("Abraham", "Joseph", "Moses");
    }

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.3")
    @ApiVersionLatest
    public List<String> getNames2() {
        return asList("Salomon", "David");
    }

}
