@echo off
echo ===================================================
echo    SISTEMA DE GERENCIAMENTO DE CONTAS BANCARIAS
echo ===================================================
echo.
echo Iniciando o sistema...
echo.

REM Verificar se o Java está instalado
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Java nao encontrado. Por favor, instale o Java 17 ou superior.
    echo Voce pode baixar o Java em: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Verificar se o Maven está instalado
mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Maven nao encontrado. Por favor, instale o Maven 3.8 ou superior.
    echo Voce pode baixar o Maven em: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Compilar o projeto
echo Compilando o projeto...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERRO: Falha ao compilar o projeto.
    pause
    exit /b 1
)

echo.
echo Projeto compilado com sucesso!
echo.

REM Executar a aplicação
echo Iniciando a aplicacao...
echo.
echo O sistema estara disponivel em: http://localhost:8080
echo.
echo Usuarios de demonstracao:
echo  - Admin: username=admin, senha=admin123
echo  - Cliente: username=usuario, senha=senha123
echo.
echo Pressione Ctrl+C para encerrar a aplicacao.
echo.

call mvn spring-boot:run

pause