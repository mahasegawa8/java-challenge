package jp.co.axa.apidemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Create an in-memory user for demonstration purposes
        auth.inMemoryAuthentication()
            .withUser("user").password("{noop}password").roles("USER")
            .and()
            .withUser("admin").password("{noop}password").roles("ADMIN");
        // {noop} indicates plain text password, not for production use
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // Enable HTTP Basic authentication
            .httpBasic()
            .and()
            .authorizeRequests()
            // Allow GET requests for all users
            .antMatchers(HttpMethod.GET, "/api/v1/employees/**").permitAll()
            // Require ADMIN role for POST, PUT, DELETE
            .antMatchers(HttpMethod.POST, "/api/v1/employees").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/api/v1/employees/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/api/v1/employees/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            // Disable CSRF protection for this simple API
            .csrf().disable()
            .formLogin().disable();
    }
}