package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
public class Paciente implements Serializable {
    /**
     * @Id define o atributo como chave primária da entidade
     * @GeneratedValue com estratégia IDENTITY delega ao banco de dados a geração do valor,
     * por meio de coluna autoincremento
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * É utilizada a anotação @NotBlank, e não @NotNull, porque um campo de texto enviado em branco
     * produz uma string vazia, e não um valor nulo. A anotação @NotNull consideraria essa string
     * válida e permitiria a gravação do registro sem conteúdo
     */
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

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
        return nome + " - " + telefone;
    }

    /**
     * @return descrição textual de todas as consultas do paciente.
     * Método previsto no diagrama de classes da atividade, mas sem uso nesta primeira versão
     * A verificação considera tanto lista nula quanto vazia, pois um paciente recuperado
     * do banco sem consultas apresenta uma lista vazia, e não uma referência nula
     */
    public String consultas(){
        if(consultas == null || consultas.isEmpty())
            return "O paciente " + nome + " não possui consultas cadastradas";

        StringBuilder sb = new StringBuilder();
        for(Consulta c : consultas)
            sb.append(c.dados()).append("\n");

        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
