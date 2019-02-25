package lz.gateway.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import lz.gateway.server.filter.zuul.pre.GrayFilter;
import lz.gateway.server.filter.zuul.route.CustomRouteLocator;

@EnableZuulProxy
@SpringBootApplication(scanBasePackages = "lz.gateway.server")
@ImportResource(locations={"classpath:application-bean.xml"})
@ComponentScan("com.gree")
@EnableRedisHttpSession  
public class GatewayApp {

	@Autowired
	ZuulProperties zuulProperties;
	@Autowired
	ServerProperties server;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {

		SpringApplication.run(GatewayApp.class,args);

	}

	@Bean
	CustomRouteLocator routeLocator() {
		CustomRouteLocator routeLocator= new CustomRouteLocator(zuulProperties.getServletPath(),zuulProperties); 
		routeLocator.setJdbcTemplate(jdbcTemplate);
		return routeLocator;
	}

}
