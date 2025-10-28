package client.ui.Private;

import client.model.CardView;
import client.model.PlayerView;
import client.model.TableStage;
import client.model.TableState;

import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ÚNICA thread de fluxo da mesa.
 * Usa Thread.sleep() + publish() em sequência (sem AnimationQueue/Task).
 */
class GameFlowWorker extends SwingWorker<Void, Runnable> {
    private final GameFrame outer;
    private final TableState state;

    private final int DEAL_DELAY;
    private final int ACTION_DELAY;
    private final int RESULT_DELAY;

    GameFlowWorker(GameFrame outer, TableState state, int dealDelay, int actionDelay, int resultDelay) {
        this.outer = outer;
        this.state = state;
        this.DEAL_DELAY = dealDelay;
        this.ACTION_DELAY = actionDelay;
        this.RESULT_DELAY = resultDelay;
    }

    @Override
    protected Void doInBackground() {
        // 1) Assentos e label de estágio
        publish(() -> outer.updateSeats(state));
        publish(() -> outer.setStageText(outer.stageMessage(state.getStage(), state)));

        switch (state.getStage()) {
            case WAITING:
            case BETTING: {
                TableStage stage = state.getStage();
                boolean enteringBetting = stage == TableStage.BETTING && outer.getLastStage() != TableStage.BETTING;
                sleep(DEAL_DELAY / 2);
                publish(() -> {
                    if (enteringBetting) {
                        outer.onBettingStageStarted();
                    } else if (stage == TableStage.WAITING) {
                        outer.resetBetUI();
                    }
                    outer.setControlsForStage(stage);
                    outer.highlightTurn(null);
                    outer.resetTable();
                    outer.setLastStage(stage);
                });
                break;
            }

            case PLAYING: {
                boolean inicioDeMao =
                        outer.getLastStage() != TableStage.PLAYING
                        || outer.getDealerRenderedSize() == 0;

                if (inicioDeMao) {
                    publish(() -> {
                        outer.resetTable();
                        outer.setControlsForStage(TableStage.PLAYING);
                        outer.setLastStage(TableStage.PLAYING);
                    });
                    sleep(ACTION_DELAY);

                    // DEAL completo na ordem especificada
                    doInitialDealSequence(state);

                    // Início da vez do primeiro jogador: delay + revela carta virada dele
                    sleep(ACTION_DELAY);
                    iniciarVezAtual(state);
                } else {
                    // Meio da mão: só aplicar diferenças (compras / revelações) com delays
                    aplicarDiferencasDealer(state);
                    aplicarDiferencasJogadores(state);

                    // Destaca/habilita o jogador da vez e revela carta virada dele (se houver)
                    sleep(ACTION_DELAY);
                    publish(() -> {
                        String turn = state.getCurrentTurnUserId();
                        outer.highlightTurn(turn);
                        outer.updateControlsForTurn(turn);
                        if (turn != null) outer.revealFirstHiddenOf(turn);
                    });
                }
                break;
            }

            case RESULTS:
                // Revela tudo pendente e mostra resultados após pausas
                aplicarDiferencasDealer(state);
                aplicarDiferencasJogadores(state);
                sleep(ACTION_DELAY);
                publish(() -> {
                    outer.setLastStage(TableStage.RESULTS);
                    outer.highlightTurn(null);
                    outer.updateControlsForTurn(null);
                    outer.setStageText(outer.stageMessage(TableStage.RESULTS, state));
                    outer.triggerPendingRoundResultDisplay();
                });
                sleep(RESULT_DELAY);
                break;
        }

        return null;
    }

    @Override
    protected void process(List<Runnable> chunks) {
        for (Runnable r : chunks) {
            try { r.run(); } catch (Exception ignore) {}
        }
    }

    // ------------------------- FLUXO COM DELAYS -------------------------

