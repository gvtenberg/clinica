package br.edu.ifto.atividade3.model.repository;

import br.edu.ifto.atividade3.model.entity.Consulta;
import br.edu.ifto.atividade3.model.entity.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedicoRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Medico> buscarTodos(){
        //return em.createQuery("from Medico").getResultList(); // HQL (Warning do compilador nesta linha: Unchecked assignment)
        return em.createQuery("from Medico", Medico.class).getResultList();
    }

    public boolean remover(Long idMedico) {
        Medico medico = em.find(Medico.class, idMedico);

        if(medico == null)
            return false;

        em.remove(medico);
        return true;
    }

    public void salvar(Medico medico) {
        em.persist(medico);
    }

    public void update(Medico medico) {
        em.merge(medico);
    }

    public Medico buscarPorId(Long idMedico) {
        return em.find(Medico.class, idMedico);
    }

    public List<Consulta> buscarConsultas(Long idMedico) {
        Query query = em.createQuery("from Consulta where medico.id = :idMedico", Consulta.class);
        query.setParameter("idMedico", idMedico);
        return query.getResultList();
    }
}
