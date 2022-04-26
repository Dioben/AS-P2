package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GUI extends Thread {
    private final BlockingQueue<Object[]> updates;
    private final ArrayList<String> conditions;
    private int totalRecordCount = 0;
    private final DefaultTableModel recordListTableModel;
    private final DefaultTableModel recordCountByIDTableModel;

    private JFrame frame;
    private JPanel mainPanel;
    private JLabel totalRecordCountLabel;
    private JScrollPane recordListPanel;
    private JScrollPane recordCountByIDPanel;
    private JTable recordListTable;
    private JTable recordCountByIDTable;
    private JLabel conditionsLabel;

    public GUI(String title) {
        updates = new LinkedBlockingQueue<>();
        conditions = new ArrayList<>();

        frame = new JFrame(title);
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(516, 352));
        frame.setPreferredSize(new Dimension(516, 352));
        frame.pack();

        recordListPanel.setMinimumSize(new Dimension(300, 240));
        recordListPanel.setMaximumSize(new Dimension(300, 240));
        recordListPanel.setPreferredSize(new Dimension(300, 240));
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

        recordCountByIDPanel.setMinimumSize(new Dimension(190, 240));
        recordCountByIDPanel.setMaximumSize(new Dimension(190, 240));
        recordCountByIDPanel.setPreferredSize(new Dimension(190, 240));
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
                if (update.length == 3) {
                    int sensorId = (int) update[0];
                    double temp = (double) update[1];
                    long timestamp = (long) update[2];
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
                } else {
                    String condition = (String) update[0];
                    conditions.add(condition);
                    StringBuilder label;
                    if (condition.length() == 1)
                        label = new StringBuilder("Failed condition: ");
                    else
                        label = new StringBuilder("Failed conditions: ");
                    Iterator<String> iterator = conditions.iterator();
                    while (iterator.hasNext()) {
                        label.append(iterator.next()).append(", ");
                    }
                    conditionsLabel.setText(label.substring(0, label.length()-2));
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addRecord(int sensorId, double temp, long timestamp) {
        try {
            updates.put(new Object[] {
                    sensorId,
                    temp,
                    timestamp
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addCondition(String condition) {
        try {
            updates.put(new Object[] {
                    condition
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
