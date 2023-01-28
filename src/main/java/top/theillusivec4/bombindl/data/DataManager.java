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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import top.theillusivec4.bombindl.GiantBombAPI;
import top.theillusivec4.bombindl.data.json.Cache;
import top.theillusivec4.bombindl.data.json.Show;
import top.theillusivec4.bombindl.data.json.Video;
import top.theillusivec4.bombindl.data.json.base.OriginalVideo;
import top.theillusivec4.bombindl.util.BDLogger;
import top.theillusivec4.bombindl.util.Constants;

public class DataManager {

  private static final Map<String, Show> SHOWS = new ConcurrentHashMap<>();
  private static final Map<String, Video> VIDEOS = new ConcurrentHashMap<>();
  private static final Map<String, List<String>> SHOWS_TO_VIDEOS = new ConcurrentHashMap<>();

  private static final Map<String, Show> NEWEST_SHOWS = new LinkedHashMap<>();
  private static final Map<String, Video> NEWEST_VIDS = new LinkedHashMap<>();

  private static String lastShowRecorded = "";
  private static String lastVideoRecorded = "";

  private static ZonedDateTime lastUpdate =
      ZonedDateTime.now(ZoneId.systemDefault()).minusYears(10);

  public static ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public static void load() {
    BDLogger.log("Loading seed data...");
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (InputStream is = classloader.getResourceAsStream("seed_shows.json")) {

      if (is != null) {
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
          Show[] shows = Constants.GSON.fromJson(reader, Show[].class);

          for (Show show : shows) {
            SHOWS.put(show.guid, show);
            SHOWS_TO_VIDEOS.put(show.guid, new CopyOnWriteArrayList<>());
          }
          SHOWS_TO_VIDEOS.put("", new CopyOnWriteArrayList<>());
          lastShowRecorded = shows[shows.length - 1].guid;
        } catch (IOException e) {
          BDLogger.error("There was an error loading show data.", e);
        }
      }
    } catch (IOException e) {
      BDLogger.error("There was an error reading show data.", e);
    }

