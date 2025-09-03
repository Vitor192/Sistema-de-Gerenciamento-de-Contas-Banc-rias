package com.bancodigital.corebanking.controller;

import com.bancodigital.corebanking.dto.RegistroRequest;
import com.bancodigital.corebanking.model.Cliente;
import com.bancodigital.corebanking.model.Conta;
import com.bancodigital.corebanking.model.Transacao;
import com.bancodigital.corebanking.service.AutenticacaoService;
import com.bancodigital.corebanking.service.ClienteService;
import com.bancodigital.corebanking.service.ContaService;
import com.bancodigital.corebanking.service.TransacaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private AutenticacaoService autenticacaoService;

    // Página inicial
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Banco Digital - Seu banco completo");
        return "index";
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página de registro
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        return "registro";
    }

    @PostMapping("/registro")
    public String registroSubmit(@ModelAttribute RegistroRequest registroRequest, RedirectAttributes redirectAttributes) {
        try {
            autenticacaoService.registrarUsuario(registroRequest.getUsername(), registroRequest.getPassword(), registroRequest.isUsar2FA());
            redirectAttributes.addFlashAttribute("successMessage", "Registro realizado com sucesso!");
            return "redirect:/login?success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao registrar: " + e.getMessage());
            return "redirect:/registro?error";
        }
    }

    // Dashboard do usuário
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            List<Conta> contas = contaService.listarContasPorCliente(cliente.getId());
            model.addAttribute("cliente", cliente);
            model.addAttribute("contas", contas);
            
            // Obter transações recentes
            List<Transacao> transacoesRecentes = transacaoService.listarTransacoesRecentes(contas);
            model.addAttribute("transacoesRecentes", transacoesRecentes);
            
            // Calcular saldo total
            double saldoTotal = contas.stream()
                .mapToDouble(conta -> conta.getSaldo().doubleValue())
                .sum();
            model.addAttribute("saldoTotal", saldoTotal);
            
            return "dashboard";
        } else {
            // Se o usuário não tem cliente associado, redirecionar para completar cadastro
            return "redirect:/completar-cadastro";
        }
    }

    // Página para completar cadastro de cliente
    @GetMapping("/completar-cadastro")
    public String completarCadastroForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "completar-cadastro";
    }

    @PostMapping("/completar-cadastro")
    public String completarCadastroSubmit(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Associar o cliente ao usuário atual
            clienteService.cadastrarClienteComUsuario(cliente, username);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cadastro completado com sucesso!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao completar cadastro: " + e.getMessage());
            return "redirect:/completar-cadastro";
        }
    }

    // Listagem de contas
    @GetMapping("/contas")
    public String listarContas(Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            List<Conta> contas = contaService.listarContasPorCliente(cliente.getId());
            model.addAttribute("contas", contas);
            return "contas/listar";
        } else {
            return "redirect:/completar-cadastro";
        }
    }

    // Detalhes da conta
    @GetMapping("/contas/{id}")
    public String detalhesConta(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            Conta conta = contaService.buscarPorId(id);
            
            // Verificar se a conta pertence ao cliente logado
            if (conta.getCliente().getId().equals(cliente.getId())) {
                model.addAttribute("conta", conta);
                
                // Obter transações da conta
                LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
                LocalDateTime dataFim = LocalDateTime.now();
                List<Transacao> transacoes = transacaoService.gerarExtratoPorPeriodo(id, dataInicio, dataFim);
                model.addAttribute("transacoes", transacoes);
                
                return "contas/detalhes";
            } else {
                // Conta não pertence ao cliente logado
                return "redirect:/contas?error=unauthorized";
            }
        } else {
            return "redirect:/completar-cadastro";
        }
    }

    // Página de transferência
    @GetMapping("/transferencia")
    public String transferenciaForm(Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            List<Conta> contas = contaService.listarContasPorCliente(cliente.getId());
            model.addAttribute("contas", contas);
            return "operacoes/transferencia";
        } else {
            return "redirect:/completar-cadastro";
        }
    }

    // Página de depósito
    @GetMapping("/deposito")
    public String depositoForm(Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            List<Conta> contas = contaService.listarContasPorCliente(cliente.getId());
            model.addAttribute("contas", contas);
            return "operacoes/deposito";
        } else {
            return "redirect:/completar-cadastro";
        }
    }

    // Página de saque
    @GetMapping("/saque")
    public String saqueForm(Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            List<Conta> contas = contaService.listarContasPorCliente(cliente.getId());
            model.addAttribute("contas", contas);
            return "operacoes/saque";
        } else {
            return "redirect:/completar-cadastro";
        }
    }

    // Página de configurações de 2FA
    @GetMapping("/configuracoes/2fa")
    public String configuracoes2FA(Model model, Principal principal) {
        String username = principal.getName();
        boolean is2FAEnabled = autenticacaoService.is2FAEnabled(username);
        
        model.addAttribute("is2FAEnabled", is2FAEnabled);
        
        // Se 2FA não estiver ativado, gerar QR code
        if (!is2FAEnabled) {
            String secretKey = autenticacaoService.gerarChaveSecreta();
            String qrCodeUrl = autenticacaoService.gerarQRCodeUrl(username, secretKey);
            
            model.addAttribute("secretKey", secretKey);
            model.addAttribute("qrCodeUrl", qrCodeUrl);
        }
        
        return "configuracoes/2fa";
    }

    // Ativar 2FA
    @PostMapping("/configuracoes/2fa/ativar")
    public String ativar2FA(@RequestParam String code, @RequestParam String secretKey, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        
        try {
            boolean verificado = autenticacaoService.verificarCodigo2FA(secretKey, code);
            
            if (verificado) {
                autenticacaoService.ativar2FA(username, secretKey);
                redirectAttributes.addFlashAttribute("successMessage", "Autenticação de dois fatores ativada com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Código inválido. Tente novamente.");
            }
            
            return "redirect:/configuracoes/2fa";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao ativar 2FA: " + e.getMessage());
            return "redirect:/configuracoes/2fa";
        }
    }

    // Desativar 2FA
    @PostMapping("/configuracoes/2fa/desativar")
    public String desativar2FA(Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        
        try {
            autenticacaoService.desativar2FA(username);
            redirectAttributes.addFlashAttribute("successMessage", "Autenticação de dois fatores desativada com sucesso!");
            return "redirect:/configuracoes/2fa";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao desativar 2FA: " + e.getMessage());
            return "redirect:/configuracoes/2fa";
        }
    }

    // Página de perfil do usuário
    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal) {
        String username = principal.getName();
        Cliente cliente = clienteService.buscarPorUsuario(username);
        
        if (cliente != null) {
            model.addAttribute("cliente", cliente);
            return "perfil";
        } else {
            return "redirect:/completar-cadastro";
        }
    }

    // Página de erro 403 (acesso negado)
    @GetMapping("/403")
    public String acessoNegado() {
        return "error/403";
    }
    
    // Página de reprodução de mídia
    @GetMapping("/media-player")
    public String mediaPlayer() {
        return "media-player";
    }
}