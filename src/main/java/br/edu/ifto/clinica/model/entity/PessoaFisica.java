package br.edu.ifto.clinica.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Entidade abstrata que representa uma pessoa física, herdando os dados de contato de Pessoa e
 * agrupando os dados pessoais (nome e CPF)
 *
 * @Entity indica ao JPA que a classe faz parte do mapeamento de entidades da hierarquia do banco de dados
 */
@Entity
public abstract class PessoaFisica extends Pessoa {
    /**
     * É utilizada a anotação @NotBlank, e não @NotNull, porque um campo de texto enviado em branco
     * produz uma string vazia, e não um valor nulo. A anotação @NotNull consideraria essa string
     * válida e permitiria a gravação do registro sem conteúdo
     */
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    /**
     * O CPF precisa ser informado no formato 123.456.789-09
     */
    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "O CPF deve ser válido e deve seguir o formato 123.456.789-09")
    @Column(unique = true)
    private String cpf;

    /**
     * @DateTimeFormat informa ao Spring o formato enviado pelo campo date do formulário
     */
    @NotNull(message = "A data de nascimento é obrigatória")
    @PastOrPresent(message = "A data de nascimento não pode ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    public PessoaFisica() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
