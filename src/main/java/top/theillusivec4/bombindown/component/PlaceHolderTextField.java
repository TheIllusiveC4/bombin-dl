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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextField;

public class PlaceHolderTextField extends JTextField {

  private final String ph;

  public PlaceHolderTextField(String ph, int columns) {
    super(columns);
    this.ph = ph;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (super.getText().length() > 0 || this.ph == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(super.getDisabledTextColor());
    g2.drawString(this.ph, this.getInsets().left,
        g.getFontMetrics().getMaxAscent() + this.getInsets().top);
  }
}
