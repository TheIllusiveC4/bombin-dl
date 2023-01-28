package top.theillusivec4.bombindown.util;

public class Enums {

  public enum Category {
    VIDS("Videos"),
    SHOWS("Shows");

    final String text;

    Category(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    public static Category of(String text) {

      for (Category value : Category.values()) {

        if (value.text.equals(text)) {
          return value;
        }
      }
      return null;
    }
  }

  public enum Quality {
    HD("HD"),
    HIGH("High"),
    LOW("Low");

    final String text;

    Quality(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    public static Quality of(String text) {

      for (Quality value : Quality.values()) {

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
