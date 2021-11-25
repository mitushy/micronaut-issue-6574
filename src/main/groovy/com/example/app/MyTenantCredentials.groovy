package com.example.app

import io.micronaut.core.annotation.Introspected

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class MyTenantCredentials extends MyUsernamePasswordCredentials {
    @NotBlank
    @NotNull
    private String tenant

    MyTenantCredentials(String tenant, String username, String password) {
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
