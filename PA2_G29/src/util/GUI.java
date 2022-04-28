package util;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GUI extends Thread {
    private final BlockingQueue<Object[]> updates;
    private final Map<String, String> extraInfo;
    private int totalRecordCount = 0;
    private final DefaultTableModel recordListTableModel;
    private final DefaultTableModel recordCountByIDTableModel;
    private DefaultTableModel conditionsTableModel;

    private JFrame frame;
    private JPanel mainPanel;
    private JLabel totalRecordCountLabel;
    private JScrollPane recordListPanel;
    private JScrollPane recordCountByIDPanel;
    private JTable recordListTable;
    private JTable recordCountByIDTable;
    private JLabel extraInfoLabel;
    private JPanel conditionsContainerPanel;
    private JTable conditionsTable;
    private JPanel recordListContainerPanel;
    private JPanel recordCountByIdContainerPanel;

    public GUI(String title) {
        updates = new LinkedBlockingQueue<>();
        extraInfo = new HashMap<>();

        $$$setupUI$$$();
        frame = new JFrame(title);
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(516, 356));
        frame.setPreferredSize(new Dimension(516, 356));
        frame.pack();

        recordListPanel.setMinimumSize(new Dimension(300, 244));
        recordListPanel.setMaximumSize(new Dimension(300, 244));
        recordListPanel.setPreferredSize(new Dimension(300, 244));
        recordListTableModel = new DefaultTableModel(new String[]{"Sensor ID", "Temp. (Cº)", "Timestamp"}, 0) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                    case 2:
                        return Integer.class;
                    case 1:
                        return Double.class;
                    default:
                        return String.class;
                }
            }
        };
        recordListTable.setModel(recordListTableModel);

        recordCountByIDPanel.setMinimumSize(new Dimension(190, 134));
        recordCountByIDPanel.setMaximumSize(new Dimension(190, 134));
        recordCountByIDPanel.setPreferredSize(new Dimension(190, 134));
        recordCountByIDTableModel = new DefaultTableModel(new String[]{"Sensor ID", "Count"}, 0) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                    case 1:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        recordCountByIDTable.setModel(recordCountByIDTableModel);
    }

    public void run() {
        frame.setVisible(true);
        Object[] update;
        try {
            while (true) {
                update = updates.take();
                if (update[0].equals("RECORD")) {
                    int sensorId = (int) update[1];
                    double temp = (double) update[2];
                    long timestamp = (long) update[3];
                    totalRecordCountLabel.setText("Total records received: " + (++totalRecordCount));
                    boolean newID = true;
                    for (int i = 0; i < recordCountByIDTableModel.getRowCount(); i++) {
                        if (recordCountByIDTableModel.getValueAt(i, 0).equals(sensorId)) {
                            recordCountByIDTableModel.setValueAt(((int) recordCountByIDTableModel.getValueAt(i, 1)) + 1, i, 1);
                            newID = false;
                            break;
                        }
                    }
                    if (newID) {
                        recordCountByIDTableModel.addRow(new Object[]{sensorId, 1});
                    }
                    recordListTableModel.addRow(new Object[]{sensorId, temp, timestamp});
                } else if (update[0].equals("CONDITION")) {
                    conditionsContainerPanel.setVisible(true);
                    String condition = (String) update[1];
                    String status = (String) update[2];
                    boolean newCond = true;
                    for (int i = 0; i < conditionsTableModel.getRowCount(); i++) {
                        if (conditionsTableModel.getValueAt(i, 1).equals(condition)) {
                            conditionsTableModel.setValueAt(status, i, 0);
                            newCond = false;
                            break;
                        }
                    }
                    if (newCond) {
                        conditionsTableModel.addRow(new Object[]{status, condition});
                        conditionsTable.sizeColumnsToFit(1);
                    }
                } else if (update[0].equals("EXTRA")) {
                    String name = (String) update[1];
                    String value = (String) update[2];
                    extraInfo.put(name, value);
                    StringBuilder label = new StringBuilder();
                    Iterator<String> iterator = extraInfo.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        label.append(key).append(": ").append(extraInfo.get(key)).append(", ");
                    }
                    extraInfoLabel.setText(label.substring(0, label.length() - 2));
                } else {
                    System.err.println("GUI got unknown update.");
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addRecord(int sensorId, double temp, long timestamp) {
        try {
            updates.put(new Object[]{
                    "RECORD",
                    sensorId,
                    temp,
                    timestamp
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addCondition(String condition, String status) {
        try {
            updates.put(new Object[]{
                    "CONDITION",
                    condition,
                    status
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addExtraInfo(String name, String value) {
        try {
            updates.put(new Object[]{
                    "EXTRA",
                    name,
                    value,
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onlyExtraInfo(Boolean bool) {
        totalRecordCountLabel.setVisible(!bool);
        recordListContainerPanel.setVisible(!bool);
        recordCountByIdContainerPanel.setVisible(!bool);
        if (bool) {
            frame.setMinimumSize(new Dimension(516, 64));
            frame.setSize(new Dimension(516, 64));
        } else
            frame.setMinimumSize(new Dimension(516, 356));
    }

    public void enableSorting() {
        TableRowSorter<DefaultTableModel> recordListTableSorter = new TableRowSorter<>(recordListTableModel);
        recordListTable.setRowSorter(recordListTableSorter);
        ArrayList<RowSorter.SortKey> recordListSortKeys = new ArrayList<>();
        recordListSortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        recordListTableSorter.setSortKeys(recordListSortKeys);

        TableRowSorter<DefaultTableModel> recordCountByIDTableSorter = new TableRowSorter<>(recordCountByIDTableModel);
        recordCountByIDTable.setRowSorter(recordCountByIDTableSorter);
        ArrayList<RowSorter.SortKey> recordCountByIDSortKeys = new ArrayList<>();
        recordCountByIDSortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        recordCountByIDTableSorter.setSortKeys(recordCountByIDSortKeys);
    }

    private void createUIComponents() {
        conditionsTable = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, 95));
                return component;
            }
        };
        conditionsTableModel = new DefaultTableModel(new String[]{"Status", "Condition"}, 0);
        conditionsTable.setModel(conditionsTableModel);
    }

    /**
     * Changes the theme of the UI window
     * <p>
     * If computer doesn't have any of the themes provided the computer's default
     * one will be used
     *
     * @param wantedLooks list of theme names
     */
    public static void setGUILook(String[] wantedLooks) {
        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        String chosenLook = null;
        for (String wantedLook : wantedLooks) {
            if (chosenLook == null)
                for (UIManager.LookAndFeelInfo look : looks)
                    if (wantedLook.equals(look.getName())) {
                        chosenLook = look.getClassName();
                        break;
                    }
        }
        if (chosenLook == null)
            chosenLook = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(chosenLook);
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        totalRecordCountLabel = new JLabel();
        totalRecordCountLabel.setText("Total records received : 0");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        mainPanel.add(totalRecordCountLabel, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer5, gbc);
        extraInfoLabel = new JLabel();
        Font extraInfoLabelFont = this.$$$getFont$$$(null, -1, -1, extraInfoLabel.getFont());
        if (extraInfoLabelFont != null) extraInfoLabel.setFont(extraInfoLabelFont);
        extraInfoLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        mainPanel.add(extraInfoLabel, gbc);
        recordListContainerPanel = new JPanel();
        recordListContainerPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(recordListContainerPanel, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Records received:");
        recordListContainerPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        recordListPanel = new JScrollPane();
        recordListPanel.setHorizontalScrollBarPolicy(30);
        recordListPanel.setVerticalScrollBarPolicy(22);
        recordListContainerPanel.add(recordListPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        recordListTable = new JTable();
        recordListTable.setAutoResizeMode(4);
        recordListTable.setEnabled(false);
        recordListPanel.setViewportView(recordListTable);
        recordCountByIdContainerPanel = new JPanel();
        recordCountByIdContainerPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(recordCountByIdContainerPanel, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Record count per sensor:");
        recordCountByIdContainerPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        recordCountByIDPanel = new JScrollPane();
        recordCountByIDPanel.setVerticalScrollBarPolicy(20);
        recordCountByIdContainerPanel.add(recordCountByIDPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        recordCountByIDTable = new JTable();
        recordCountByIDTable.setAutoResizeMode(4);
        recordCountByIDTable.setEnabled(false);
        recordCountByIDPanel.setViewportView(recordCountByIDTable);
        conditionsContainerPanel = new JPanel();
        conditionsContainerPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        conditionsContainerPanel.setVisible(false);
        recordCountByIdContainerPanel.add(conditionsContainerPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Conditions:");
        conditionsContainerPanel.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        scrollPane1.setVerticalScrollBarPolicy(20);
        conditionsContainerPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        conditionsTable.setAutoResizeMode(0);
        conditionsTable.setCellSelectionEnabled(false);
        conditionsTable.setEnabled(false);
        scrollPane1.setViewportView(conditionsTable);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
