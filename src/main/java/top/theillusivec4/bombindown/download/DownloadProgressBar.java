package top.theillusivec4.bombindown.download;

import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DownloadProgressBar extends JProgressBar implements TableCellRenderer {

  public DownloadProgressBar(int min, int max) {
    super(min, max);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
    this.setValue((int) ((Float) value).floatValue());
    return this;
  }
}
