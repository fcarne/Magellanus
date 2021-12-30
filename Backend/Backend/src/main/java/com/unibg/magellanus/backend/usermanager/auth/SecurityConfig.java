package com.unibg.magellanus.backend.usermanager.auth;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseToken;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	TokenFilter<FirebaseToken> tokenFilter;

	@Bean
	public AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return (httpServletRequest, httpServletResponse, e) -> {
			Map<String, Object> errorObject = new HashMap<>();
			int errorCode = 401;
			errorObject.put("message", "Unauthorized access of protected resource, invalid credentials");
			errorObject.put("error", HttpStatus.UNAUTHORIZED);
			errorObject.put("code", errorCode);
			errorObject.put("timestamp", new Timestamp(new Date().getTime()));
			httpServletResponse.setContentType("application/json;charset=UTF-8");
			httpServletResponse.setStatus(errorCode);
			httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorObject));
		};
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().formLogin().disable().httpBasic().disable()
			.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint())
			.and().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers(HttpMethod.POST, "/user/signup").permitAll()
				.anyRequest().authenticated().and()
				.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}
