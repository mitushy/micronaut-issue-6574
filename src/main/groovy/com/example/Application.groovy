package com.example

import groovy.transform.CompileStatic
import io.micronaut.runtime.Micronaut

@CompileStatic
class Application {
    static void main(String[] args) {
        def context = Micronaut.build(args)
            .mainClass(Application.class)
            .start()
        println "Environment: ${context.environment.getActiveNames()}"
    }
}
