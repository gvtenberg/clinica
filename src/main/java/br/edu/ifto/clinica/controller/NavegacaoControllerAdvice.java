package br.edu.ifto.clinica.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @ControllerAdvice faz esta classe ser aplicada a TODOS os controllers da aplicação, sem precisar
 * repetir código em cada um deles
 * @ModelAttribute("paginaAtual") faz o valor retornado por este método ser adicionado automaticamente
 * ao modelo de toda requisição, antes da view ser renderizada - com o nome "paginaAtual". Como o
 * Thymeleaf compartilha o mesmo contexto entre a página principal e todos os fragmentos que ela usa,
 * esse valor fica visível dentro do header.html mesmo ele estando em outro arquivo
 */
@ControllerAdvice
public class NavegacaoControllerAdvice {

    @ModelAttribute("paginaAtual")
    public String paginaAtual(HttpServletRequest request) {
        String caminho = request.getRequestURI();

        if(caminho.startsWith("/consultas")){
            return "consultas";
        }
        if(caminho.startsWith("/pacientes")){
            return "pacientes";
        }
        if(caminho.startsWith("/medicos")){
            return "medicos";
        }

        return "";
    }
}
