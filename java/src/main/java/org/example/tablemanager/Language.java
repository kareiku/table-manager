package org.example.tablemanager;

public enum Language {
    en_US("Excel Table Manager", "Exit", "Filter by..."),
    es_ES("Gestor de tablas Excel", "Salir", "Filtrar por...");

    private final String[] messages;

    Language(String... messages) {
        this.messages = messages;
    }

    public String get(int index) {
        return this.messages.length > index ? this.messages[index] : "";
    }
}
