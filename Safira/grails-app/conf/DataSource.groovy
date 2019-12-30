dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
//            logSql = false
//            dbCreate = "update"
//            pooled = true
//            url = "jdbc:mysql://localhost:3306/safira"
//            driverClassName = "com.mysql.jdbc.Driver"
//            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
//            username = "root"
//            password = "root"
//            properties {
//                maxActive = 50
//                maxIdle = 25
//                minIdle = 5
//                initialSize = 5
//                minEvictableIdleTimeMillis = 60000
//                timeBetweenEvictionRunsMillis = 60000
//                maxWait = 10000
//                validationQuery = "/* ping */"
//            }
        }
    }
//    development {
//        dataSource {
//            dbCreate = "update"
//            url = "jdbc:postgresql://localhost:5432/safira"
//            driverClassName = "org.postgresql.Driver"
//            username = "postgres"]]
//            password = "polo1908"
//            dialect = org.hibernate.dialect.PostgreSQLDialect
//        }
//    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
            logSql = false
            dbCreate = "update"
            pooled = true
            url = "jdbc:mysql://localhost/safira"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
            username = "root"
            password = "root"
            properties {
                maxActive = 50
                maxIdle = 25
                minIdle = 5
                initialSize = 5
                minEvictableIdleTimeMillis = 60000
                timeBetweenEvictionRunsMillis = 60000
                maxWait = 10000
                validationQuery = "/* ping */"
            }
        }
    }
}

