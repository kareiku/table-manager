package org.example;

import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class TableManager {
    private static final Language LANG = Language.en_US;
    private static final JComboBox<String> columns = new JComboBox<>();
    private static final JTextField filter = new JTextField(20);
    private static JTable table;
    private static Vector<Vector<Object>> originalData = null;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignore) {
        }
        SwingUtilities.invokeLater(TableManager::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame(LANG.get(Language.Key.Title));
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(LANG.get(Language.Key.FileMenu));
        JMenuItem openItem = new JMenuItem(LANG.get(Language.Key.OpenFile));
        JMenuItem exitItem = new JMenuItem(LANG.get(Language.Key.Exit));
        JPanel controls = new JPanel();

        openItem.addActionListener(ignored -> {
            try {
                openFile();
                JScrollPane tableScrollPane = new JScrollPane(table);
                frame.getContentPane().removeAll();
                frame.add(controls, BorderLayout.NORTH);
                frame.add(tableScrollPane, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            } catch (IOException ignore) {
            }
        });

        exitItem.addActionListener(ignore -> System.exit(0));

        filter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        controls.add(columns);
        controls.add(filter);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setJMenuBar(menuBar);
        frame.add(controls, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    public static void openFile() throws IOException {
        File file = askOpenFile();
        if (file != null) {
            Sheet sheet = askChooseSheet(file);
            if (sheet != null) {
                loadSheet(sheet);
            }
        }
    }

    private static File askOpenFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("OK");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle(LANG.get(Language.Key.SelectFile));
        fileChooser.setFileFilter(new FileNameExtensionFilter(LANG.get(Language.Key.ExcelFiles) + " (*.xls;*.xlsx;*.xlsm)", "xls", "xlsx", "xlsm"));
        return fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

    private static Sheet askChooseSheet(File file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            List<String> sheetNames = new ArrayList<>();
            workbook.sheetIterator().forEachRemaining(sheet -> sheetNames.add(sheet.getSheetName()));
            String selectedSheet = (String) JOptionPane.showInputDialog(null,
                    null, LANG.get(Language.Key.SelectSheet),
                    JOptionPane.QUESTION_MESSAGE, null, sheetNames.toArray(), sheetNames.get(0)
            );
            return selectedSheet != null ? workbook.getSheet(selectedSheet) : null;
        }
    }

    private static void loadSheet(Sheet sheet) {
        List<String[]> data = new ArrayList<>();
        String[] header = new String[sheet.getRow(0).getLastCellNum()];
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String[] rowData = new String[header.length];
                for (int j = 0; j < header.length; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = cell != null ? cell.toString() : "";
                    if (i == 0) {
                        header[j] = cellValue;
                    } else {
                        rowData[j] = cellValue;
                    }
                }
                if (i > 0) {
                    data.add(rowData);
                }
            }
        }

        columns.removeAllItems();
        for (String s : header) {
            columns.addItem(s);
        }
        columns.setSelectedIndex(0);

        DefaultTableModel tableModel = new DefaultTableModel(data.toArray(new String[0][0]), header);
        table = new JTable(tableModel);

        originalData = new Vector<>();
        for (Object row : tableModel.getDataVector()) {
            originalData.add(new Vector<>((Vector<?>) row));
        }
    }

    private static void filterTable() {
        String query = filter.getText().toLowerCase();
        int columnIndex = columns.getSelectedIndex();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        if (originalData == null) {
            originalData = new Vector<>();
            for (Object row : model.getDataVector()) {
                originalData.add(new Vector<>((Vector<?>) row));
            }
        }

        Vector<Vector<Object>> filteredData = query.isEmpty()
                ? originalData
                : originalData.stream()
                .filter(row -> row.get(columnIndex).toString().toLowerCase().contains(query))
                .collect(Collectors.toCollection(Vector::new));

        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames.add(model.getColumnName(i));
        }

        model.setDataVector(filteredData, columnNames);
    }
}
