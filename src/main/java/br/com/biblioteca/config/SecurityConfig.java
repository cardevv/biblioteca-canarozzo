package br.com.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

// Configuracao do Spring Security: login por sessao (form login) e regras por papel
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Paginas e recursos liberados sem login
                .requestMatchers("/login.html", "/css/**", "/js/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Dados do usuario logado: qualquer usuario autenticado
                .requestMatchers("/api/usuarios/me").authenticated()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // Acervo: todos podem consultar, so ADMIN altera
                .requestMatchers(HttpMethod.GET, "/api/livros/**", "/api/autores/**", "/api/categorias/**").authenticated()
                .requestMatchers("/api/livros/**", "/api/autores/**", "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers("/api/exemplares/**").hasRole("ADMIN")
                .requestMatchers("/api/isbn/**").hasRole("ADMIN")

                // Emprestimos: leitor ve so os proprios; o resto e do ADMIN
                .requestMatchers(HttpMethod.GET, "/api/emprestimos/meus").authenticated()
                .requestMatchers("/api/emprestimos/**").hasRole("ADMIN")

                // Reservas: listagem geral so ADMIN; criar/cancelar/minhas qualquer logado
                // (o Service confere se a reserva pertence ao usuario)
                .requestMatchers(HttpMethod.GET, "/api/reservas").hasRole("ADMIN")
                .requestMatchers("/api/reservas/**").authenticated()

                // Qualquer outra coisa (inclusive as paginas .html) exige login
                .anyRequest().authenticated()
            )
            // Login padrao do Spring, mas usando a nossa pagina login.html
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login") // o form faz POST /login com username e password
                .defaultSuccessUrl("/index.html", true)
                .failureUrl("/login.html?erro")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?saiu")
            )
            // Para chamadas da API sem login, devolve 401 (o JS redireciona para o login)
            // em vez de redirecionar para a pagina HTML
            .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                request -> request.getRequestURI().startsWith("/api/")
            ))
            // CSRF desabilitado para a API: o front e JavaScript servido no mesmo dominio
            // e a autenticacao e por sessao; por ser um projeto academico, simplificamos.
            // Tambem para /login e /logout, porque as paginas sao HTML estatico (sem token).
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/login", "/logout", "/h2-console/**"))
            // O console do H2 usa frames; liberamos frames da mesma origem
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    // BCrypt: gera um hash com "sal" aleatorio; o Spring usa para comparar a senha no login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
