package br.edu.ifto.clinica.model.entity;

import jakarta.persistence.Entity;

import java.io.Serializable;

/**
 * Entidade que representa o secretário, herdando os dados pessoais de PessoaFisica e a conta de Pessoa
 */
@Entity
public class Secretario extends PessoaFisica implements Serializable {
    public Secretario() {}
}