package com.bancodigital.corebanking.service;

import com.bancodigital.corebanking.model.Usuario;
import com.bancodigital.corebanking.repository.UsuarioRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public Usuario registrarUsuario(String username, String password, boolean usar2FA) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setUsing2FA(usar2FA);

        if (usar2FA) {
            SecretGenerator secretGenerator = new DefaultSecretGenerator();
            String secret = secretGenerator.generate();
            usuario.setSecretKey(secret);
        }

        return usuarioRepository.save(usuario);
    }

    public boolean verificarCodigo2FA(Usuario usuario, String codigo) {
        if (!usuario.isUsing2FA()) {
            return true; // 2FA não está ativado para este usuário
        }

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        return verifier.isValidCode(usuario.getSecretKey(), codigo);
    }

    public String gerarSecretKey() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        return secretGenerator.generate();
    }

    public void ativar2FA(Usuario usuario) {
        if (!usuario.isUsing2FA()) {
            SecretGenerator secretGenerator = new DefaultSecretGenerator();
            String secret = secretGenerator.generate();
            usuario.setSecretKey(secret);
            usuario.setUsing2FA(true);
            usuarioRepository.save(usuario);
        }
    }

    public void desativar2FA(Usuario usuario) {
        if (usuario.isUsing2FA()) {
            usuario.setSecretKey(null);
            usuario.setUsing2FA(false);
            usuarioRepository.save(usuario);
        }
    }
}