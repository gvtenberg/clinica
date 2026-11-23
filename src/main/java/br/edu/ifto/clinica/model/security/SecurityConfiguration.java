package br.edu.ifto.clinica.model.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // classe de configuração
@EnableWebSecurity // indica que serão definidas configurações personalizadas de segurança
public class SecurityConfiguration {

    /**
     * @param http objeto fornecido pelo Spring para configurar a segurança das requisições HTTP
     * @return a cadeia de filtros de segurança aplicada a todas as requisições
     * As regras são avaliadas na ordem em que aparecem e a primeira que corresponder é aplicada.
     * Por isso, as regras mais específicas (como /pacientes/remove/**) vêm antes das mais gerais
     * (como /pacientes/**), e anyRequest() fica por último.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Arquivos estáticos: acessíveis sem login
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // O / foi removido para cair direto na tela de login

                        // O paciente pode fazer o próprio cadastro, sem login
                        .requestMatchers("/pacientes/form").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pacientes/save").permitAll()

                        // Cada médico ou paciente vê somente as próprias consultas
                        .requestMatchers("/consultas/minhas").hasAnyRole("MEDICO", "PACIENTE")

                        // Consultas são gerenciadas pelo secretário e administrador
                        .requestMatchers("/consultas/**").hasAnyRole("SECRETARIO", "ADMIN")

                        // Somente o administrador exclui pacientes
                        .requestMatchers("/pacientes/remove/**").hasRole("ADMIN")
                        .requestMatchers("/pacientes/**").hasAnyRole("SECRETARIO", "ADMIN")

                        // O secretário apenas consulta a lista de médicos
                        .requestMatchers("/medicos/list").hasAnyRole("SECRETARIO", "ADMIN")
                        .requestMatchers("/medicos/**").hasRole("ADMIN")

                        // Somente o administrador gerencia secretários
                        .requestMatchers("/secretarios/**").hasRole("ADMIN")
                        
                        .anyRequest().authenticated() // demais requisições exigem usuário autenticado
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL da página de login que criaremos
                        .defaultSuccessUrl("/") // sem "true": volta para a página que o usuário tentou acessar antes
                        .permitAll() // a página de login é acessível sem autenticação
                )
                .httpBasic(withDefaults()) // autenticação básica (usuário e senha no cabeçalho HTTP)
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .rememberMe(withDefaults()); // mantém o usuário autenticado após fechar o navegador
        return http.build();
    }

    /**
     * Instancia o codificador BCrypt e deixa seu controle sob responsabilidade do Spring.
     * Sempre que o Spring Security precisar codificar ou comparar uma senha, utilizará este bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}