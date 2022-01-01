package com.wipro.training3.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	@Bean
	public RouteLocator gatewayRoute(
			RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/auth/**")
						.filters(f -> f.rewritePath(
			    				   "/auth/(?<segment>.*)", 
			    				   "/api/${segment}"))
                        .uri("lb://auth-server"))
				      .route(p -> p.path("/topics/**")
				    		       .filters(f -> f.rewritePath(
				    				   "/topics/(?<segment>.*)", 
				    				   "/api/courses/${segment}"))
	                               .uri("lb://course-details/"))
				      .build();
	}
}
