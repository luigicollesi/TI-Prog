# TI-Prog

**TI-Prog** é uma aplicação Java desktop que implementa o jogo de Blackjack com temática “Underground”. O projeto inclui:

- Tela de login/autenticação de usuário (Swing + MySQL).  
- Lógica completa de jogo de Blackjack (baralho, apostas, turnos do jogador e do “bot”).  
- Registro do histórico de partidas no banco de dados MySQL.  
- Interface gráfica (Java Swing) com elementos visuais personalizados e diálogos customizados.  

---

## Índice

1. [Visão Geral](#visão-geral)  
2. [Pré-requisitos](#pré-requisitos)  
3. [Instalação e Configuração](#instalação-e-configuração)  
4. [Como Rodar](#como-rodar)  
5. [Estrutura do Projeto](#estrutura-do-projeto)  
6. [Funcionalidades Principais](#funcionalidades-principais)  
7. [Como Jogar](#como-jogar)  
8. [Personalização Visual](#personalização-visual)  
9. [Banco de Dados](#banco-de-dados)  
10. [Gerar Executável (.exe)](#gerar-executável-exe)  
11. [Contribuição](#contribuição)  
12. [Licença](#licença)  

---

## Visão Geral

Este projeto — **TI-Prog** — foi desenvolvido como parte do Trabalho de Conclusão de Curso em TI (ou disciplina equivalente). O objetivo principal é:

- Oferecer uma experiência de Blackjack com interface Swing adequada para desktop.  
- Permitir que vários usuários criem contas, façam login e joguem, mantendo histórico de cada partida.  
- Demonstrar integração Java ↔ MySQL (JDBC) para persistência de dados (usuários, histórico de partidas, resultados).  

A estética “Underground” foi aplicada em ícones, imagens de fundo e estilos de botões, para criar uma atmosfera diferenciada.

---

## Pré-requisitos

Antes de iniciar, certifique-se de ter os seguintes itens instalados:

1. **Java Development Kit (JDK 17 ou superior)**  
   - Variável de ambiente `JAVA_HOME` apontando para a instalação do JDK.  
   - `javac` e `java` disponíveis no `PATH`.

2. **MySQL Server (8.x ou superior)**  
   - Conta de acesso com permissão para criar esquema e tabelas.  
   - Cliente MySQL (ou Workbench) para executar scripts de criação e importação do dump.

3. **Biblioteca JDBC do MySQL**  
   - O driver [`mysql-connector-java`](https://dev.mysql.com/downloads/connector/j/) deve estar disponível no _classpath_ (geralmente incluído via Maven/Gradle ou colocando o `.jar` em `lib/`).

4. **Ferramenta de Build (opcional, mas recomendada)**  
   - Maven (ou Gradle) para compilar, gerar `.jar` e resolver dependências.  
   - Se não usar Maven/Gradle, bastarão os utilitários `javac` e `jar`.

5. **Cliente SQLite (opcional)**  
   - Caso prefira rodar uma versão SQLite, basta ter um cliente SQLite (DB Browser for SQLite, DBeaver, SQLiteStudio etc.).

---

## Instalação e Configuração

1. **Clonar o repositório**  
   ```bash
   git clone https://github.com/luigicollesi/TI-Prog.git
   cd TI-Prog
