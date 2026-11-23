package br.edu.ifto.clinica.controller;

import br.edu.ifto.clinica.model.entity.Perfil;
import br.edu.ifto.clinica.model.entity.Secretario;
import br.edu.ifto.clinica.model.entity.Usuario;
import br.edu.ifto.clinica.model.repository.PerfilRepository;
import br.edu.ifto.clinica.model.repository.SecretarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

// Versão adaptada de médico
@Transactional
@Controller
@RequestMapping("secretarios")
public class SecretarioController {
    private final SecretarioRepository secretarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public SecretarioController(SecretarioRepository secretarioRepository, PerfilRepository perfilRepository,
                                PasswordEncoder passwordEncoder) {
        this.secretarioRepository = secretarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/form")
    public ModelAndView form(Secretario secretario, ModelMap model) {
        return new ModelAndView("secretarios/form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("secretarios", secretarioRepository.findAll());
        return new ModelAndView("secretarios/list", model);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid Secretario secretario, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if(result.hasErrors())
            return new ModelAndView("secretarios/form", model); // Retorna ao formulário com os dados preenchidos

        criarConta(secretario);
        secretarioRepository.save(secretario);
        attributes.addFlashAttribute("sucesso", "Secretário(a) '" + secretario.getNome() + "' cadastrado(a) com sucesso!");
        return new ModelAndView("redirect:/secretarios/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid Secretario secretario, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if(result.hasErrors())
            return new ModelAndView("secretarios/form", model);

        secretario.setUsuario(secretarioRepository.findById(secretario.getId()).orElseThrow().getUsuario());
        secretarioRepository.save(secretario);
        attributes.addFlashAttribute("sucesso", "Dados do(a) Secretário(a) '" + secretario.getNome() + "' atualizados com sucesso");
        return new ModelAndView("redirect:/secretarios/list");
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Optional<Secretario> secretarioOptional = secretarioRepository.findById(id);

        if(secretarioOptional.isEmpty()){
            attributes.addFlashAttribute("erro", "Erro ao remover o secretário com id = " + id);
            return new ModelAndView("redirect:/secretarios/list");
        }

        secretarioRepository.deleteById(id);
        attributes.addFlashAttribute("sucesso", "Secretário(a) com id = " + id + " removido(a) com sucesso");
        return new ModelAndView("redirect:/secretarios/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("secretario", secretarioRepository.findById(id).orElseThrow());
        return new ModelAndView("secretarios/form", model);
    }

    private void criarConta(Secretario secretario) {
        Perfil perfil = perfilRepository.findByNome("SECRETARIO");
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil SECRETARIO não encontrado");
        }

        Usuario digitado = secretario.getUsuario();
        Usuario conta = new Usuario(digitado.getUsuario(), passwordEncoder.encode(digitado.getSenha()));
        conta.adicionarPerfil(perfil);
        secretario.setId(null); // cadastro não sobrescreve uma pessoa existente
        secretario.setUsuario(conta);
    }
}
