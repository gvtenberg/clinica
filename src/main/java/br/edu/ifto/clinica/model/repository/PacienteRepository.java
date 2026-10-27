package br.edu.ifto.clinica.model.repository;

import br.edu.ifto.clinica.model.entity.Consulta;
import br.edu.ifto.clinica.model.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface de repositório responsável pelas operações de acesso aos dados da entidade Paciente
 * Ao estender JpaRepository, o Spring Data JPA cria automaticamente a implementação concreta
 * desta interface em tempo de execução, disponibilizando métodos prontos para operações CRUD.
 * Os parâmetros genéricos <Paciente, Long> indicam a entidade gerenciada e o tipo da sua chave primária.
 *
 * @Repository indica ao Spring que esta interface pertence à camada de persistência/acesso a dados
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Busca todas as consultas associadas a um determinado paciente via JPQL.
     * A anotação @Query especifica explicitamente a instrução JPQL necessária para que o Spring Data
     * JPA retorne objetos do tipo Consulta, navegando pela associação c.paciente.id e evitando erros de
     * derivação automática de nome de método. O parâmetro nomeado :id é vinculado pela anotação @Param("id"),
     * garantindo proteção contra SQL Injection. O resultado atende à visualização de consultas e à
     * verificação de integridade referencial no controller antes de uma exclusão.
     *
     * @param id identificador do paciente pesquisado
     * @return lista com todas as consultas vinculadas ao paciente informado
     */
    @Query("select c from Consulta c where c.paciente.id = :id")
    public List<Consulta> findConsultasById(@Param("id") Long id);
}
