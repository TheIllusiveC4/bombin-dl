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

public class Logo {

  @SerializedName("icon_url")
  public String iconUrl;
  @SerializedName("medium_url")
  public String mediumUrl;
  @SerializedName("screen_url")
  public String screenUrl;
  @SerializedName("screen_large_url")
  public String screenLargeUrl;
  @SerializedName("small_url")
  public String smallUrl;
  @SerializedName("super_url")
  public String superUrl;
  @SerializedName("thumb_url")
  public String thumbUrl;
  @SerializedName("tiny_url")
  public String tinyUrl;
  @SerializedName("original_url")
  public String originalUrl;
}
