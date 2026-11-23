package br.edu.ifto.clinica.model.security;

import br.edu.ifto.clinica.model.entity.Perfil;
import br.edu.ifto.clinica.model.entity.Usuario;
import br.edu.ifto.clinica.model.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = repository.findByUsuario(login);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Perfil perfil : usuario.getPerfis()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + perfil.getNome()));
        }

        return User.withUsername(usuario.getUsuario())
                .password(usuario.getSenha())
                .authorities(authorities)
                .disabled(!usuario.isAtivo())
                .build();
    }
}
