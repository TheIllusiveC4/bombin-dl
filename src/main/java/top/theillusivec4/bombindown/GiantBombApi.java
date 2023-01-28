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

package top.theillusivec4.bombindown;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.UserPrefs;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.util.BombinDownLogger;

public class GiantBombApi {

  private static final int DELAY = 1500;
  private static final AtomicLong LAST_REQUEST = new AtomicLong(0);

  private static HttpClient client;

  public static void init() {
    client = HttpClient.newHttpClient();
  }

  public static void updateAll() {
    fetchLatestShows();
    fetchLatestVideos();
    DataManager.writeLatestUpdates();
  }

  public static void fetchLatestShows() {
    BombinDownLogger.log("Fetching latest Giant Bomb shows...");
    rateLimit();
    try {
      String req = "https://www.giantbomb.com/api/video_shows/?offset=100&format=json&api_key=" +
          UserPrefs.INSTANCE.getApiKey();
      HttpRequest request =
          HttpRequest.newBuilder().uri(new URI(req)).timeout(Duration.of(10, ChronoUnit.SECONDS))
              .GET().build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      DataManager.updateShows(JsonParser.parseString(response.body()));
    } catch (URISyntaxException | IOException | InterruptedException e) {
      BombinDownLogger.error("There was an error fetching the latest Giant Bomb shows.", e);
    }
    BombinDownLogger.log("Finished updating shows.");
  }

  public static void fetchLatestVideos() {
    BombinDownLogger.log("Fetching latest Giant Bomb videos...");
    rateLimit();
    int count = 0;
    boolean found = false;
    try {
      while (!found && count < 168) {
        String req = "https://www.giantbomb.com/api/videos/?offset=" + (count * 100) +
            "&format=json&api_key=" + UserPrefs.INSTANCE.getApiKey();
        HttpRequest request =
            HttpRequest.newBuilder().uri(new URI(req)).timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        found = DataManager.updateVideos(JsonParser.parseString(response.body()));
        count++;
      }
    } catch (URISyntaxException | IOException | InterruptedException e) {
      BombinDownLogger.error("There was an error fetching the latest Giant Bomb videos.", e);
    }
    BombinDownLogger.log("Finished updating videos.");
  }

  public static void rateLimit() {

    if (LAST_REQUEST.get() == 0) {
      LAST_REQUEST.set(System.currentTimeMillis());
      return;
    }
    boolean msg = false;

    while (System.currentTimeMillis() - LAST_REQUEST.get() < DELAY) {

      if (!msg) {
        msg = true;
        BombinDownLogger.warn("Waiting for rate limiter...");
      }
    }
    LAST_REQUEST.set(System.currentTimeMillis());
  }

  public static void findBestQuality(Video vid) {

    if (vid.uhdUrl == null && vid.hdUrl != null) {
      try {
        String url = vid.hdUrl.replace("h5000k", "h8000k");
        url = url.replace("_4000.", "_8000.");
        url = url.replace("_5000.", "_8000.");
        String req = url + "?api_key=" + UserPrefs.INSTANCE.getApiKey();
        URL net = new URL(req);
        HttpURLConnection connection;
        GiantBombApi.rateLimit();
        connection = (HttpURLConnection) net.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");

        if (connection.getResponseCode() != 404) {
          vid.uhdUrl = url;
        }
        connection.disconnect();
      } catch (IOException e) {
        BombinDownLogger.error(
            "There was an error trying to parse higher quality downloads for video " + vid.guid +
                ".", e);
      }
    }
  }
}
