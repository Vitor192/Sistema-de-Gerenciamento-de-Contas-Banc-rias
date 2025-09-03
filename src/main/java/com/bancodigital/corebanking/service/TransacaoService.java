package com.bancodigital.corebanking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancodigital.corebanking.exception.SaldoInsuficienteException;
import com.bancodigital.corebanking.exception.TransacaoInvalidaException;
import com.bancodigital.corebanking.model.Conta;
import com.bancodigital.corebanking.model.Transacao;
import com.bancodigital.corebanking.model.Transacao.TipoTransacao;
import com.bancodigital.corebanking.repository.ContaRepository;
import com.bancodigital.corebanking.repository.TransacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;

    /**
     * Realiza um depósito em uma conta
     * 
     * @param contaId ID da conta
     * @param valor Valor a ser depositado
     * @param descricao Descrição da transação
     * @return A transação realizada
     */
    @Transactional
    public Transacao realizarDeposito(Long contaId, BigDecimal valor, String descricao) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransacaoInvalidaException("O valor do depósito deve ser maior que zero");
        }

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new TransacaoInvalidaException("Conta não encontrada"));

        if (!conta.isAtiva()) {
            throw new TransacaoInvalidaException("Conta inativa");
        }

        // Atualizar saldo da conta
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);

        // Registrar transação
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.DEPOSITO);
        transacao.setValor(valor);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setDescricao(descricao);
        transacao.setContaOrigem(conta);

        return transacaoRepository.save(transacao);
    }

    /**
     * Realiza um saque em uma conta
     * 
     * @param contaId ID da conta
     * @param valor Valor a ser sacado
     * @param descricao Descrição da transação
     * @return A transação realizada
     */
    @Transactional
    public Transacao realizarSaque(Long contaId, BigDecimal valor, String descricao) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransacaoInvalidaException("O valor do saque deve ser maior que zero");
        }

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new TransacaoInvalidaException("Conta não encontrada"));

        if (!conta.isAtiva()) {
            throw new TransacaoInvalidaException("Conta inativa");
        }

        // Verificar se há saldo suficiente (considerando o limite)
        BigDecimal saldoDisponivel = conta.getSaldo().add(conta.getLimite());
        if (valor.compareTo(saldoDisponivel) > 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque");
        }

        // Atualizar saldo da conta
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);

        // Registrar transação
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.SAQUE);
        transacao.setValor(valor);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setDescricao(descricao);
        transacao.setContaOrigem(conta);

        return transacaoRepository.save(transacao);
    }

    /**
     * Realiza uma transferência entre contas
     * 
     * @param contaOrigemId ID da conta de origem
     * @param contaDestinoId ID da conta de destino
     * @param valor Valor a ser transferido
     * @param descricao Descrição da transação
     * @return A transação realizada
     */
    @Transactional
    public Transacao realizarTransferencia(Long contaOrigemId, Long contaDestinoId, BigDecimal valor, String descricao) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransacaoInvalidaException("O valor da transferência deve ser maior que zero");
        }

        if (contaOrigemId.equals(contaDestinoId)) {
            throw new TransacaoInvalidaException("As contas de origem e destino não podem ser iguais");
        }

        Conta contaOrigem = contaRepository.findById(contaOrigemId)
                .orElseThrow(() -> new TransacaoInvalidaException("Conta de origem não encontrada"));

        Conta contaDestino = contaRepository.findById(contaDestinoId)
                .orElseThrow(() -> new TransacaoInvalidaException("Conta de destino não encontrada"));

        if (!contaOrigem.isAtiva() || !contaDestino.isAtiva()) {
            throw new TransacaoInvalidaException("Uma das contas está inativa");
        }

        // Verificar se há saldo suficiente (considerando o limite)
        BigDecimal saldoDisponivel = contaOrigem.getSaldo().add(contaOrigem.getLimite());
        if (valor.compareTo(saldoDisponivel) > 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência");
        }

        // Atualizar saldos das contas
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        // Registrar transação de saída
        Transacao transacaoSaida = new Transacao();
        transacaoSaida.setTipo(TipoTransacao.TRANSFERENCIA);
        transacaoSaida.setValor(valor);
        transacaoSaida.setDataHora(LocalDateTime.now());
        transacaoSaida.setDescricao(descricao);
        transacaoSaida.setContaOrigem(contaOrigem);
        transacaoSaida.setContaDestino(contaDestino);
        transacaoRepository.save(transacaoSaida);

        return transacaoSaida;
    }

    /**
     * Busca todas as transações de uma conta (tanto origem quanto destino)
     * 
     * @param contaId ID da conta
     * @return Lista de transações
     */
    public List<Transacao> buscarTransacoesPorConta(Long contaId) {
        return transacaoRepository.findByContaOrigemIdOrContaDestinoId(contaId, contaId);
    }

    /**
     * Busca todas as transações de uma conta com paginação (tanto origem quanto destino)
     * 
     * @param contaId ID da conta
     * @param pageable Informações de paginação
     * @return Página de transações
     */
    public Page<Transacao> buscarTransacoesPorConta(Long contaId, Pageable pageable) {
        return transacaoRepository.findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(contaId, contaId, pageable);
    }

    /**
     * Busca todas as transações de uma conta em um período
     * 
     * @param contaId ID da conta
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Lista de transações
     */
    public List<Transacao> buscarTransacoesPorContaEPeriodo(Long contaId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transacaoRepository.findByContaOrigemIdAndDataHoraBetweenOrderByDataHoraDesc(contaId, dataInicio, dataFim);
    }

    /**
     * Busca todas as transações de uma conta em um período com paginação
     * 
     * @param contaId ID da conta
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @param pageable Informações de paginação
     * @return Página de transações
     */
    public Page<Transacao> buscarTransacoesPorContaEPeriodo(Long contaId, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        return transacaoRepository.findByContaOrigemIdAndDataHoraBetweenOrderByDataHoraDesc(contaId, dataInicio, dataFim, pageable);
    }

    /**
     * Busca uma transação pelo ID
     * 
     * @param id ID da transação
     * @return Transação encontrada ou vazio
     */
    public Optional<Transacao> buscarPorId(Long id) {
        return transacaoRepository.findById(id);
    }
    
    /**
     * Lista as transações recentes de uma lista de contas
     * 
     * @param contas Lista de contas
     * @return Lista de transações recentes
     */
    public List<Transacao> listarTransacoesRecentes(List<Conta> contas) {
        if (contas == null || contas.isEmpty()) {
            return List.of();
        }
        
        // Obtém as últimas 10 transações de cada conta (origem e destino) e combina em uma única lista
        return contas.stream()
            .flatMap(conta -> {
                List<Transacao> transacoesOrigem = transacaoRepository.findTop10ByContaOrigemIdOrderByDataHoraDesc(conta.getId());
                List<Transacao> transacoesDestino = transacaoRepository.findTop10ByContaDestinoIdOrderByDataHoraDesc(conta.getId());
                return java.util.stream.Stream.concat(transacoesOrigem.stream(), transacoesDestino.stream());
            })
            .sorted((t1, t2) -> t2.getDataHora().compareTo(t1.getDataHora()))
            .distinct() // Evita duplicações
            .limit(10)
            .toList();
    }
    
    /**
     * Gera extrato por período para uma conta (incluindo transações de origem e destino)
     * 
     * @param contaId ID da conta
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @return Lista de transações no período
     */
    public List<Transacao> gerarExtratoPorPeriodo(Long contaId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transacaoRepository.findByDataHoraBetweenAndContaOrigemIdOrDataHoraBetweenAndContaDestinoIdOrderByDataHoraDesc(
            dataInicio, dataFim, contaId, dataInicio, dataFim, contaId);
    }
}