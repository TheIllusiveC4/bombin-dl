package top.theillusivec4.bombindown.container;

import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import top.theillusivec4.bombindown.component.CredentialsDialog;
import top.theillusivec4.bombindown.component.PreferencesDialog;

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
