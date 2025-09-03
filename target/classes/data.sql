-- Script de inicialização do banco de dados
-- Este script será executado automaticamente pelo Spring Boot ao iniciar a aplicação

-- Inserir dados apenas se as tabelas estiverem vazias
INSERT INTO usuario (id, username, password, ativo, usando_2fa, secret_key, perfil)
SELECT 1, 'admin', '$2a$10$rAGZzAY5Ghdwi4Z7G0JXpOvNwqKnMqQD9K8XD9bZ1YQEwbRR.Ykm2', true, false, NULL, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username = 'admin');

INSERT INTO usuario (id, username, password, ativo, usando_2fa, secret_key, perfil)
SELECT 2, 'usuario', '$2a$10$rAGZzAY5Ghdwi4Z7G0JXpOvNwqKnMqQD9K8XD9bZ1YQEwbRR.Ykm2', true, false, NULL, 'CLIENTE'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username = 'usuario');

-- Inserir clientes
INSERT INTO cliente (id, nome, cpf, email, telefone, data_nascimento, endereco, cidade, estado, cep, usuario_id)
SELECT 1, 'Administrador', '123.456.789-00', 'admin@bancodigital.com', '(11) 99999-9999', '1990-01-01', 'Av. Paulista, 1000', 'São Paulo', 'SP', '01310-100', 1
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf = '123.456.789-00');

INSERT INTO cliente (id, nome, cpf, email, telefone, data_nascimento, endereco, cidade, estado, cep, usuario_id)
SELECT 2, 'João Silva', '987.654.321-00', 'joao@email.com', '(11) 98888-8888', '1985-05-15', 'Rua das Flores, 123', 'São Paulo', 'SP', '04567-000', 2
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf = '987.654.321-00');

-- Inserir contas
INSERT INTO conta (id, numero, agencia, tipo, saldo, limite, ativa, cliente_id)
SELECT 1, '0001-01', '0001', 'CORRENTE', 5000.00, 1000.00, true, 1
WHERE NOT EXISTS (SELECT 1 FROM conta WHERE numero = '0001-01');

INSERT INTO conta (id, numero, agencia, tipo, saldo, limite, ativa, cliente_id)
SELECT 2, '0001-02', '0001', 'POUPANCA', 10000.00, 0.00, true, 1
WHERE NOT EXISTS (SELECT 1 FROM conta WHERE numero = '0001-02');

INSERT INTO conta (id, numero, agencia, tipo, saldo, limite, ativa, cliente_id)
SELECT 3, '0002-01', '0001', 'CORRENTE', 1500.00, 500.00, true, 2
WHERE NOT EXISTS (SELECT 1 FROM conta WHERE numero = '0002-01');

-- Inserir algumas transações de exemplo
INSERT INTO transacao (id, tipo, valor, data_hora, descricao, conta_id, conta_destino_id, saldo_resultante)
SELECT 1, 'DEPOSITO', 5000.00, '2023-01-01 10:00:00', 'Depósito inicial', 1, NULL, 5000.00
WHERE NOT EXISTS (SELECT 1 FROM transacao WHERE id = 1);

INSERT INTO transacao (id, tipo, valor, data_hora, descricao, conta_id, conta_destino_id, saldo_resultante)
SELECT 2, 'DEPOSITO', 10000.00, '2023-01-01 10:30:00', 'Depósito inicial', 2, NULL, 10000.00
WHERE NOT EXISTS (SELECT 1 FROM transacao WHERE id = 2);

INSERT INTO transacao (id, tipo, valor, data_hora, descricao, conta_id, conta_destino_id, saldo_resultante)
SELECT 3, 'DEPOSITO', 1500.00, '2023-01-01 11:00:00', 'Depósito inicial', 3, NULL, 1500.00
WHERE NOT EXISTS (SELECT 1 FROM transacao WHERE id = 3);

INSERT INTO transacao (id, tipo, valor, data_hora, descricao, conta_id, conta_destino_id, saldo_resultante)
SELECT 4, 'TRANSFERENCIA_ENVIADA', 500.00, '2023-01-15 14:30:00', 'Transferência para João', 1, 3, 4500.00
WHERE NOT EXISTS (SELECT 1 FROM transacao WHERE id = 4);

INSERT INTO transacao (id, tipo, valor, data_hora, descricao, conta_id, conta_destino_id, saldo_resultante)
SELECT 5, 'TRANSFERENCIA_RECEBIDA', 500.00, '2023-01-15 14:30:00', 'Transferência de Administrador', 3, 1, 2000.00
WHERE NOT EXISTS (SELECT 1 FROM transacao WHERE id = 5);

-- Configurar sequências de IDs para evitar conflitos
ALTER TABLE usuario AUTO_INCREMENT = 100;
ALTER TABLE cliente AUTO_INCREMENT = 100;
ALTER TABLE conta AUTO_INCREMENT = 100;
ALTER TABLE transacao AUTO_INCREMENT = 100;