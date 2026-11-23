package br.edu.ifto.clinica.model.repository;

import br.edu.ifto.clinica.model.entity.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório usado para procurar um CPF entre pacientes, médicos e secretários
 */
@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {

    PessoaFisica findByCpf(String cpf);
}
