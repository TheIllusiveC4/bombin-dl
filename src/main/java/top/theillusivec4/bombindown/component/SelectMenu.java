package top.theillusivec4.bombindown.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SelectMenu extends JPanel {

  private final CheckboxTableModel tableModel;

  private JButton selectAll;
  private JButton selectNone;
  private JButton download;

  private boolean freeze;

  public SelectMenu(CheckboxTableModel tableModel) {
    super();
    this.tableModel = tableModel;
    this.initComponents();
  }

  private void initComponents() {
    this.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    this.tableModel.addTableModelListener(e -> {

      if (!freeze && e.getColumn() == 0) {
        int totalSelected = this.getSelected().size();

        if (totalSelected > 0) {
          this.download.setEnabled(true);
          this.download.setText("Download " + "(" + totalSelected + ")");
        } else {
          this.download.setEnabled(false);
          this.download.setText("Download");
        }
      }
    });
    this.selectAll = new JButton("Select All");
    this.selectAll.addActionListener(e -> selectAllRows(true));
    gc = new GridBagConstraints();
    gc.gridx = 0;
    this.add(this.selectAll, gc);

    this.selectNone = new JButton("Select None");
    this.selectNone.addActionListener(e -> selectAllRows(false));
    gc = new GridBagConstraints();
    gc.gridx = 1;
    this.add(this.selectNone, gc);

    gc = new GridBagConstraints();
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    this.add(new JPanel(), gc);

    this.download = new JButton("Download");
    gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LAST_LINE_END;
    gc.gridx = 3;
    this.download.setEnabled(false);
    this.add(this.download, gc);
  }

  public List<String> getSelected() {
    List<String> result = new ArrayList<>();

    for (int i = 0; i < this.tableModel.getRowCount(); i++) {
      boolean checked = (Boolean) this.tableModel.getValueAt(i, 0);

      if (checked) {
        result.add((String) this.tableModel.getValueAt(i, 1));
      }
    }
    return result;
  }

  private void selectAllRows(boolean state) {
    this.freeze = true;

    for (int i = 0; i < this.tableModel.getRowCount(); i++) {
      this.tableModel.setValueAt(state, i, 0);
    }
    this.freeze = false;
    this.tableModel.fireTableCellUpdated(0, 0);
  }

  public JButton getSelectAll() {
    return this.selectAll;
  }

  public JButton getSelectNone() {
    return this.selectNone;
  }

  public JButton getDownload() {
    return this.download;
  }
}
