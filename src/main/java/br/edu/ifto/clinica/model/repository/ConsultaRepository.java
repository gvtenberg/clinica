package br.edu.ifto.clinica.model.repository;

import br.edu.ifto.clinica.model.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório responsável pelas operações de acesso aos dados da entidade Consulta
 * Ao estender JpaRepository, o Spring Data JPA cria automaticamente a implementação concreta
 * desta interface em tempo de execução, disponibilizando métodos prontos para operações CRUD.
 * Os parâmetros genéricos <Consulta, Long> indicam a entidade gerenciada e o tipo da sua chave primária.
 *
 * @Repository indica ao Spring que esta interface pertence à camada de persistência/acesso a dados
 */
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}
