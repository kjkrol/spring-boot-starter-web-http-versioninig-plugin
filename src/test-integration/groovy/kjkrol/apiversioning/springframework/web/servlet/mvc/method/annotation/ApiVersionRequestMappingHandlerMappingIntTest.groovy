package kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import tool.ApiVersioningApplication

import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.LATEST
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.X_API_VERSION
import static org.hamcrest.Matchers.is
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [ApiVersioningApplication])
@ActiveProfiles("test")
@WebAppConfiguration
@SpringBootTest
class ApiVersionRequestMappingHandlerMappingIntTest extends Specification {

    @Autowired
    WebApplicationContext context

    MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    def "should execute method annotated with @ApiVersion that contains a specific version"() {
        given:
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .get("/names")
                    .accept(APPLICATION_JSON)
                    .header(X_API_VERSION, "1.0.0")

        when:
            ResultActions resultActions = mockMvc.perform(request)

        then:
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().stringValues(X_API_VERSION, "1.0.0"))
                    .andExpect(jsonPath('$', is(["Adam", "Noe"])))
    }

    def "should execute method annotated with @ApiVersionLatest if 'last' version was passed by X-API-version header"() {
        given:
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .get("/names")
                    .accept(APPLICATION_JSON)
                    .header(X_API_VERSION, LATEST)

        when:
            ResultActions resultActions = mockMvc.perform(request)

        then:
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().stringValues(X_API_VERSION, "1.0.3"))
                    .andExpect(jsonPath('$', is(["Solomon", "David"])))
    }

    def "should execute method annotated with @ApiVersionSupported if X-API-version header was not present"() {
        given:
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .get("/names")
                    .accept(APPLICATION_JSON)

        when:
            ResultActions resultActions = mockMvc.perform(request)

        then:
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().stringValues(X_API_VERSION, '1.0.1, 1.0.2'))
                    .andExpect(jsonPath('$', is(["Abraham", "Joseph", "Moses"])))
    }

    def "should response with HTTP 404 status if requested version does not exists"() {
        given:
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .get("/names")
                    .header(X_API_VERSION, "x.x.x")
                    .accept(APPLICATION_JSON)

        when:
            ResultActions resultActions = mockMvc.perform(request)

        then:
            resultActions.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));
    }
}
