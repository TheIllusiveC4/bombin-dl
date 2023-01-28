package top.theillusivec4.bombindown;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import top.theillusivec4.bombindown.component.CheckboxTableModel;
import top.theillusivec4.bombindown.component.DownloadSelectDialog;
import top.theillusivec4.bombindown.component.FilteredSearch;
import top.theillusivec4.bombindown.component.SelectMenu;
import top.theillusivec4.bombindown.container.DownloadContainer;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.FileManager;
import top.theillusivec4.bombindown.data.Settings;
import top.theillusivec4.bombindown.data.json.Show;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.util.Enums;
import top.theillusivec4.bombindown.util.Filter;

public class BombinDown {

  private static final Map<String, TreeSet<Video>> SHOWS = new HashMap<>();

  public static DownloadContainer downloadContainer;

  public static void main(String[] args) {
    FileHandler fh;

    try {
      fh = new FileHandler("bombin-down/data/latest.log");
      LOGGER.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);
    } catch (SecurityException | IOException e) {
      e.printStackTrace();
    }
    LOGGER.info("Successfully initialized logger.");
    FileManager.load();
    DataManager.load();
    GiantBombApi.initialize();
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        BombinDown.LOGGER.log(Level.SEVERE, "There was an error creating the GUI.");
        BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }

      if (Settings.INSTANCE.getApiKey().isEmpty()) {
        JFrame frame = new JFrame();
        JDialog apiDialog = createAPIKeyDialog(frame);
        apiDialog.setModal(true);
        apiDialog.setVisible(true);

        if (Settings.INSTANCE.getApiKey().isEmpty()) {
          frame.dispose();
        } else {
          createAndShowGUI();
        }
      } else {
        createAndShowGUI();
      }
    });
  }

  private static void createAndShowGUI() {
    BombinDown.LOGGER.info("Setting up GUI...");
    JFrame frame = new JFrame("Bombin' Down");
    frame.setJMenuBar(createMenuBar(frame));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {

        if (downloadContainer != null) {
          downloadContainer.save();
        }
      }

      @Override
      public void windowOpened(WindowEvent e) {
        super.windowOpened(e);
      }
    });
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setPreferredSize(new Dimension(800, 800));
    frame.add(tabbedPane);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    boolean checkUpdates = DataManager.getLastUpdate()
        .isBefore(ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1));

    if (checkUpdates) {
      JDialog dialog = new JDialog(frame, "Updating");
      dialog.setLayout(new GridLayout(2, 1));
      JLabel updating = new JLabel("Grabbing the latest shows and videos from Giant Bomb...");
      updating.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
      JLabel please = new JLabel(
          "Please be patient, this could take a while if the application hasn't been run before or it's been a long time.");
      please.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
      dialog.add(updating);
      dialog.add(please);
      SwingWorker<Void, Void> fetchUpdates = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
          GiantBombApi.updateAll();
//        try {
//          Thread.sleep(5000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
          return null;
        }

        @Override
        protected void done() {
          SwingUtilities.invokeLater(() -> {
            dialog.dispose();
            tabbedPane.addTab("Shows", makeBrowsePanel(frame));
            tabbedPane.addTab("Videos", makeSearchPanel(frame));
            downloadContainer = new DownloadContainer(frame);
            tabbedPane.addTab("Downloads", downloadContainer);
          });
        }
      };
      fetchUpdates.execute();
      dialog.setModal(true);
      dialog.pack();
      dialog.setLocationRelativeTo(null);
      dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      dialog.setVisible(true);
    } else {
      BombinDown.LOGGER.info(
          "Cached updates were last timestamped within 24 hours, skipping latest update check...");
      tabbedPane.addTab("Shows", makeBrowsePanel(frame));
      tabbedPane.addTab("Videos", makeSearchPanel(frame));
      downloadContainer = new DownloadContainer(frame);
      tabbedPane.addTab("Downloads", downloadContainer);
    }
    BombinDown.LOGGER.info("Loading finished.");
  }

  private static JPanel makeBrowsePanel(JFrame frame) {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    CheckboxTableModel tableModel =
        new CheckboxTableModel("GUID", "Show Title", "Episodes", "Membership");

    FilteredSearch search = new FilteredSearch(frame);
    search.addSearchListener((filter, s) -> searchShows(tableModel, filter, s));
    gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LINE_START;
    panel.add(search, gc);

    SelectMenu selectMenu = new SelectMenu(tableModel);
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_START;
    panel.add(selectMenu, gc);

    DownloadSelectDialog downloadDialog = new DownloadSelectDialog(frame);
    selectMenu.getDownload().addActionListener(e -> {
      List<String> showIds = selectMenu.getSelected();
      List<String> vidIds = new ArrayList<>();

      for (String showId : showIds) {
        TreeSet<Video> vids = SHOWS.get(showId);

        if (vids != null) {

          for (Video vid : vids) {
            vidIds.add(vid.guid);
          }
        }
      }
      downloadDialog.setSelected(vidIds);
      downloadDialog.setVisible(true);
    });
    downloadDialog.addDisposeListener(() -> selectMenu.getSelectNone().doClick());

    searchShows(tableModel, new Filter(), "");

    JTable table = new JTable(tableModel);
    tableModel.addTableModelListener(e -> {
      if (!freeze && e.getColumn() == 0 && table.getSelectedRows().length > 1) {
        freeze = true;
        boolean val = (Boolean) tableModel.getValueAt(e.getFirstRow(), 0);

        for (int i = 0; i < table.getSelectedRows().length; i++) {
          tableModel.setValueAt(val, table.getSelectedRows()[i], 0);
        }
        freeze = false;
      }
    });
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    TableColumnModel columnModel = table.getColumnModel();
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
    table.setFillsViewportHeight(true);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setRowHeight(20);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridy = 2;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.weighty = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane, gc);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    return panel;
  }

  private static void searchShows(CheckboxTableModel tableModel, Filter filter, String searchTerm) {
    tableModel.setRowCount(0);
    List<Object[]> data = new ArrayList<>();
    SHOWS.clear();

    for (Show show : DataManager.getShows()) {

      if (show.title.toLowerCase().contains(searchTerm.toLowerCase())) {
        List<Video> results = new ArrayList<>();

        for (String video : DataManager.getVideos(show.guid)) {
          Video vid = DataManager.getVideo(video);

          if (vid != null && filter.apply(vid)) {
            results.add(vid);
          }
        }

        if (!results.isEmpty()) {
          SHOWS.put(show.guid, new TreeSet<>(results));
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
        SHOWS.put("", new TreeSet<>(results));
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
      tableModel.addRow(datum);
    }
  }

  private static void search(DefaultTableModel tableModel, Filter filter, String searchTerm) {
    tableModel.setRowCount(0);
    Collection<Video> videos = DataManager.getVideos();
    TreeSet<Video> sortedVideos = new TreeSet<>(videos);

    for (Video video : sortedVideos) {

      if (video.name.toLowerCase().contains(searchTerm.toLowerCase())) {
        Show show = DataManager.getShow(video.videoShow);
        String showName = "";

        if (show != null) {
          showName = show.title;
        }

        if (filter.apply(video)) {
          tableModel.addRow(
              new Object[] {false, video.guid, video.name, showName, video.publishDate,
                  video.premium ? "Premium" : "Free"});
        }
      }
    }
  }

  private static boolean freeze;

  private static JPanel makeSearchPanel(JFrame frame) {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    CheckboxTableModel tableModel =
        new CheckboxTableModel("GUID", "Video Title", "Show Title", "Published", "Membership");

    FilteredSearch search = new FilteredSearch(frame);
    search.addSearchListener((filter, s) -> search(tableModel, filter, s));
    search(tableModel, new Filter(), "");
    gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LINE_START;
    panel.add(search, gc);

    SelectMenu selectMenu = new SelectMenu(tableModel);
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_START;
    panel.add(selectMenu, gc);

    DownloadSelectDialog downloadDialog = new DownloadSelectDialog(frame);
    selectMenu.getDownload().addActionListener(e -> {
      downloadDialog.setSelected(selectMenu.getSelected());
      downloadDialog.setVisible(true);
    });
    downloadDialog.addDisposeListener(() -> selectMenu.getSelectNone().doClick());

    JTable searchResults = new JTable(tableModel);
    searchResults.setRowSelectionAllowed(true);
    searchResults.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tableModel.addTableModelListener(e -> {
      if (!freeze && e.getColumn() == 0 && searchResults.getSelectedRows().length > 1) {
        freeze = true;
        boolean val = (Boolean) tableModel.getValueAt(e.getFirstRow(), 0);

        for (int i = 0; i < searchResults.getSelectedRows().length; i++) {
          tableModel.setValueAt(val, searchResults.getSelectedRows()[i], 0);
        }
        freeze = false;
      }
    });
    TableColumnModel columnModel = searchResults.getColumnModel();
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
    searchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    searchResults.setFillsViewportHeight(true);
    searchResults.setShowGrid(false);
    searchResults.setIntercellSpacing(new Dimension(0, 0));
    searchResults.setRowHeight(20);
    JScrollPane scrollPane = new JScrollPane(searchResults);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridy = 2;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.weighty = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    panel.add(scrollPane, gc);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    return panel;
  }

  private static JMenuBar createMenuBar(final JFrame frame) {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu;
    JMenuItem menuItem;

    menu = new JMenu("Settings");
    menu.setMnemonic(KeyEvent.VK_A);
    menu.getAccessibleContext().setAccessibleDescription("Global settings for the application.");

    menuItem = new JMenuItem("Manage Credentials...", KeyEvent.VK_T);
    menuItem.getAccessibleContext()
        .setAccessibleDescription("Manage the application's Giant Bomb credentials.");
    final JDialog apiDialog = createAPIKeyDialog(frame);
    menuItem.addActionListener(e -> apiDialog.setVisible(true));
    menu.add(menuItem);

    menuItem = new JMenuItem("Preferences...", KeyEvent.VK_C);
    menuItem.getAccessibleContext()
        .setAccessibleDescription("Manage the application's user-level preferences.");
    final JDialog prefDialog = createPreferencesDialog(frame);
    menuItem.addActionListener(e -> prefDialog.setVisible(true));
    menu.add(menuItem);

    menuBar.add(menu);
    return menuBar;
  }

  private static JDialog createPreferencesDialog(final JFrame frame) {
    final JDialog dialog = new JDialog(frame, "Preferences");
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setModal(true);
    dialog.setLayout(new GridBagLayout());
    ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    GridBagConstraints gc = new GridBagConstraints();
    JLabel qualityLabel = new JLabel("Download quality: ");
    qualityLabel.getAccessibleContext()
        .setAccessibleDescription("The quality to download when not specified.");
    JComboBox<String> qualityBox = new JComboBox<>(
        Arrays.stream(Enums.Quality.values()).map(Enums.Quality::getText).toArray(String[]::new));
    qualityBox.setSelectedItem(Settings.INSTANCE.getQuality().getText());
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(qualityLabel, gc);
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(qualityBox, gc);

    JLabel directoryLabel = new JLabel("Download to: ");
    directoryLabel.getAccessibleContext()
        .setAccessibleDescription("The default directory to download files to when not specified.");
    File directory = Settings.INSTANCE.getDownloadDirectory();
    JTextField directoryField = new JTextField(directory.toString(), 30);
    JButton directoryButton = new JButton("...");
    directoryButton.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(directory);
      chooser.setDialogTitle("Select the download directory");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.setAcceptAllFileFilterUsed(false);

      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(directoryLabel, gc);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridy = 1;
    gc.gridx = 1;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(directoryField, gc);
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.gridx = 4;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(directoryButton, gc);

    JLabel formatDescription = new JLabel(
        "<html>Possible template fields: <br>" +
            " {guid} - The unique ID of the file<br>" +
            " {title} - The title of the file<br>" +
            " {year} - The year the file was published<br>" +
            " {month} - The month the file was published<br>" +
            " {day} - The day of the month the file was published</html>");
    formatDescription.setFont(formatDescription.getFont().deriveFont(Font.ITALIC));
    gc = new GridBagConstraints();
    gc.gridy = 2;
    gc.gridx = 1;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(10, 0, 10, 0);
    dialog.add(formatDescription, gc);
    JLabel formatLabel = new JLabel("File output name template: ");
    gc = new GridBagConstraints();
    gc.gridy = 3;
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(formatLabel, gc);
    JTextField formatField = new JTextField();
    formatField.setText(Settings.INSTANCE.getFileOutputTemplate());
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 1;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(formatField, gc);

    JLabel metadataLabel = new JLabel("Include metadata: ");
    metadataLabel.getAccessibleContext()
        .setAccessibleDescription(
            "Check to download available metadata along with videos and shows.");
    JCheckBox metadata = new JCheckBox();
    metadata.setSelected(Settings.INSTANCE.isIncludeMetadata());
    gc = new GridBagConstraints();
    gc.gridy = 4;
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(metadataLabel, gc);
    gc = new GridBagConstraints();
    gc.gridy = 4;
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(metadata, gc);

    JLabel imagesLabel = new JLabel("Include images: ");
    imagesLabel.getAccessibleContext()
        .setAccessibleDescription(
            "Check to download thumbnails and logos along with videos and shows.");
    JCheckBox images = new JCheckBox();
    images.setSelected(Settings.INSTANCE.isIncludeMetadata());
    gc = new GridBagConstraints();
    gc.gridy = 5;
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(imagesLabel, gc);
    gc = new GridBagConstraints();
    gc.gridy = 5;
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(images, gc);

    JLabel spinnerLabel = new JLabel("Maximum simultaneous downloads: ");
    gc = new GridBagConstraints();
    gc.gridy = 6;
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(spinnerLabel, gc);
    SpinnerNumberModel model =
        new SpinnerNumberModel(Settings.INSTANCE.getMaxDownloads(), 1, 20, 1);
    JSpinner spinner = new JSpinner(model);
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.gridy = 6;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(spinner, gc);

    JButton save = new JButton("Save");
    JButton cancel = new JButton("Cancel");
    save.addActionListener(e -> {
      Settings.INSTANCE.setFileOutputTemplate(formatField.getText());
      Settings.INSTANCE.setQuality(Enums.Quality.of((String) qualityBox.getSelectedItem()));
      Settings.INSTANCE.setDownloadDirectory(new File(directoryField.getText()));
      int max = model.getNumber().intValue();
      Settings.INSTANCE.setMaxDownloads(max);

      if (downloadContainer != null) {
        downloadContainer.setMaxDownloads(max);
      }
      Settings.INSTANCE.setIncludeImages(images.isSelected());
      Settings.INSTANCE.setIncludeMetadata(metadata.isSelected());
      Settings.INSTANCE.save();
      dialog.dispose();
    });
    cancel.addActionListener(e -> dialog.dispose());
    gc = new GridBagConstraints();
    gc.gridy = 8;
    gc.gridx = 3;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(20, 0, 10, 0);
    dialog.add(save, gc);
    gc.gridx = 5;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(cancel, gc);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    return dialog;
  }

  private static JDialog createAPIKeyDialog(final JFrame frame) {
    final JDialog dialog = new JDialog(frame, "Manage Credentials");
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setBounds(0, 0, 450, 200);
    dialog.setLocationRelativeTo(null);
    dialog.setModal(true);
    dialog.setLayout(new GridBagLayout());
    ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
    JLabel label = new JLabel("API Key: ");
    JTextField field = new JTextField(Settings.INSTANCE.getApiKey(), 40);
    GridBagConstraints gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(label, gc);
    gc = new GridBagConstraints();
    gc.gridx = 2;
    gc.gridwidth = 2;
    gc.anchor = GridBagConstraints.LINE_START;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    dialog.add(field, gc);
    JLabel premLabel = new JLabel("Premium: ");
    JCheckBox checkBox = new JCheckBox();
    checkBox.setSelected(Settings.INSTANCE.getPremium());
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    dialog.add(premLabel, gc);
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.gridx = 2;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(checkBox, gc);
    JButton save = new JButton("Save");
    JButton cancel = new JButton("Cancel");
    save.addActionListener(e -> {
      if (field.getText().isEmpty()) {
        JOptionPane.showMessageDialog(frame, "An API Key is required to use this application.",
            "Invalid API Key", JOptionPane.WARNING_MESSAGE);
      } else {
        Settings.INSTANCE.setApiKey(field.getText());
        Settings.INSTANCE.setPremium(checkBox.isSelected());
        Settings.INSTANCE.save();
        dialog.dispose();
      }
    });
    cancel.addActionListener(e -> {
      if (Settings.INSTANCE.getApiKey().isEmpty()) {
        JOptionPane.showMessageDialog(frame, "An API Key is required to use this application.",
            "Invalid API Key", JOptionPane.WARNING_MESSAGE);
      } else {
        dialog.dispose();
      }
    });
    gc = new GridBagConstraints();
    gc.gridy = 2;
    gc.gridx = 3;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(20, 0, 10, 0);
    dialog.add(save, gc);
    gc.gridy = 2;
    gc.gridx = 4;
    gc.anchor = GridBagConstraints.LINE_START;
    dialog.add(cancel, gc);
    return dialog;
  }
}
