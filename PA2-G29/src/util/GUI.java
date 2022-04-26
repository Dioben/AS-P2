package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

public class GUI {
    private int totalRecordCount = 0;
    private final DefaultTableModel recordListTableModel;
    private final DefaultTableModel recordCountByIDTableModel;

    private JPanel mainPanel;
    private JLabel totalRecordCountLabel;
    private JScrollPane recordListPanel;
    private JScrollPane recordCountByIDPanel;
    private JTable recordListTable;
    private JTable recordCountByIDTable;

    public GUI(String title) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(516, 326));
        frame.setPreferredSize(new Dimension(516, 326));
        frame.pack();
        frame.setVisible(true);

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
        TableRowSorter<DefaultTableModel> recordListTableSorter = new TableRowSorter<>(recordListTableModel);
        recordListTable.setRowSorter(recordListTableSorter);
        ArrayList<RowSorter.SortKey> recordListSortKeys = new ArrayList<>();
        recordListSortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        recordListTableSorter.setSortKeys(recordListSortKeys);

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
        TableRowSorter<DefaultTableModel> recordCountByIDTableSorter = new TableRowSorter<>(recordCountByIDTableModel);
        recordCountByIDTable.setRowSorter(recordCountByIDTableSorter);
        ArrayList<RowSorter.SortKey> recordCountByIDSortKeys = new ArrayList<>();
        recordCountByIDSortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        recordCountByIDTableSorter.setSortKeys(recordCountByIDSortKeys);
        recordCountByIDTableSorter.setSortsOnUpdates(true);
    }

    public void addRecord(int sensorId, double temp, long timestamp) {
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
