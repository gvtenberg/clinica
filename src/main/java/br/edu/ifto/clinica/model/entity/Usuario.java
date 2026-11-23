package br.edu.ifto.clinica.model.entity;

import br.edu.ifto.clinica.model.validation.Cadastro;
import br.edu.ifto.clinica.model.validation.Edicao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O usuário é obrigatório", groups = Cadastro.class)
    @Size(max = 50, message = "O usuário deve ter no máximo 50 caracteres", groups = Cadastro.class)
    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @NotBlank(message = "A senha é obrigatória", groups = Cadastro.class)
    @Size(max = 50, message = "A senha deve ter no máximo 50 caracteres", groups = {Cadastro.class, Edicao.class})
    @Column(nullable = false, length = 100)
    private String senha; // hash BCrypt

    private boolean ativo = true;

    @ManyToMany
    @JoinTable(name = "usuario_perfil",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id"))
    private List<Perfil> perfis = new ArrayList<>();

    public Usuario() {}

    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public void adicionarPerfil(Perfil perfil) {
        perfis.add(perfil);
        perfil.getUsuarios().add(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<Perfil> getPerfis() { return perfis; }
}