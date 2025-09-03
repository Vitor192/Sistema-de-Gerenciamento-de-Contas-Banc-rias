package com.bancodigital.corebanking.model;

/**
 * Enum que representa os tipos de transações bancárias
 */
public enum TipoTransacao {
    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    TRANSFERENCIA_ENVIADA("Transferência Enviada"),
    TRANSFERENCIA_RECEBIDA("Transferência Recebida"),
    PAGAMENTO("Pagamento"),
    ESTORNO("Estorno");
    
    private final String descricao;
    
    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}