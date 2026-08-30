package br.edu.ifto.atividade3.model.repository;

import br.edu.ifto.atividade3.model.entity.Consulta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório responsável pelo acesso aos dados da entidade Consulta
 *
 * @Repository informa ao Spring que esta classe faz parte da camada de acesso a dados.
 * Com isso, o Spring cria automaticamente um objeto desta classe na inicialização da aplicação e o
 * entrega ao controller que precisar dele, sem que seja necessário instanciá-la manualmente com new
 */
@Repository
public class ConsultaRepository {
    /**
     * @PersistenceContext solicita ao container a injeção do EntityManager, que é a interface
     * do JPA responsável por gerenciar as entidades e executar as operações no banco de dados
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * @return lista com todas as consultas cadastradas.
     * A consulta é escrita em JPQL, linguagem que opera sobre as entidades do modelo e não
     * diretamente sobre as tabelas. O segundo argumento do método createQuery informa o tipo
     * esperado do resultado, dispensando conversão e eliminando o aviso de atribuição não verificada
     */
    public List<Consulta> buscarTodas() {
        //return em.createQuery("from Consulta").getResultList(); // HQL (Warning do compilador nesta linha: Unchecked assignment)
        return em.createQuery("from Consulta", Consulta.class).getResultList();
    }

    /**
     * @param idConsulta identificador da consulta a ser removida.
     * @return true se a consulta foi localizada e removida, false caso o registro não exista.
     * O método find retorna nulo quando o identificador não corresponde a nenhum registro,
     * situação em que a remoção não deve ser tentada, pois o método remove não aceita
     * referência nula. Isso ocorre quando o mesmo registro é excluído duas vezes, por exemplo
     * a partir de duas abas do navegador ou pelo retorno a uma página já carregada
     */
    public boolean remover(Long idConsulta) {
        Consulta consulta = em.find(Consulta.class, idConsulta);

        if(consulta == null)
            return false;

        em.remove(consulta);
        return true;
    }

    /**
     * @param consulta objeto a ser inserido no banco de dados.
     * O método persist é utilizado em objetos novos, ainda sem identificador.
     * As associações com paciente e médico não possuem cascata, de modo que apenas as
     * chaves estrangeiras são gravadas, sem alteração das entidades associadas
     */
    public void salvar(Consulta consulta) {
        em.persist(consulta);
    }

    /**
     * @param consulta objeto a ser atualizado no banco de dados.
     * O objeto recebido foi montado pelo Spring a partir dos dados do formulário, ou seja,
     * é um objeto novo na memória que o JPA nunca viu, mesmo tendo um identificador.
     * O método merge localiza no banco o registro com esse identificador e copia para ele
     * os valores informados. É diferente do persist, que serve para registros ainda inexistentes
     */
    public void update(Consulta consulta) {
        em.merge(consulta);
    }

    /**
     * @param idConsulta identificador da consulta procurada
     * @return a consulta correspondente, ou nulo caso o identificador não exista no banco
     */
    public Consulta buscarPorId(Long idConsulta) {
        return em.find(Consulta.class, idConsulta);
    }
}
