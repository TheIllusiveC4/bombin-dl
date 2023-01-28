package top.theillusivec4.bombindown.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {

  public static final String ID = "bombin-down";
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
