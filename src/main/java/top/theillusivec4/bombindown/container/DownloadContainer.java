package top.theillusivec4.bombindown.container;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import top.theillusivec4.bombindown.component.DownloadSelectDialog;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.FileManager;
import top.theillusivec4.bombindown.data.UserPrefs;
import top.theillusivec4.bombindown.data.json.DownloadTracker;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.download.Download;
import top.theillusivec4.bombindown.download.DownloadProgressBar;
import top.theillusivec4.bombindown.download.DownloadTableModel;
import top.theillusivec4.bombindown.util.BombinDownLogger;
import top.theillusivec4.bombindown.util.Constants;
import top.theillusivec4.bombindown.util.video.VideoUtils;

public class DownloadContainer extends JPanel {

  private final JFrame parent;

  private DownloadTableModel tableModel;

  private JButton importButton;
  private JButton restartButton;
  private JButton cancelButton;
  private JButton clearButton;
  private JButton clearAllButton;

  private List<Download> selectedDownloads;

  public DownloadContainer(JFrame parent) {
    super();
    this.parent = parent;
    this.selectedDownloads = new ArrayList<>();
    this.initComponents();
  }

  public void setMaxDownloads(int num) {
    this.tableModel.setMaxDownloads(num);
  }

  private void initComponents() {
    this.tableModel = new DownloadTableModel();
    this.loadToTable();

    this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    this.setLayout(new GridBagLayout());
    GridBagConstraints gc;

    JPanel panel = new JPanel();

    this.importButton = new JButton("Import from GUID File");
    this.importButton.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
      chooser.setFileFilter(filter);
      chooser.setDialogTitle("Select the file to import");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setAcceptAllFileFilterUsed(false);

      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        loadFileToTable(chooser.getSelectedFile());
      }
    });
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_START;
    panel.add(this.importButton, gc);

    this.restartButton = new JButton("Restart");
    this.restartButton.setEnabled(false);
    this.restartButton.addActionListener(e -> {

      if (this.selectedDownloads.size() == 1) {
        Download download = this.selectedDownloads.get(0);
        this.tableModel.beginDownload(download);
        this.cancelButton.setEnabled(true);
        this.restartButton.setEnabled(false);
      }
    });
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_START;
    panel.add(this.restartButton, gc);

    this.cancelButton = new JButton("Cancel");
    this.cancelButton.setEnabled(false);
    this.cancelButton.addActionListener(e -> {

      if (this.selectedDownloads.size() == 1) {
        Download download = this.selectedDownloads.get(0);
        this.tableModel.cancelDownload(download);
        this.restartButton.setEnabled(true);
        this.cancelButton.setEnabled(false);
      }
    });
    panel.add(this.cancelButton, gc);

    this.clearButton = new JButton("Clear");
    this.clearButton.setEnabled(false);
    this.clearButton.addActionListener(e -> {
      List<Download> downloads = new ArrayList<>(this.selectedDownloads);

      for (Download selectedDownload : downloads) {
        this.tableModel.removeDownload(selectedDownload.getUrl());
      }
    });
    panel.add(this.clearButton, gc);

    this.clearAllButton = new JButton("Clear Completed");
    this.clearAllButton.addActionListener(e -> {
      List<Download> downloads = new ArrayList<>();

      for (int i = 0; i < this.tableModel.getRowCount(); i++) {

        if (this.tableModel.getValueAt(i, 5)
            .equals(" " + Constants.DownloadStatus.COMPLETED.getText())) {
          downloads.add(this.tableModel.getDownload(i));
        }
      }

      for (Download download : downloads) {
        this.tableModel.removeDownload(download.getUrl());
      }
    });
    panel.add(this.clearAllButton, gc);

    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 0;
    gc.gridy = 0;
    gc.weightx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(panel, gc);

    JTable table = new JTable(this.tableModel);
    table.setRowSelectionAllowed(true);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.getSelectionModel().addListSelectionListener(e -> {

      if (!e.getValueIsAdjusting()) {
        this.selectedDownloads.clear();

        for (int i = 0; i < table.getSelectedRows().length; i++) {
          this.selectedDownloads.add(this.tableModel.getDownload(table.getSelectedRows()[i]));
        }
      }

      if (this.selectedDownloads.isEmpty()) {
        this.restartButton.setEnabled(false);
        this.cancelButton.setEnabled(false);
        this.clearButton.setEnabled(false);
      } else {

        if (this.selectedDownloads.size() == 1) {
          Download download = this.selectedDownloads.get(0);
          this.restartButton.setEnabled(
              download.getStatus() == Constants.DownloadStatus.CANCELLED ||
                  download.getStatus() == Constants.DownloadStatus.FAILED);
          this.cancelButton.setEnabled(
              download.getStatus() == Constants.DownloadStatus.DOWNLOADING ||
                  download.getStatus() == Constants.DownloadStatus.QUEUED);
        } else {
          this.restartButton.setEnabled(false);
          this.cancelButton.setEnabled(false);
        }
        this.clearButton.setEnabled(true);
      }
    });
    DownloadProgressBar renderer = new DownloadProgressBar(0, 100);
    renderer.setStringPainted(true);
    table.setDefaultRenderer(JProgressBar.class, renderer);
    TableColumnModel columnModel = table.getColumnModel();
    TableColumn first = columnModel.getColumn(0);
    first.setMinWidth(100);
    first.setPreferredWidth(200);
    first.setMaxWidth(400);
    TableColumn second = columnModel.getColumn(1);
    second.setMinWidth(80);
    second.setMaxWidth(80);
    TableColumn third = columnModel.getColumn(2);
    third.setMinWidth(80);
    third.setPreferredWidth(100);
    third.setMaxWidth(200);
    TableColumn fourth = columnModel.getColumn(3);
    fourth.setMinWidth(80);
    fourth.setMaxWidth(80);
    TableColumn fifth = columnModel.getColumn(4);
    fifth.setMinWidth(70);
    fifth.setMaxWidth(70);
    TableColumn sixth = columnModel.getColumn(5);
    sixth.setMinWidth(70);
    sixth.setPreferredWidth(100);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    table.setFillsViewportHeight(true);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setRowHeight(20);
    JScrollPane scrollPane = new JScrollPane(table);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridy = 1;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.weighty = 1;
    gc.insets = new Insets(10, 0, 0, 0);
    this.add(scrollPane, gc);
  }

  private void loadFileToTable(File selectedFile) {

    try (BufferedReader reader = Files.newBufferedReader(selectedFile.toPath())) {
      List<String> guids = new ArrayList<>();

      while (reader.ready()) {
        String guid = reader.readLine();

        if (DataManager.getVideo(guid) != null) {
          guids.add(guid);
        }
      }
      DownloadSelectDialog dialog = new DownloadSelectDialog(this.parent);
      dialog.setSelected(guids);
      dialog.setVisible(true);
    } catch (IOException e) {
      BombinDownLogger.error(
          "There was an error loading the file " + selectedFile + " into downloads.", e);
    }
  }

  private void loadToTable() {
    List<DownloadTracker> trackers = FileManager.readDownloads();

    if (!trackers.isEmpty()) {

      for (DownloadTracker tracker : trackers) {
        Video vid = DataManager.getVideo(tracker.video());

        if (vid != null) {
          this.tableModel.addDownload(
              new Download(this.tableModel, tracker.url(), tracker.output(), vid,
                  tracker.metadata(), tracker.images(),
                  Constants.DownloadStatus.of(tracker.status())));
        } else {
          JOptionPane.showMessageDialog(this, "Saved download tracking for " + tracker.url() +
                  " could not find a valid video entry and failed loading.", "Malformed Download",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  public int add(TreeSet<Video> videos) {
    List<Download> downloads = new ArrayList<>();

    for (Video video : videos) {
      String url = VideoUtils.getQualityUrl(video, UserPrefs.INSTANCE.getQuality());

      if (url != null) {
        String output =
            UserPrefs.INSTANCE.getFileOutputTemplate() + url.substring(url.lastIndexOf("."));
        output = output.replace("{guid}", video.guid);
        output = output.replace("{title}", video.name);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
          Date date = dateFormat.parse(video.publishDate);
          Calendar calendar = new GregorianCalendar();
          calendar.setTime(date);
          output = output.replace("{year}", "" + calendar.get(Calendar.YEAR));
          output = output.replace("{month}",
              "" + String.format("%02d", calendar.get(Calendar.MONTH) + 1));
          output = output.replace("{day}",
              "" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        } catch (ParseException e) {
          BombinDownLogger.error("There was an error parsing date " + video.publishDate + ".", e);
        }
        output = VideoUtils.cleanFileName(output, "_");
        downloads.add(
            new Download(this.tableModel, url + "?api_key=" + UserPrefs.INSTANCE.getApiKey(),
                output,
                video, UserPrefs.INSTANCE.isIncludeMetadata(),
                UserPrefs.INSTANCE.isIncludeImages()));
      } else {
        JOptionPane.showMessageDialog(this, video.name +
                " could not be downloaded because there is no valid URL at your chosen quality level or lower. Please try a higher quality level.",
            "No URL Found", JOptionPane.ERROR_MESSAGE);
      }
    }
    BombinDownLogger.log("Queued " + downloads.size() + " downloads.");
    this.tableModel.addDownloads(downloads);
    return downloads.size();
  }

  public void save() {
    List<DownloadTracker> trackers = new ArrayList<>();

    for (int i = 0; i < this.tableModel.getRowCount(); i++) {
      Download download = this.tableModel.getDownload(i);

      if (download != null) {
        trackers.add(
            new DownloadTracker(download.getUrl(), download.getVideo().guid, download.getOutput(),
                download.getStatus().getText(), download.isMetadata(), download.isImages()));
      }
    }
    FileManager.writeDownloads(trackers);
  }
}
