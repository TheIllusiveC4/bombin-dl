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
import java.util.logging.Level;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.Settings;
import top.theillusivec4.bombindown.data.json.Video;

public class GiantBombApi {

  private static final int DELAY = 1500;
  private static final AtomicLong LAST_REQUEST = new AtomicLong(0);

  private static HttpClient client;

  public static void initialize() {
    client = HttpClient.newHttpClient();
  }

  public static void updateAll() {
    fetchLatestShows();
    fetchLatestVideos();
    DataManager.writeLatestUpdates();
  }

  public static void fetchLatestShows() {
    BombinDown.LOGGER.info("Fetching latest Giant Bomb shows...");
    rateLimit();
    try {
      String req = "https://www.giantbomb.com/api/video_shows/?offset=100&format=json&api_key=" +
          Settings.INSTANCE.getApiKey();
      HttpRequest request =
          HttpRequest.newBuilder().uri(new URI(req)).timeout(Duration.of(10, ChronoUnit.SECONDS))
              .GET().build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      DataManager.updateShows(JsonParser.parseString(response.body()));
    } catch (URISyntaxException | IOException | InterruptedException e) {
      BombinDown.LOGGER.log(Level.SEVERE,
          "There was an error fetching the latest Giant Bomb shows.");
      BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    BombinDown.LOGGER.info("Shows finished updating.");
  }

  public static void fetchLatestVideos() {
    BombinDown.LOGGER.info("Fetching latest Giant Bomb videos...");
    rateLimit();
    int count = 0;
    boolean found = false;
    try {
      while (!found && count < 168) {
        String req = "https://www.giantbomb.com/api/videos/?offset=" + (count * 100) +
            "&format=json&api_key=" + Settings.INSTANCE.getApiKey();
        HttpRequest request =
            HttpRequest.newBuilder().uri(new URI(req)).timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        found = DataManager.updateVideos(JsonParser.parseString(response.body()));
        count++;
      }
    } catch (URISyntaxException | IOException | InterruptedException e) {
      BombinDown.LOGGER.log(Level.SEVERE,
          "There was an error fetching the latest Giant Bomb videos.");
      BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    BombinDown.LOGGER.info("Videos finished updating.");
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
        BombinDown.LOGGER.info(
            "Request to Giant Bomb API made within 1.5 seconds after the last request and will be delayed.");
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
        String req = url + "?api_key=" + Settings.INSTANCE.getApiKey();
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
        BombinDown.LOGGER.log(Level.SEVERE,
            "There was an error trying to parse higher quality downloads for video " + vid.guid +
                ".");
        BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }
    }
  }
}
