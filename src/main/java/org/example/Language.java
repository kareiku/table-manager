package org.example;

import java.util.Map;

public enum Language {
    en_US(Map.ofEntries(
            Map.entry(Key.Title, "Excel Table Manager"),
            Map.entry(Key.Exit, "Exit"),
            Map.entry(Key.FileMenu, "File"),
            Map.entry(Key.OpenFile, "Open..."),
            Map.entry(Key.SelectFile, "Select a file"),
            Map.entry(Key.ExcelFiles, "Excel files"),
            Map.entry(Key.SelectSheet, "Select a sheet")
    )),
    es_ES(Map.ofEntries(
            Map.entry(Key.Title, "Gestor de tablas Excel"),
            Map.entry(Key.Exit, "Salir"),
            Map.entry(Key.FileMenu, "Archivo"),
            Map.entry(Key.OpenFile, "Abrir"),
            Map.entry(Key.SelectFile, "Seleccione un archivo"),
            Map.entry(Key.ExcelFiles, "Archivos Excel"),
            Map.entry(Key.SelectSheet, "Seleccione una hoja")
    ));

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
        FileMenu,
        OpenFile,
        SelectFile,
        ExcelFiles,
        SelectSheet
    }
}
