package br.edu.ifto.clinica.controller;

import br.edu.ifto.clinica.model.entity.Perfil;
import br.edu.ifto.clinica.model.entity.PessoaFisica;
import br.edu.ifto.clinica.model.entity.Secretario;
import br.edu.ifto.clinica.model.entity.Usuario;
import br.edu.ifto.clinica.model.repository.PerfilRepository;
import br.edu.ifto.clinica.model.repository.PessoaFisicaRepository;
import br.edu.ifto.clinica.model.repository.SecretarioRepository;
import br.edu.ifto.clinica.model.repository.UsuarioRepository;
import br.edu.ifto.clinica.model.validation.Cadastro;
import br.edu.ifto.clinica.model.validation.Edicao;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

// Versão adaptada de médico
@Transactional
@Controller
@RequestMapping("secretarios")
public class SecretarioController {
    private final SecretarioRepository secretarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final PessoaFisicaRepository pessoaFisicaRepository;

    public SecretarioController(SecretarioRepository secretarioRepository, PerfilRepository perfilRepository,
                                PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository,
                                PessoaFisicaRepository pessoaFisicaRepository) {
        this.secretarioRepository = secretarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.pessoaFisicaRepository = pessoaFisicaRepository;
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
    public ModelAndView save(@Validated({Default.class, Cadastro.class}) Secretario secretario, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        validaCpf(secretario, result);
        validaIdade(secretario, result);
        validaUsuario(secretario, result);

        if(result.hasErrors())
            return new ModelAndView("secretarios/form", model); // Retorna ao formulário com os dados preenchidos

        criarConta(secretario);
        secretarioRepository.save(secretario);
        attributes.addFlashAttribute("sucesso", "Secretário(a) '" + secretario.getNome() + "' cadastrado(a) com sucesso!");
        return new ModelAndView("redirect:/secretarios/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Validated({Default.class, Edicao.class}) Secretario secretario, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        validaCpf(secretario, result);
        validaIdade(secretario, result);

        if(result.hasErrors())
            return new ModelAndView("secretarios/form", model);

        atualizarConta(secretario);
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

    /**
     * Na edição, mantém a conta que está no banco e troca a senha somente se ela foi preenchida
     */
    private void atualizarConta(Secretario secretario) {
        Usuario conta = secretarioRepository.findById(secretario.getId()).orElseThrow().getUsuario();
        Usuario digitado = secretario.getUsuario();

        if (conta != null && digitado != null && digitado.getSenha() != null && !digitado.getSenha().isBlank()) {
            conta.setSenha(passwordEncoder.encode(digitado.getSenha()));
        }

        secretario.setUsuario(conta);
    }

    /**
     * Registra erro se o CPF pertence a outro paciente, médico ou secretário. Na edição, o próprio registro não conta como repetido
     */
    private void validaCpf(Secretario secretario, BindingResult result) {
        PessoaFisica existente = pessoaFisicaRepository.findByCpf(secretario.getCpf());
        if (existente != null && !existente.getId().equals(secretario.getId())) {
            result.rejectValue("cpf", "cpf.duplicado", "Este CPF já está cadastrado");
        }
    }

    /**
     * Registra erro se o nome de usuário já pertence a outra conta
     */
    private void validaUsuario(Secretario secretario, BindingResult result) {
        if (secretario.getUsuario() != null && usuarioRepository.findByUsuario(secretario.getUsuario().getUsuario()) != null) {
            result.rejectValue("usuario.usuario", "usuario.duplicado", "Este usuário já está em uso");
        }
    }

    /**
     * Registra erro se a pessoa tem menos de 18 anos
     */
    private void validaIdade(Secretario secretario, BindingResult result) {
        LocalDate nascimento = secretario.getDataNascimento();
        if (nascimento != null && nascimento.isAfter(LocalDate.now().minusYears(18))) {
            result.rejectValue("dataNascimento", "idade.minima", "O(A) secretário(a) deve ter 18 anos ou mais");
        }
    }
}
