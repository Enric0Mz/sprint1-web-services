# 🎰 Simulador Educacional de Cassino Online

Este é um projeto desenvolvido como parte da **Global Solution da XP Inc.**, com o objetivo de educar jovens do ensino médio sobre os riscos inerentes aos jogos de azar online, como o popular "Fortune Tiger" (conhecido como "Tigrinho" no Brasil), e contrastá-los com o potencial de construção de patrimônio através de investimentos financeiros.

## 🎯 Objetivo Educacional

O principal propósito deste simulador é desmistificar a ideia de "ganho fácil" em apostas. Através de uma simulação realista (com probabilidades altamente desfavoráveis ao jogador, refletindo a realidade dos cassinos), o usuário experimentará as perdas financeiras no longo prazo. Após cada perda, o sistema apresentará opções de investimentos reais, demonstrando o quanto o dinheiro "perdido" poderia ter rendido se aplicado de forma inteligente.

## 📚 Integrantes do projeto
- Breno Silva - RM97864
- Enrico Marquez - RM99325
- Gustavo Dias - RM550820
- Joel Barros - RM550378
- Leonardo Moreira - RM550988

## ✨ Funcionalidades

O simulador oferece as seguintes funcionalidades principais:

* **Gestão de Usuários:**
    * Cadastro de novos usuários com saldo inicial "falso" de R$ 10.000,00.
    * Login e logout de usuários.
    * Visualização do saldo atual.
* **Jogo "Tigrinho" Simplificado:**
    * Simulação de um slot machine com um botão de "Apostar".
    * Definição do valor da aposta (entre R$ 100,00 e R$ 1.000,00).
    * Determinação do resultado com base em probabilidades desfavoráveis:
        * **95% de chance de PERDA.**
        * 4% de chance de pequeno ganho (1:1).
        * 0.9% de chance de médio ganho (2:1).
        * 0.1% de chance de grande ganho (5:1).
    * Atualização imediata do saldo após cada rodada.
* **Histórico e Estatísticas:**
    * Visualização das últimas 20 apostas realizadas.
    * Estatísticas agregadas de apostas (total apostado, total ganho/perdido, percentual de vitórias/derrotas).
* **Demonstração de Alternativas de Investimento:**
    * **Ao perder uma aposta, o sistema sugere opções de investimento reais** (CDB e Tesouro Selic).
    * Mostra o quanto o valor perdido renderia em diferentes períodos (1 mês, 6 meses, 1 ano, 5 anos) com base em taxas de juros compostos didáticas.
    * Acompanha mensagens educacionais sobre a importância de investir e a realidade da construção de patrimônio.

## 💻 Tecnologias Utilizadas

Este projeto foi construído utilizando as seguintes tecnologias, alinhadas com as diretrizes da XP Inc. e padrões de mercado:

* **Linguagem de Programação:** Java 17+
* **Framework:** Spring Boot 3+
* **Gerenciador de Dependências:** Maven
* **Banco de Dados (Desenvolvimento):** H2 Database (em memória, para agilidade)
* **Persistência:** Spring Data JPA / Hibernate
* **Segurança:** Spring Security (para criptografia de senhas)
* **Anotações para Boilerplate:** Lombok
* **Documentação de API:** SpringDoc OpenAPI (Swagger UI)
* **Controle de Versão:** Git
* **Hospedagem de Código:** GitHub

## 🚀 Como Iniciar o Projeto

Siga os passos abaixo para configurar e rodar o projeto em sua máquina.

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

