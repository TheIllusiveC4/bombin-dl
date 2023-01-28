/*
 * Copyright (C) 2023 C4
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.bombindown.component;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class CheckboxTableModel extends DefaultTableModel {

  public CheckboxTableModel(String... columns) {
    super(createColumns(columns), 0);
  }

  private static String[] createColumns(String... columns) {
    String[] cols = new String[columns.length + 1];
    cols[0] = "";
    System.arraycopy(columns, 0, cols, 1, columns.length);
    return cols;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnIndex == 0 ? Boolean.class : String.class;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 0;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {

    if (value instanceof Boolean bool && column == 0) {
      Vector rowData = getDataVector().get(row);
      rowData.set(0, bool);
      fireTableCellUpdated(row, column);
    }
  }
}
