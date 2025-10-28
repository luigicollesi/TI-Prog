package server.game;

import server.net.ClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableManager {
    private final List<Table> tables = new ArrayList<>();
    private int nextId = 1;

    public synchronized Table assignTable(ClientHandler handler) {
        Optional<Table> available = tables.stream()
            .filter(Table::hasSeat)
            .findFirst();

        Table table = available.orElseGet(this::createTable);
        table.addPlayer(handler);
        return table;
    }

    public synchronized void removePlayer(Table table, ClientHandler handler) {
        if (table == null) {
            return;
        }
        table.removePlayer(handler);
        if (table.isEmpty()) {
            tables.remove(table);
        }
    }

    private Table createTable() {
        Table table = new Table("mesa-" + nextId++);
        tables.add(table);
        return table;
    }
}
