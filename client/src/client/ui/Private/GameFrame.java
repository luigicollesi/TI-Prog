package client.ui.Private;

import client.model.*;
import client.net.ServerConnection;
import client.net.ServerListener;
import client.i18n.I18n;
import client.ui.utility.CustomDialog;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;
import client.ui.utility.SmokyBorder;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.swing.border.Border;

public class GameFrame extends JFrame implements ServerListener {
    // Delays expostos via construtor do worker (não precisam ser public)
    static final int CARD_WIDTH = 100;
    static final int CARD_HEIGHT = 145;
    static final int DEAL_DELAY = 600;
    static final int ACTION_DELAY = 2000;
    static final int RESULT_DELAY = 0;

    final ServerConnection connection;
    final MenuFrame menuFrame;
    final String userId;
    final String username;

    final PanelImage backgroundPanel;
    final JLabel lblSaldo;
    final JLabel lblStage;
    final JLabel lblAposta;

    final JPanel dealerPanel;
    final PlayerArea selfArea;
    final PlayerArea leftArea;
    final PlayerArea rightArea;
    final List<PlayerArea> orderedAreas = new ArrayList<>(3);

    final JLabel lblDealerTotal;

    final JPanel controlPanel;
    final JPanel bettingPanel;
    final JPanel actionPanel;

    final RoundButton btnChat;
    final RoundButton btnReset;
    final RoundButton btnAposta5;
    final RoundButton btnAposta10;
    final RoundButton btnAposta25;
    final RoundButton btnAposta50;
    final RoundButton btnFinalizarAposta;
    final RoundButton btnComprar;
    final RoundButton btnManter;
    final RoundButton btnVoltar;

    private static final Border INACTIVE_PLAYER_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    private static final Border ACTIVE_PLAYER_BORDER =
        BorderFactory.createCompoundBorder(
            new SmokyBorder(new Color(150, 20, 20), 6, 48),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        );

    // Render atual (permanece privado; o worker usa helpers)
    private final List<RenderedCard> dealerCards = new ArrayList<>();
    private TableStage lastStage = TableStage.WAITING;

    private final Deque<FlowTask> flowQueue = new ArrayDeque<>();
    private SwingWorker<?, ?> stateWorker;
    private int apostaAtual = 0;
    private boolean apostaEnviada = false;
    private int currentBalance;
    private String pendingRoundResultMessage;
    private Integer pendingRoundResultBalance;

    private final ImageIcon backgroundImage = new ImageIcon("public/Images/GameFundo.png");

    private boolean awaitingRoundResultDisplay;
    private final ChatFrame chatFrame;

    public GameFrame(MenuFrame parent, ServerConnection connection, String userId, String username, int balance) {
        this.connection = connection;
        this.menuFrame = parent;
        this.userId = userId;
        this.username = username;
        this.currentBalance = balance;
        this.connection.addListener(this);

        awaitingRoundResultDisplay = false;
        chatFrame = new ChatFrame(this, username, connection::sendChat);

        setTitle(I18n.get("game.title"));
        setSize(1280, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        backgroundPanel = new PanelImage(backgroundImage.getImage(), true);
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        lblSaldo = new JLabel(I18n.get("game.saldo", balance), SwingConstants.LEFT);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 28));
        lblSaldo.setForeground(Color.WHITE);
        header.add(lblSaldo, BorderLayout.WEST);

        lblStage = new JLabel(I18n.get("game.waiting"), SwingConstants.CENTER);
        lblStage.setFont(new Font("Arial", Font.BOLD, 24));
        lblStage.setForeground(Color.WHITE);
        header.add(lblStage, BorderLayout.CENTER);

        btnVoltar = new RoundButton(I18n.get("game.btn.back"), Color.BLACK);
        btnVoltar.setPreferredSize(new Dimension(150, 50));
        btnVoltar.addActionListener(e -> {
            connection.leaveTable();
            setVisible(false);
            menuFrame.open(this);
        });
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        headerButtons.setOpaque(false);

