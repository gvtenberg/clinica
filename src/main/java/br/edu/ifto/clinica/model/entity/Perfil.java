package br.edu.ifto.clinica.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nome; // ADMIN, SECRETARIO, MEDICO ou PACIENTE, sem o prefixo ROLE_

    @ManyToMany(mappedBy = "perfis")
    private List<Usuario> usuarios = new ArrayList<>();

    public Perfil() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Usuario> getUsuarios() { return usuarios; }
}