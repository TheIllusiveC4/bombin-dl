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

package top.theillusivec4.bombindl.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import top.theillusivec4.bombindl.data.UserPrefs;

public class CredentialsDialog extends JDialog {

  private final JFrame parent;

  private JLabel apiLabel;
  private JTextField apiField;
  private JLabel premiumLabel;
  private JCheckBox premiumBox;

  private JButton save;
  private JButton cancel;

  public CredentialsDialog(JFrame parent) {
    super(parent, "Manage Credentials");
    this.parent = parent;
    this.initComponents();
  }

  private void initComponents() {
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setBounds(0, 0, 450, 200);
    this.setLocationRelativeTo(null);
    this.setModal(true);
    this.setLayout(new GridBagLayout());
    ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    this.apiLabel = new JLabel("API Key: ");
    GridBagConstraints gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.apiLabel, gc);

    this.apiField = new JTextField(UserPrefs.INSTANCE.getApiKey(), 40);
    gc = new GridBagConstraints();
    gc.gridx = 2;
    gc.gridwidth = 2;
    gc.anchor = GridBagConstraints.LINE_START;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    this.add(this.apiField, gc);

    this.premiumLabel = new JLabel("Premium: ");
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.premiumLabel, gc);

    this.premiumBox = new JCheckBox();
    this.premiumBox.setSelected(UserPrefs.INSTANCE.getPremium());
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.gridx = 2;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.premiumBox, gc);

    this.save = new JButton("Save");
    this.save.addActionListener(e -> {
      if (this.apiField.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this.parent, "An API Key is required to use this application.",
            "Invalid API Key", JOptionPane.WARNING_MESSAGE);
      } else {
        UserPrefs.INSTANCE.setApiKey(this.apiField.getText());
        UserPrefs.INSTANCE.setPremium(this.premiumBox.isSelected());
        UserPrefs.INSTANCE.save();
        this.dispose();
      }
    });
    gc = new GridBagConstraints();
    gc.gridy = 2;
    gc.gridx = 3;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(20, 0, 10, 0);
    this.add(save, gc);

    this.cancel = new JButton("Cancel");
    this.cancel.addActionListener(e -> {
      if (UserPrefs.INSTANCE.getApiKey().isEmpty()) {
        JOptionPane.showMessageDialog(this.parent, "An API Key is required to use this application.",
            "Invalid API Key", JOptionPane.WARNING_MESSAGE);
      } else {
        this.dispose();
      }
    });
    gc.gridy = 2;
    gc.gridx = 4;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(cancel, gc);
  }
}
