import java.util.*;
import java.util.stream.Collectors;

public class BlackjackGame {
    private final Deck deck;
    private final List<Player> players;
    private final BlackjackUI ui;

    public BlackjackGame(List<Player> players, BlackjackUI ui) {
        this.deck    = new Deck();
        this.players = players;
        this.ui      = ui;
    }

    public void start() {
        dealInitialCards();
        for (Player p : players) {
            ui.displayMessage("=== Turno de " + p.getName() + " ===");
            playerTurn(p);
        }
        determineWinner();
    }

    private void dealInitialCards() {
        for (Player p : players) {
            Card up   = deck.draw();
            Card down = deck.draw();
            p.addFaceUp(up);
            p.addFaceDown(down);
            ui.displayFaceUpCard(p, up);
        }
    }

    private void playerTurn(Player p) {
        while (true) {
            if (p.isBusted()) {
                ui.displayMessage(p.getName() + " estourou com " + p.getBestScore() + " pontos!");
                break;
            }
            boolean hit = ui.askHitOrStand(p);
            if (!hit) break;
            Card novo = deck.draw();
            p.addFaceDown(novo);
            ui.displayNewCard(p, novo);
        }
    }

    private void determineWinner() {
        int    best    = 0;
        List<Player> winners = new ArrayList<>();

        for (Player p : players) {
            int score = p.getBestScore();
            ui.revealHand(p, p.getAllCards(), score);
            if (score <= 21) {
                if (score > best) {
                    best    = score;
                    winners.clear();
                    winners.add(p);
                } else if (score == best) {
                    winners.add(p);
                }
            }
        }

        if (winners.isEmpty()) {
            ui.displayMessage("Todos estouraram. Sem vencedores.");
        } else if (winners.size() == 1) {
            ui.displayMessage("Vencedor: " + winners.get(0).getName() +
                              " com " + best + " pontos!");
        } else {
            String names = winners.stream()
                                  .map(Player::getName)
                                  .collect(Collectors.joining(", "));
            ui.displayMessage("Empate entre: " + names + " com " + best + " pontos!");
        }
    }
}
