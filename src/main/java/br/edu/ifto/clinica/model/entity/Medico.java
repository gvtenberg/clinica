package br.edu.ifto.clinica.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;

/**
 * Entidade que representa o médico, conforme o diagrama de classes da atividade
 *
 * @Entity indica ao JPA que a classe deve ser mapeada para uma tabela do banco de dados
 * @Table define o nome da tabela correspondente
 */
@Entity
public class Medico extends PessoaFisica implements Serializable {
    @NotBlank(message = "O CRM é obrigatório")
    private String crm;

    /**
     * @OneToMany representa a multiplicidade 1 - 0..* do diagrama de classes: um médico
     * pode realizar várias consultas.
     * O atributo mappedBy indica que esta é a extremidade inversa da associação, ou seja,
     * a chave estrangeira está na tabela consulta, no atributo medico da classe Consulta.
     * O lado que contém a chave estrangeira é chamado de lado dono, e é o único capaz de
     * gravar a associação. Por isso, alterações feitas nesta coleção não chegam ao banco
     */
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas;

    public Medico() {}

    /**
     * @return descrição textual do médico, reunindo nome e CRM
     * Método previsto no diagrama de classes da atividade, mas sem uso nesta primeira versão
     */
    public String dados(){
        return getNome() + " - CRM: " + crm;
    }

    /**
     * @return descrição textual de todas as consultas realizadas pelo médico.
     * Método previsto no diagrama de classes da atividade, mas sem uso nesta primeira versão
     * A verificação considera tanto a lista nula quanto vazia, pois um médico recuperado
     * do banco sem consultas apresenta uma lista vazia, e não uma referência nula
     */
    public String consultas(){
        if(consultas == null || consultas.isEmpty())
            return "O médico " + getNome() + " não possui consultas cadastradas";

        StringBuilder sb = new StringBuilder();
        for(Consulta c : consultas)
            sb.append(c.dados()).append("\n");

        return sb.toString();
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
