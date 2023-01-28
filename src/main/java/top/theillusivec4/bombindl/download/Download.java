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

package top.theillusivec4.bombindl.download;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import javax.swing.SwingUtilities;
import top.theillusivec4.bombindl.GiantBombAPI;
import top.theillusivec4.bombindl.data.DataManager;
import top.theillusivec4.bombindl.data.UserPrefs;
import top.theillusivec4.bombindl.data.json.Show;
import top.theillusivec4.bombindl.data.json.Video;
import top.theillusivec4.bombindl.data.json.base.OriginalVideo;
import top.theillusivec4.bombindl.util.BombinDLLogger;
import top.theillusivec4.bombindl.util.Constants;
import top.theillusivec4.bombindl.util.video.VideoUtils;

public class Download implements Runnable {

  private final String url;
  private final String output;
  private final Video video;
  private final DownloadTableModel table;

  private final boolean metadata;
  private final boolean images;

  private long size;
  private long downloaded;
  private String subDirectory;

  private Constants.DownloadStatus status;

  private float speed = 0;

  public Download(DownloadTableModel table, String url, String output, Video video,
                  boolean metadata, boolean images) {
    this(table, url, output, video, metadata, images, Constants.DownloadStatus.QUEUED);
  }

  public Download(DownloadTableModel table, String url, String output, Video video,
                  boolean metadata, boolean images, Constants.DownloadStatus status) {
    this.url = url;
//    this.url = "http://speedtest.tele2.net/1GB.zip";
    this.output = output;
    this.video = video;
    this.size = -1;
    this.downloaded = 0;
    this.table = table;
    this.metadata = metadata;
    this.images = images;

    if (status == Constants.DownloadStatus.DOWNLOADING) {
      this.status = Constants.DownloadStatus.QUEUED;
    } else {
      this.status = status;
    }
    this.subDirectory = "Miscellaneous";
  }

  public boolean isMetadata() {
    return this.metadata;
  }

  public boolean isImages() {
    return this.images;
  }

  public String getUrl() {
    return this.url;
  }

  public Video getVideo() {
    return this.video;
  }

  public String getOutput() {
    return this.output;
  }

  public long getSize() {
    return this.size;
  }

  public float getSpeed() {
    return this.speed;
  }

  public long getDownloaded() {
    return this.downloaded;
  }

  public Constants.DownloadStatus getStatus() {
    return this.status;
  }

  public void cancel() {
    this.status = Constants.DownloadStatus.CANCELLED;
    this.notifyUpdate();
  }

  public void fail() {
    this.status = Constants.DownloadStatus.FAILED;
    this.notifyUpdate();
  }

  public void download() {
    this.status = Constants.DownloadStatus.DOWNLOADING;
    this.notifyUpdate();
  }

  public void queue() {
    this.status = Constants.DownloadStatus.QUEUED;
    this.notifyUpdate();
  }

  public void complete() {
    this.status = Constants.DownloadStatus.COMPLETED;
    this.notifyUpdate();
  }

  private static final int MAX_BUFFER = 1024 * 16;

