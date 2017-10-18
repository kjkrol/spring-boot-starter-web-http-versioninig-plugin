package kjkrol.apiversioning;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ExampleController {

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.0")
    public List<String> getNames() {
        return Arrays.asList("Mary", "Joseph");
    }

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.1")
    public List<String> getNames1() {
        return Arrays.asList("Paul", "John", "Peter");
    }

    @GetMapping(path = "/names", produces = APPLICATION_JSON_VALUE)
    @ApiVersion("1.0.2")
    public List<String> getNames2() {
        return Arrays.asList("Noe", "Joseph", "Abraham");
    }

}
