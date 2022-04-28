package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    private JScrollPane conditionsPanel;
    private JTable conditionsTable;
    private JPanel recordListContainerPanel;
    private JPanel recordCountByIdContainerPanel;

    public GUI(String title) {
        updates = new LinkedBlockingQueue<>();
        extraInfo = new HashMap<>();

        frame = new JFrame(title);
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(516, 356));
        frame.setPreferredSize(new Dimension(516, 356));
        frame.pack();

        recordListPanel.setMinimumSize(new Dimension(300, 244));
        recordListPanel.setMaximumSize(new Dimension(300, 244));
        recordListPanel.setPreferredSize(new Dimension(300, 244));
        recordListTableModel = new DefaultTableModel(new String[] {"Sensor ID", "Temp. (Cº)", "Timestamp"}, 0) {
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
        recordCountByIDTableModel = new DefaultTableModel(new String[] {"Sensor ID", "Count"}, 0) {
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
                        recordCountByIDTableModel.addRow(new Object[] {sensorId, 1});
                    }
                    recordListTableModel.addRow(new Object[] {sensorId, temp, timestamp});
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
                        conditionsTableModel.addRow(new Object[] {status, condition});
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
                    extraInfoLabel.setText(label.substring(0, label.length()-2));
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
            updates.put(new Object[] {
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
            updates.put(new Object[] {
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
            updates.put(new Object[] {
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
        }
        else
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
        conditionsTableModel = new DefaultTableModel(new String[] {"Status", "Condition"}, 0);
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
}
