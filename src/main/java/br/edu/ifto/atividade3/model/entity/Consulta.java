package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta")
public class Consulta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data da consulta é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime data;

    @NotNull(message = "O valor da consulta é obrigatório")
    @Positive(message = "O valor da consulta deve ser maior que zero")
    private Double valor;   // Alterado para Double para não aparecer a mensagem de falha de conversão

    private String observacao;

    @NotNull(message = "Selecione um paciente para a consulta")
    @ManyToOne(optional = false)                        // 0..* - 1 no diagrama de classes. "optional = false" torna a associação obrigatória
    @JoinColumn(name = "id_paciente", nullable = false) // Não é necessário, pois o nome das colunas é gerado automaticamente, mas facilita inserir dados no arquivo import.sql
    private Paciente paciente;                          // nullable = false" torna NOT NULL a coluna "id_paciente"

    @NotNull(message = "Selecione um médico para a consulta")
    @ManyToOne(optional = false)                        // 0..* - 1 no diagrama de classes. "optional = false" torna a associação obrigatória
    @JoinColumn(name = "id_medico", nullable = false)   // Não é necessário, pois o nome das colunas é gerado automaticamente, mas facilita inserir dados no arquivo import.sql
    private Medico medico;                              // nullable = false" torna NOT NULL a coluna "id_medico"

    public Consulta() {}

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
