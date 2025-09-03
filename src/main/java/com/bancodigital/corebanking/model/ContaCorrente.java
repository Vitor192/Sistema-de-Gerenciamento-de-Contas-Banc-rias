package com.bancodigital.corebanking.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CORRENTE")
public class ContaCorrente extends Conta {
    
    private BigDecimal limiteChequeEspecial = BigDecimal.ZERO;
    private BigDecimal taxaManutencao = new BigDecimal("15.90");
    
    @Override
    public BigDecimal calcularTaxa(BigDecimal valor) {
        // Taxa de 0.3% para transferências
        return valor.multiply(new BigDecimal("0.003"));
    }
}