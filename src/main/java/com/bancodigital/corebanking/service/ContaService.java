package com.bancodigital.corebanking.service;

import com.bancodigital.corebanking.exception.SaldoInsuficienteException;
import com.bancodigital.corebanking.exception.RecursoNaoEncontradoException;
import com.bancodigital.corebanking.model.*;
import com.bancodigital.corebanking.repository.ContaRepository;
import com.bancodigital.corebanking.repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public List<Conta> listarContas() {
        return contaRepository.findAll();
    }

    public List<Conta> listarContasPorCliente(Long clienteId) {
        return contaRepository.findByClienteId(clienteId);
    }

    public Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }

    public Conta buscarContaPorNumero(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }

    @Transactional
    public ContaCorrente criarContaCorrente(Cliente cliente) {
        ContaCorrente conta = new ContaCorrente();
        conta.setCliente(cliente);
        conta.setNumeroConta(gerarNumeroConta());
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.ZERO);
        conta.setLimiteChequeEspecial(new BigDecimal("500.00"));
        return contaRepository.save(conta);
    }

    @Transactional
    public ContaPoupanca criarContaPoupanca(Cliente cliente) {
        ContaPoupanca conta = new ContaPoupanca();
        conta.setCliente(cliente);
        conta.setNumeroConta(gerarNumeroConta());
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.ZERO);
        return contaRepository.save(conta);
    }

    private String gerarNumeroConta() {
        Random random = new Random();
        String numeroConta;
        do {
            numeroConta = String.format("%08d", random.nextInt(100000000));
        } while (contaRepository.existsByNumeroConta(numeroConta));
        return numeroConta;
    }

    @Transactional
    public Transacao depositar(String numeroConta, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que zero");
        }

        Conta conta = buscarContaPorNumero(numeroConta);
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setTipo(Transacao.TipoTransacao.DEPOSITO);
        transacao.setValor(valor);
        transacao.setTaxa(BigDecimal.ZERO);
        transacao.setContaDestino(conta);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setDescricao("Depósito em conta");

        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao sacar(String numeroConta, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser maior que zero");
        }

        Conta conta = buscarContaPorNumero(numeroConta);
        BigDecimal saldoDisponivel = conta.getSaldo();
        
        // Verifica se é conta corrente com cheque especial
        if (conta instanceof ContaCorrente) {
            saldoDisponivel = saldoDisponivel.add(((ContaCorrente) conta).getLimiteChequeEspecial());
        }
        
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setTipo(Transacao.TipoTransacao.SAQUE);
        transacao.setValor(valor);
        transacao.setTaxa(BigDecimal.ZERO);
        transacao.setContaOrigem(conta);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setDescricao("Saque em conta");

        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao transferir(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser maior que zero");
        }

        Conta contaOrigem = buscarContaPorNumero(numeroContaOrigem);
        Conta contaDestino = buscarContaPorNumero(numeroContaDestino);
        
        BigDecimal taxa = contaOrigem.calcularTaxa(valor);
        BigDecimal valorTotal = valor.add(taxa);
        
        BigDecimal saldoDisponivel = contaOrigem.getSaldo();
        
        // Verifica se é conta corrente com cheque especial
        if (contaOrigem instanceof ContaCorrente) {
            saldoDisponivel = saldoDisponivel.add(((ContaCorrente) contaOrigem).getLimiteChequeEspecial());
        }
        
        if (saldoDisponivel.compareTo(valorTotal) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valorTotal));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Transacao transacao = new Transacao();
        transacao.setTipo(Transacao.TipoTransacao.TRANSFERENCIA);
        transacao.setValor(valor);
        transacao.setTaxa(taxa);
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setDescricao("Transferência entre contas");

        return transacaoRepository.save(transacao);
    }
    
    @Transactional
    public Transacao pix(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de PIX deve ser maior que zero");
        }

        Conta contaOrigem = buscarContaPorNumero(numeroContaOrigem);
        Conta contaDestino = buscarContaPorNumero(numeroContaDestino);
        
        // PIX não tem taxa
        BigDecimal taxa = BigDecimal.ZERO;
        
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o PIX");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Transacao transacao = new Transacao();
        transacao.setTipo(Transacao.TipoTransacao.PIX);
        transacao.setValor(valor);
        transacao.setTaxa(taxa);
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setDescricao("Transferência via PIX");

        return transacaoRepository.save(transacao);
    }

    public List<Transacao> extrato(String numeroConta) {
        Conta conta = buscarContaPorNumero(numeroConta);
        return transacaoRepository.findByContaOrigemIdOrContaDestinoId(conta.getId(), conta.getId());
    }
    
    public List<Transacao> extratoPeriodo(String numeroConta, LocalDateTime inicio, LocalDateTime fim) {
        Conta conta = buscarContaPorNumero(numeroConta);
        List<Transacao> transacoes = transacaoRepository.findByContaOrigemIdOrContaDestinoId(conta.getId(), conta.getId());
        return transacoes.stream()
                .filter(t -> t.getDataHora().isAfter(inicio) && t.getDataHora().isBefore(fim))
                .toList();
    }
}