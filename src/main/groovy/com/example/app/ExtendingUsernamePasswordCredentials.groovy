package com.example.app

import io.micronaut.core.annotation.Introspected
import io.micronaut.security.authentication.UsernamePasswordCredentials

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class ExtendingUsernamePasswordCredentials extends UsernamePasswordCredentials {
    @NotBlank
    @NotNull
    private String tenant

    ExtendingUsernamePasswordCredentials(String tenant, String username, String password) {
        super(username, password)
        this.tenant = tenant
    }

    String setTenant(String tenant) {
        this.tenant = tenant
    }

    String getTenant() {
        return tenant
    }
}
