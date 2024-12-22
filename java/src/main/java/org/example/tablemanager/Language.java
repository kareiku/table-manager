package org.example.tablemanager;

import java.util.Map;

public enum Language {
    en_US(Map.ofEntries(
            Map.entry(Key.Title, "Excel Table Manager"),
            Map.entry(Key.Exit, "Exit"),
            Map.entry(Key.Filter, "Filter by..."))),
    es_ES(Map.ofEntries(
            Map.entry(Key.Title, "Gestor de tablas Excel"),
            Map.entry(Key.Exit, "Salir"),
            Map.entry(Key.Filter, "Filtrar por...")));

    private final Map<Key, String> messages;

    Language(Map<Key, String> messages) {
        this.messages = messages;
    }

    public String get(Key key) {
        return this.messages.get(key);
    }

    public enum Key {
        Title,
        Exit,
        Filter
    }
}
