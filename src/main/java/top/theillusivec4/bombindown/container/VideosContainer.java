package top.theillusivec4.bombindown.container;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import top.theillusivec4.bombindown.component.CheckboxTableModel;
import top.theillusivec4.bombindown.component.DownloadSelectDialog;
import top.theillusivec4.bombindown.component.FilteredSearch;
import top.theillusivec4.bombindown.component.SelectMenu;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.json.Show;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.util.video.Filter;

public class VideosContainer extends JPanel {

  private final JFrame parent;

  private CheckboxTableModel tableModel;
  private FilteredSearch search;
  private SelectMenu selectMenu;
  private DownloadSelectDialog downloadDialog;

  private JTable searchResults;
  private JScrollPane scrollPane;

  private boolean freeze;

  public VideosContainer(JFrame parent) {
    super();
    this.parent = parent;
    this.initComponents();
  }

  private void initComponents() {
    this.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    this.tableModel =
        new CheckboxTableModel("GUID", "Video Title", "Show Title", "Published", "Membership");
    this.search = new FilteredSearch(this.parent);
    this.search.addSearchListener(this::search);
    this.search(new Filter(), "");
    gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.search, gc);

    this.selectMenu = new SelectMenu(this.tableModel);
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.selectMenu, gc);

    this.downloadDialog = new DownloadSelectDialog(this.parent);
    this.selectMenu.getDownload().addActionListener(e -> {
      this.downloadDialog.setSelected(this.selectMenu.getSelected());
      this.downloadDialog.setVisible(true);
    });
    this.downloadDialog.addDisposeListener(() -> this.selectMenu.getSelectNone().doClick());

    this.searchResults = new JTable(this.tableModel);
    this.searchResults.setRowSelectionAllowed(true);
    this.searchResults.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    this.tableModel.addTableModelListener(e -> {
      if (!this.freeze && e.getColumn() == 0 && this.searchResults.getSelectedRows().length > 1) {
        this.freeze = true;
        boolean val = (Boolean) this.tableModel.getValueAt(e.getFirstRow(), 0);

        for (int i = 0; i < this.searchResults.getSelectedRows().length; i++) {
          this.tableModel.setValueAt(val, this.searchResults.getSelectedRows()[i], 0);
        }
        this.freeze = false;
      }
    });
    TableColumnModel columnModel = this.searchResults.getColumnModel();
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
    this.searchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    this.searchResults.setFillsViewportHeight(true);
    this.searchResults.setShowGrid(false);
    this.searchResults.setIntercellSpacing(new Dimension(0, 0));
    this.searchResults.setRowHeight(20);
    this.scrollPane = new JScrollPane(this.searchResults);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridy = 2;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.weighty = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    this.add(this.scrollPane, gc);
    this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }

  private void search(Filter filter, String searchTerm) {
    this.tableModel.setRowCount(0);
    Collection<Video> videos = DataManager.getVideos();
    TreeSet<Video> sortedVideos = new TreeSet<>(videos);

    for (Video video : sortedVideos) {

      if (video.name.toLowerCase().contains(searchTerm.toLowerCase()) ||
          video.guid.contains(searchTerm)) {
        Show show = DataManager.getShow(video.videoShow);
        String showName = "";

        if (show != null) {
          showName = show.title;
        }

        if (filter.apply(video)) {
          this.tableModel.addRow(
              new Object[] {false, video.guid, video.name, showName, video.publishDate,
                  video.premium ? "Premium" : "Free"});
        }
      }
    }
  }
}
