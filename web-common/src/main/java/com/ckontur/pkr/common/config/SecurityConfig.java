package com.ckontur.pkr.common.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.ckontur.pkr.common.component.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public abstract class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${jwt.hmac.secret:YoRHa_No.2_Type_B}")
    private String secret;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC512(secret);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/", "/auth/**", "/v2/api-docs", "/swagger-ui/**", "/swagger-resources/**", "/actuator/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
