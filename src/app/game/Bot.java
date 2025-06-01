package app.game;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    // Guarda todas as cartas que o bot já recebeu
    private final List<String[]> cartasBot = new ArrayList<>();
    
    public int game(String[] carta1, String[] carta2) {
        cartasBot.clear();
        cartasBot.add(carta1);
        cartasBot.add(carta2);

        int somaAtual = Game.somar(cartasBot);
        return (somaAtual < 17) ? 0 : 1;
    }

    public int add(String[] novaCarta) {
        cartasBot.add(novaCarta);

        int somaAtual = Game.somar(cartasBot);
        return (somaAtual < 17) ? 0 : 1;
    }
}
