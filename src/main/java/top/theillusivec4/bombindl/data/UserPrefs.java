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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Objects;
import top.theillusivec4.bombindl.util.BDLogger;
import top.theillusivec4.bombindl.util.Constants;

public class UserPrefs {

  public static UserPrefs INSTANCE = new UserPrefs();

  private String apiKey;
  private boolean premium;
  private Constants.VideoQuality videoQuality;
  private String downloadDirectory;
  private String fileOutputTemplate;
  private String showFallback;
  private String freeLabel;
  private String premiumLabel;
  private boolean replaceSpaces;
  private String removeCharacters;
  private boolean includeMetadata;
  private boolean includeImages;
  private int maxDownloads;

  public UserPrefs() {
    this.apiKey = "";
    this.videoQuality = Constants.VideoQuality.HD;
    this.downloadDirectory = FileManager.DOWNLOADS.getAbsolutePath();
    this.premium = false;
    this.fileOutputTemplate = "{year}-{month}-{day} - {title}";
    this.showFallback = "{year}-{month}-{day} - {title}";
    this.freeLabel = "";
    this.premiumLabel = " (Premium)";
    this.replaceSpaces = false;
    this.removeCharacters = "";
    this.maxDownloads = 3;
    this.includeImages = false;
    this.includeMetadata = false;
  }

  public void load() {

    if (FileManager.PREFS.exists()) {
      BDLogger.log("Loading previously saved preferences...");

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
        this.showFallback = Objects.requireNonNullElse(userPrefs.showFallback,
            "{year}-{month}-{day} - {title}");
        this.freeLabel = Objects.requireNonNullElse(userPrefs.freeLabel, "");
        this.premiumLabel = Objects.requireNonNullElse(userPrefs.premiumLabel, " (Premium)");
        this.replaceSpaces = userPrefs.replaceSpaces;
        this.removeCharacters = Objects.requireNonNullElse(this.removeCharacters, "");
        this.maxDownloads = userPrefs.maxDownloads == 0 ? 3 : userPrefs.maxDownloads;
        this.includeImages = userPrefs.includeImages;
        this.includeMetadata = userPrefs.includeMetadata;
      } catch (Exception e) {
        BDLogger.error("There was an error reading preferences.", e);
      }
      BDLogger.log("Finished loading previously saved preferences.");
    }
  }

  public void save() {
    BDLogger.log("Saving preferences...");

    try (Writer writer = Files.newBufferedWriter(FileManager.PREFS.toPath())) {

      if (INSTANCE.downloadDirectory.isEmpty()) {
        INSTANCE.downloadDirectory = FileManager.DOWNLOADS.getAbsolutePath();
      }

      if (INSTANCE.fileOutputTemplate.isEmpty()) {
        INSTANCE.fileOutputTemplate = "{year}-{month}-{day} - {title}";
      }

      if (INSTANCE.showFallback.isEmpty()) {
        INSTANCE.showFallback = "{year}-{month}-{day} - {title}";
      }
      Constants.GSON.toJson(INSTANCE, writer);
    } catch (IOException e) {
      BDLogger.error("There was an error saving preferences.", e);
    }
    BDLogger.log("Finished saving preferences.");
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

  public String getShowFallback() {
    return showFallback;
  }

  public void setShowFallback(String showFallback) {
    this.showFallback = showFallback;
  }

  public boolean isReplaceSpaces() {
    return replaceSpaces;
  }

  public void setReplaceSpaces(boolean replaceSpaces) {
    this.replaceSpaces = replaceSpaces;
  }

  public String getRemoveCharacters() {
    return removeCharacters;
  }

  public void setRemoveCharacters(String removeCharacters) {
    this.removeCharacters = removeCharacters;
  }

  public String getFreeLabel() {
    return freeLabel;
  }

  public void setFreeLabel(String freeLabel) {
    this.freeLabel = freeLabel;
  }

  public String getPremiumLabel() {
    return premiumLabel;
  }

  public void setPremiumLabel(String premiumLabel) {
    this.premiumLabel = premiumLabel;
  }
}
