## Micronaut issue/6574 sample app

https://github.com/micronaut-projects/micronaut-core/issues/6574

Repo contains an expanded version of the below
```groovy
// Basically a local copy of 
// https://github.com/micronaut-projects/micronaut-security/blob/master/security/src/main/java/io/micronaut/security/authentication/UsernamePasswordCredentials.java
// for the sake of testing the diff between inheriting from local/external @Introspected classes
@Introspected
class Local {
    @NotBlank @NotNull String username
    @NotBlank @NotNull String password
}

@Introspected
class LocalPlus extends Local {
    @NotBlank @NotNull String tenant
    // username, password inherited from local class
}


@Introspected
class ExternalPlus extends io.micronaut.security.authentication.UsernamePasswordCredentials {
    @NotBlank @NotNull String tenant
    // username, password inherited from external class
}


BeanIntrospection<LocalPlus> local = BeanIntrospection.getIntrospection(LocalPlus)
assert local.getIndexedProperties(Constraint.class).size() == 3 // true

BeanIntrospection<ExternalPlus> external = BeanIntrospection.getIntrospection(ExternalPlus)
assert local.getIndexedProperties(Constraint.class).size() == 3 // FALSE: actual Constraints found = 1 (for 'tenant')
```
