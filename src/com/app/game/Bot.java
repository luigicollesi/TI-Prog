package com.app.game;

public class Bot {
    private int soma = 0;

    public int game(String[] carta1, String[] carta2) {
        soma = valor(carta1[0]) + valor(carta2[0]);
        return soma < 17 ? 0 : 1;
    }

    public int add(String[] novaCarta) {
        soma += valor(novaCarta[0]);
        return soma < 17 ? 0 : 1;
    }

    private int valor(String v) {
        switch (v) {
            case "J":
            case "Q":
            case "K": return 10;
            case "A": return 11;
            default: return Integer.parseInt(v);
        }
    }
}
