package com.bancodigital.corebanking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_conta")
public abstract class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroConta;

    private String agencia;

    private BigDecimal saldo = BigDecimal.ZERO;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    private boolean ativa = true;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "contaOrigem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoesEnviadas = new ArrayList<>();

    @OneToMany(mappedBy = "contaDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoesRecebidas = new ArrayList<>();

    public abstract BigDecimal calcularTaxa(BigDecimal valor);
}