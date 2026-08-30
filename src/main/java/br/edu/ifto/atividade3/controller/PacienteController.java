package br.edu.ifto.atividade3.controller;

import br.edu.ifto.atividade3.model.entity.Paciente;
import br.edu.ifto.atividade3.model.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsável pelas operações CRUD da entidade Paciente.
 *
 * @Controller indica ao Spring que esta classe trata requisições web e devolve nomes de views
 * @RequestMapping define o prefixo comum a todas as URLs mapeadas nesta classe
 * @Transactional faz com que cada método execute dentro de uma transação, permitindo que o
 * EntityManager envie as alterações ao banco ao final da execução
 */
@Transactional
@Controller
@RequestMapping("pacientes")
public class PacienteController {
    @Autowired
    private PacienteRepository pacienteRepository;

    /**
     * @param pacienteRepository repositório entregue automaticamente pelo Spring.
     * Como a classe possui apenas um construtor, o Spring o utiliza para entregar o
     * repositório, sem que seja necessário criá-lo manualmente com new
     */
    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * @param paciente objeto vazio adicionado automaticamente ao modelo pelo Spring
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view do formulário de cadastro.
     * O parâmetro Paciente é incluído no modelo com o nome "paciente", que é o mesmo utilizado
     * pelo th:object do formulário. Sem ele, o Thymeleaf não conseguiria vincular os campos
     */
    @GetMapping("/form")
    public ModelAndView form(Paciente paciente, ModelMap model) {
        return new ModelAndView("pacientes/form", model);
    }

    /**
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view com a listagem de todos os pacientes
     */
    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("pacientes", pacienteRepository.buscarTodos());
        return new ModelAndView("pacientes/list", model);
    }

    /**
     * @param paciente objeto preenchido com os dados enviados pelo formulário
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro
     * @PostMapping mapeia requisições HTTP POST, utilizadas no envio de dados que alteram o estado da aplicação
     * @Valid aciona a verificação das restrições declaradas nos atributos da entidade antes da persistência.
     * Em caso de erro, o formulário é renderizado na própria resposta do POST, preservando os dados digitados
     */
    @PostMapping("/save")
    public ModelAndView save(@Valid Paciente paciente, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return new ModelAndView("pacientes/form", model); // Retorna ao formulário com os dados preenchidos

        pacienteRepository.salvar(paciente);
        return new ModelAndView("redirect:/pacientes/list");
    }

    /**
     * @param paciente objeto preenchido com os dados enviados pelo formulário, contendo o identificador
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro.
     * A validação é necessária também na alteração: sem ela, um campo esvaziado seria detectado apenas
     * pelo Hibernate durante a gravação, resultando em erro interno em vez de mensagem no formulário.
     * O repositório utiliza merge, pois o objeto recebido do formulário está desanexado do contexto de persistência
     */
    @PostMapping("/update")
    public ModelAndView update(@Valid Paciente paciente, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return new ModelAndView("pacientes/form", model);

        pacienteRepository.update(paciente);
        return new ModelAndView("redirect:/pacientes/list");
    }

    /**
     * @param id identificador do paciente a ser excluído
     * @param attributes atributos de redirecionamento, utilizados para transportar a mensagem após o redirect
     * @return redirecionamento para a listagem de pacientes
     * @PathVariable é utilizado quando o valor da variável é passado diretamente na URL.
     * A existência de consultas é verificada antes da exclusão porque a tabela consulta possui
     * chave estrangeira para paciente, e o banco não permite apagar um registro que ainda é referenciado.
     * O terceiro caso trata o registro inexistente, situação que ocorre quando a mesma exclusão
     * é feita duas vezes, por exemplo a partir de duas abas do navegador
     */
    @GetMapping("/remove/{id}") // Por que não usar DeleteMapping?
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        if(!pacienteRepository.buscarConsultas(id).isEmpty()){ // Para evitar erro de ConstraintViolationException quando existe consulta com o paciente a ser excluído
            attributes.addFlashAttribute("erro", "Paciente possui consultas e não pode ser excluído");
        } else if(pacienteRepository.remover(id))
            attributes.addFlashAttribute("sucesso", "Paciente removido com sucesso");
        else
            attributes.addFlashAttribute("erro", "Erro ao remover o paciente");

        return new ModelAndView("redirect:/pacientes/list");
    }

    /**
     * @param id identificador do paciente a ser editado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view do formulário preenchido com os dados do paciente.
     * É utilizado o mesmo formulário do cadastro. A distinção entre inclusão e alteração é feita
     * na view pela verificação do identificador do objeto, que define a URL de destino do formulário
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("paciente", pacienteRepository.buscarPorId(id));
        return new ModelAndView("pacientes/form", model);
    }
}
