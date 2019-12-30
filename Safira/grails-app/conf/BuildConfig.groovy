grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}.war"
grails.project.dependency.resolver = "maven"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.tomcat.jvmArgs = ["-Xms256m", "-Xmx1024m", "-XX:PermSize=512m", "-XX:MaxPermSize=512m", "-XX:UseGCOverheadLimit",
                         "-Dfile.encoding=UTF-8"]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcach    e:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums false // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugin
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.20'
    }

    plugins {
        compile "org.grails.plugins:jrimum-bopepo:0.4"
//        runtime ":hibernate:$grailsVersion"
//        runtime ":jquery:1.10.2"
//        compile ":jquery-ui:1.8.24"
//        runtime ":Gresources:1.2.1"
////        runtime ":fields:1.3"
////        compile ':scaffolding:2.0.1'
//
//        compile ":font-awesome-resources:3.2.1.2"
//        compile ":cache-headers:1.1.5"
//        runtime ":zipped-resources:1.0"
//        runtime ":cached-resources:1.0"
//        runtime ":yui-minify-resources:0.1.5"
//
////        build ':jetty:2.0.3'
//        build ':tomcat:2.2.3'
//
//        runtime ":database-migration:1.3.8"
//
//        compile ":spring-security-core:1.2.7.3"
//        compile ':cache:1.1.1'
//        compile ":jasper:1.6.1"

//        runtime ":app-info:1.0.3"
//        compile ":app-info-hibernate:0.3"
        build ":tomcat:$grailsVersion"
        compile ':spring-security-core:1.2.7.3'
    }
}

grails.plugin.location."asgard-license" = "../AsgardLicense"
grails.plugin.location."asgard-base" = "../AsgardBase"