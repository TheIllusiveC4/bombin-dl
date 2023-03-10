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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import top.theillusivec4.bombindl.util.Constants;

public class DownloadTableModel extends DefaultTableModel {

  private final DownloadExecutor executor;
  private final List<Download> downloads;
  private final Map<String, Download> downloadMap;
  private final Map<String, Download> pendingRemoval;

  public DownloadTableModel() {
    super(
        new String[] {"File", "Speed", "Progress", "Total Size", "ETA", "Status"},
        0);
    this.executor = new DownloadExecutor();
    this.downloads = new ArrayList<>();
    this.downloadMap = new HashMap<>();
    this.pendingRemoval = new HashMap<>();
  }

  public void setMaxDownloads(int num) {
    this.executor.setMaxDownloads(num);
  }

  public void addDownload(Download download) {

    if (!downloadMap.containsKey(download.getUrl())) {
      this.downloads.add(0, download);
      this.downloadMap.put(download.getUrl(), download);

      if (download.getStatus() == Constants.DownloadStatus.QUEUED) {
        this.executor.beginDownload(download);
      }
      this.fireTableRowsInserted(0, 0);
    }
  }

  public void addDownloads(List<Download> downloads) {
    int size = 0;

    for (Download download : downloads) {

      if (!downloadMap.containsKey(download.getUrl())) {
        this.downloads.add(0, download);
        this.downloadMap.put(download.getUrl(), download);

        if (download.getStatus() == Constants.DownloadStatus.QUEUED) {
          this.executor.beginDownload(download);
        }
        size++;
      }
    }
    this.fireTableRowsInserted(0, size - 1);
  }

  public Download getDownload(int row) {
    return this.downloads.get(row);
  }

  public Download getDownload(String url) {
    return this.downloadMap.get(url);
  }

  public void beginDownload(Download download) {
    download.queue();
    this.executor.beginDownload(download);
    this.updateDownload(download);
  }

  public void updateDownload(Download download) {
    int row = this.downloads.indexOf(download);
    this.fireTableRowsUpdated(row, row);

    if (this.pendingRemoval.containsKey(download.getUrl()) &&
        download.getStatus() == Constants.DownloadStatus.CANCELLED) {
      Download removed = this.downloads.remove(row);

      if (removed != null) {
        this.downloadMap.remove(removed.getUrl());
        this.fireTableRowsDeleted(row, row);
      }
    }
  }

  public void cancelDownload(Download download) {
    download.cancel();
    this.executor.stopDownload(download);
    this.updateDownload(download);
  }

  public void removeDownload(Download download, int row) {
    this.executor.stopDownload(download);

    if (download.getStatus() == Constants.DownloadStatus.DOWNLOADING) {
      this.pendingRemoval.put(download.getUrl(), download);
    } else {
      Download removed = this.downloads.remove(row);

      if (removed != null) {
        this.downloadMap.remove(removed.getUrl());
        this.fireTableRowsDeleted(row, row);
      }
    }
  }

  public void removeDownload(String url) {
    Download download = null;
    int row = -1;

    for (int i = 0; i < this.downloads.size(); i++) {
      Download current = this.downloads.get(i);

      if (current.getUrl().equals(url)) {
        row = i;
        download = current;
      }
    }

    if (row >= 0) {
      this.removeDownload(download, row);
    }
  }

  @Override
  public int getRowCount() {
    return this.downloads != null ? this.downloads.size() : 0;
  }

  private static final DecimalFormat DF = new DecimalFormat("0.00");

  @Override
  public Object getValueAt(int row, int col) {
    Download download = this.downloads.get(row);

    switch (col) {
      case 0: // Output
        return " " + download.getOutput();
      case 1: // Speed

        if (download.getStatus() == Constants.DownloadStatus.DOWNLOADING) {
          return " " + getFormattedSpeed(download.getSpeed());
        }
        return "";
      case 2: // Progress

        if (download.getStatus() == Constants.DownloadStatus.COMPLETED) {
          return 100.0f;
        }
        long downloaded = download.getDownloaded();
        long total = download.getSize();
        return total == 0 ? 0.0f : ((float) downloaded / total) * 100;
      case 3: // Total Size
        long size = download.getSize();

        if (size == -1) {
          return "";
        }
        return " " + getFormattedSize(size);
      case 4: // Remaining Time
        long downloadSize = download.getSize();

        if (downloadSize == 0L || download.getSpeed() == 0.0F) {
          return "";
        } else if (download.getStatus() == Constants.DownloadStatus.DOWNLOADING) {
          float remaining = (downloadSize - download.getDownloaded()) / 1024f;
          long time = (long) (remaining / download.getSpeed());

          if (time > 362439L) {
            return ">99:99:99";
          }
          return " " + getFormattedTime(time);
        }
        return "";
      case 5: // Status
        return " " + download.getStatus().getText();
    }
    return "";
  }

  private static String getFormattedTime(long time) {
    String s = "";
    s += (String.format("%02d", time / 3600)) + ":";
    time %= 3600;
    s += (String.format("%02d", time / 60)) + ":";
    time %= 60;
    s += String.format("%02d", time);
    return s;
  }

  private static String getFormattedSpeed(float speed) {
    String unit = "KB/s";
    float formattedSpeed = speed;

    if (formattedSpeed >= 1000.0f) {
      formattedSpeed = speed / 1000.0f;
      unit = "MB/s";
    }
    return DF.format(formattedSpeed) + " " + unit;
  }

  private static String getFormattedSize(long size) {
    float result = 0;
    String unit = "B";
    float kilobytes = size / 1024f;

    if (kilobytes >= 1.0f) {
      unit = "KB";
      result = kilobytes;
      float megabytes = kilobytes / 1024f;

      if (megabytes >= 1.0f) {
        unit = "MB";
        result = megabytes;
        float gigabytes = megabytes / 1024f;

        if (gigabytes >= 1.0f) {
          unit = "GB";
          result = gigabytes;
        }
      }
    }
    return DF.format(result) + " " + unit;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnIndex == 2 ? DownloadProgressBar.class : String.class;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }
//
//  public void sortDownloads() {
//    this.downloads.sort((o1, o2) -> {
//      int compareDates =
//          LocalDateTime.parse(o2.getDate()).compareTo(LocalDateTime.parse(o1.getDate()));
//
//      if (compareDates == 0) {
//        return o2.getVideo().compareTo(o1.getVideo());
//      }
//      return compareDates;
//    });
//    this.fireTableRowsUpdated(0, this.downloads.size() - 1);
//  }
}
