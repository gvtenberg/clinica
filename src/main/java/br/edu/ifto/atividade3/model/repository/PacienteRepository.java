package br.edu.ifto.atividade3.model.repository;

import br.edu.ifto.atividade3.model.entity.Consulta;
import br.edu.ifto.atividade3.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PacienteRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Paciente> buscarTodos() {
        //return em.createQuery("from Paciente").getResultList(); // HQL (Warning do compilador nesta linha: Unchecked assignment)
        return em.createQuery("from Paciente", Paciente.class).getResultList();
    }

    public boolean remover(Long idPaciente) {
        Paciente paciente = em.find(Paciente.class, idPaciente);

        if(paciente == null)
            return false;

        em.remove(paciente);
        return true;
    }

    public void salvar(Paciente paciente) {
        em.persist(paciente);
    }

    public void update(Paciente paciente) {
        em.merge(paciente);
    }

    public Paciente buscarPorId(Long idPaciente) {
        return em.find(Paciente.class, idPaciente);
    }

    public List<Consulta> buscarConsultas(Long idPaciente) {
        Query query = em.createQuery("from Consulta where paciente.id = :idPaciente", Consulta.class);
        query.setParameter("idPaciente", idPaciente);
        return query.getResultList();
    }
}
