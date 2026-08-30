package br.edu.ifto.atividade3.controller;

import br.edu.ifto.atividade3.model.entity.Consulta;
import br.edu.ifto.atividade3.model.repository.ConsultaRepository;
import br.edu.ifto.atividade3.model.repository.MedicoRepository;
import br.edu.ifto.atividade3.model.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    public ConsultaController(ConsultaRepository consultaRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

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

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("consultas", consultaRepository.buscarTodas());
        return new ModelAndView("consultas/list", model);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid Consulta consulta, BindingResult result, ModelMap model,
                             RedirectAttributes attributes) {                               // RedirectAttributes porque redirect cria uma nova requisição
        if(result.hasErrors() || !validaConsulta(consulta)) {                                // validaConsulta() verifica se médico e paciente foram selecionados no form
            model.addAttribute("medicos", medicoRepository.buscarTodos());      // Mostra a lista de médicos e pacientes...
            model.addAttribute("pacientes", pacienteRepository.buscarTodos());  // ...quando retorna de um erro de formulário
            return new ModelAndView("consultas/form", model);
        }

        consultaRepository.salvar(consulta);
        attributes.addFlashAttribute("sucesso", "Consulta cadastrada com sucesso!");
        return new ModelAndView("redirect:/consultas/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid Consulta consulta, BindingResult result, ModelMap model,
                               RedirectAttributes attributes) {                             // RedirectAttributes porque redirect cria uma nova requisição
        if(result.hasErrors() || !validaConsulta(consulta)) {                                // validaConsulta() verifica se médico e paciente foram selecionados no form
            model.addAttribute("medicos", medicoRepository.buscarTodos());      // Mostra a lista de médicos e pacientes...
            model.addAttribute("pacientes", pacienteRepository.buscarTodos());  // ...quando retorna de um erro de formulário
            return new ModelAndView("consultas/form", model);
        }

        consultaRepository.update(consulta);
        attributes.addFlashAttribute("sucesso", "Consulta atualizada com sucesso");
        return new ModelAndView("redirect:/consultas/list");
    }

    @GetMapping("/remove/{id}") // Por que não usar DeleteMapping?
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        if(consultaRepository.remover(id))
            attributes.addFlashAttribute("sucesso", "Consulta removida com sucesso");
        else
            attributes.addFlashAttribute("erro", "Erro ao remover consulta");

        return new ModelAndView("redirect:/consultas/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consulta", consultaRepository.buscarPorId(id)); // Falta verificar se consulta é null (excluída em outra aba, por exemplo)
        model.addAttribute("medicos", medicoRepository.buscarTodos());
        model.addAttribute("pacientes", pacienteRepository.buscarTodos());
        return new ModelAndView("consultas/form", model);
    }

    @GetMapping("/paciente/{id}")
    public ModelAndView consultaPaciente(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consultas", pacienteRepository.buscarConsultas(id));
        model.addAttribute("paciente", pacienteRepository.buscarPorId(id)); // Para a view mostrar mais dados
        return new ModelAndView("consultas/detalhes", model);
    }

    @GetMapping("/medico/{id}")
    public ModelAndView consultaMedico(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consultas", medicoRepository.buscarConsultas(id));
        model.addAttribute("medico", medicoRepository.buscarPorId(id)); // Para a view mostrar mais dados
        return new ModelAndView("consultas/detalhes", model);
    }

    // Método para checar se um médico e um paciente foram selecionados ao salvar uma consulta (teste quando os campos do form estão sem required)
    private boolean validaConsulta(Consulta consulta){
        return consulta.getMedico() != null && consulta.getMedico().getId() != null &&
                consulta.getPaciente() != null && consulta.getPaciente().getId() != null;
    }
}
