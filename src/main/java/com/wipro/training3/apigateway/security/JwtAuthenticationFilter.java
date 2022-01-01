package com.wipro.training3.apigateway.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.wipro.training3.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter 
                               implements GlobalFilter {
	
	private JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		ServerHttpRequest req 
		       = (ServerHttpRequest) exchange.getRequest();
		
		final List<String> apiEndpoints = List.of("/api/register", "/api/login");
		
		Predicate<ServerHttpRequest> isApiSecured 
		            = r -> apiEndpoints.stream()
				          .noneMatch(uri -> 
				           r.getURI().getPath().contains(uri));
		
		if(isApiSecured.test(req)) {
			if(!req.getHeaders().containsKey("Authorization")) {
				ServerHttpResponse response 
				           = exchange.getResponse();
			    response.setStatusCode(HttpStatus.UNAUTHORIZED);
			   return response.setComplete();
			}
			
			final String token = req.getHeaders()
	                .getOrEmpty("Authorization")
	                .get(0);

			if(!jwtUtil.validateToken(token)) {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			
			return response.setComplete();
			}
		}
		return chain.filter(exchange);
    }
	
}
