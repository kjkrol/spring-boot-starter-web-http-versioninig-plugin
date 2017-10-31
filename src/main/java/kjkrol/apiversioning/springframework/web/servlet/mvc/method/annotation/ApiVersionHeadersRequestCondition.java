package kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static kjkrol.apiversioning.springframework.web.servlet.mvc.method.annotation.ApiVersionHeader.HEADER_NAME;

class ApiVersionHeadersRequestCondition extends AbstractRequestCondition<ApiVersionHeadersRequestCondition> {

    private final HeadersRequestCondition headersRequestCondition;

    private final Set<NameValueExpression<String>> expressions = new HashSet<>();

    ApiVersionHeadersRequestCondition(String... headers) {
        this(new HeadersRequestCondition(headers));
    }

    private ApiVersionHeadersRequestCondition(HeadersRequestCondition headersRequestCondition) {
        this.headersRequestCondition = headersRequestCondition;
        expressions.addAll(headersRequestCondition.getExpressions());
    }

    @Override
    protected Collection<NameValueExpression<String>> getContent() {
        return this.expressions;
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }

    @Override
    public ApiVersionHeadersRequestCondition combine(ApiVersionHeadersRequestCondition other) {
        HeadersRequestCondition newHeadersRequestCondition = headersRequestCondition.combine(other.headersRequestCondition);
        return new ApiVersionHeadersRequestCondition(newHeadersRequestCondition);
    }

    @Nullable
    @Override
    public ApiVersionHeadersRequestCondition getMatchingCondition(HttpServletRequest request) {
        HeadersRequestCondition headersRequestCondition = this.headersRequestCondition.getMatchingCondition(request);
        if (nonNull(request.getHeader(HEADER_NAME)) && getContent().isEmpty()) {
            return null;
        }
        if (isNull(headersRequestCondition)) {
            return null;
        }
        return this;
    }

    @Override
    public int compareTo(ApiVersionHeadersRequestCondition other, HttpServletRequest request) {
        return this.headersRequestCondition.compareTo(other.headersRequestCondition, request);
    }
}
