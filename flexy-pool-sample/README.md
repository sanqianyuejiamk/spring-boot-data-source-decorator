**Spring Boot DataSource Decorator**

Spring Boot autoconfiguration for integration with 
* [P6Spy](https://github.com/p6spy/p6spy) - adds ability to intercept and log sql queries, including interception of a most `Connection`, `Statement` and `ResultSet` methods invocations
* [Datasource Proxy](https://github.com/ttddyy/datasource-proxy) - more lightweight than p6spy, supports only `beforeQuery` and `afterQuery` events  
* [FlexyPool](https://github.com/vladmihalcea/flexy-pool) - adds connection pool metrics (jmx, codahale, dropwizard) and flexible strategies for adjusting pool size on demand
* [Spring Cloud Sleuth](https://github.com/spring-cloud/spring-cloud-sleuth) - library for distributed tracing, if found in classpath enables jdbc connections (p6spy) and queries (p6spy, datasource-proxy) tracing 

