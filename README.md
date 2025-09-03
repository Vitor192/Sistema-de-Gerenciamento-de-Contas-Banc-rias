# Sistema de Gerenciamento de Contas Bancárias

Sistema completo para gerenciamento de contas bancárias com autenticação segura, operações bancárias e interface web responsiva.

## Funcionalidades

- Cadastro e autenticação de usuários com opção de 2FA (Two-Factor Authentication)
- Gerenciamento de contas bancárias (corrente, poupança, investimento)
- Operações bancárias: transferências, depósitos, saques
- Extrato bancário com filtros por período
- Dashboard com resumo financeiro
- Interface responsiva com Bootstrap 5

## Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior
- MySQL 8.0 ou superior

## Configuração

1. Clone o repositório:
```
git clone https://github.com/seu-usuario/sistema-bancario.git
cd sistema-bancario
```

2. Configure o banco de dados MySQL:
```sql
CREATE DATABASE banco_digital;
CREATE USER 'bancouser'@'localhost' IDENTIFIED BY 'bancopass';
GRANT ALL PRIVILEGES ON banco_digital.* TO 'bancouser'@'localhost';
FLUSH PRIVILEGES;
```

3. Configure as propriedades da aplicação em `src/main/resources/application.properties` (já configurado por padrão):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banco_digital
spring.datasource.username=bancouser
spring.datasource.password=bancopass
```

## Execução

1. Compile e execute o projeto com Maven:
```
mvn clean install
mvn spring-boot:run
```

2. Acesse a aplicação em seu navegador:
```
http://localhost:8080
```

## Usuário de Demonstração

Após iniciar a aplicação pela primeira vez, um usuário de demonstração será criado automaticamente:

- **Usuário**: admin
- **Senha**: admin123
- **2FA**: Desativado

## Estrutura do Projeto

```
src/main/java/com/bancodigital/corebanking/
├── config/           # Configurações do Spring e segurança
├── controller/       # Controladores REST e MVC
├── dto/              # Objetos de transferência de dados
├── exception/        # Exceções personalizadas
├── model/            # Entidades JPA
├── repository/       # Repositórios Spring Data
├── service/          # Serviços de negócio
└── util/             # Classes utilitárias
```

## Tecnologias Utilizadas

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Thymeleaf
- Bootstrap 5
- MySQL
- Lombok
- JWT (JSON Web Token)
- TOTP (Time-based One-Time Password) para 2FA

## Segurança

O sistema implementa as seguintes medidas de segurança:

- Autenticação com JWT
- Senhas criptografadas com BCrypt
- Autenticação de dois fatores (2FA) com TOTP
- Proteção contra CSRF
- Validação de entrada de dados
- Controle de acesso baseado em funções

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.