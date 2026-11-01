package br.edu.ifto.clinica.model.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
                        // Páginas públicas: acessíveis sem login
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()

//####################### Alterar depois para mostrar somente consultas do médico/paciente logado #######################//
                        .requestMatchers("/consultas/paciente/1").hasAnyRole("PACIENTE", "SECRETARIA", "ADMIN")
                        .requestMatchers("/consultas/medico/7").hasAnyRole("MEDICO", "SECRETARIA", "ADMIN")

                        // Consultas são gerenciadas pela secretaria e administrador
                        .requestMatchers("/consultas/**").hasAnyRole("SECRETARIA", "ADMIN")

                        // Somente o administrador exclui pacientes
                        .requestMatchers("/pacientes/remove/**").hasRole("ADMIN")
                        .requestMatchers("/pacientes/**").hasAnyRole("SECRETARIA", "ADMIN")

                        // A secretaria apenas consulta a lista de médicos
                        .requestMatchers("/medicos/list").hasAnyRole("SECRETARIA", "ADMIN")
                        .requestMatchers("/medicos/**").hasRole("ADMIN")

                        //.requestMatchers(HttpMethod.POST, "/pessoa/save").permitAll()
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
     * @param passwordEncoder codificador usado para gerar o hash BCrypt das senhas
     * @return serviço que o Spring Security consulta para localizar usuários no momento do login
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();
        UserDetails secretaria = User.withUsername("secretaria")
                .password(passwordEncoder.encode("123"))
                .roles("SECRETARIA")
                .build();
        UserDetails medico = User.withUsername("medico")
                .password(passwordEncoder.encode("123"))
                .roles("MEDICO")
                .build();
        UserDetails paciente = User.withUsername("paciente")
                .password(passwordEncoder.encode("123"))
                .roles("PACIENTE")
                .build();
        return new InMemoryUserDetailsManager(admin, secretaria, medico, paciente);
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