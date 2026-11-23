package br.edu.ifto.clinica.controller;

import br.edu.ifto.clinica.model.entity.Paciente;
import br.edu.ifto.clinica.model.entity.Perfil;
import br.edu.ifto.clinica.model.entity.Usuario;
import br.edu.ifto.clinica.model.repository.PacienteRepository;
import br.edu.ifto.clinica.model.repository.PerfilRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * Controller responsável pelas operações CRUD da entidade Paciente
 *
 * @Controller indica ao Spring que esta classe trata requisições web e devolve nomes de views
 * @RequestMapping define o prefixo comum a todas as URLs mapeadas nesta classe
 * @Transactional garante que as operações do controller sejam executadas dentro de um contexto transacional
 */
@Transactional
@Controller
@RequestMapping("pacientes")
public class PacienteController {
    private final PacienteRepository pacienteRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param pacienteRepository repositório entregue automaticamente pelo Spring.
     * Como a classe possui apenas um construtor, o Spring o utiliza para entregar o
     * repositório, sem que seja necessário criá-lo manualmente com new
     */
    public PacienteController(PacienteRepository pacienteRepository, PerfilRepository perfilRepository,
                              PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
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
        model.addAttribute("pacientes", pacienteRepository.findAll());
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
    public ModelAndView save(@Valid Paciente paciente, BindingResult result, ModelMap model, RedirectAttributes attributes,
                             Authentication authentication, HttpServletRequest request) throws ServletException {
        if(result.hasErrors())
            return new ModelAndView("pacientes/form", model); // Retorna ao formulário com os dados preenchidos

        String senhaDigitada = paciente.getUsuario().getSenha(); // guardada antes de virar hash em criarConta()
        criarConta(paciente);
        pacienteRepository.save(paciente);
        attributes.addFlashAttribute("sucesso", "Paciente '" + paciente.getNome() + "' cadastrado(a) com sucesso!");

        if(ehEquipe(authentication))
            return new ModelAndView("redirect:/pacientes/list");

        if(authentication == null) { // visitante: já entra com a conta que acabou de criar
            request.login(paciente.getUsuario().getUsuario(), senhaDigitada);
            request.changeSessionId(); // novo id de sessão após o login
        }

        return new ModelAndView("redirect:/");
    }

    /**
     * @param paciente objeto preenchido com os dados enviados pelo formulário, contendo o identificador
     * @param result resultado da validação, que deve ser declarado imediatamente após o objeto validado
     * @param model objeto utilizado para transportar dados do controller para a view
     * @return redirecionamento para a listagem em caso de sucesso, ou o próprio formulário em caso de erro.
     * A validação é necessária também na alteração: sem ela, um campo esvaziado seria detectado apenas
     * pelo banco durante a gravação, resultando em erro interno em vez de mensagem no formulário.
     * O repositório utiliza o método save(), que reconhece a presença do ID e realiza a atualização (merge)
     */
    @PostMapping("/update")
    public ModelAndView update(@Valid Paciente paciente, BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if(result.hasErrors())
            return new ModelAndView("pacientes/form", model);

        paciente.setUsuario(pacienteRepository.findById(paciente.getId()).orElseThrow().getUsuario());
        pacienteRepository.save(paciente);
        attributes.addFlashAttribute("sucesso", "Dados do(a) Paciente '" + paciente.getNome() + "' atualizados com sucesso");
        return new ModelAndView("redirect:/pacientes/list");
    }

    /**
     * @param id identificador do paciente a ser excluído
     * @param attributes atributos de redirecionamento, utilizados para transportar a mensagem após o redirect
     * @return redirecionamento para a listagem de pacientes
     * @PathVariable é utilizado quando o valor da variável é passado diretamente na URL.
     * A existência de consultas é verificada antes da exclusão para evitar violação de integridade referencial.
     */
    @GetMapping("/remove/{id}") // Por que não usar DeleteMapping?
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Optional<Paciente> pacienteOptional = pacienteRepository.findById(id);    // Para evitar erro de ConstraintViolationException quando o paciente a ser excluído possui consulta cadastrada

        if(pacienteOptional.isEmpty()){
            attributes.addFlashAttribute("erro", "Erro ao remover o paciente com id = " + id);
            return new ModelAndView("redirect:/pacientes/list");
        }

        Paciente paciente = pacienteOptional.get();
        if(!paciente.getConsultas().isEmpty()){
            attributes.addFlashAttribute("erro", "Paciente '" + paciente.getNome() + "' possui consultas e não pode ser excluído(a)");
        } else {
            pacienteRepository.deleteById(id);
            attributes.addFlashAttribute("sucesso", "Paciente com id = " + id + " removido(a) com sucesso");
        }

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
        model.addAttribute("paciente", pacienteRepository.findById(id).orElseThrow());
        return new ModelAndView("pacientes/form", model);
    }

    /**
     * Monta a conta de acesso da pessoa com o perfil inicial e a senha codificada.
     * Aproveita do formulário somente o usuário e a senha
     */
    private void criarConta(Paciente paciente) {
        Perfil perfil = perfilRepository.findByNome("PACIENTE");
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil PACIENTE não encontrado");
        }

        Usuario digitado = paciente.getUsuario();
        Usuario conta = new Usuario(digitado.getUsuario(), passwordEncoder.encode(digitado.getSenha()));
        conta.adicionarPerfil(perfil);
        paciente.setId(null); // cadastro não sobrescreve uma pessoa existente
        paciente.setUsuario(conta);
    }

    /**
     * @param authentication dados de quem está logado, ou null quando é um visitante
     * @return true se quem está cadastrando é secretário ou administrador
     */
    private boolean ehEquipe(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SECRETARIO") || authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }

        return false;
    }
}
