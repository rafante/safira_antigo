grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        mavenRepo 'https://repo.grails.org/grails/plugins'
        mavenCentral()
        grailsCentral()
        mavenRepo "http://repo.grails.org/grails/core"
        mavenRepo "http://repo.grails.org/grails/plugins/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        //compile ":rest:0.8"

        // runtime 'mysql:mysql-connector-java:5.1.21'
    }

    plugins {
        compile "org.grails.plugins:jrimum-bopepo:0.4"
        build(":tomcat:$grailsVersion",
                ":release:2.2.1") {
            export = false
        }
        //runtime ":rest:0.8"
        runtime ":jquery:1.10.2"
        compile ":jquery-ui:1.8.24"

        runtime ":hibernate:$grailsVersion"
        runtime ":resources:1.2.1"
//        runtime ":fields:1.3"

        compile ":font-awesome-resources:3.2.1.2"
        compile ":cache-headers:1.1.5"
        runtime ":zipped-resources:1.0"
        runtime ":cached-resources:1.0"
        runtime ":yui-minify-resources:0.1.5"

        build ":tomcat:$grailsVersion"

        runtime ":database-migration:1.3.5"

        compile ':cache:1.1.1'
        compile ":jasper:1.6.1"
    }
}