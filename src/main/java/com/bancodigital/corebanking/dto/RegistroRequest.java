package com.bancodigital.corebanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroRequest {
    private String username;
    private String password;
    private boolean usar2FA;
}