package hu.cubix.timeoff.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String MANAGER = "manager";
    public static final String USER = "user";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/timeoff/**").hasAnyAuthority(MANAGER, USER)
                .requestMatchers(HttpMethod.PUT, "/api/timeoff/**").hasAnyAuthority(MANAGER, USER)
                .requestMatchers(HttpMethod.DELETE, "/api/timeoff/**").hasAnyAuthority(MANAGER, USER)
                .requestMatchers(HttpMethod.PATCH, "/api/timeoff/evaluate").hasAuthority(MANAGER)
                .anyRequest().authenticated()
            )
            .build();
    }
}
