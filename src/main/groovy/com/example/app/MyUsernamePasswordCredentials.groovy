package com.example.app

import io.micronaut.core.annotation.Introspected
import io.micronaut.security.authentication.AuthenticationRequest

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

// Basically a local copy of io.micronaut.security.authentication.UsernamePasswordCredentials
@Introspected
class MyUsernamePasswordCredentials implements Serializable, AuthenticationRequest<String, String> {
    @NotBlank
    @NotNull
    private String username

    @NotBlank
    @NotNull
    private String password

    MyUsernamePasswordCredentials(String username, String password) {
        this.username = username
        this.password = password
    }

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }

    @Override
    String getIdentity() {
        return getUsername()
    }

    @Override
    String getSecret() {
        return getPassword()
    }
}
