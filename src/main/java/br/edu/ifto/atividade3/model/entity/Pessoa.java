package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Entidade abstrata base que representa uma pessoa no sistema, agrupando os dados de contato comuns de PessoaFisica e PessoaJuridica
 *
 * @Entity indica ao JPA que a classe faz parte do mapeamento de entidades do banco de dados
 * @Inheritance define a estratégia de herança utilizada para o mapeamento da hierarquia no banco de dados
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Pessoa {
    /**
     * @Id define o atributo como chave primária da entidade
     * @GeneratedValue com estratégia AUTO delega ao provedor JPA (Hibernate)
     * a decisão da melhor estratégia, que provavelmente será SEQUENCE
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * O telefone precisa ser formatado da seguinte forma: dois dígitos entre parênteses, espaço,
     * quatro ou cinco dígitos, hífen e quatro dígitos. Aceita telefone fixo, (63) 1234-5678,
     * e celular, (63) 99123-4567
     */
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
            message = "O telefone deve seguir o formato (63) 99123-4567 ou (63) 1234-5678")
    private String telefone;

    /**
     * O e-mail é obrigatório e precisa ser informado no formato nome@domoinio.com
     */
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve seguir o formato nome@dominio.com")
    private String email;

    public Pessoa() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
