package com.example

import com.example.app.ExtendingUsernamePasswordCredentials
import com.example.app.MyTenantCredentials
import com.example.app.MyUsernamePasswordCredentials
import io.micronaut.core.beans.BeanIntrospection
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.validation.validator.Validator
import jakarta.inject.Inject
import spock.lang.Specification

import javax.validation.Constraint

@MicronautTest
class MyControllerTest extends Specification {

    @Inject
    EmbeddedApplication<?> application

    @Inject
    @Client(value = '/')
    HttpClient client

    @Inject
    Validator validator

    void 'Test bean introspection'() {
        when:
        def first = BeanIntrospection.getIntrospection(MyTenantCredentials)

        then:
        ["tenant", "username", "password"].each {
            assert first.getProperty(it).isPresent()
        }
        first.getIndexedProperties(Constraint.class).size() == 3

        when: "we try with the other class"
        def failing = BeanIntrospection.getIntrospection(ExtendingUsernamePasswordCredentials)

        then:
        ["tenant", "username", "password"].each {
            assert failing.getProperty(it).isPresent()
        }
        failing.getIndexedProperties(Constraint.class).size() == 3 // FAILS: returns 1
    }

    void 'Test validator for inherited props'() {
        expect:
        def constraints = validator.validate(object)
        constraints.size() == failures * 2 // times 2 because null fails both @NotBlank & @NotNull

        where:
        object                                                             || failures
        new MyUsernamePasswordCredentials("user", "pass")                  || 0
        new MyUsernamePasswordCredentials(null, "pass")                    || 1 // username
        new MyUsernamePasswordCredentials("user", null)                    || 1 // pass
        new MyUsernamePasswordCredentials(null, null)                      || 2 // user, pass

        new MyTenantCredentials("tenant", "user", "pass")                  || 0
        new MyTenantCredentials(null, "user", "pass")                      || 1 // tenant
        new MyTenantCredentials("tenant", null, "pass")                    || 1 // user - from UsernamePasswordCredentials
        new MyTenantCredentials("tenant", "user", null)                    || 1 // pass - from UsernamePasswordCredentials
        new MyTenantCredentials("tenant", null, null)                      || 2 // user, pass - from UsernamePasswordCredentials

        // Similarly, external lib validation work
        new UsernamePasswordCredentials("user", "pass")                    || 0
        new UsernamePasswordCredentials(null, "pass")                      || 1 // username
        new UsernamePasswordCredentials("user", null)                      || 1 // pass
        new UsernamePasswordCredentials(null, null)                        || 2 // user, pass

        // Problems start here.
        // The prop defined in the current module ('tenant') has its validation working
        new ExtendingUsernamePasswordCredentials("tenant", "user", "pass") || 0
        new ExtendingUsernamePasswordCredentials(null, "user", "pass")     || 1 // tenant
        // But props inherited from micronaut-security's UsernamePasswordCredentials no longer do validation
        new ExtendingUsernamePasswordCredentials("tenant", null, "pass")   || 1 // user - from UsernamePasswordCredentials
        new ExtendingUsernamePasswordCredentials("tenant", "user", null)   || 1 // pass - from UsernamePasswordCredentials
        new ExtendingUsernamePasswordCredentials("tenant", null, null)     || 2 // user, pass - from UsernamePasswordCredentials
    }

    void 'Test valid'() {
        when:
        HttpRequest request = HttpRequest.create(HttpMethod.POST, '/my/login')
            .body([tenant: tenant, username: username, password: password])
            .accept(MediaType.APPLICATION_JSON_TYPE)
        client.toBlocking().exchange(request)

        then:
        HttpClientResponseException e = thrown()
        e.status == status

        where:
        tenant         || username     || password     || status
        "tenant"       || "user"       || "pass_wrong" || HttpStatus.UNAUTHORIZED
        "tenant"       || "user_wrong" || "pass"       || HttpStatus.UNAUTHORIZED
        "tenant_wrong" || "user"       || "pass"       || HttpStatus.UNAUTHORIZED
        null           || "user"       || "pass"       || HttpStatus.BAD_REQUEST
        "tenant"       || null         || "pass"       || HttpStatus.BAD_REQUEST
        "tenant"       || "user"       || null         || HttpStatus.BAD_REQUEST
        "tenant"       || "user"       || ""           || HttpStatus.BAD_REQUEST
        ""             || ""           || ""           || HttpStatus.BAD_REQUEST
    }

    void 'Test @valid with payload extending foreign class'() {
        when:
        HttpRequest request = HttpRequest.create(HttpMethod.POST, '/my/loginWithExtended')
            .body([tenant: tenant, username: username, password: password])
            .accept(MediaType.APPLICATION_JSON_TYPE)
        client.toBlocking().exchange(request)

        then:
        HttpClientResponseException e = thrown()
        e.status == status

        where:
        tenant         || username     || password     || status
        "tenant"       || "user"       || "pass_wrong" || HttpStatus.UNAUTHORIZED
        "tenant"       || "user_wrong" || "pass"       || HttpStatus.UNAUTHORIZED
        "tenant_wrong" || "user"       || "pass"       || HttpStatus.UNAUTHORIZED
        null           || "user"       || "pass"       || HttpStatus.BAD_REQUEST
        "tenant"       || null         || "pass"       || HttpStatus.BAD_REQUEST
        "tenant"       || "user"       || null         || HttpStatus.BAD_REQUEST
        "tenant"       || "user"       || ""           || HttpStatus.BAD_REQUEST
        ""             || ""           || ""           || HttpStatus.BAD_REQUEST
    }
}
