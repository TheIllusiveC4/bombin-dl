package top.theillusivec4.bombindown.data;

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
import java.util.logging.Level;
import top.theillusivec4.bombindown.BombinDown;
import top.theillusivec4.bombindown.data.json.DownloadTracker;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.util.Constants;
import top.theillusivec4.bombindown.util.QualityLink;

public class FileManager {

  public static final File ROOT = new File(Constants.ID);
  public static final File EXPORTS = new File(ROOT, "exports");
  public static final File DOWNLOADS = new File(ROOT, "downloads");
  public static final File DATA = new File(ROOT, "data");
  public static final File CACHE = new File(DATA, "cache.json");
  public static final File SETTINGS = new File(DATA, "settings.json");
  public static final File DOWNLOAD_TRACKING = new File(DATA, "downloads.json");

  public static void load() {
    BombinDown.LOGGER.info("Loading directories...");
    ROOT.mkdir();
    DOWNLOADS.mkdir();
    DATA.mkdir();
    EXPORTS.mkdir();

    if (!SETTINGS.exists()) {

      try (Writer writer = Files.newBufferedWriter(SETTINGS.toPath())) {
        Constants.GSON.toJson(Settings.INSTANCE, writer);
      } catch (IOException e) {
        BombinDown.LOGGER.log(Level.SEVERE, "There was an error accessing saved settings.");
        BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }
    } else {
      Settings.INSTANCE.load();
    }
    BombinDown.LOGGER.info("Loading finished.");
  }

  public static WriteResult writeLinks(Collection<Video> videos) {
    String fileName =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")) + ".txt";
    Path path = new File(EXPORTS, fileName).toPath();
    int count = 0;

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {

      for (Video video : videos) {
        String url = QualityLink.get(video, Settings.INSTANCE.getQuality());

        if (url != null) {
          count++;
          url = url + "?api_key=" + Settings.INSTANCE.getApiKey();
          writer.write(url);
          writer.newLine();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return new WriteResult(fileName, count);
  }

  public static void writeDownloads(List<DownloadTracker> trackers) {

    try (BufferedWriter writer = Files.newBufferedWriter(DOWNLOAD_TRACKING.toPath())) {
      Constants.GSON.toJson(trackers, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<DownloadTracker> readDownloads() {
    List<DownloadTracker> trackers = new ArrayList<>();

    if (DOWNLOAD_TRACKING.exists()) {

      try (BufferedReader reader = Files.newBufferedReader(DOWNLOAD_TRACKING.toPath())) {
        DownloadTracker[] json = Constants.GSON.fromJson(reader, DownloadTracker[].class);
        trackers.addAll(Arrays.asList(json));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return trackers;
  }

  public static record WriteResult(String file, int count) {

  }
}
