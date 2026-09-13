package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CNPJ;

@Entity
public class PessoaJuridica extends Pessoa {
    /**
     * É utilizada a anotação @NotBlank, e não @NotNull, porque um campo de texto enviado em branco
     * produz uma string vazia, e não um valor nulo. A anotação @NotNull consideraria essa string
     * válida e permitiria a gravação do registro sem conteúdo
     */
    @NotBlank(message = "A Razão Social é obrigatória")
    private String razaoSocial;

    /**
     * O CNPJ é obrigatório e deve seguir o novo padrão alfanumérico que
     * entrou em vigor em Julho/2026: 12.ABC.345/01DE-35
     */
    @NotBlank(message = "O CNPJ é obrigatório")
    @CNPJ(message = "O CNPJ deve seguir o formato 12.ABC.345/01DE-35") // Não testado, pois PessoaJuridica ainda não possui uso no sistema
    private String cnpj;

    public PessoaJuridica() {}

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