* **JDK 17+:** Baixe o OpenJDK (Eclipse Temurin é recomendado) ou Oracle JDK.
    * [https://adoptium.net/temurin/releases/](https://adoptium.net/temurin/releases/)
* **Maven:** Geralmente já vem integrado com o IntelliJ, mas pode ser instalado separadamente se necessário.
* **Git:** Para clonar o repositório.
    * [https://git-scm.com/downloads](https://git-scm.com/downloads)
* **IntelliJ IDEA (Community Edition ou Ultimate):** IDE recomendada para desenvolvimento Java/Spring Boot.
    * [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
* **Postman ou Insomnia:** Para testar os endpoints da API.
    * [https://www.postman.com/downloads/](https://www.postman.com/downloads/)
    * [https://insomnia.rest/download](https://insomnia.rest/download)
* **Apache JMeter (Opcional):** Para testes de performance e carga.
    * [https://jmeter.apache.org/download_jmeter.cgi](https://jmeter.apache.org/download_jmeter.cgi)

### Configuração e Execução

1.  **Clonar o Repositório:**

    - git clone [https://github.com/Enric0Mz/sprint1-web-services](https://github.com/Enric0Mz/sprint1-web-services)
    - cd simulador-aposta


2.  **Importar para o IntelliJ IDEA:**
    * Abra o IntelliJ IDEA.
    * Selecione `File` -> `Open...` e navegue até a pasta `simulador-aposta` que você acabou de clonar.
    * O IntelliJ deve reconhecer automaticamente o projeto Maven e baixar as dependências. Aguarde a conclusão.

3.  **Configurar o Banco de Dados H2:**
    * O projeto já vem configurado para usar o H2 Database em memória. As configurações estão no arquivo `src/main/resources/application.properties`.

    ```properties
    # H2 Database Configuration
    spring.h2.console.enabled=true
    spring.h2.console.path=/h2-console
    spring.h2.console.settings.web-allow-others=true
    spring.datasource.url=jdbc:h2:mem:simuladoraposta_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    ```

4.  **Executar a Aplicação:**
    * No IntelliJ, localize o arquivo principal da sua aplicação: `src/main/java/com/globalsolution/simuladoraposta/SimuladorApostaApplication.java`.
    * Clique no botão "Run" (seta verde) ao lado do método `main`.
    * A aplicação será iniciada na porta `8080` (padrão do Spring Boot).

## 🧪 Como Testar a API

Com a aplicação rodando, você pode usar o Postman ou Insomnia para testar os endpoints.

### Acessar o Console H2

* Abra seu navegador e vá para: `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:simuladoraposta_db`
* **User Name:** `sa`
* **Password:** (deixe em branco)
* Clique em "Connect". Aqui você pode visualizar as tabelas (`USUARIO`, `APOSTA`, `INVESTIMENTO`) e seus dados.

### Endpoints da API

A documentação completa dos endpoints pode ser acessada via Swagger UI: `http://localhost:8080/swagger-ui.html`

#### 1. Cadastro de Usuário

* **URL:** `/api/usuarios/cadastrar`
* **Método:** `POST`
* **Corpo (JSON):**
    ```json
    {
        "username": "meu_jogador",
        "password": "minhasenha"
    }
    ```
* **Retorno Esperado:** `201 Created` e os dados do usuário com saldo inicial.

#### 2. Login de Usuário

* **URL:** `/api/usuarios/login`
* **Método:** `POST`
* **Corpo (JSON):**
    ```json
    {
        "username": "meu_jogador",
        "password": "minhasenha"
    }
    ```
* **Retorno Esperado:** `200 OK` e os dados do usuário logado.

#### 3. Consultar Saldo do Usuário

* **URL:** `/api/usuarios/{username}/saldo`
* **Método:** `GET`
* **Exemplo:** `http://localhost:8080/api/usuarios/meu_jogador/saldo`
* **Retorno Esperado:** `200 OK` e o saldo atual do usuário.

#### 4. Apostar no Tigrinho

* **URL:** `/api/jogo/tigrinho/{usuarioId}/apostar`
* **Método:** `POST`
* **Corpo (JSON):**
    ```json
    {
        "valorApostado": 500.00
    }
    ```
* **Retorno Esperado:** `200 OK`. Retorna o resultado da aposta (`PERDA`, `GANHO_PEQUENO`, etc.), o saldo atualizado, e **se o resultado for `PERDA`**, incluirá uma lista de `sugestoesInvestimento` com o potencial de rendimento do valor perdido em CDB e Tesouro Selic.

#### 5. Histórico de Apostas

* **URL:** `/api/jogo/{usuarioId}/historico`
* **Método:** `GET`
* **Retorno Esperado:** `200 OK` e uma lista das últimas 20 apostas do usuário.

#### 6. Estatísticas do Usuário

* **URL:** `/api/jogo/{usuarioId}/estatisticas`
* **Método:** `GET`
* **Retorno Esperado:** `200 OK` e um JSON com `totalRodadas`, `totalApostado`, `totalGanhoLiquido`, `saldoAtual`, `percentualVitorias`, `percentualDerrotas`. Observe o `totalGanhoLiquido` para ver o saldo líquido real após as simulações, que deve ser predominantemente negativo.

---

## 📄 Licença

[MIT License](LICENSE)