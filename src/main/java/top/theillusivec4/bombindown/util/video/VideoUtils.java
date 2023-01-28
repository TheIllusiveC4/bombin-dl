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

package top.theillusivec4.bombindown.util.video;

import java.util.Arrays;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.util.Constants;

public class VideoUtils {

  private static final int[] ILLEGAL_CHARS =
      {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
          21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

  static {
    Arrays.sort(ILLEGAL_CHARS);
  }

  public static String cleanFileName(String badFileName, String replacement) {
    StringBuilder cleanName = new StringBuilder();
    int len = badFileName.codePointCount(0, badFileName.length());

    for (int i = 0; i < len; i++) {
      int c = badFileName.codePointAt(i);

      if (Arrays.binarySearch(ILLEGAL_CHARS, c) < 0) {
        cleanName.appendCodePoint(c);
      } else {

        for (int l = 0; l < replacement.codePoints().count(); l++) {
          cleanName.appendCodePoint(replacement.codePointAt(l));
        }
      }
    }
    return cleanName.toString();
  }

  public static String getQualityUrl(Video video, Constants.VideoQuality videoQuality) {

    if (videoQuality == Constants.VideoQuality.HD) {

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
    } else if (videoQuality == Constants.VideoQuality.HIGH) {

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
