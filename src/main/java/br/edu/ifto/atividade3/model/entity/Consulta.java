package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade que representa a consulta médica, conforme o diagrama de classes da atividade.
 * Corresponde ao lado dono das duas associações do modelo: é nesta tabela que ficam
 * as chaves estrangeiras para paciente e para médico
 *
 * @Entity indica ao JPA que a classe deve ser mapeada para uma tabela do banco de dados
 * @Table define o nome da tabela correspondente
 */
@Entity
@Table(name = "consulta")
public class Consulta implements Serializable {
    /**
     * @Id define o atributo como chave primária da entidade
     * @GeneratedValue com estratégia IDENTITY delega ao banco de dados a geração do valor,
     * por meio de coluna autoincremento
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @DateTimeFormat informa ao Spring o formato utilizado na conversão entre o texto enviado
     * pelo campo datetime-local do formulário e o tipo LocalDateTime
     */
    @NotNull(message = "A data da consulta é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime data;

    /**
     * O tipo utilizado é Double, e não o primitivo double, porque um campo de formulário enviado
     * em branco produz texto vazio. Em um atributo primitivo, a conversão falharia antes da
     * validação e o usuário receberia a mensagem padrão do Spring sobre incompatibilidade de tipo.
     * Com o tipo Double, o campo vazio é convertido para nulo e a mensagem definida em @NotNull é exibida.
     */
    @NotNull(message = "O valor da consulta é obrigatório")
    @Positive(message = "O valor da consulta deve ser maior que zero")
    private Double valor;

    private String observacao;

    /**
     * @ManyToOne representa a multiplicidade 0..* - 1 do diagrama de classes: várias consultas
     * para um mesmo paciente. O atributo optional definido como false torna a associação obrigatória
     * @JoinColumn define o nome da coluna de chave estrangeira. Embora o nome pudesse ser gerado
     * automaticamente, a declaração explícita facilita a inserção de dados no arquivo import.sql.
     * O atributo nullable definido como false torna a coluna id_paciente obrigatória no banco (NOT NULL)
     */
    @NotNull(message = "Selecione um paciente para a consulta")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    /**
     * @ManyToOne representa a multiplicidade 0..* - 1 do diagrama de classes: várias consultas
     * para um mesmo médico. Demais atributos seguem a mesma finalidade descrita na associação com Paciente
     */
    @NotNull(message = "Selecione um médico para a consulta")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    public Consulta() {}

    /**
     * @return descrição textual da consulta, reunindo paciente, data, médico, valor e observação
     * Método previsto no diagrama de classes da atividade, mas sem uso nesta primeira versão
     */
    public String dados(){
        return paciente.getNome() + " | " + data.toString() + " | Dr(a) " + medico.getNome() + " | R$ " +  valor + " | Obs.: " + observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
