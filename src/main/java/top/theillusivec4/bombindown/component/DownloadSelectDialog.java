package top.theillusivec4.bombindown.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import top.theillusivec4.bombindown.BombinDown;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.FileManager;
import top.theillusivec4.bombindown.data.Settings;
import top.theillusivec4.bombindown.data.json.Show;
import top.theillusivec4.bombindown.data.json.Video;

public class DownloadSelectDialog extends JDialog {

  private final TreeSet<Video> videos;
  private final List<Runnable> listeners;

  private JPanel main;
  private JButton selectAll;
  private JButton selectNone;
  private JButton download;
  private JButton export;
  private CheckboxTableModel tableModel;
  private JTable table;
  private boolean freeze;

  public DownloadSelectDialog(JFrame parent) {
    super(parent, "Confirm Download");
    this.videos = new TreeSet<>();
    this.listeners = new ArrayList<>();
    this.initComponents();
  }

  private void initComponents() {
    this.main = new JPanel();
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setBounds(0, 0, 800, 600);
    this.setLocationRelativeTo(null);
    this.setModal(true);
    this.main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    this.main.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    this.tableModel =
        new CheckboxTableModel("GUID", "Video Title", "Show Title", "Published", "Membership");

    this.table = new JTable(this.tableModel);
    this.table.setRowSelectionAllowed(true);
    this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    this.tableModel.addTableModelListener(e -> {
      if (!this.freeze && e.getColumn() == 0 && this.table.getSelectedRows().length > 1) {
        this.freeze = true;
        boolean val = (Boolean) this.tableModel.getValueAt(e.getFirstRow(), 0);

        for (int i = 0; i < this.table.getSelectedRows().length; i++) {
          this.tableModel.setValueAt(val, this.table.getSelectedRows()[i], 0);
        }
        this.freeze = false;
      }
    });
    TableColumnModel columnModel = this.table.getColumnModel();
    TableColumn zeroth = columnModel.getColumn(0);
    zeroth.setMinWidth(30);
    zeroth.setMaxWidth(30);
    TableColumn first = columnModel.getColumn(1);
    first.setMinWidth(50);
    first.setPreferredWidth(80);
    first.setMaxWidth(120);
    TableColumn second = columnModel.getColumn(2);
    second.setMinWidth(150);
    second.setPreferredWidth(250);
    second.setMaxWidth(450);
    TableColumn third = columnModel.getColumn(3);
    third.setMinWidth(100);
    third.setPreferredWidth(200);
    third.setMaxWidth(400);
    TableColumn fourth = columnModel.getColumn(4);
    fourth.setMinWidth(150);
    fourth.setPreferredWidth(250);
    fourth.setMaxWidth(450);
    TableColumn fifth = columnModel.getColumn(5);
    fifth.setMinWidth(50);
    fifth.setPreferredWidth(100);
    this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    this.table.setFillsViewportHeight(true);
    this.table.setShowGrid(false);
    this.table.setIntercellSpacing(new Dimension(0, 0));
    this.table.setRowHeight(20);
    JScrollPane scrollPane = new JScrollPane(this.table);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridy = 0;
    gc.gridwidth = 5;
    gc.weightx = 1;
    gc.weighty = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    this.main.add(scrollPane, gc);

    this.selectAll = new JButton("Select All");
    this.selectAll.addActionListener(e -> selectAllRows(true));
    gc = new GridBagConstraints();
    gc.gridx = 0;
    gc.gridy = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    this.main.add(this.selectAll, gc);

    this.selectNone = new JButton("Select None");
    this.selectNone.addActionListener(e -> selectAllRows(false));
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.gridy = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    this.main.add(this.selectNone, gc);

    gc = new GridBagConstraints();
    gc.gridx = 2;
    gc.gridy = 1;
    gc.weightx = 1;
    this.main.add(new JPanel(), gc);

    this.download = new JButton("Begin Download");
    this.download.addActionListener(e -> {
      TreeSet<Video> videos = this.getSelected();

      if (videos.isEmpty()) {
        return;
      }

      if (BombinDown.downloadContainer.add(videos) > 0) {
        JOptionPane.showMessageDialog(this, "Queued " + videos.size() + " videos for download.");
        this.fireDisposeListeners();
        this.dispose();
      }
    });
    gc = new GridBagConstraints();
    gc.gridx = 3;
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(10, 0, 0, 0);
    this.main.add(this.download, gc);

    this.export = new JButton("Export to TXT");
    this.export.addActionListener(e -> {
      TreeSet<Video> videos = this.getSelected();

      if (videos.isEmpty()) {
        return;
      }
      FileManager.WriteResult result = FileManager.writeLinks(videos);

      if (result != null) {
        JOptionPane.showMessageDialog(this.rootPane,
            "Exported " + result.count() + " download links to bombin-down/exports/" +
                result.file());
      } else {
        JOptionPane.showMessageDialog(this.rootPane,
            "There was an unexpected error during the export process.");
      }
      this.fireDisposeListeners();
      this.dispose();
    });
    gc = new GridBagConstraints();
    gc.gridx = 4;
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(10, 0, 0, 0);
    this.main.add(this.export, gc);

    this.add(main);
  }

  private void fireDisposeListeners() {

    for (Runnable listener : this.listeners) {
      listener.run();
    }
  }

  public TreeSet<Video> getSelected() {
    TreeSet<Video> result = new TreeSet<>();
    List<String> ids = new ArrayList<>();

    for (int i = 0; i < this.tableModel.getRowCount(); i++) {
      boolean checked = (Boolean) this.tableModel.getValueAt(i, 0);

      if (checked) {
        ids.add((String) this.tableModel.getValueAt(i, 1));
      }
    }
    boolean premium = false;

    for (String s : ids) {
      Video video = DataManager.getVideo(s);

      if (video != null) {

        if (video.premium) {
          premium = true;
        }
        result.add(video);
      }
    }

    if (premium && !Settings.INSTANCE.getPremium()) {
      Object[] options = {"Confirm", "Cancel"};
      int option = JOptionPane.showOptionDialog(this,
          "You have selected one or more premium videos despite not enabling Premium in Manage API Credentials. Please note that if you are not a premium member, the downloads will fail. Proceed anyway?",
          "Premium Mismatch", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
          options, options[0]);

      if (option == 1) {
        return new TreeSet<>();
      }
    }
    return result;
  }

  public void addDisposeListener(Runnable listener) {
    this.listeners.add(listener);
  }

  private void selectAllRows(boolean state) {
    this.freeze = true;

    for (int i = 0; i < this.tableModel.getRowCount(); i++) {
      this.tableModel.setValueAt(state, i, 0);
    }
    this.freeze = false;
    this.tableModel.fireTableCellUpdated(0, 0);
  }

  public void setSelected(List<String> selected) {
    this.videos.clear();
    this.tableModel.setRowCount(0);

    for (String s : selected) {
      Video video = DataManager.getVideo(s);

      if (video != null) {
        this.videos.add(video);
      }
    }

    for (Video video : this.videos) {
      Show show = DataManager.getShow(video.videoShow);
      String showName = "";

      if (show != null) {
        showName = show.title;
      }
      this.tableModel.addRow(
          new Object[] {true, video.guid, video.name, showName, video.publishDate,
              video.premium ? "Premium" : "Free"});
    }
  }
}
