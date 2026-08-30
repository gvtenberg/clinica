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

@Transactional
@Controller
@RequestMapping("pacientes")
public class PacienteController {
    @Autowired
    private PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/form")
    public ModelAndView form(Paciente paciente, ModelMap model) {
        return new ModelAndView("pacientes/form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("pacientes", pacienteRepository.buscarTodos());
        return new ModelAndView("pacientes/list", model);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid Paciente paciente, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return new ModelAndView("pacientes/form", model); // Retorna ao formulário com os dados preenchidos

        pacienteRepository.salvar(paciente);
        return new ModelAndView("redirect:/pacientes/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid Paciente paciente, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return new ModelAndView("pacientes/form", model);

        pacienteRepository.update(paciente);
        return new ModelAndView("redirect:/pacientes/list");
    }

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

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("paciente", pacienteRepository.buscarPorId(id));
        return new ModelAndView("pacientes/form", model);
    }
}