        btnChat = new RoundButton(I18n.get("game.btn.chat"), Color.BLACK);
        btnChat.setPreferredSize(new Dimension(140, 50));
        btnChat.addActionListener(e -> toggleChat());

        headerButtons.add(btnChat);
        headerButtons.add(btnVoltar);
        header.add(headerButtons, BorderLayout.EAST);
        backgroundPanel.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel lblDealerTitle = new JLabel(I18n.get("game.dealer"), SwingConstants.CENTER);
        lblDealerTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblDealerTitle.setForeground(Color.WHITE);
        lblDealerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(lblDealerTitle);
        center.add(Box.createVerticalStrut(10));

        dealerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        dealerPanel.setOpaque(false);
        dealerPanel.setMaximumSize(new Dimension(900, 180));
        dealerPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        center.add(dealerPanel);
        center.add(Box.createVerticalStrut(8));

        lblDealerTotal = new JLabel("", SwingConstants.CENTER);
        lblDealerTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblDealerTotal.setForeground(Color.WHITE);
        lblDealerTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(lblDealerTotal);
        center.add(Box.createVerticalGlue());
        backgroundPanel.add(center, BorderLayout.CENTER);

        JPanel southOuter = new JPanel(new BorderLayout(0, 15));
        southOuter.setOpaque(false);

        JPanel playerRow = new JPanel(new GridLayout(1, 3, 20, 0));
        playerRow.setOpaque(false);

        leftArea = new PlayerArea("Jogador 2");
        selfArea = new PlayerArea("Você");
        rightArea = new PlayerArea("Jogador 3");

        orderedAreas.add(selfArea);
        orderedAreas.add(leftArea);
        orderedAreas.add(rightArea);

        playerRow.add(leftArea.container);
        playerRow.add(selfArea.container);
        playerRow.add(rightArea.container);
        leftArea.render(null);
        rightArea.render(null);
        southOuter.add(playerRow, BorderLayout.CENTER);

        JPanel controlsWrapper = new JPanel();
        controlsWrapper.setOpaque(false);
        controlsWrapper.setLayout(new BoxLayout(controlsWrapper, BoxLayout.Y_AXIS));

        lblAposta = new JLabel(I18n.get("game.bet.current", 0), SwingConstants.CENTER);
        lblAposta.setFont(new Font("Arial", Font.BOLD, 22));
        lblAposta.setForeground(Color.WHITE);
        lblAposta.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsWrapper.add(lblAposta);
        controlsWrapper.add(Box.createVerticalStrut(10));

        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlPanel.setOpaque(false);
        controlsWrapper.add(controlPanel);

        bettingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bettingPanel.setOpaque(false);

        btnReset = createBetButton(I18n.get("game.btn.reset"), 0);
        btnReset.addActionListener(e -> {
            apostaAtual = 0;
            updateBetLabel();
        });
        bettingPanel.add(btnReset);

        btnAposta5 = createBetButton("+5", 5);
        btnAposta10 = createBetButton("+10", 10);
        btnAposta25 = createBetButton("+25", 25);
        btnAposta50 = createBetButton("+50", 50);

        bettingPanel.add(btnAposta5);
        bettingPanel.add(btnAposta10);
        bettingPanel.add(btnAposta25);
        bettingPanel.add(btnAposta50);

        btnFinalizarAposta = new RoundButton(I18n.get("game.btn.sendbet"), Color.BLACK);
        btnFinalizarAposta.setPreferredSize(new Dimension(220, 55));
        btnFinalizarAposta.addActionListener(e -> enviarAposta());
        bettingPanel.add(btnFinalizarAposta);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setOpaque(false);

        btnComprar = new RoundButton(I18n.get("game.btn.buy"), Color.BLACK);
        btnComprar.setPreferredSize(new Dimension(220, 60));