    try (InputStream is = classloader.getResourceAsStream("seed_videos.json")) {

      if (is != null) {
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
          Video[] videos = Constants.GSON.fromJson(reader, Video[].class);
          Map<String, List<String>> shows = new HashMap<>();

          for (Video video : videos) {
            VIDEOS.put(video.guid, video);
            String showName = video.videoShow;

            if (showName == null) {
              showName = "";
            }
            shows.computeIfAbsent(showName, (k) -> new ArrayList<>()).add(video.guid);
          }
          for (Map.Entry<String, List<String>> entry : SHOWS_TO_VIDEOS.entrySet()) {
            entry.getValue().addAll(shows.getOrDefault(entry.getKey(), new ArrayList<>()));
          }
          lastVideoRecorded = videos[0].guid;
        } catch (IOException e) {
          BDLogger.error("There was an error loading video data.", e);
        }
      }
    } catch (IOException e) {
      BDLogger.error("There was an error reading video data.", e);
    }

    if (FileManager.CACHE.exists()) {

      try (Reader reader = Files.newBufferedReader(FileManager.CACHE.toPath())) {
        Cache cache = Constants.GSON.fromJson(reader, Cache.class);
        lastUpdate = ZonedDateTime.parse(cache.timestamp(),
            DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss").withZone(ZoneId.systemDefault()));
        Show[] shows = cache.shows();

        for (Show show : shows) {
          SHOWS.put(show.guid, show);
          NEWEST_SHOWS.put(show.guid, show);
          SHOWS_TO_VIDEOS.put(show.guid, new CopyOnWriteArrayList<>());
        }

        if (shows.length > 0) {
          lastShowRecorded = shows[shows.length - 1].guid;
        }
        Video[] vids = cache.videos();

        for (Video vid : vids) {
          VIDEOS.put(vid.guid, vid);
          NEWEST_VIDS.put(vid.guid, vid);
          SHOWS_TO_VIDEOS.computeIfAbsent(vid.videoShow == null ? "" : vid.videoShow,
              (k) -> new ArrayList<>()).add(vid.guid);
        }

        if (vids.length > 0) {
          lastVideoRecorded = vids[0].guid;
        }
      } catch (Exception e) {
        BDLogger.error("There was an error reading new shows and videos.", e);
      }
    }
    BDLogger.log("Finished loading seed data.");
  }

  public static Collection<Show> getShows() {
    return SHOWS.values();
  }

  public static Show getShow(String id) {

    if (id == null) {
      id = "";
    }
    return SHOWS.get(id);
  }

  public static Collection<Video> getVideos() {
    return VIDEOS.values();
  }

  public static Video getVideo(String id) {
    return VIDEOS.get(id);
  }

  public static List<String> getVideos(String show) {
    return SHOWS_TO_VIDEOS.getOrDefault(show, new ArrayList<>());
  }

  public static void updateShows(JsonElement jsonElement) {
    JsonObject obj = jsonElement.getAsJsonObject();
    JsonArray results = obj.getAsJsonArray("results");
    Show[] shows = Constants.GSON.fromJson(results, Show[].class);
    List<Show> showsToAdd = new ArrayList<>();

    for (int i = shows.length - 1; i >= 0; i--) {
      Show show = shows[i];

      if (lastShowRecorded.equals(show.guid)) {
        break;
      }
      showsToAdd.add(show);
    }

    for (Show show : showsToAdd) {
      NEWEST_SHOWS.put(show.guid, show);
      SHOWS.put(show.guid, show);
    }
    BDLogger.log("Found " + showsToAdd.size() + " new shows.");
  }

  public static boolean updateVideos(JsonElement jsonElement) {
    JsonObject obj = jsonElement.getAsJsonObject();
    JsonArray results = obj.getAsJsonArray("results");
    OriginalVideo[] videos = Constants.GSON.fromJson(results, OriginalVideo[].class);
    List<Video> videosToAdd = new ArrayList<>();
    boolean found = false;

    for (OriginalVideo originalVideo : videos) {

      if (lastVideoRecorded.equals(originalVideo.guid)) {
        found = true;
        break;
      }
      Video vid = new Video(originalVideo);
      GiantBombAPI.findBestQuality(vid);
      videosToAdd.add(vid);
    }

    for (Video video : videosToAdd) {
      VIDEOS.put(video.guid, video);
      String showGuid = video.videoShow;

      if (showGuid == null) {
        showGuid = "";
      }
      NEWEST_VIDS.put(video.guid, video);
      SHOWS_TO_VIDEOS.computeIfAbsent(showGuid, (k) -> new ArrayList<>()).add(video.guid);
    }
    BDLogger.log("Found " + videosToAdd.size() + " new videos.");
    return found;
  }

  public static void writeLatestUpdates() {
    BDLogger.log("Saving updates...");
    Show[] shows = new Show[] {};
    Video[] videos = new Video[] {};
    String timestamp = ZonedDateTime.now(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss"));

    if (!NEWEST_SHOWS.isEmpty()) {
      List<Show> result = new ArrayList<>();

      for (Map.Entry<String, Show> entry : NEWEST_SHOWS.entrySet()) {
        result.add(entry.getValue());
      }
      shows = result.toArray(new Show[0]);
    }

    if (!NEWEST_VIDS.isEmpty()) {
      List<Video> result = new ArrayList<>();

      for (Map.Entry<String, Video> entry : NEWEST_VIDS.entrySet()) {
        result.add(entry.getValue());
      }
      videos = result.toArray(new Video[0]);
    }

    try (Writer writer = Files.newBufferedWriter(FileManager.CACHE.toPath())) {
      Constants.GSON.toJson(new Cache(timestamp, shows, videos), writer);
    } catch (IOException e) {
      BDLogger.error("There was an error while saving latest updates.", e);
    }
    BDLogger.log("Finished saving updates.");
  }
}
