package top.theillusivec4.bombindown.util;

import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.json.Video;

public class QualityLink {

  public static String get(Video video, Enums.Quality quality) {

    if (quality == Enums.Quality.HD) {

      if (video.uhdUrl != null) {
        return video.uhdUrl;
      }

      if (video.hdUrl != null) {
        return video.hdUrl;
      }

      if (video.highUrl != null) {
        return video.highUrl;
      }

      if (video.lowUrl != null) {
        return video.lowUrl;
      }
    } else if (quality == Enums.Quality.HIGH) {

      if (video.highUrl != null) {
        return video.highUrl;
      }

      if (video.lowUrl != null) {
        return video.lowUrl;
      }
    } else {

      if (video.lowUrl != null) {
        return video.lowUrl;
      }
    }
    return null;
  }
}
