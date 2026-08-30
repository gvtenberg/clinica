package br.edu.ifto.atividade3.controller;

import br.edu.ifto.atividade3.model.entity.Medico;
import br.edu.ifto.atividade3.model.repository.MedicoRepository;
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
@RequestMapping("medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository medicoRepository;

    public MedicoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @GetMapping("/form")
    public ModelAndView form(Medico medico, ModelMap model) {
        return new ModelAndView("medicos/form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("medicos", medicoRepository.buscarTodos());
        return new ModelAndView("medicos/list", model);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid Medico medico, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return new ModelAndView("medicos/form", model); // Retorna ao formulário com os dados preenchidos

        medicoRepository.salvar(medico);
        return new ModelAndView("redirect:/medicos/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid Medico medico, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return new ModelAndView("medicos/form", model);

        medicoRepository.update(medico);
        return new ModelAndView("redirect:/medicos/list");
    }

    @GetMapping("/remove/{id}") // Por que não usar DeleteMapping?
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        if(!medicoRepository.buscarConsultas(id).isEmpty()){ // Para evitar erro de ConstraintViolationException quando existe consulta com o médico a ser excluído
            attributes.addFlashAttribute("erro", "Médico possui consultas e não pode ser excluído");
        } else if(medicoRepository.remover(id))
            attributes.addFlashAttribute("sucesso", "Médico removido com sucesso");
        else
            attributes.addFlashAttribute("erro", "Erro ao remover o medico");

        return new ModelAndView("redirect:/medicos/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("medico", medicoRepository.buscarPorId(id));
        return new ModelAndView("medicos/form", model);
    }
}
