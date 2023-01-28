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

package top.theillusivec4.bombindl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {

  public static final String ID = "bombin-dl";
  public static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  public enum VideoQuality {
    HD("HD"),
    HIGH("High"),
    LOW("Low");

    final String text;

    VideoQuality(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    public static VideoQuality of(String text) {

      for (VideoQuality value : VideoQuality.values()) {

        if (value.text.equals(text)) {
          return value;
        }
      }
      return null;
    }
  }

  public enum DownloadStatus {
    QUEUED("Queued"),
    DOWNLOADING("Downloading"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    FAILED("Failed");

    final String text;

    DownloadStatus(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    public static DownloadStatus of(String text) {

      for (DownloadStatus value : DownloadStatus.values()) {

        if (value.text.equals(text)) {
          return value;
        }
      }
      return null;
    }
  }
}
