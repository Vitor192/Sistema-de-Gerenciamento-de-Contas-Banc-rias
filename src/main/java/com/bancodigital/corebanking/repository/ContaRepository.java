package com.bancodigital.corebanking.repository;

import com.bancodigital.corebanking.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByNumeroConta(String numeroConta);
    List<Conta> findByClienteId(Long clienteId);
    boolean existsByNumeroConta(String numeroConta);
}