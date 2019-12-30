import groovy.sql.Sql

// File: grails-app/conf/spring/resources.groovy
beans = {

    // Create Spring bean for Groovy SQL.
    // groovySql is the name of the bean and can be used
    // for injection.
    groovySql(Sql, ref('dataSource'))

}