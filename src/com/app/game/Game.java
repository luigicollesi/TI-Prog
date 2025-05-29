package com.app.game;

import com.app.ui.CustomDialog;
import com.app.ui.GameFrame;

import javax.swing.*;
import java.util.*;

public class Game {
    private final List<String[]> baralho = new ArrayList<>();
    private final List<String[]> cartasBot = new ArrayList<>();
    private final List<String[]> cartasJogador = new ArrayList<>();
    private final Bot bot;
    private final GameFrame frame;

    public Game(GameFrame frame) {
        this.frame = frame;
        this.bot = new Bot();

        inicializarBaralho();
        jogar();
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

        baralho.addAll(Arrays.asList(cartas));
        Collections.shuffle(baralho);
    }

    private void jogar() {
        // === Bot recebe cartas ===
        String[] carta1 = baralho.remove(0);
        cartasBot.add(carta1);
        frame.cartasBot(carta1, true);
        esperar(1000);

        String[] carta2 = baralho.remove(0);
        cartasBot.add(carta2);
        frame.cartasBot(carta2, false);
        esperar(1000);

        int resposta = bot.game(carta1, carta2);
        while (resposta == 0) {
            String[] nova = baralho.remove(0);
            cartasBot.add(nova);
            frame.cartasBot(nova, false);
            esperar(1000);
            resposta = bot.add(nova);
        }

        // === Jogador recebe cartas ===
        String[] j1 = baralho.remove(0);
        cartasJogador.add(j1);
        frame.cartasPlayer(j1, somar(cartasJogador));
        esperar(1000);

        String[] j2 = baralho.remove(0);
        cartasJogador.add(j2);
        frame.cartasPlayer(j2, somar(cartasJogador));
        esperar(1000);

        // Ativa botões de jogada
        frame.game();
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

        String resultado = totalJogador > 21 ? "Você estourou!" :
                           totalBot > 21 ? "Bot estourou! Você venceu!" :
                           totalJogador > totalBot ? "Você venceu!" :
                           totalBot > totalJogador ? "Bot venceu!" :
                           "Empate!";

        CustomDialog.showMessage(frame,
                "Você: " + totalJogador + " | Bot: " + totalBot + "\n" + resultado,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    private int somar(List<String[]> cartas) {
        int soma = 0;
        for (String[] carta : cartas) {
            String valor = carta[0];
            switch (valor) {
                case "J":
                case "Q":
                case "K":
                    soma += 10;
                    break;
                case "A":
                    soma += 11;
                    break;
                default:
                    try {
                        soma += Integer.parseInt(valor);
                    } catch (NumberFormatException e) {
                        System.err.println("Valor inválido: " + valor);
                    }
            }
        }
        return soma;
    }

    private void esperar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
