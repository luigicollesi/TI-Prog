package client.ui.Private;

import client.model.CardView;
import client.model.DealSequence;
import client.model.TableStage;

import javax.swing.SwingWorker;

class GameDealWorker extends SwingWorker<Void, Runnable> {
    private final GameFrame outer;
    private final DealSequence sequence;
    private final int dealDelay;
    private final int actionDelay;

    GameDealWorker(GameFrame outer, DealSequence sequence, int dealDelay, int actionDelay) {
        this.outer = outer;
        this.sequence = sequence;
        this.dealDelay = dealDelay;
        this.actionDelay = actionDelay;
    }

    @Override
    protected Void doInBackground() {
        publish(() -> {
            outer.resetTable();
            outer.setControlsForStage(TableStage.PLAYING);
            outer.highlightTurn(null);
            outer.setStageText("Distribuindo cartas...");
            outer.setLastStage(TableStage.PLAYING);
        });
        sleep(actionDelay);

        // Dealer cards
        for (DealSequence.CardDeal card : sequence.getDealerCards()) {
            CardView view = new CardView(card.getRank(), card.getSuit(), !card.isFaceUp());
            publish(() -> outer.addDealerCard(view, card.isFaceUp()));
            sleep(dealDelay);
        }

        // Players (já estão em ordem de turno na sequência)
        for (DealSequence.PlayerDeal deal : sequence.getPlayerDeals()) {
            String playerId = deal.getPlayerId();
            for (DealSequence.CardDeal card : deal.getCards()) {
                CardView view = new CardView(card.getRank(), card.getSuit(), !card.isFaceUp());
                publish(() -> outer.addCardToPlayer(playerId, view, card.isFaceUp()));
                sleep(dealDelay);
            }
        }

        // Inicia vez do primeiro jogador
        String firstTurn = sequence.getFirstTurnUserId();
        if (firstTurn != null && !firstTurn.isBlank()) {
            sleep(actionDelay);
            publish(() -> {
                outer.highlightTurn(firstTurn);
                outer.updateControlsForTurn(firstTurn);
                outer.revealFirstHiddenOf(firstTurn);
            });
        } else {
            publish(() -> {
                outer.highlightTurn(null);
                outer.updateControlsForTurn(null);
            });
        }

        return null;
    }

    @Override
    protected void process(java.util.List<Runnable> chunks) {
        for (Runnable runnable : chunks) {
            runnable.run();
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
