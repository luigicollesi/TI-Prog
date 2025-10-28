package client.net;

import client.model.DealSequence;
import client.model.HistoryEntryModel;
import client.model.TableState;

import java.util.List;

public interface ServerListener {
    default void onLoginSuccess(String userId, String username, int balance) {}

    default void onLoginFailure(String message) {}

    default void onRegisterResult(boolean success, String message) {}

    default void onJoinAcknowledged(String tableId, int balance) {}

    default void onDealSequence(DealSequence sequence) {}

    default void onTableState(TableState state) {}

    default void onErrorMessage(String message) {}

    default void onInfoMessage(String message) {}

    default void onRoundResult(String message, int balance) {}

    default void onBalanceUpdate(int balance) {}

    default void onHistoryLoaded(List<HistoryEntryModel> entries) {}

    default void onLogout() {}

    default void onAccountDeleted() {}

    default void onConnectionClosed() {}
}
