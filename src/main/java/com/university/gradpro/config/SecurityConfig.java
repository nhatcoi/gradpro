package com.university.gradpro.config;

import com.university.gradpro.auth.security.CustomUserDetailsService;
import com.university.gradpro.auth.security.JwtAuthenticationEntryPoint;
import com.university.gradpro.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/topics/available/**").permitAll()
                        
                        // Admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/manage/**").hasRole("ADMIN")
                        
                        // Training Department
                        .requestMatchers("/api/registration-periods/**").hasAnyRole("ADMIN", "TRAINING_DEPT")
                        .requestMatchers("/api/councils/assign/**").hasAnyRole("ADMIN", "TRAINING_DEPT")
                        .requestMatchers("/api/schedules/**").hasAnyRole("ADMIN", "TRAINING_DEPT")
                        .requestMatchers("/api/reports/university/**").hasAnyRole("ADMIN", "TRAINING_DEPT")
                        
                        // Department Head
                        .requestMatchers("/api/topics/approve/**").hasAnyRole("ADMIN", "DEPT_HEAD")
                        .requestMatchers("/api/topics/assign-supervisor/**").hasAnyRole("ADMIN", "DEPT_HEAD")
                        .requestMatchers("/api/councils/create/**").hasAnyRole("ADMIN", "DEPT_HEAD")
                        .requestMatchers("/api/reports/department/**").hasAnyRole("ADMIN", "DEPT_HEAD")
                        
                        // Lecturer
                        .requestMatchers("/api/topics/propose/**").hasAnyRole("ADMIN", "DEPT_HEAD", "LECTURER")
                        .requestMatchers("/api/registrations/approve/**").hasAnyRole("ADMIN", "LECTURER")
                        .requestMatchers("/api/milestones/**").hasAnyRole("ADMIN", "LECTURER")
                        .requestMatchers("/api/progress/evaluate/**").hasAnyRole("ADMIN", "LECTURER")
                        .requestMatchers("/api/evaluations/council/**").hasAnyRole("ADMIN", "LECTURER")
                        
                        // Student
                        .requestMatchers("/api/topics/student-propose/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/registrations/register/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/progress/submit/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/final-reports/submit/**").hasAnyRole("ADMIN", "STUDENT")
                        
                        // Authenticated users
                        .anyRequest().authenticated()
                );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
