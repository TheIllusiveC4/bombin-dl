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

package top.theillusivec4.bombindl;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import top.theillusivec4.bombindl.component.CredentialsDialog;
import top.theillusivec4.bombindl.container.DownloadContainer;
import top.theillusivec4.bombindl.container.MenuBar;
import top.theillusivec4.bombindl.container.ShowsContainer;
import top.theillusivec4.bombindl.container.VideosContainer;
import top.theillusivec4.bombindl.data.DataManager;
import top.theillusivec4.bombindl.data.FileManager;
import top.theillusivec4.bombindl.data.UserPrefs;
import top.theillusivec4.bombindl.data.json.Video;
import top.theillusivec4.bombindl.util.BombinDLLogger;

public class BombinDL {

  private static DownloadContainer downloadContainer;
  private static ShowsContainer showsContainer;
  private static VideosContainer videosContainer;

  private static JTabbedPane mainPane;

  public static void main(String[] args) {
    BombinDLLogger.load();
    FileManager.load();
    DataManager.load();
    GiantBombAPI.init();
    SwingUtilities.invokeLater(BombinDL::initGui);
  }

  public static int addDownloads(TreeSet<Video> videos) {
    return downloadContainer.add(videos);
  }

  private static void initGui() {
    BombinDLLogger.log("Starting application...");
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      BombinDLLogger.warn("Failed to set GUI appearance.", e);
    }

    if (UserPrefs.INSTANCE.getApiKey().isEmpty()) {
      JFrame frame = new JFrame();
      JDialog apiDialog = new CredentialsDialog(frame);
      apiDialog.setModal(true);
      apiDialog.setVisible(true);

      if (UserPrefs.INSTANCE.getApiKey().isEmpty()) {
        frame.dispose();
      } else {
        createAndShowGUI();
      }
    } else {
      createAndShowGUI();
    }
  }

  private static void createAndShowGUI() {
    BombinDLLogger.log("Setting up GUI...");
    JFrame frame = new JFrame("Bombin' DL");
    frame.setJMenuBar(new MenuBar(frame));
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
    mainPane = new JTabbedPane();
    mainPane.setPreferredSize(new Dimension(800, 800));
    frame.add(mainPane);
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
          GiantBombAPI.updateAll();
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
            addTabs(frame);
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
      BombinDLLogger.log(
          "Cached updates were last timestamped within 24 hours, skipping latest update check...");
      addTabs(frame);
    }
    BombinDLLogger.log("Finished loading GUI.");
  }

  private static void addTabs(JFrame frame) {
    mainPane.addTab("Shows", showsContainer = new ShowsContainer(frame));
    mainPane.addTab("Videos", videosContainer = new VideosContainer(frame));
    mainPane.addTab("Downloads", downloadContainer = new DownloadContainer(frame));
  }

  public static void setMaxDownloads(int max) {

    if (downloadContainer != null) {
      downloadContainer.setMaxDownloads(max);
    }
  }
}
