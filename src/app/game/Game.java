package app.game;

import javax.swing.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import app.db.DatabaseOperations;
import app.ui.Private.GameFrame;
import app.ui.utility.CustomDialog;

public class Game {
    private final List<String[]> baralho = new ArrayList<>();
    private final List<String[]> cartasBot = new ArrayList<>();
    private final List<String[]> cartasJogador = new ArrayList<>();
    private final Bot bot;
    private final GameFrame frame;
    private final String userId;
    private int valorAposta;
    protected int saldo;

    public Game(GameFrame frame, String userId) {
        this.frame = frame;
        this.bot = new Bot();
        this.userId = userId;

        try {
            ResultSet rs = DatabaseOperations.executeQuery(
                "SELECT money FROM usuarios WHERE id = ?",
                new String[]{userId}
            );

            if (rs != null && rs.next()) {
                this.saldo = rs.getInt("money");
                rs.getStatement().getConnection().close();
            } else {
                CustomDialog.showMessage(frame, "Usuário não encontrado no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            CustomDialog.showMessage(frame, "Erro ao buscar saldo do banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void Init(int apostaAtual) {
        this.valorAposta = apostaAtual;
        this.saldo -= apostaAtual;

        DatabaseOperations.executeUpdate(
            "UPDATE usuarios SET money = ? WHERE id = ?",
            new String[]{String.valueOf(saldo), userId}
        );

        inicializarBaralho();
        executarSequencia();
    }

    private void inicializarBaralho() {
        // Criação direta do baralho
        String[][] cartas = {
                {"2", "Copas"}, {"3", "Copas"}, {"4", "Copas"}, {"5", "Copas"}, {"6", "Copas"}, {"7", "Copas"},
                {"8", "Copas"}, {"9", "Copas"}, {"10", "Copas"}, {"J", "Copas"}, {"Q", "Copas"}, {"K", "Copas"}, {"A", "Copas"},
                {"2", "Espadas"}, {"3", "Espadas"}, {"4", "Espadas"}, {"5", "Espadas"}, {"6", "Espadas"}, {"7", "Espadas"},
                {"8", "Espadas"}, {"9", "Espadas"}, {"10", "Espadas"}, {"J", "Espadas"}, {"Q", "Espadas"}, {"K", "Espadas"}, {"A", "Espadas"},
                {"2", "Ouros"}, {"3", "Ouros"}, {"4", "Ouros"}, {"5", "Ouros"}, {"6", "Ouros"}, {"7", "Ouros"},
                {"8", "Ouros"}, {"9", "Ouros"}, {"10", "Ouros"}, {"J", "Ouros"}, {"Q", "Ouros"}, {"K", "Ouros"}, {"A", "Ouros"},
                {"2", "Paus"}, {"3", "Paus"}, {"4", "Paus"}, {"5", "Paus"}, {"6", "Paus"}, {"7", "Paus"},
                {"8", "Paus"}, {"9", "Paus"}, {"10", "Paus"}, {"J", "Paus"}, {"Q", "Paus"}, {"K", "Paus"}, {"A", "Paus"}
        };

        baralho.clear();
        baralho.addAll(Arrays.asList(cartas));
        Collections.shuffle(baralho);
    }

    private void executarSequencia() {
        SwingWorker<Void, Runnable> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Bot recebe primeira carta visível
                String[] carta1 = baralho.remove(0);
                cartasBot.add(carta1);
                publish(() -> frame.cartasBot(carta1, true));
                Thread.sleep(1000);

                // Bot recebe segunda carta virada
                String[] carta2 = baralho.remove(0);
                cartasBot.add(carta2);
                publish(() -> frame.cartasBot(carta2, false));
                Thread.sleep(1000);

                int resposta = bot.game(carta1, carta2);
                while (resposta == 0) {
                    String[] nova = baralho.remove(0);
                    cartasBot.add(nova);
                    String[] finalNova = nova;
                    publish(() -> frame.cartasBot(finalNova, false));
                    Thread.sleep(1000);
                    resposta = bot.add(nova);
                }

                // Jogador recebe carta 1
                String[] j1 = baralho.remove(0);
                cartasJogador.add(j1);
                int parcial1 = somar(cartasJogador);
                publish(() -> frame.cartasPlayer(j1, parcial1));
                Thread.sleep(1000);

                // Jogador recebe carta 2
                String[] j2 = baralho.remove(0);
                cartasJogador.add(j2);
                int parcial2 = somar(cartasJogador);
                publish(() -> frame.cartasPlayer(j2, parcial2));
                Thread.sleep(1000);

                publish(() -> frame.game());
                return null;
            }

            @Override
            protected void process(List<Runnable> updates) {
                for (Runnable r : updates) {
                    r.run(); // Atualiza na EDT
                }
            }
        };

        worker.execute();
    }

    public void comprar() {
        String[] nova = baralho.remove(0);
        cartasJogador.add(nova);
        frame.cartasPlayer(nova, somar(cartasJogador));
    }

    public void manter() {
        // Revela cartas do bot
        for (String[] carta : cartasBot) {
            frame.cartasBot(carta, true);
            esperar(500);
        }

        int totalBot = somar(cartasBot);
        int totalJogador = somar(cartasJogador);

        String resultado;
        int valorGame = valorAposta;
        boolean venceu = false;

        if (totalJogador > 21) {
            resultado = "Você estourou!";
        } else if (totalBot > 21 || totalJogador > totalBot) {
            venceu = true;
            boolean vitoriaPor21 = (totalJogador == 21);
            double multiplicador = vitoriaPor21 ? 2.5 : 2.0;
            int premio = (int) (valorAposta * multiplicador);
            saldo += premio;
            valorGame = premio;

            // Atualiza no banco de dados
            DatabaseOperations.executeUpdate(
                "UPDATE usuarios SET money = ? WHERE id = ?",
                new String[]{String.valueOf(saldo), userId}
            );

            resultado = vitoriaPor21 ? "Blackjack! Você venceu com 21!" : "Você venceu!";
        } else if (totalBot > totalJogador) {
            resultado = "Bot venceu!";
        } else {
            resultado = "Empate!";
            saldo += valorAposta; // Empate: devolve aposta?
            
            DatabaseOperations.executeUpdate(
                "UPDATE usuarios SET money = ? WHERE id = ?",
                new String[]{String.valueOf(saldo), userId}
            );
        }

        String cartasPlayerJSON = converterCartasParaJson(cartasJogador);
        String cartasBotJSON = converterCartasParaJson(cartasBot);

        DatabaseOperations.executeUpdate(
            "INSERT INTO historico_partidas (user_id, valor, cartas_player, cartas_bot, venceu) VALUES (?, ?, ?, ?, ?)",
            new String[]{userId, String.valueOf(valorGame), cartasPlayerJSON, cartasBotJSON, venceu ? "1" : "0"}
        );

        CustomDialog.showMessage(frame,
                "Você: " + totalJogador + " | Bot: " + totalBot + "\n" + resultado,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
        
        cartasBot.clear();
        cartasJogador.clear();
        frame.restart();
    }

    public static int somar(List<String[]> cartas) {
        int soma = 0;
        int ases = 0;

        for (String[] carta : cartas) {
            String valor = carta[0];
            switch (valor) {
                case "J":
                case "Q":
                case "K":
                    soma += 10;
                    break;
                case "A":
                    soma += 11; // Assume inicialmente como 11
                    ases++;
                    break;
                default:
                    try {
                        soma += Integer.parseInt(valor);
                    } catch (NumberFormatException e) {
                        System.err.println("Valor inválido: " + valor);
                    }
            }
        }

        // Corrige os Ases de 11 para 1 se a soma tiver passado de 21
        while (soma > 21 && ases > 0) {
            soma -= 10; // trocar um Ás de 11 para 1
            ases--;
        }

        return soma;
    }

    private void esperar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private String converterCartasParaJson(List<String[]> cartas) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < cartas.size(); i++) {
            String[] carta = cartas.get(i);
            sb.append("[\"").append(carta[0]).append("\",\"").append(carta[1]).append("\"]");
            if (i < cartas.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public int getSaldo() {
        return saldo;
    }
}
