// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// package com.example.sms.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// /**
//  *
//  * @author pinaa
//  */
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     private UserDetailsService userDetailsService;

//     @Bean
//     public static PasswordEncoder passwordEncoder(){
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//         .csrf(csrf -> csrf.disable()) 
//         .authorizeHttpRequests(auth -> auth
//                                 .requestMatchers("/static/**").permitAll()
//                                 .requestMatchers("/frontend/**").permitAll()
//                                 .requestMatchers("/register/**").permitAll()
//                                 .requestMatchers("/users").hasRole("ADMIN")
//                                 .requestMatchers("/superadmin/satkers/**").hasRole("ADMIN")
//                                 .requestMatchers("/superadmin/users/**").hasRole("ADMIN")
//                                 .requestMatchers("/superadmin/roles/**").hasRole("ADMIN")
//                                 .requestMatchers("/superadmin/provinces/**").hasRole("ADMIN")
//                                 .requestMatchers("/superadmin/programs/**").hasRole("ADMIN")
//                                 .requestMatchers("/operator/surveys/**").permitAll()
//                                 .requestMatchers("/main.css").permitAll()
//                                 .requestMatchers("/SMS-Logo.png").permitAll()
//                                 .anyRequest().authenticated()
//                 ).formLogin(
//                         form -> form
//                                 .loginPage("/login")
//                                 .loginProcessingUrl("/login")
//                                 .defaultSuccessUrl("/index", true)
//                                 .permitAll()
//                 ).logout(
//                         logout -> logout
//                                 .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                                 .logoutSuccessUrl("/login?logout")
//                                 .permitAll()
//                 );
//         return http.build();
//     }

//     @Autowired
//     public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//         auth
//                 .userDetailsService(userDetailsService)
//                 .passwordEncoder(passwordEncoder());
//     }
// }

package com.example.sms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for web application and REST API
 * 
 * @author rest-api
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security configuration for REST API
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/current").authenticated()
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/satkers/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/provinces/**").hasRole("ADMIN")
                        .requestMatchers("/api/programs/**").hasRole("ADMIN")
                        .requestMatchers("/api/kegiatans/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic();

        return http.build();
    }

    // /**
    // * Security configuration for web application (existing configuration)
    // */
    // @Bean
    // public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception
    // {
    // http
    // .securityMatcher("/**")
    // .csrf(csrf -> csrf.disable())
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/static/**").permitAll()
    // .requestMatchers("/frontend/**").permitAll()
    // .requestMatchers("/register/**").permitAll()
    // .requestMatchers("/users").hasRole("ADMIN")
    // .requestMatchers("/superadmin/satkers/**").hasRole("ADMIN")
    // .requestMatchers("/superadmin/users/**").hasRole("ADMIN")
    // .requestMatchers("/superadmin/roles/**").hasRole("ADMIN")
    // .requestMatchers("/superadmin/provinces/**").hasRole("ADMIN")
    // .requestMatchers("/superadmin/programs/**").hasRole("ADMIN")
    // .requestMatchers("/operator/surveys/**").permitAll()
    // .requestMatchers("/main.css").permitAll()
    // .requestMatchers("/SMS-Logo.png").permitAll()
    // .anyRequest().authenticated())
    // .formLogin(
    // form -> form
    // .loginPage("/login")
    // .loginProcessingUrl("/login")
    // .defaultSuccessUrl("/index", true)
    // .permitAll())
    // .logout(
    // logout -> logout
    // .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
    // .logoutSuccessUrl("/login?logout")
    // .permitAll());

    // return http.build();
    // }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}