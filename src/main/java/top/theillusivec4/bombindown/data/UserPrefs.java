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

package top.theillusivec4.bombindown.data;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Objects;
import top.theillusivec4.bombindown.util.BombinDownLogger;
import top.theillusivec4.bombindown.util.Constants;

public class UserPrefs {

  public static UserPrefs INSTANCE = new UserPrefs();

  private String apiKey;
  private boolean premium;
  private Constants.VideoQuality videoQuality;
  private String downloadDirectory;
  private String fileOutputTemplate;
  private boolean includeMetadata;
  private boolean includeImages;
  private int maxDownloads;

  public UserPrefs() {
    this.apiKey = "";
    this.videoQuality = Constants.VideoQuality.HD;
    this.downloadDirectory = FileManager.DOWNLOADS.getAbsolutePath();
    this.premium = false;
    this.fileOutputTemplate = "{year}-{month}-{day} - {title}";
    this.maxDownloads = 3;
    this.includeImages = false;
    this.includeMetadata = false;
  }

  public void load() {

    if (FileManager.PREFS.exists()) {
      BombinDownLogger.log("Loading previously saved preferences...");

      try (Reader reader = Files.newBufferedReader(FileManager.PREFS.toPath())) {
        UserPrefs userPrefs = Constants.GSON.fromJson(reader, UserPrefs.class);
        this.apiKey = Objects.requireNonNullElse(userPrefs.apiKey, "");
        this.videoQuality =
            Objects.requireNonNullElse(userPrefs.videoQuality, Constants.VideoQuality.HD);
        this.downloadDirectory = Objects.requireNonNullElseGet(userPrefs.downloadDirectory,
            FileManager.DOWNLOADS::getAbsolutePath);
        this.premium = userPrefs.premium;
        this.fileOutputTemplate = Objects.requireNonNullElse(userPrefs.fileOutputTemplate,
            "{year}-{month}-{day} - {title}");
        this.maxDownloads = userPrefs.maxDownloads == 0 ? 3 : userPrefs.maxDownloads;
        this.includeImages = userPrefs.includeImages;
        this.includeMetadata = userPrefs.includeMetadata;
      } catch (Exception e) {
        BombinDownLogger.error("There was an error reading preferences.", e);
      }
      BombinDownLogger.log("Finished loading previously saved preferences.");
    }
  }

  public void save() {
    BombinDownLogger.log("Saving preferences...");

    try (Writer writer = Files.newBufferedWriter(FileManager.PREFS.toPath())) {

      if (INSTANCE.downloadDirectory.isEmpty()) {
        INSTANCE.downloadDirectory = FileManager.DOWNLOADS.getAbsolutePath();
      }

      if (INSTANCE.fileOutputTemplate.isEmpty()) {
        INSTANCE.fileOutputTemplate = "{year}-{month}-{day} - {title}";
      }
      Constants.GSON.toJson(INSTANCE, writer);
    } catch (IOException e) {
      BombinDownLogger.error("There was an error saving preferences.", e);
    }
    BombinDownLogger.log("Finished saving preferences.");
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public Constants.VideoQuality getQuality() {
    return videoQuality;
  }

  public void setQuality(Constants.VideoQuality videoQuality) {
    this.videoQuality = videoQuality;
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
