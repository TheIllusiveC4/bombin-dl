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

package top.theillusivec4.bombindl.container;

import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import top.theillusivec4.bombindl.component.CredentialsDialog;
import top.theillusivec4.bombindl.component.PreferencesDialog;

public class MenuBar extends JMenuBar {

  private final JFrame parent;

  private JMenu menu;
  private JMenuItem menuItem;

  private JDialog apiDialog;
  private JDialog prefsDialog;

  public MenuBar(JFrame parent) {
    super();
    this.parent = parent;
    this.initComponents();
  }

  private void initComponents() {
    this.menu = new JMenu("Settings");
    this.menu.setMnemonic(KeyEvent.VK_A);
    this.menu.getAccessibleContext().setAccessibleDescription("Global settings for the application.");

    this.menuItem = new JMenuItem("Manage Credentials...", KeyEvent.VK_T);
    this.menuItem.getAccessibleContext().setAccessibleDescription("Manage the application's Giant Bomb credentials.");
    this.apiDialog = new CredentialsDialog(this.parent);
    this.menuItem.addActionListener(e -> this.apiDialog.setVisible(true));
    this.menu.add(this.menuItem);

    this.menuItem = new JMenuItem("Preferences...", KeyEvent.VK_C);
    this.menuItem.getAccessibleContext().setAccessibleDescription("Manage the application's user-level preferences.");
    this.prefsDialog = new PreferencesDialog(this.parent);
    this.menuItem.addActionListener(e -> this.prefsDialog.setVisible(true));
    this.menu.add(this.menuItem);

    this.add(menu);
  }
}
