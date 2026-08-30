package br.edu.ifto.atividade3.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "medico")
public class Medico implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório") // @NotNull não funcionou aqui pq permite string vazia
    private String nome;

    @NotBlank(message = "O CRM é obrigatório")
    private String crm;

    @OneToMany(mappedBy = "medico") // 1 - 0..* no diagrama de classes
    private List<Consulta> consultas;

    public Medico() {}

    public String dados(){
        return nome + " - CRM: " + crm;
    }

    public String consultas(){
        if(consultas == null || consultas.isEmpty())
            return "O médico " + nome + " não possui consultas cadastradas";

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
