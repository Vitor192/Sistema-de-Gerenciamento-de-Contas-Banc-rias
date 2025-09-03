package com.bancodigital.corebanking.controller;

import com.bancodigital.corebanking.config.JwtService;
import com.bancodigital.corebanking.dto.AutenticacaoRequest;
import com.bancodigital.corebanking.dto.AutenticacaoResponse;
import com.bancodigital.corebanking.dto.RegistroRequest;
import com.bancodigital.corebanking.dto.VerificacaoTotpRequest;
import com.bancodigital.corebanking.model.Usuario;
import com.bancodigital.corebanking.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/registrar")
    public ResponseEntity<AutenticacaoResponse> registrar(@RequestBody RegistroRequest request) {
        Usuario usuario = autenticacaoService.registrarUsuario(
                request.getUsername(),
                request.getPassword(),
                request.isUsar2FA()
        );
        
        String jwtToken = jwtService.generateToken(usuario);
        
        AutenticacaoResponse response = new AutenticacaoResponse(
                jwtToken,
                usuario.isUsing2FA(),
                usuario.isUsing2FA() ? usuario.getSecretKey() : null
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AutenticacaoResponse> autenticar(@RequestBody AutenticacaoRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        // Se o usuário tem 2FA ativado, não gera token ainda
        if (usuario.isUsing2FA()) {
            return ResponseEntity.ok(new AutenticacaoResponse(
                    null,
                    true,
                    null
            ));
        }
        
        String jwtToken = jwtService.generateToken(usuario);
        
        return ResponseEntity.ok(new AutenticacaoResponse(
                jwtToken,
                false,
                null
        ));
    }
    
    @PostMapping("/verificar-totp")
    public ResponseEntity<AutenticacaoResponse> verificarTotp(@RequestBody VerificacaoTotpRequest request) {
        Usuario usuario = (Usuario) autenticacaoService.loadUserByUsername(request.getUsername());
        
        if (!autenticacaoService.verificarCodigo2FA(usuario, request.getCode())) {
            return ResponseEntity.badRequest().build();
        }
        
        String jwtToken = jwtService.generateToken(usuario);
        
        return ResponseEntity.ok(new AutenticacaoResponse(
                jwtToken,
                true,
                null
        ));
    }
}