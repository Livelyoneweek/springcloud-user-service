package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectPostProcessor<Object> objectPostProcessor;
    private final Environment env;

//    @Bean
//    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
//        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
//        http.csrf().disable();
//        http.headers().frameOptions().disable();
//        http.authorizeRequests().antMatchers("/**").hasIpAddress("172.16.10.231") //IP 주소는 변경할 것
//                        .and().addFilter(getAuthenticationFilter(authenticationManager));
//        http.authorizeHttpRequests().requestMatchers(PathRequest.toH2Console()).permitAll();
//        return http.build();
//    }
//
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
//        return authConfiguration.getAuthenticationManager();
//    }
//
//    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
//        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
//        authenticationFilter.setAuthenticationManager(authenticationManager);
//
//        return authenticationFilter;
//    }


    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeHttpRequests(authorize -> {
                    try {
                        authorize
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .antMatchers("/actuator/**").permitAll()
//                                .requestMatchers(new IpAddressMatcher("172.16.10.231")).permitAll() //ip 확인할 것
                                .anyRequest().permitAll()
                                .and()
                                .addFilter(getAuthenticationFilter());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        return http.build();
    }

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        return auth.build();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env);
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
        authenticationFilter.setAuthenticationManager(authenticationManager(builder));
        return authenticationFilter;
    }

}
