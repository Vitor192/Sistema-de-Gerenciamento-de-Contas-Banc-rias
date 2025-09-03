package com.bancodigital.corebanking.controller;

import com.bancodigital.corebanking.model.Conta;
import com.bancodigital.corebanking.model.Transacao;
import com.bancodigital.corebanking.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping
    public ResponseEntity<List<Conta>> listarContas() {
        return ResponseEntity.ok(contaService.listarContas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> buscarContaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarContaPorId(id));
    }

    @GetMapping("/numero/{numeroConta}")
    public ResponseEntity<Conta> buscarContaPorNumero(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarContaPorNumero(numeroConta));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Conta>> listarContasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(contaService.listarContasPorCliente(clienteId));
    }

    @PostMapping("/{numeroConta}/deposito")
    public ResponseEntity<Transacao> depositar(
            @PathVariable String numeroConta,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaService.depositar(numeroConta, valor));
    }

    @PostMapping("/{numeroConta}/saque")
    public ResponseEntity<Transacao> sacar(
            @PathVariable String numeroConta,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaService.sacar(numeroConta, valor));
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Transacao> transferir(
            @RequestParam String numeroContaOrigem,
            @RequestParam String numeroContaDestino,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaService.transferir(numeroContaOrigem, numeroContaDestino, valor));
    }

    @PostMapping("/pix")
    public ResponseEntity<Transacao> pix(
            @RequestParam String numeroContaOrigem,
            @RequestParam String numeroContaDestino,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaService.pix(numeroContaOrigem, numeroContaDestino, valor));
    }

    @GetMapping("/{numeroConta}/extrato")
    public ResponseEntity<List<Transacao>> extrato(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.extrato(numeroConta));
    }

    @GetMapping("/{numeroConta}/extrato/periodo")
    public ResponseEntity<List<Transacao>> extratoPeriodo(
            @PathVariable String numeroConta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(contaService.extratoPeriodo(numeroConta, inicio, fim));
    }
}