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
