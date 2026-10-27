package br.edu.ifto.clinica.controller;

import br.edu.ifto.clinica.model.entity.Medico;
import br.edu.ifto.clinica.model.repository.MedicoRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller responsável pelas operações CRUD da entidade Medico
 *
 * @Controller indica ao Spring que esta classe trata requisições web e devolve nomes de views
 * @RequestMapping define o prefixo comum a todas as URLs mapeadas nesta classe
 * @Transactional garante que as operações do controller sejam executadas dentro de um contexto transacional
 */
@Transactional
@Controller
@RequestMapping("medicos")
public class MedicoController {
    private final MedicoRepository medicoRepository;

    /**
     * @param medicoRepository repositório entregue automaticamente pelo Spring.
     * Como a classe possui apenas um construtor, o Spring o utiliza para entregar o
     * repositório, sem que seja necessário criá-lo manualmente com new
     */
    public MedicoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    /**
     * @param medico objeto vazio adicionado automaticamente ao modelo pelo Spring
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view do formulário de cadastro.
     * O parâmetro Medico é incluído no modelo com o nome "medico", que é o mesmo utilizado
     * pelo th:object do formulário. Sem ele, o Thymeleaf não conseguiria vincular os campos
     */
    @GetMapping("/form")
    public ModelAndView form(Medico medico, ModelMap model) {
        return new ModelAndView("medicos/form", model);
    }

    /**
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view com a listagem de todos os médicos
     */
    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("medicos", medicoRepository.findAll());
        return new ModelAndView("medicos/list", model);
    }

    /**
     * @param medico objeto preenchido com os dados enviados pelo formulário
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro
     * @PostMapping mapeia requisições HTTP POST, utilizadas no envio de dados que alteram o estado da aplicação
     * @Valid aciona a verificação das restrições declaradas nos atributos da entidade antes da persistência.
     * Em caso de erro, o formulário é renderizado na própria resposta do POST, preservando os dados digitados
     */
    @PostMapping("/save")
    public ModelAndView save(@Valid Medico medico, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if(result.hasErrors())
            return new ModelAndView("medicos/form", model); // Retorna ao formulário com os dados preenchidos

        medicoRepository.save(medico);
        attributes.addFlashAttribute("sucesso", "Médico(a) '" + medico.getNome() + "' cadastrado(a) com sucesso!");
        return new ModelAndView("redirect:/medicos/list");
    }

    /**
     * @param medico objeto preenchido com os dados enviados pelo formulário, contendo o identificador
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro.
     * A validação é necessária também na alteração: sem ela, um campo esvaziado seria detectado apenas
     * pelo banco durante a gravação, resultando em erro interno em vez de mensagem no formulário.
     * O repositório utiliza o método save(), que reconhece a presença do ID e realiza a atualização (merge)
     */
    @PostMapping("/update")
    public ModelAndView update(@Valid Medico medico, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if(result.hasErrors())
            return new ModelAndView("medicos/form", model);

        medicoRepository.save(medico);
        attributes.addFlashAttribute("sucesso", "Dados do(a) Médico(a) '" + medico.getNome() + "' atualizados com sucesso");
        return new ModelAndView("redirect:/medicos/list");
    }

    /**
     * @param id identificador do médico a ser excluído
     * @param attributes atributos de redirecionamento, utilizados para transportar a mensagem após o redirect
     * @return redirecionamento para a listagem de médicos
     * @PathVariable é utilizado quando o valor da variável é passado diretamente na URL.
     * A existência de consultas é verificada antes da exclusão para evitar violação de integridade referencial.
     */
    @GetMapping("/remove/{id}") // Por que não usar DeleteMapping?
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Optional<Medico> medicoOptional = medicoRepository.findById(id);    // Para evitar erro de ConstraintViolationException quando o médico a ser excluído possui consulta cadastrada

        if(medicoOptional.isEmpty()){
            attributes.addFlashAttribute("erro", "Erro ao remover o médico com id = " + id);
            return new ModelAndView("redirect:/medicos/list");
        }

        Medico medico = medicoOptional.get();
        if(!medico.getConsultas().isEmpty()){
            attributes.addFlashAttribute("erro", "Médico(a) '" + medico.getNome() + "' possui consultas e não pode ser excluído(a)");
        } else {
            medicoRepository.deleteById(id);
            attributes.addFlashAttribute("sucesso", "Médico(a) com id = " + id + " removido(a) com sucesso");
        }

        return new ModelAndView("redirect:/medicos/list");
    }

    /**
     * @param id identificador do médico a ser editado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return view do formulário preenchido com os dados do médico.
     * É utilizado o mesmo formulário do cadastro. A distinção entre inclusão e alteração é feita
     * na view pela verificação do identificador do objeto, que define a URL de destino do formulário
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("medico", medicoRepository.findById(id).orElseThrow());
        return new ModelAndView("medicos/form", model);
    }
}