  public void run() {
    URL url = null;
    BombinDLLogger.log("Starting download for " + this.output + " from " + this.url + "...");
    createDirectoryForShow();
    SwingUtilities.invokeLater(this::download);

    try {
      url = new URL(this.url);
    } catch (MalformedURLException e) {
      BombinDLLogger.error("There was an error forming the download url " + this.url + ".", e);
    }

    if (url == null) {
      SwingUtilities.invokeLater(this::fail);
      return;
    }
    HttpURLConnection connection;

    if (this.metadata) {
      BombinDLLogger.log("Copying metadata for " + this.output + "...");
      File file = new File(UserPrefs.INSTANCE.getDownloadDirectory(),
          this.subDirectory + "/" + this.subDirectory + ".metadata.json");
      Show show = DataManager.getShow(this.video.videoShow);

      if (!file.exists()) {

        if (show != null) {

          try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            Constants.GSON.toJson(show, writer);
          } catch (IOException e) {
            BombinDLLogger.error("There was an error copying metadata for " + show.title + ".");
          }
        }
      }
      String fileName = this.output.substring(0, this.output.lastIndexOf("."));
      file = new File(UserPrefs.INSTANCE.getDownloadDirectory(),
          this.subDirectory + "/" + fileName + ".metadata.json");

      if (!file.exists()) {
        OriginalVideo orig = new OriginalVideo(this.video);

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
          Constants.GSON.toJson(orig, writer);
        } catch (IOException e) {
          BombinDLLogger.error("There was an error copying metadata for " + this.output + ".");
        }
      }
      BombinDLLogger.log("Copied metadata for " + this.output + ".");
    }

    if (this.images) {
      BombinDLLogger.log("Downloading images for " + this.output + "...");
      String fileName = this.output.substring(0, this.output.lastIndexOf("."));

      if (this.video.image != null && this.video.image.originalUrl != null) {
        this.downloadSimple(this.video.image.originalUrl, fileName);
      }
      Show show = DataManager.getShow(this.video.videoShow);

      if (show != null) {
        String showName = VideoUtils.cleanFileName(show.title, "_");

        if (show.logo != null && show.logo.originalUrl != null) {
          this.downloadSimple(show.logo.originalUrl, showName + "_logo");
        }

        if (show.image != null && show.image.originalUrl != null) {
          this.downloadSimple(show.image.originalUrl, showName + "_image");
        }
      }
      BombinDLLogger.log("Downloaded images for " + this.output + ".");
    }
    GiantBombAPI.rateLimit();

    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
    } catch (IOException e) {
      BombinDLLogger.error("There was an error downloading " + this.output + ".", e);
      SwingUtilities.invokeLater(this::fail);
      return;
    }
    long size = connection.getContentLengthLong();

    if (size == -1) {
      SwingUtilities.invokeLater(this::fail);
      return;
    }
    SwingUtilities.invokeLater(() -> {
      this.size = size;
      this.notifyUpdate();
    });

    try {
      connection.connect();

      if (connection.getResponseCode() != 200) {
        SwingUtilities.invokeLater(this::fail);
        return;
      }
    } catch (IOException e) {
      BombinDLLogger.error("There was an error downloading " + this.output + ".", e);
      SwingUtilities.invokeLater(this::fail);
      connection.disconnect();
      return;
    }

    try (InputStream in = connection.getInputStream()) {

      try (FileOutputStream fos = new FileOutputStream(
          new File(UserPrefs.INSTANCE.getDownloadDirectory(),
              this.subDirectory + "/" + this.output))) {

        try (BufferedOutputStream bout = new BufferedOutputStream(fos, MAX_BUFFER)) {
          byte[] data = new byte[MAX_BUFFER];
          SwingUtilities.invokeLater(() -> {
            this.downloaded = 0;
            this.notifyUpdate();
          });
          long totalDownloaded = 0;
          int x;
          long lastSpeedRecording = System.currentTimeMillis();
          long time = System.nanoTime();

          while ((x = in.read(data, 0, MAX_BUFFER)) >= 0) {

            if (Thread.interrupted()) {
              connection.disconnect();
              BombinDLLogger.log("Cancelled download for " + this.output + ".");
              SwingUtilities.invokeLater(this::cancel);
              return;
            }
            totalDownloaded += x;
            bout.write(data, 0, x);

            if ((System.currentTimeMillis() - lastSpeedRecording) / 1000f >= 0.5f) {
              lastSpeedRecording = System.currentTimeMillis();
              long elapsed = System.nanoTime() - time;
              float elapsedSeconds = (float) elapsed / 1000000000f;
              float newSpeed = totalDownloaded / 1024f / elapsedSeconds;
              final long downloaded = totalDownloaded;
              SwingUtilities.invokeLater(() -> {
                this.downloaded = downloaded;
                this.speed = newSpeed;
                this.notifyUpdate();
              });
            }
          }

          if (this.status == Constants.DownloadStatus.DOWNLOADING) {
            BombinDLLogger.log("Completed download for " + this.output + " from " + this.url + ".");
            SwingUtilities.invokeLater(this::complete);
          }
        }
      }
    } catch (IOException e) {
      BombinDLLogger.error("There was an error downloading " + this.output + ".");
      SwingUtilities.invokeLater(this::fail);
    } finally {
      connection.disconnect();
    }
  }

  private void downloadSimple(String url, String name) {
    HttpURLConnection connection = null;
    GiantBombAPI.rateLimit();
    BombinDLLogger.log("Starting nested download for " + url + "...");

    try {
      URL imagesUrl = new URL(url);
      String ext = imagesUrl.toString().substring(imagesUrl.toString().lastIndexOf("."));
      File file =
          new File(UserPrefs.INSTANCE.getDownloadDirectory(), this.subDirectory + "/" + name + ext);

      if (file.exists()) {
        return;
      }
      connection = (HttpURLConnection) imagesUrl.openConnection();
      connection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");

      try (InputStream in = connection.getInputStream()) {

        try (FileOutputStream fos = new FileOutputStream(file)) {

          try (BufferedOutputStream bout = new BufferedOutputStream(fos, MAX_BUFFER)) {
            byte[] data = new byte[MAX_BUFFER];
            int x;

            while ((x = in.read(data, 0, MAX_BUFFER)) >= 0) {
              bout.write(data, 0, x);
            }
          }
        }
      }
    } catch (IOException e) {
      BombinDLLogger.error("There was an error downloading " + this.output + ".", e);
    } finally {

      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private void createDirectoryForShow() {
    Show show = DataManager.getShow(video.videoShow);

    if (show != null) {
      this.subDirectory = VideoUtils.cleanFileName(show.title, "_");
    }
    new File(UserPrefs.INSTANCE.getDownloadDirectory(), this.subDirectory).mkdir();
  }

  public void notifyUpdate() {
    this.table.updateDownload(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Download download = (Download) o;
    return this.url.equals(download.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.url);
  }
}