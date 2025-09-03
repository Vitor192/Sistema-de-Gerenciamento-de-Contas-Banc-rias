package com.bancodigital.corebanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutenticacaoResponse {
    private String token;
    private boolean requires2FA;
    private String secretKey;
}