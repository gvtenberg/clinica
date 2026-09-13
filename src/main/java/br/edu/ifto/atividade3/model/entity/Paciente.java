package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 * Entidade que representa o paciente, conforme o diagrama de classes da atividade
 *
 * @Entity indica ao JPA que a classe deve ser mapeada para uma tabela do banco de dados
 * @Table define o nome da tabela correspondente
 */
@Entity
@Table(name = "paciente")
public class Paciente extends PessoaFisica implements Serializable {
    /**
     * @OneToMany representa a multiplicidade 1 - 0..* do diagrama de classes: um paciente
     * pode possuir várias consultas.
     * O atributo mappedBy indica que esta é a extremidade inversa da associação, ou seja,
     * a chave estrangeira está na tabela consulta, no atributo paciente da classe Consulta.
     * O lado que contém a chave estrangeira é chamado de lado dono, e é o único capaz de
     * gravar a associação. Por isso, alterações feitas nesta coleção não chegam ao banco
     */
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas;

    public Paciente() {}

    /**
     * @return descrição textual do paciente, reunindo nome e telefone
     * Método previsto no diagrama de classes da atividade, mas sem uso nesta primeira versão
     */
    public String dados(){
        return getNome() + " - " + getTelefone();
    }

    /**
     * @return descrição textual de todas as consultas do paciente.
     * Método previsto no diagrama de classes da atividade, mas sem uso nesta primeira versão
     * A verificação considera tanto lista nula quanto vazia, pois um paciente recuperado
     * do banco sem consultas apresenta uma lista vazia, e não uma referência nula
     */
    public String consultas(){
        if(consultas == null || consultas.isEmpty())
            return "O paciente " + getNome() + " não possui consultas cadastradas";

        StringBuilder sb = new StringBuilder();
        for(Consulta c : consultas)
            sb.append(c.dados()).append("\n");

        return sb.toString();
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
