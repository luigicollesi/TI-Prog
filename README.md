# 🃏 TI-Prog – Blackjack Underground

TI-Prog é um jogo de **Blackjack** desenvolvido em Java, com interface gráfica personalizada utilizando **Java Swing** e persistência local com **MySQL**.

O jogo possui visual temático *underground*, com cartas, bot, apostas e histórico de partidas salvos no banco de dados.

## 📁 Estrutura do Projeto

- `src/`: Código-fonte Java.
- `public/Images/`: Imagens do jogo (fundo, cartas, ícones).
- `TI-Prog.jar`: Arquivo executável Java.
- `BancoDados.sql`: Script SQL com a estrutura do banco de dados.
- `README.md`: Este arquivo.

## ✅ Requisitos

Antes de rodar o projeto, instale:

- Java 17 ou superior
- MySQL Server
- Linux (ou outro sistema com terminal e Java)

## ⬇️ Como Clonar o Projeto

Abra o terminal e execute:

```bash
git clone https://github.com/luigicollesi/TI-Prog.git
cd TI-Prog
```

## 🗃️ Como Criar e Configurar o Banco de Dados MySQL

A aplicação utiliza MySQL como banco de dados. Para criar e configurar:

1. Acesse seu MySQL com um usuário com permissões de criação:

```bash
mysql -u root -p
```

2. Importe o script SQL:

```bash
mysql -u root -p tiprog < BancoDados.sql
```

4. Atualize as configurações de conexão no código, se necessário (usuário, senha, host).

## ▶️ Como Executar o Jogo no Linux

Com o banco de dados configurado corretamente, execute:

```bash
java -jar TI-Prog.jar
```

A aplicação será iniciada com a interface de login e integrará com o banco MySQL.

## 📜 Licença

Distribuído sob a Licença MIT. Consulte o arquivo `LICENSE` para mais informações.
