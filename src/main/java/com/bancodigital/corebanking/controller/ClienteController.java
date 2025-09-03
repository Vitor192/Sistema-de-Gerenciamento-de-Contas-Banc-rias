package com.bancodigital.corebanking.controller;

import com.bancodigital.corebanking.model.Cliente;
import com.bancodigital.corebanking.model.ContaCorrente;
import com.bancodigital.corebanking.model.ContaPoupanca;
import com.bancodigital.corebanking.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarClientePorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarClientePorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@Valid @RequestBody Cliente cliente) {
        Cliente novoCliente = clienteService.cadastrarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.atualizarCliente(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/contas/corrente")
    public ResponseEntity<ContaCorrente> criarContaCorrente(@PathVariable Long id) {
        ContaCorrente conta = clienteService.criarContaCorrenteParaCliente(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(conta);
    }
    
    @PostMapping("/{id}/contas/poupanca")
    public ResponseEntity<ContaPoupanca> criarContaPoupanca(@PathVariable Long id) {
        ContaPoupanca conta = clienteService.criarContaPoupancaParaCliente(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(conta);
    }
}