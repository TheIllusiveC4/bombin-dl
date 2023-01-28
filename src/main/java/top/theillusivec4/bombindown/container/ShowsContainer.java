package top.theillusivec4.bombindown.container;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class ShowsContainer extends JPanel {

  private final JFrame parent;
  private final Map<String, TreeSet<Video>> shows = new HashMap<>();

  private CheckboxTableModel tableModel;
  private FilteredSearch search;
  private SelectMenu selectMenu;
  private DownloadSelectDialog downloadDialog;

  private JTable searchResults;
  private JScrollPane scrollPane;

  private boolean freeze;

  public ShowsContainer(JFrame parent) {
    super();
    this.parent = parent;
    this.initComponents();
  }

  private void initComponents() {
    this.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    this.tableModel = new CheckboxTableModel("GUID", "Show Title", "Episodes", "Membership");

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
      List<String> showIds = this.selectMenu.getSelected();
      List<String> vidIds = new ArrayList<>();

      for (String showId : showIds) {
        TreeSet<Video> vids = this.shows.get(showId);

        if (vids != null) {

          for (Video vid : vids) {
            vidIds.add(vid.guid);
          }
        }
      }
      this.downloadDialog.setSelected(vidIds);
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
    this.searchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    TableColumnModel columnModel = this.searchResults.getColumnModel();
    TableColumn zeroth = columnModel.getColumn(0);
    zeroth.setMinWidth(30);
    zeroth.setMaxWidth(30);
    TableColumn first = columnModel.getColumn(1);
    first.setMinWidth(30);
    first.setPreferredWidth(60);
    first.setMaxWidth(100);
    TableColumn second = columnModel.getColumn(2);
    second.setMinWidth(100);
    second.setPreferredWidth(200);
    second.setMaxWidth(400);
    TableColumn third = columnModel.getColumn(3);
    third.setMinWidth(20);
    third.setPreferredWidth(80);
    third.setMaxWidth(100);
    TableColumn fourth = columnModel.getColumn(4);
    fourth.setMinWidth(50);
    fourth.setPreferredWidth(100);
    this.searchResults.setFillsViewportHeight(true);
    this.searchResults.setShowGrid(false);
    this.searchResults.setIntercellSpacing(new Dimension(0, 0));
    this.searchResults.setRowHeight(20);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridy = 2;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.weighty = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    this.scrollPane = new JScrollPane(this.searchResults);
    this.add(scrollPane, gc);
    this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }

  private void search(Filter filter, String searchTerm) {
    this.tableModel.setRowCount(0);
    List<Object[]> data = new ArrayList<>();
    this.shows.clear();

    for (Show show : DataManager.getShows()) {

      if (show.title.toLowerCase().contains(searchTerm.toLowerCase()) ||
          show.guid.contains(searchTerm)) {
        List<Video> results = new ArrayList<>();

        for (String video : DataManager.getVideos(show.guid)) {
          Video vid = DataManager.getVideo(video);

          if (vid != null && filter.apply(vid)) {
            results.add(vid);
          }
        }

        if (!results.isEmpty()) {
          this.shows.put(show.guid, new TreeSet<>(results));
          data.add(new Object[] {false, show.guid, show.title, "" + results.size(),
              show.premium ? "Premium" : "Free"});
        }
      }
    }

    if (searchTerm.isEmpty()) {
      List<Video> results = new ArrayList<>();

      for (String video : DataManager.getVideos("")) {
        Video vid = DataManager.getVideo(video);

        if (vid != null && filter.apply(vid)) {
          results.add(vid);
        }
      }

      if (!results.isEmpty()) {
        this.shows.put("", new TreeSet<>(results));
        data.add(new Object[] {false, "", "Miscellaneous", "" + results.size(), ""});
      }
    }
    data.sort((o1, o2) -> {
      Object obj1 = o1[1];
      Object obj2 = o2[1];

      if (obj1 instanceof String first && obj2 instanceof String second) {

        if (first.isEmpty()) {
          return 1;
        } else if (second.isEmpty()) {
          return -1;
        } else {
          int firstNum = Integer.parseInt(first.substring(5));
          int secondNum = Integer.parseInt(second.substring(5));
          return firstNum - secondNum;
        }
      }
      return 0;
    });

    for (Object[] datum : data) {
      this.tableModel.addRow(datum);
    }
  }
}
