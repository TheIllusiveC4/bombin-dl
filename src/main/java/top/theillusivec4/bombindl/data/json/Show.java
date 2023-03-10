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

package top.theillusivec4.bombindl.data.json;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Show implements Comparable<Show> {

  public String guid;
  public String title;
  public boolean premium;
  public String deck;
  public Logo logo;
  public Image image;
  @SerializedName("site_detail_url")
  public String siteDetailsUrl;
  @SerializedName("api_detail_url")
  public String apiDetailsUrl;
  @SerializedName("api_videos_url")
  public String apiVideosUrl;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Show show = (Show) o;
    return guid.equals(show.guid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guid);
  }

  @Override
  public int compareTo(Show o) {
    String first = this.guid;
    String second = o.guid;

    if (first.isEmpty()) {
      return 1;
    } else if (second.isEmpty()) {
      return -1;
    } else {
      int firstNum = Integer.parseInt(first.substring(5));
      int secondNum = Integer.parseInt(second.substring(5));
      return firstNum - secondNum;
    }
  }
}
