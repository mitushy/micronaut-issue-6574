package com.example.app

import groovy.util.logging.Slf4j
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated

import javax.validation.Valid

@Controller("/my")
@Validated
@Slf4j
class MyController {

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Consumes([MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON])
    @Post("/login")
    MutableHttpResponse<?> login(@Valid @Body MyTenantCredentials tenantCreds, HttpRequest<?> request) {
        if (tenantCreds.tenant == "tenant" && tenantCreds.username == "user" && tenantCreds.password == "pass") {
            return HttpResponse.ok()
        }
        return HttpResponse.unauthorized()
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Consumes([MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON])
    @Post("/loginWithExtended")
    MutableHttpResponse<?> loginWithExtended(@Valid @Body ExtendingUsernamePasswordCredentials tenantCreds, HttpRequest<?> request) {
        if (tenantCreds.tenant == "tenant" && tenantCreds.username == "user" && tenantCreds.password == "pass") {
            return HttpResponse.ok()
        }
        return HttpResponse.unauthorized()
    }
}
