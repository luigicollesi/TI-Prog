import java.util.*;
import java.util.stream.Collectors;

public class BlackjackGame implements BlackjackUI.BlackjackUIListener {
    private final Deck deck;
    private final List<Player> players;
    private final BlackjackUI ui;

    // usado para sinalizar a escolha do jogador via listener
    private Boolean hitWanted;

    public BlackjackGame(List<Player> players, BlackjackUI ui) {
        this.deck    = new Deck();
        this.players = players;
        this.ui      = ui;
        // registra este jogo como listener dos cliques
        this.ui.setListener(this);
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
            ui.displayFaceUpCard(p.getName(), up);
        }
    }

    private void playerTurn(Player p) {
        while (true) {
            if (p.isBusted()) {
                ui.displayMessage(p.getName() + " estourou com " + p.getBestScore() + " pontos!");
                break;
            }

            // solicita ação do usuário e aguarda o evento onHit/onStand
            synchronized (this) {
                hitWanted = null;
                ui.enableControls(true);
                try {
                    while (hitWanted == null) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (!hitWanted) {
                // jogador escolheu “Parar”
                break;
            } else {
                // jogador escolheu “Comprar”
                Card novo = deck.draw();
                p.addFaceDown(novo);
                ui.displayNewCard(p.getName());
            }
        }
    }

    private void determineWinner() {
        int best = 0;
        List<Player> winners = new ArrayList<>();

        for (Player p : players) {
            int score = p.getBestScore();
            ui.revealHand(p.getName(), p.getAllCards(), score);

            if (score <= 21) {
                if (score > best) {
                    best = score;
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
            ui.displayMessage("Vencedor: " + winners.get(0).getName()
                              + " com " + best + " pontos!");
        } else {
            String names = winners.stream()
                                  .map(Player::getName)
                                  .collect(Collectors.joining(", "));
            ui.displayMessage("Empate entre: " + names
                              + " com " + best + " pontos!");
        }
    }

    // Listener chamado quando o jogador clica em “Comprar”
    @Override
    public void onHit() {
        synchronized (this) {
            hitWanted = true;
            ui.enableControls(false);
            this.notify();
        }
    }

    // Listener chamado quando o jogador clica em “Parar”
    @Override
    public void onStand() {
        synchronized (this) {
            hitWanted = false;
            ui.enableControls(false);
            this.notify();
        }
    }
}