        btnManter = new RoundButton(I18n.get("game.btn.stand"), Color.BLACK);
        btnManter.setPreferredSize(new Dimension(220, 60));
        btnManter.addActionListener(e -> {
            connection.sendAction("STAND");
            btnComprar.setEnabled(false);
            btnManter.setEnabled(false);
        });
        actionPanel.add(btnManter);

        btnComprar.addActionListener(e -> {
            connection.sendAction("HIT");
            // trava até o próximo estado chegar
            btnComprar.setEnabled(false);
            btnManter.setEnabled(false);
        });
        actionPanel.add(btnComprar);

        controlPanel.add(bettingPanel);
        controlsWrapper.add(Box.createVerticalStrut(10));
        controlsWrapper.add(actionPanel);
        southOuter.add(controlsWrapper, BorderLayout.SOUTH);
        backgroundPanel.add(southOuter, BorderLayout.SOUTH);

        setControlsForStage(TableStage.WAITING);
        updateBetLabel();
        updateBalance(balance);
    }

    private RoundButton createBetButton(String text, int value) {
        RoundButton button = new RoundButton(text, Color.BLACK);
        button.setPreferredSize(new Dimension(120, 55));
        if (value > 0) {
            button.addActionListener(e -> adicionarAposta(value));
        }
        return button;
    }

    public void open() {
        setLocationRelativeTo(menuFrame);
        setVisible(true);
        connection.joinTable();
    }

    public void updateBalance(int balance) {
        currentBalance = balance;
        lblSaldo.setText(I18n.get("game.saldo", balance));
    }

    private void adicionarAposta(int valor) {
        if (apostaEnviada) {
            return;
        }
        if (currentBalance >= (apostaAtual + valor)) {
            apostaAtual += valor;
            updateBetLabel();
        } else {
            CustomDialog.showMessage(this, I18n.get("game.err.balance"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarAposta() {
        if (apostaAtual == 0) {
            CustomDialog.showMessage(this, I18n.get("game.err.nobet"), I18n.get("dialog.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        connection.placeBet(apostaAtual);
        apostaEnviada = true;
        setBettingButtonsEnabled(false);
    }

    private void updateBetLabel() {
        lblAposta.setText(I18n.get("game.bet.current", apostaAtual));
    }

    @Override
    public void onJoinAcknowledged(String tableId, int balance) {
        updateBalance(balance);
        lblStage.setText(I18n.get("game.bet.phase"));
        apostaEnviada = false;
        resetBetUI();
        setControlsForStage(TableStage.BETTING);
        resetTable();
    }

    @Override
    public void onTableState(TableState state) {
        enqueueState(state);
    }

    @Override
    public void onDealSequence(DealSequence sequence) {
        enqueueDealSequence(sequence);
    }

    // ===================== REMOVIDO: applyState/runActions/build* e classes de animação =====================

    // --------------------- Helpers usados pelo GameFlowWorker (package-private) ---------------------

    void updateSeats(TableState state) {
        PlayerView selfView = state.findPlayer(userId).orElse(null);
        if (selfView != null && state.getStage() == TableStage.BETTING) {
            int serverBet = selfView.getBet();
            if (serverBet > 0) {
                if (apostaAtual != serverBet) {
                    apostaAtual = serverBet;
                    updateBetLabel();
                }
                apostaEnviada = true;
            }
        }
        List<PlayerView> others = new ArrayList<>();
        for (PlayerView view : state.getPlayers()) {
            if (!view.getUserId().equals(userId)) {
                others.add(view);
            }
        }
        selfArea.render(selfView);
        leftArea.render(others.isEmpty() ? null : others.get(0));
        rightArea.render(others.size() > 1 ? others.get(1) : null);
    }

    void resetTable() {
        dealerCards.clear();
        dealerPanel.removeAll();
        dealerPanel.revalidate();
        dealerPanel.repaint();
        setDealerActive(false);
        updateDealerTotalLabel();
        for (PlayerArea area : orderedAreas) {
            area.clearHand();
        }
    }

    void addDealerCard(CardView card, boolean faceUp) {
        RenderedCard rc = new RenderedCard(card, faceUp, CARD_WIDTH, CARD_HEIGHT);
        dealerCards.add(rc);
        dealerPanel.add(rc.label);
        dealerPanel.revalidate();
        dealerPanel.repaint();
        updateDealerTotalLabel();
    }

    void revealDealerCard(int index) {
        if (index < 0 || index >= dealerCards.size()) return;
        dealerCards.get(index).setFaceUp(true);
        dealerPanel.revalidate();
        dealerPanel.repaint();
        updateDealerTotalLabel();
    }

    void updateDealerCard(int index, CardView card) {
        if (index < 0 || index >= dealerCards.size()) return;
        dealerCards.get(index).setCard(card);
        updateDealerTotalLabel();
    }

    int getDealerRenderedSize() { return dealerCards.size(); }
    boolean isDealerRenderedFaceUp(int index) {
        return index >= 0 && index < dealerCards.size() && dealerCards.get(index).faceUp;
    }

    void addCardToPlayer(String playerId, CardView card, boolean faceUp) {
        PlayerArea area = findArea(playerId);
        if (area != null) {
            CardView stored = new CardView(card.getRank(), card.getSuit(), !faceUp || card.isHidden());
            area.addCard(stored, faceUp);
        }
    }

    void updatePlayerCard(String playerId, int index, CardView card) {
        PlayerArea area = findArea(playerId);
        if (area != null) area.updateCard(index, card);
    }

    void revealPlayerCard(String playerId, int index) {
        PlayerArea area = findArea(playerId);
        if (area != null) area.revealCard(index);
    }

    void revealFirstHiddenOf(String playerId) {
        PlayerArea area = findArea(playerId);
        if (area != null) area.revealFirstHidden();
    }

    int getPlayerRenderedSize(String playerId) {
        PlayerArea area = findArea(playerId);
        return (area == null) ? 0 : area.cards.size();
    }

    boolean isPlayerRenderedFaceUp(String playerId, int index) {
        PlayerArea area = findArea(playerId);
        return area != null && index >= 0 && index < area.cards.size() && area.cards.get(index).faceUp;
    }

    void highlightTurn(String playerId) {
        boolean dealerActive = playerId == null && lastStage == TableStage.PLAYING;
        setDealerActive(dealerActive);
        for (PlayerArea area : orderedAreas) {
            boolean active = playerId != null && playerId.equals(area.getPlayerId());
            area.setActive(active);
        }
    }

    void updateControlsForTurn(String playerId) {
        boolean myTurn = playerId != null && playerId.equals(userId);
        btnComprar.setEnabled(myTurn);
        btnManter.setEnabled(myTurn);
        actionPanel.setVisible(myTurn && lastStage == TableStage.PLAYING);
    }

    void setControlsForStage(TableStage stage) {
        boolean betting = stage == TableStage.BETTING;
        bettingPanel.setVisible(betting);
        setBettingButtonsEnabled(betting && !apostaEnviada);
        if (!betting) {
            btnComprar.setEnabled(false);
            btnManter.setEnabled(false);
            actionPanel.setVisible(false);
        }
    }

    void onBettingStageStarted() {
        resetBetUI();
        setBettingButtonsEnabled(true);
    }

    private void setBettingButtonsEnabled(boolean enabled) {
        btnFinalizarAposta.setEnabled(enabled);
        btnReset.setEnabled(enabled);
        btnAposta5.setEnabled(enabled);
        btnAposta10.setEnabled(enabled);
        btnAposta25.setEnabled(enabled);
        btnAposta50.setEnabled(enabled);
    }

    String stageMessage(TableStage stage, TableState state) {
        if (stage == null) {
            return I18n.get("game.waiting");
        }
        return switch (stage) {
            case BETTING -> I18n.get("game.bet.phase");
            case PLAYING -> {
                if (state == null || state.getCurrentTurnUserId() == null) {
                    yield I18n.get("game.playing.running");
                }
                PlayerArea area = findArea(state.getCurrentTurnUserId());
                if (area != null) {
                    yield I18n.get("game.playing.turn", area.getDisplayName());
                }
                yield I18n.get("game.playing.running");
            }
            case RESULTS -> I18n.get("game.results");
            case WAITING -> I18n.get("game.wait.players");
        };
    }

    void setStageText(String text) {
        lblStage.setText(text);
    }

    void setLastStage(TableStage s) { this.lastStage = s; }
    TableStage getLastStage() { return this.lastStage; }

    void resetBetUI() {
        apostaAtual = 0;
        apostaEnviada = false;
        updateBetLabel();
    }

    private PlayerArea findArea(String pid) {
        if (pid == null) return null;
        for (PlayerArea area : orderedAreas) {
            if (pid.equals(area.getPlayerId())) {
                return area;
            }
        }
        return null;
    }

    private void enqueueState(TableState state) {
        synchronized (flowQueue) {
            flowQueue.addLast(FlowTask.forState(state));
            if (stateWorker == null) {
                startNextTask();
            }
        }
    }

    private void enqueueDealSequence(client.model.DealSequence sequence) {
        synchronized (flowQueue) {
            flowQueue.addLast(FlowTask.forDeal(sequence));
            if (stateWorker == null) {
                startNextTask();
            }
        }
    }

    private void updateDealerTotalLabel() {
        if (lblDealerTotal == null) {
            return;
        }
        String description = totalDescription(dealerCardViews());
        if (description == null || description.isBlank()) {
            lblDealerTotal.setText("");
        } else {
            lblDealerTotal.setText("Total do Dealer: " + description);
        }
    }

    private List<CardView> dealerCardViews() {
        List<CardView> list = new ArrayList<>(dealerCards.size());
        for (RenderedCard rc : dealerCards) {
            CardView base = rc.card;
            String rank = base != null ? base.getRank() : "";
            String suit = base != null ? base.getSuit() : "";
            boolean hidden = (base != null && base.isHidden()) || !rc.faceUp;
            list.add(new CardView(rank, suit, hidden));
        }
        return list;
    }

    void triggerPendingRoundResultDisplay() {
        String message;
        Integer balance;
        synchronized (this) {
            boolean hasMessage = pendingRoundResultMessage != null && !pendingRoundResultMessage.isBlank();
            if (!hasMessage && pendingRoundResultBalance == null) {
                awaitingRoundResultDisplay = true;
                return;
            }
            awaitingRoundResultDisplay = false;
            message = pendingRoundResultMessage;
            balance = pendingRoundResultBalance;
            pendingRoundResultMessage = null;
            pendingRoundResultBalance = null;
        }
        if (balance != null) {
            updateBalance(balance);
        }
        if (message != null && !message.isBlank() && isVisible()) {
            CustomDialog.showMessage(this, message, I18n.get("game.result.title"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setDealerActive(boolean active) {
        dealerPanel.revalidate();
        dealerPanel.repaint();
    }

    private void startNextTask() {
        FlowTask task;
        synchronized (flowQueue) {
            task = flowQueue.pollFirst();
            if (task == null) {
                stateWorker = null;
                return;
            }
        }

        SwingWorker<?, ?> worker;
        if (task.type == FlowTask.Type.DEAL) {
            worker = new GameDealWorker(this, task.dealSequence, DEAL_DELAY, ACTION_DELAY) {
                @Override
                protected void done() {
                    onWorkerFinished(this);
                }
            };
        } else {
            worker = new GameFlowWorker(this, task.tableState, DEAL_DELAY, ACTION_DELAY, RESULT_DELAY) {
                @Override
                protected void done() {
                    onWorkerFinished(this);
                }
            };
        }

        synchronized (flowQueue) {
            stateWorker = worker;
        }
        worker.execute();
    }

    private void onWorkerFinished(SwingWorker<?, ?> worker) {
        try { worker.get(); } catch (Exception ignored) {}
        synchronized (flowQueue) {
            stateWorker = null;
            if (!flowQueue.isEmpty()) {
                startNextTask();
            }
        }
    }

    @Override
    public void onRoundResult(String message, int balance) {
        boolean triggerNow = false;
        synchronized (this) {
            pendingRoundResultMessage = message;
            pendingRoundResultBalance = balance;
            if (awaitingRoundResultDisplay) {
                awaitingRoundResultDisplay = false;
                triggerNow = true;
            }
        }
        if (triggerNow) {
            triggerPendingRoundResultDisplay();
        }
    }

    @Override
    public void onBalanceUpdate(int balance) {
        updateBalance(balance);
    }

    @Override
    public void onErrorMessage(String message) {
        CustomDialog.showMessage(this, message, I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onInfoMessage(String message) {
        if (message != null && !message.isBlank()) {
            CustomDialog.showMessage(this, message, I18n.get("dialog.info"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void onChatMessage(String fromUsername, String message) {
        if (fromUsername == null || message == null) {
            return;
        }
        chatFrame.addIncomingMessage(fromUsername, message);
    }

    @Override
    public void onLogout() {
        resetTable();
        setVisible(false);
        chatFrame.deleteHistoryFile();
        chatFrame.setVisible(false);
        menuFrame.open(this);
    }

    @Override
    public void onAccountDeleted() {
        chatFrame.deleteHistoryFile();
    }

    @Override
    public void onConnectionClosed() {
        chatFrame.deleteHistoryFile();
        JOptionPane.showMessageDialog(this, I18n.get("game.connection.closed"), "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    @Override
    public void dispose() {
        connection.removeListener(this);
        chatFrame.deleteHistoryFile();
        chatFrame.dispose();
        super.dispose();
    }

    // ===================== Classes internas mantidas =====================

    private static class CardHandPanel extends JPanel {
        private static final int DEFAULT_GAP = 10;
        private static final int MIN_STEP = 12;
        private static final int VERTICAL_PADDING = 12;

        CardHandPanel() {
            setOpaque(false);
            setLayout(null);
        }

        @Override
        public Dimension getPreferredSize() {
            int count = getComponentCount();
            int width = CARD_WIDTH;
            if (count > 1) {
                width += (CARD_WIDTH + DEFAULT_GAP) * (count - 1);
            }
            int height = CARD_HEIGHT + VERTICAL_PADDING * 2;
            return new Dimension(width, height);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(CARD_WIDTH, CARD_HEIGHT + VERTICAL_PADDING * 2);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, CARD_HEIGHT + VERTICAL_PADDING * 2);
        }

        @Override
        public void doLayout() {
            int count = getComponentCount();
            if (count == 0) return;

            int width = getWidth();
            if (width <= 0) {
                width = getPreferredSize().width;
            }

            int step;
            if (count == 1) {
                step = 0;
            } else if (width > CARD_WIDTH) {
                double raw = (double) (width - CARD_WIDTH) / (count - 1);
                if (raw < 1) {
                    step = 1;
                } else {
                    step = (int) Math.floor(raw);
                    step = Math.min(step, CARD_WIDTH + DEFAULT_GAP);
                    step = Math.max(step, MIN_STEP);
                }
            } else {
                step = MIN_STEP;
            }

            int totalWidth = CARD_WIDTH + (count - 1) * step;
            int startX = Math.max(0, (width - totalWidth) / 2);
            int top = Math.max(0, (getHeight() - CARD_HEIGHT) / 2);

            for (int i = 0; i < count; i++) {
                Component comp = getComponent(i);
                int x = startX + i * step;
                comp.setBounds(x, top, CARD_WIDTH, CARD_HEIGHT);
            }
        }
    }

    private void toggleChat() {
        if (chatFrame.isVisible()) {
            chatFrame.setVisible(false);
        } else {
            chatFrame.setLocationRelativeTo(this);
            chatFrame.setVisible(true);
        }
    }

    void applyTranslations() {
        setTitle(I18n.get("game.title"));
        lblSaldo.setText(I18n.get("game.saldo", currentBalance));
        lblStage.setText(stageMessage(lastStage, null));
        btnChat.setText(I18n.get("game.btn.chat"));
        btnReset.setText(I18n.get("game.btn.reset"));
        btnFinalizarAposta.setText(I18n.get("game.btn.sendbet"));
        btnComprar.setText(I18n.get("game.btn.buy"));
        btnManter.setText(I18n.get("game.btn.stand"));
        btnVoltar.setText(I18n.get("game.btn.back"));
        updateBetLabel();
        chatFrame.applyTranslations();
    }

    private class PlayerArea {
        private final JPanel container;
        private final JLabel lblName;
        private final JLabel lblStatus;
        private final JLabel lblBet;
        private final JLabel lblTotal;
        private final CardHandPanel handPanel;
        private final String placeholder;
        private final List<RenderedCard> cards = new ArrayList<>();
        private String playerId;
        private String displayName;

        PlayerArea(String placeholder) {
            this.placeholder = placeholder;
            container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setBorder(INACTIVE_PLAYER_BORDER);

            lblName = new JLabel(placeholder, SwingConstants.CENTER);
            lblName.setFont(new Font("Arial", Font.BOLD, 22));
            lblName.setForeground(Color.WHITE);
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

            lblStatus = new JLabel("", SwingConstants.CENTER);
            lblStatus.setFont(new Font("Arial", Font.PLAIN, 18));
            lblStatus.setForeground(Color.LIGHT_GRAY);
            lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

            lblBet = new JLabel("", SwingConstants.CENTER);
            lblBet.setFont(new Font("Arial", Font.PLAIN, 18));
            lblBet.setForeground(Color.WHITE);
            lblBet.setAlignmentX(Component.CENTER_ALIGNMENT);

            lblTotal = new JLabel("", SwingConstants.CENTER);
            lblTotal.setFont(new Font("Arial", Font.PLAIN, 18));
            lblTotal.setForeground(Color.WHITE);
            lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

            handPanel = new CardHandPanel();

            container.add(lblName);
            container.add(lblStatus);
            container.add(lblBet);
            container.add(lblTotal);
            container.add(Box.createVerticalStrut(5));
            container.add(handPanel);
        }

        void render(PlayerView view) {
            if (view == null) {
                playerId = null;
                displayName = placeholder;
                lblName.setText(placeholder);
                lblStatus.setText("");
                lblBet.setText("");
                lblTotal.setText("");
                clearHand();
                container.setVisible(false);
                return;
            }
            container.setVisible(true);
            playerId = view.getUserId();
            displayName = view.getUsername();
            lblName.setText(displayName);
            lblStatus.setText(statusText(view.getStatus()));
            lblBet.setText("Aposta: $" + view.getBet());
        }

        void addCard(CardView card, boolean faceUp) {
            RenderedCard rc = new RenderedCard(card, faceUp, CARD_WIDTH, CARD_HEIGHT);
            cards.add(rc);
            handPanel.add(rc.label);
            handPanel.revalidate();
            handPanel.repaint();
            lblTotal.setText(GameFrame.totalDescription(cardViews()));
        }

        void revealCard(int index) {
            if (index < 0 || index >= cards.size()) return;
            cards.get(index).setFaceUp(true);
            handPanel.revalidate();
            handPanel.repaint();
            lblTotal.setText(GameFrame.totalDescription(cardViews()));
        }

        void updateCard(int index, CardView card) {
            if (index < 0 || index >= cards.size()) return;
            cards.get(index).setCard(card);
            handPanel.revalidate();
            handPanel.repaint();
            lblTotal.setText(GameFrame.totalDescription(cardViews()));
        }

        void revealFirstHidden() {
            for (int i = 0; i < cards.size(); i++) {
                if (!cards.get(i).faceUp) {
                    revealCard(i);
                    break;
                }
            }
        }

        void clearHand() {
            cards.clear();
            handPanel.removeAll();
            handPanel.revalidate();
            handPanel.repaint();
            lblTotal.setText("");
        }

        void setActive(boolean active) {
            container.setBorder(active ? ACTIVE_PLAYER_BORDER : INACTIVE_PLAYER_BORDER);
            container.revalidate();
            container.repaint();
        }

        String getPlayerId() { return playerId; }
        String getDisplayName() { return displayName != null ? displayName : placeholder; }

        private List<CardView> cardViews() {
            List<CardView> list = new ArrayList<>(cards.size());
            for (RenderedCard rc : cards) {
                CardView base = rc.card;
                String rank = (base == null) ? "" : base.getRank();
                String suit = (base == null) ? "" : base.getSuit();
                list.add(new CardView(rank, suit, !rc.faceUp));
            }
            return list;
        }

        private String statusText(String status) {
            if (status == null) return "";
            return switch (status.toUpperCase()) {
                case "BETTING" -> "Aguardando aposta";
                case "BUST" -> "Estourou";
                case "STAND" -> "Parou";
                case "RESULT" -> "Rodada finalizada";
                case "PLAYING" -> "Jogando";
                default -> "";
            };
        }
    }

    private static class FlowTask {
        enum Type { DEAL, STATE }
        final Type type;
        final DealSequence dealSequence;
        final TableState tableState;

        private FlowTask(Type type, DealSequence seq, TableState state) {
            this.type = type;
            this.dealSequence = seq;
            this.tableState = state;
        }

        static FlowTask forDeal(DealSequence seq) {
            return new FlowTask(Type.DEAL, seq, null);
        }

        static FlowTask forState(TableState state) {
            return new FlowTask(Type.STATE, null, state);
        }
    }

    private static class RenderedCard {
        CardView card;
        boolean faceUp;
        final JLabel label;
        final int width;
        final int height;

        RenderedCard(CardView card, boolean faceUp, int width, int height) {
            this.faceUp = faceUp;
            this.width = width;
            this.height = height;
            this.label = new JLabel();
            setCard(card);
        }

        void setFaceUp(boolean faceUp) {
            this.faceUp = faceUp;
            updateIcon();
        }

        void setCard(CardView card) {
            this.card = (card != null) ? card : new CardView("", "", true);
            updateIcon();
        }

        private void updateIcon() {
            String path = "public/Cartas/Fundo.png";
            if (faceUp && card != null) {
                String rank = card.getRank();
                String suit = card.getSuit();
                if (rank != null && !rank.isBlank() && suit != null && !suit.isBlank()) {
                    path = "public/Cartas/" + suit + "/" + rank + ".png";
                }
            }
            Image base = new ImageIcon(path).getImage();
            label.setIcon(new ImageIcon(base.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            label.setPreferredSize(new Dimension(width, height));
        }
    }

    private static String totalDescription(List<CardView> cards) {
        if (cards == null || cards.isEmpty()) return "";
        int total = 0, aces = 0;
        boolean hidden = false;
        for (CardView card : cards) {
            if (card.isHidden()) { hidden = true; continue; }
            String rank = card.getRank();
            if (rank == null || rank.isBlank()) {
                hidden = true;
                continue;
            }
            switch (rank) {
                case "J": case "Q": case "K": total += 10; break;
                case "A": total += 11; aces++; break;
                default: total += Integer.parseInt(rank);
            }
        }
        while (total > 21 && aces-- > 0) total -= 10;
        if (hidden) return total == 0 ? "?" : total + "+";
        return total > 0 ? String.valueOf(total) : "";
    }
}
