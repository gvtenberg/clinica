package br.edu.ifto.atividade3.model.repository;

import br.edu.ifto.atividade3.model.entity.Consulta;
import br.edu.ifto.atividade3.model.entity.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório responsável pelo acesso aos dados da entidade Medico
 *
 * @Repository informa ao Spring que esta classe faz parte da camada de acesso a dados.
 * Com isso, o Spring cria automaticamente um objeto desta classe na inicialização da aplicação e o
 * entrega ao controller que precisar dele, sem que seja necessário instanciá-la manualmente com new
 */
@Repository
public class MedicoRepository {
    /**
     * @PersistenceContext solicita ao container a injeção do EntityManager, que é a interface
     * do JPA responsável por gerenciar as entidades e executar as operações no banco de dados
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * @return lista com todos os médicos cadastrados.
     * A consulta é escrita em JPQL, linguagem que opera sobre as entidades do modelo e não
     * diretamente sobre as tabelas. O segundo argumento do método createQuery informa o tipo
     * esperado do resultado, dispensando conversão e eliminando o aviso de atribuição não verificada
     */
    public List<Medico> buscarTodos(){
        //return em.createQuery("from Medico").getResultList(); // HQL (Warning do compilador nesta linha: Unchecked assignment)
        return em.createQuery("from Medico", Medico.class).getResultList();
    }

    /**
     * @param idMedico identificador do médico a ser removido.
     * @return true se o médico foi localizado e removido, false caso o registro não exista.
     * O método find retorna nulo quando o identificador não corresponde a nenhum registro,
     * situação em que a remoção não deve ser tentada, pois o método remove não aceita
     * referência nula. Isso ocorre quando o mesmo registro é excluído duas vezes, por exemplo
     * a partir de duas abas do navegador ou pelo retorno a uma página já carregada
     */
    public boolean remover(Long idMedico) {
        Medico medico = em.find(Medico.class, idMedico);

        if(medico == null)
            return false;

        em.remove(medico);
        return true;
    }

    /**
     * @param medico objeto a ser inserido no banco de dados.
     * O método persist é utilizado em objetos novos, ainda sem identificador.
     * A coleção de consultas é o lado inverso da associação e não possui cascata,
     * portanto o persist grava apenas o registro do médico, sem afetar consultas
     */
    public void salvar(Medico medico) {
        em.persist(medico);
    }

    /**
     * @param medico objeto a ser atualizado no banco de dados.
     * O objeto recebido foi montado pelo Spring a partir dos dados do formulário, ou seja,
     * é um objeto novo na memória que o JPA nunca viu, mesmo tendo um identificador.
     * O método merge localiza no banco o registro com esse identificador e copia para ele
     * os valores informados. É diferente do persist, que serve para registros ainda inexistentes
     */
    public void update(Medico medico) {
        em.merge(medico);
    }

    /**
     * @param idMedico identificador do médico procurado
     * @return o médico correspondente, ou nulo caso o identificador não exista
     */
    public Medico buscarPorId(Long idMedico) {
        return em.find(Medico.class, idMedico);
    }

    /**
     * @param idMedico identificador do médico
     * @return lista com todas as consultas realizadas pelo médico informado.
     * A consulta navega pela associação declarada na entidade Consulta, utilizando o atributo medico
     * e seu identificador. O valor é informado por meio de parâmetro nomeado, o que evita a concatenação
     * de texto na consulta. Além de atender ao requisito de visualizar as consultas de um médico, o
     * resultado é utilizado pelo controller para verificar a existência de vínculos antes da exclusão
     */
    public List<Consulta> buscarConsultas(Long idMedico) {
        //Query query = em.createQuery("from Consulta where medico.id = :idMedico", Consulta.class); // Warning do compilador nesta linha: Unchecked assignment
        TypedQuery<Consulta> query = em.createQuery("from Consulta where medico.id = :idMedico", Consulta.class);
        query.setParameter("idMedico", idMedico);
        return query.getResultList();
    }
}
