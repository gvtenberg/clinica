package br.edu.ifto.atividade3.model.repository;

import br.edu.ifto.atividade3.model.entity.Consulta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConsultaRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Consulta> buscarTodas() {
        //return em.createQuery("from Consulta").getResultList(); // HQL (Warning do compilador nesta linha: Unchecked assignment)
        return em.createQuery("from Consulta", Consulta.class).getResultList();
    }

    public boolean remover(Long idConsulta) {
        Consulta consulta = em.find(Consulta.class, idConsulta);

        if(consulta == null)
            return false;

        em.remove(consulta);
        return true;
    }

    public void salvar(Consulta consulta) {
        em.persist(consulta);
    }

    public void update(Consulta consulta) {
        em.merge(consulta);
    }

    public Consulta buscarPorId(Long idConsulta) {
        return em.find(Consulta.class, idConsulta);
    }
}
