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

package top.theillusivec4.bombindl.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import top.theillusivec4.bombindl.data.json.DownloadTracker;
import top.theillusivec4.bombindl.data.json.Video;
import top.theillusivec4.bombindl.util.BombinDLLogger;
import top.theillusivec4.bombindl.util.Constants;
import top.theillusivec4.bombindl.util.video.VideoUtils;

public class FileManager {

  public static final File ROOT = new File(Constants.ID);
  public static final File EXPORTS = new File(ROOT, "exports");
  public static final File DOWNLOADS = new File(ROOT, "downloads");
  public static final File DATA = new File(ROOT, "data");
  public static final File CACHE = new File(DATA, "cache.json");
  public static final File PREFS = new File(DATA, "preferences.json");
  public static final File DOWNLOAD_TRACKING = new File(DATA, "downloads.json");

  public static void load() {
    BombinDLLogger.log("Loading directories...");
    DOWNLOADS.mkdir();
    DATA.mkdir();
    EXPORTS.mkdir();

    if (!PREFS.exists()) {

      try (Writer writer = Files.newBufferedWriter(PREFS.toPath())) {
        Constants.GSON.toJson(UserPrefs.INSTANCE, writer);
      } catch (IOException e) {
        BombinDLLogger.error("There was an error accessing saved preferences.", e);
      }
    } else {
      UserPrefs.INSTANCE.load();
    }
    BombinDLLogger.log("Finished loading directories.");
  }

  public static WriteResult writeLinks(Collection<Video> videos) {
    BombinDLLogger.log("Saving " + videos.size() + " download link(s)...");
    String fileName =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")) + ".txt";
    Path path = new File(EXPORTS, fileName).toPath();
    int count = 0;

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {

      for (Video video : videos) {
        String url = VideoUtils.getQualityUrl(video, UserPrefs.INSTANCE.getQuality());

        if (url != null) {
          count++;
          url = url + "?api_key=" + UserPrefs.INSTANCE.getApiKey();
          writer.write(url);
          writer.newLine();
        }
      }
    } catch (IOException e) {
      BombinDLLogger.error("There was an error exporting links.", e);
      return null;
    }
    BombinDLLogger.log("Finished saving " + count + " download link(s) to " + path + ".");
    return new WriteResult(fileName, count);
  }

  public static void writeDownloads(List<DownloadTracker> trackers) {
    BombinDLLogger.log("Saving " + trackers.size() + " download(s) in progress...");

    try (BufferedWriter writer = Files.newBufferedWriter(DOWNLOAD_TRACKING.toPath())) {
      Constants.GSON.toJson(trackers, writer);
    } catch (IOException e) {
      BombinDLLogger.error("There was an error saving tracked downloads.", e);
    }
    BombinDLLogger.log("Finished saving downloads in progress.");
  }

  public static List<DownloadTracker> readDownloads() {
    BombinDLLogger.log("Loading saved downloads in progress...");
    List<DownloadTracker> trackers = new ArrayList<>();

    if (DOWNLOAD_TRACKING.exists()) {

      try (BufferedReader reader = Files.newBufferedReader(DOWNLOAD_TRACKING.toPath())) {
        DownloadTracker[] json = Constants.GSON.fromJson(reader, DownloadTracker[].class);
        trackers.addAll(Arrays.asList(json));
      } catch (IOException e) {
        BombinDLLogger.error("There was an error loading saved tracked downloads.", e);
      }
    }
    BombinDLLogger.log("Loaded " + trackers.size() + " saved download(s) in progress.");
    return trackers;
  }

  public static record WriteResult(String file, int count) {

  }
}
