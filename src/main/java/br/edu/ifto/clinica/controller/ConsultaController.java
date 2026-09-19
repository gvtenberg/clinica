package br.edu.ifto.clinica.controller;

import br.edu.ifto.clinica.model.entity.Consulta;
import br.edu.ifto.clinica.model.repository.ConsultaRepository;
import br.edu.ifto.clinica.model.repository.MedicoRepository;
import br.edu.ifto.clinica.model.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsável pelas operações CRUD da entidade Consulta e pela consulta
 * das agendas de um paciente e de um médico
 *
 * @Controller indica ao Spring que esta classe trata requisições web e devolve nomes de views
 * @RequestMapping define o prefixo comum a todas as URLs mapeadas nesta classe
 * @Transactional faz com que cada método execute dentro de uma transação, permitindo que o
 * EntityManager envie as alterações ao banco ao final da execução
 */
@Transactional
@Controller
@RequestMapping("consultas")
public class ConsultaController {
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    /**
     * @param consultaRepository repositório de consultas entregue automaticamente pelo Spring
     * @param medicoRepository repositório de médicos, necessário para preencher o campo de seleção do formulário
     * @param pacienteRepository repositório de pacientes, necessário para preencher o campo de seleção do formulário.
     * Como a classe possui apenas um construtor, o Spring o utiliza para entregar os
     * repositórios, sem que seja necessário criá-los manualmente com new
     */
    public ConsultaController(ConsultaRepository consultaRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * @param consulta objeto vazio adicionado automaticamente ao modelo pelo Spring
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view do formulário de cadastro de consultas.
     * As listas de médicos e de pacientes são enviadas à view para preencher os campos de seleção,
     * pois a consulta deve estar obrigatoriamente associada a um médico e a um paciente.
     * A data e o valor recebem conteúdo inicial somente quando se trata de um novo cadastro, identificado pela ausência de identificador no objeto
     */
    @GetMapping("/form")
    public ModelAndView form(Consulta consulta, ModelMap model) {
        if(consulta.getId() == null){
            consulta.setData(java.time.LocalDateTime.now());
            consulta.setValor(300.0);
        }

        model.addAttribute("medicos", medicoRepository.buscarTodos());
        model.addAttribute("pacientes", pacienteRepository.buscarTodos());
        return new ModelAndView("consultas/form", model);
    }

    /**
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view com a listagem de todas as consultas
     */
    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("consultas", consultaRepository.buscarTodas());
        return new ModelAndView("consultas/list", model);
    }

    /**
     * @param consulta objeto preenchido com os dados enviados pelo formulário
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @param attributes atributos de redirecionamento, utilizados para transportar a mensagem após o redirect
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro
     * @Valid aciona a verificação das restrições declaradas nos atributos da entidade.
     * Em caso de erro, as listas de médicos e de pacientes precisam ser enviadas novamente ao modelo,
     * pois o formulário é renderizado outra vez e os campos de seleção ficariam sem opções
     */
    @PostMapping("/save")
    public ModelAndView save(@Valid Consulta consulta, BindingResult result, ModelMap model,
                             RedirectAttributes attributes) {                               // RedirectAttributes porque redirect cria uma nova requisição
        validaConsulta(consulta, result);                                                   // validaConsulta() verifica se médico e paciente foram selecionados no form

        if(result.hasErrors()) {
            model.addAttribute("medicos", medicoRepository.buscarTodos());      // Mostra a lista de médicos e pacientes...
            model.addAttribute("pacientes", pacienteRepository.buscarTodos());  // ...quando retorna de um erro de formulário
            return new ModelAndView("consultas/form", model);
        }

        consultaRepository.salvar(consulta);
        attributes.addFlashAttribute("sucesso", "Consulta do(a) Paciente '" + pacienteRepository.buscarPorId(consulta.getPaciente().getId()).getNome() + "' cadastrada com sucesso!");
        return new ModelAndView("redirect:/consultas/list");
    }

