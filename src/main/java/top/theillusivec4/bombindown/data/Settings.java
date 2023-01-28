package top.theillusivec4.bombindown.data;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.logging.Level;
import top.theillusivec4.bombindown.BombinDown;
import top.theillusivec4.bombindown.util.Constants;
import top.theillusivec4.bombindown.util.Enums;

public class Settings {

  public static Settings INSTANCE = new Settings();

  private String apiKey;
  private boolean premium;
  private Enums.Quality quality;
  private String downloadDirectory;
  private String fileOutputTemplate;
  private boolean includeMetadata;
  private boolean includeImages;
  private int maxDownloads;

  public Settings() {
    resetToDefaults();
  }

  public void load() {

    if (FileManager.SETTINGS.exists()) {

      try (Reader reader = Files.newBufferedReader(FileManager.SETTINGS.toPath())) {
        Settings settings = Constants.GSON.fromJson(reader, Settings.class);
        this.apiKey = settings.apiKey;
        this.quality = settings.quality;
        this.downloadDirectory = settings.downloadDirectory;
        this.premium = settings.premium;
        this.fileOutputTemplate = settings.fileOutputTemplate;
        this.maxDownloads = settings.maxDownloads;
        this.includeImages = settings.includeImages;
        this.includeMetadata = settings.includeMetadata;
      } catch (Exception e) {
        BombinDown.LOGGER.log(Level.SEVERE,
            "There was an error reading preferences. Resetting to defaults...");
        BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        this.resetToDefaults();
        this.save();
      }
    }
  }

  private void resetToDefaults() {
    this.apiKey = "";
    this.quality = Enums.Quality.HD;
    this.downloadDirectory = FileManager.DOWNLOADS.getAbsolutePath();
    this.premium = false;
    this.fileOutputTemplate = "{year}-{month}-{day} - {title}";
    this.maxDownloads = 3;
    this.includeImages = false;
    this.includeMetadata = false;
  }

  public void save() {

    try (Writer writer = Files.newBufferedWriter(FileManager.SETTINGS.toPath())) {

      if (INSTANCE.downloadDirectory.isEmpty()) {
        INSTANCE.downloadDirectory = FileManager.DOWNLOADS.getAbsolutePath();
      }

      if (INSTANCE.fileOutputTemplate.isEmpty()) {
        INSTANCE.fileOutputTemplate = "{year}-{month}-{day} - {title}";
      }
      Constants.GSON.toJson(INSTANCE, writer);
    } catch (IOException e) {
      BombinDown.LOGGER.log(Level.SEVERE, "There was an error saving preferences.");
      BombinDown.LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public Enums.Quality getQuality() {
    return quality;
  }

  public void setQuality(Enums.Quality quality) {
    this.quality = quality;
  }

  public File getDownloadDirectory() {
    return new File(downloadDirectory);
  }

  public void setDownloadDirectory(File downloadDirectory) {
    this.downloadDirectory = downloadDirectory.getAbsolutePath();
  }

  public void setPremium(boolean state) {
    this.premium = state;
  }

  public boolean getPremium() {
    return this.premium;
  }

  public void setFileOutputTemplate(String template) {
    this.fileOutputTemplate = template;
  }

  public String getFileOutputTemplate() {
    return this.fileOutputTemplate;
  }

  public int getMaxDownloads() {
    return this.maxDownloads;
  }

  public void setMaxDownloads(int maxDownloads) {
    this.maxDownloads = maxDownloads;
  }

  public boolean isIncludeMetadata() {
    return this.includeMetadata;
  }

  public void setIncludeMetadata(boolean includeMetadata) {
    this.includeMetadata = includeMetadata;
  }

  public boolean isIncludeImages() {
    return this.includeImages;
  }

  public void setIncludeImages(boolean includeImages) {
    this.includeImages = includeImages;
  }
}
