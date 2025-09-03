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
@DiscriminatorValue("POUPANCA")
public class ContaPoupanca extends Conta {
    
    private BigDecimal taxaRendimento = new BigDecimal("0.003"); // 0.3% ao mês
    
    @Override
    public BigDecimal calcularTaxa(BigDecimal valor) {
        // Sem taxa para transferências até R$ 1000
        if (valor.compareTo(new BigDecimal("1000")) <= 0) {
            return BigDecimal.ZERO;
        }
        // Taxa de 0.1% para valores acima de R$ 1000
        return valor.multiply(new BigDecimal("0.001"));
    }
}