    /**
     * @param consulta objeto preenchido com os dados enviados pelo formulário, contendo o identificador
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @param attributes atributos de redirecionamento, utilizados para transportar a mensagem após o redirect
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro.
     * A validação é necessária também na alteração: sem ela, um campo esvaziado seria detectado apenas
     * pelo Hibernate durante a gravação, resultando em erro interno em vez de mensagem no formulário
     */
    @PostMapping("/update")
    public ModelAndView update(@Valid Consulta consulta, BindingResult result, ModelMap model,
                               RedirectAttributes attributes) {                             // RedirectAttributes porque redirect cria uma nova requisição
        validaConsulta(consulta, result);                                                   // validaConsulta() verifica se médico e paciente foram selecionados no form

        if(result.hasErrors()) {
            model.addAttribute("medicos", medicoRepository.buscarTodos());      // Mostra a lista de médicos e pacientes...
            model.addAttribute("pacientes", pacienteRepository.buscarTodos());  // ...quando retorna de um erro de formulário
            return new ModelAndView("consultas/form", model);
        }

        consultaRepository.update(consulta);
        attributes.addFlashAttribute("sucesso", "Consulta do(a) Paciente '" + pacienteRepository.buscarPorId(consulta.getPaciente().getId()).getNome() + "' atualizada com sucesso");
        return new ModelAndView("redirect:/consultas/list");
    }

    /**
     * @param id identificador da consulta a ser excluída
     * @param attributes atributos de redirecionamento, utilizados para transportar a mensagem após o redirect
     * @return redirecionamento para a listagem de consultas
     * @PathVariable é utilizado quando o valor da variável é passado diretamente na URL.
     * Não é necessário verificar vínculos, pois nenhuma tabela possui chave estrangeira para consulta.
     * A verificação de retorno trata o caso do registro já excluído, situação que ocorre quando a
     * mesma exclusão é feita duas vezes, por exemplo a partir de duas abas do navegador
     */
    @GetMapping("/remove/{id}") // Por que não usar DeleteMapping?
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        if(consultaRepository.remover(id))
            attributes.addFlashAttribute("sucesso", "Consulta com id = " + id + " removida com sucesso");
        else
            attributes.addFlashAttribute("erro", "Erro ao remover consulta com id = " + id);

        return new ModelAndView("redirect:/consultas/list");
    }

    /**
     * @param id identificador da consulta a ser editada
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view do formulário preenchida com os dados da consulta.
     * É utilizado o mesmo formulário do cadastro, acompanhado das listas de médicos e de pacientes,
     * necessárias para que os campos de seleção apresentem as opções disponíveis
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consulta", consultaRepository.buscarPorId(id)); // Futuramente: verificar se consulta é null (qndo excluída em outra aba, por exemplo)
        model.addAttribute("medicos", medicoRepository.buscarTodos());
        model.addAttribute("pacientes", pacienteRepository.buscarTodos());
        return new ModelAndView("consultas/form", model);
    }

    /**
     * @param id identificador do paciente
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view com todas as consultas do paciente informado.
     * Atende ao requisito de visualizar todas as consultas de um paciente. A view "detalhes" é
     * compartilhada com a listagem por médico e diferencia os dois casos pela presença dos
     * atributos "paciente" e "medico" no modelo
     */
    @GetMapping("/paciente/{id}")
    public ModelAndView consultaPaciente(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consultas", pacienteRepository.buscarConsultas(id));
        model.addAttribute("paciente", pacienteRepository.buscarPorId(id)); // Para a view mostrar mais dados
        return new ModelAndView("consultas/detalhes", model);
    }

    /**
     * @param id identificador do médico
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view com todas as consultas realizadas pelo médico informado.
     * Atende ao requisito de visualizar todas as consultas realizadas por um médico, utilizando
     * a mesma view empregada na listagem por paciente
     */
    @GetMapping("/medico/{id}")
    public ModelAndView consultaMedico(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consultas", medicoRepository.buscarConsultas(id));
        model.addAttribute("medico", medicoRepository.buscarPorId(id)); // Para a view mostrar mais dados
        return new ModelAndView("consultas/detalhes", model);
    }

    /**
     * @param consulta objeto preenchido a partir do formulário
     * @param result resultado da validação, onde as mensagens de erro são registradas.
     * O formulário envia apenas o identificador do médico e do paciente. Quando nenhuma opção é
     * selecionada, o Spring cria um objeto Medico e um objeto Paciente com identificador nulo.
     * A anotação @NotNull verifica apenas se o objeto existe, e como ele existe, a validação passa.
     * Por isso a verificação precisa ser feita sobre o identificador, e não sobre o objeto.
     * As mensagens são registradas no BindingResult para que apareçam no formulário junto
     * com as demais mensagens de validação
     */
    private void validaConsulta(Consulta consulta, BindingResult result){
        if (consulta.getMedico() == null || consulta.getMedico().getId() == null)
            result.rejectValue("medico.id", "medico.obrigatorio", "Selecione um médico para a consulta");

        if (consulta.getPaciente() == null || consulta.getPaciente().getId() == null)
            result.rejectValue("paciente.id", "paciente.obrigatorio", "Selecione um paciente para a consulta");
    }
}
