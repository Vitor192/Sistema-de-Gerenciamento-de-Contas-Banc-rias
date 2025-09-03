package com.bancodigital.corebanking.repository;

import com.bancodigital.corebanking.model.Transacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    // Métodos básicos de consulta
    List<Transacao> findByContaOrigemId(Long contaId);
    List<Transacao> findByContaDestinoId(Long contaId);
    List<Transacao> findByContaOrigemIdOrContaDestinoId(Long contaOrigemId, Long contaDestinoId);
    List<Transacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    // Métodos ordenados por data/hora
    List<Transacao> findByContaOrigemIdOrderByDataHoraDesc(Long contaId);
    Page<Transacao> findByContaOrigemIdOrderByDataHoraDesc(Long contaId, Pageable pageable);
    Page<Transacao> findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(Long contaOrigemId, Long contaDestinoId, Pageable pageable);
    
    // Métodos com filtro de período
    List<Transacao> findByContaOrigemIdAndDataHoraBetweenOrderByDataHoraDesc(Long contaId, LocalDateTime inicio, LocalDateTime fim);
    Page<Transacao> findByContaOrigemIdAndDataHoraBetweenOrderByDataHoraDesc(Long contaId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    
    // Métodos com filtro de período para origem ou destino
    List<Transacao> findByDataHoraBetweenAndContaOrigemIdOrDataHoraBetweenAndContaDestinoIdOrderByDataHoraDesc(
        LocalDateTime inicio1, LocalDateTime fim1, Long contaOrigemId, 
        LocalDateTime inicio2, LocalDateTime fim2, Long contaDestinoId);
    
    // Método para obter as N transações mais recentes
    List<Transacao> findTop10ByContaOrigemIdOrderByDataHoraDesc(Long contaId);
    List<Transacao> findTop10ByContaDestinoIdOrderByDataHoraDesc(Long contaId);
}