    /**
     * DEAL completo:
     * Dealer ↑, dealer ↓,
     * p1 ↑, p1 ↓, p2 ↑, p2 ↓, p3 ↑, p3 ↓
     * (rotaciona a lista para começar pelo primeiro a jogar)
     */
    private void doInitialDealSequence(TableState s) {
        // Dealer
        List<CardView> dealer = s.getDealerCards();
        if (!dealer.isEmpty()) {
            CardView c0 = dealer.get(0);
            publish(() -> outer.addDealerCard(c0, !c0.isHidden())); // face-up
            sleep(DEAL_DELAY);
        }
        if (dealer.size() > 1) {
            CardView c1 = dealer.get(1);
            publish(() -> outer.addDealerCard(c1, false));          // face-down (fundo)
            sleep(DEAL_DELAY);
        }
        for (int i = 2; i < dealer.size(); i++) {
            CardView cx = dealer.get(i);
            boolean faceUp = !cx.isHidden();
            publish(() -> outer.addDealerCard(cx, faceUp));
            sleep(DEAL_DELAY);
        }

        // Jogadores em ordem de turno (começando pelo currentTurn)
        for (PlayerView p : playersInTurnOrder(s)) {
            String pid = p.getUserId();
            List<CardView> cards = p.getCards();
            if (cards == null || cards.isEmpty()) continue;

            // 1ª carta face-up
            CardView c0 = cards.get(0);
            publish(() -> outer.addCardToPlayer(pid, c0, !c0.isHidden()));
            sleep(DEAL_DELAY);

            // 2ª carta face-down (fundo) se existir
            if (cards.size() > 1) {
                CardView c1 = cards.get(1);
                publish(() -> outer.addCardToPlayer(pid, c1, false));
                sleep(DEAL_DELAY);
            }

            // Extras (se houver), respeitando hidden
            for (int i = 2; i < cards.size(); i++) {
                CardView cx = cards.get(i);
                boolean faceUp = !cx.isHidden();
                publish(() -> outer.addCardToPlayer(pid, cx, faceUp));
                sleep(DEAL_DELAY);
            }
        }
    }

    /** Início da vez: destaca turno, habilita botões e revela a 1ª carta virada do jogador da vez */
    private void iniciarVezAtual(TableState s) {
        String turn = s.getCurrentTurnUserId();
        publish(() -> {
            outer.highlightTurn(turn);
            outer.updateControlsForTurn(turn);
            if (turn != null) outer.revealFirstHiddenOf(turn);
        });
    }

    /** Novas cartas + revelações do dealer com delays (estado vem do servidor) */
    private void aplicarDiferencasDealer(TableState s) {
        if (s.getCurrentTurnUserId() == null) {
            publish(() -> outer.highlightTurn(null));
        }
        List<CardView> dealer = s.getDealerCards();
        int oldSize = outer.getDealerRenderedSize();

        // Revelações da mão atual antes de novas compras
        int min = Math.min(oldSize, dealer.size());
        for (int i = 0; i < min; i++) {
            boolean renderedFaceUp = outer.isDealerRenderedFaceUp(i);
            CardView cv = dealer.get(i);
            if (!renderedFaceUp && !cv.isHidden()) {
                final int idx = i;
                final CardView revealedCard = cv;
                publish(() -> outer.updateDealerCard(idx, revealedCard));
                sleep(ACTION_DELAY);
                publish(() -> outer.revealDealerCard(idx));
            }
        }

        // Novas cartas compradas após a revelação
        for (int i = oldSize; i < dealer.size(); i++) {
            CardView card = dealer.get(i);
            boolean faceUp = !card.isHidden();
            publish(() -> outer.addDealerCard(card, faceUp));
            sleep(DEAL_DELAY);
        }
    }

    /** Novas cartas + revelações dos jogadores com delays (estado vem do servidor) */
    private void aplicarDiferencasJogadores(TableState s) {
        for (PlayerView view : s.getPlayers()) {
            String pid = view.getUserId();
            List<CardView> cards = view.getCards();
            if (cards == null) cards = List.of();

            int oldSize = outer.getPlayerRenderedSize(pid);

            // Novas cartas
            for (int i = oldSize; i < cards.size(); i++) {
                CardView c = cards.get(i);
                boolean faceUp = !c.isHidden();
                final CardView cc = c;
                publish(() -> outer.addCardToPlayer(pid, cc, faceUp));
                sleep(DEAL_DELAY);
            }

            // Revelações
            int min = Math.min(oldSize, cards.size());
            for (int i = 0; i < min; i++) {
                boolean rUp = outer.isPlayerRenderedFaceUp(pid, i);
                CardView cv = cards.get(i);
                if (!rUp && !cv.isHidden()) {
                    final int idx = i;
                    final CardView revealed = cv;
                    publish(() -> outer.updatePlayerCard(pid, idx, revealed));
                    sleep(ACTION_DELAY);
                    publish(() -> outer.revealPlayerCard(pid, idx));
                }
            }
        }
    }

    // ------------------------- HELPERS -------------------------

    private List<PlayerView> playersInTurnOrder(TableState s) {
        List<PlayerView> players = new ArrayList<>(s.getPlayers());
        String first = s.getCurrentTurnUserId();
        if (first == null || players.isEmpty()) return players;

        int idx = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUserId().equals(first)) { idx = i; break; }
        }
        Collections.rotate(players, -idx);
        return players;
    }

    private void sleep(int millis) {
        try { Thread.sleep(millis); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
