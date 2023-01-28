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

package top.theillusivec4.bombindl.data.json.base;

import com.google.gson.annotations.SerializedName;
import top.theillusivec4.bombindl.data.json.Image;
import top.theillusivec4.bombindl.data.json.Logo;
import top.theillusivec4.bombindl.data.json.Show;

public class VideoShow {

  public int id;
  public String title;
  public boolean premium;
  public Logo logo;
  public Image image;
  @SerializedName("site_detail_url")
  public String siteDetailsUrl;
  @SerializedName("api_detail_url")
  public String apiDetailsUrl;
  @SerializedName("api_videos_url")
  public String apiVideosUrl;

  public VideoShow(Show show) {
    this.id = Integer.parseInt(show.guid.substring(5));
    this.title = show.title;
    this.premium = show.premium;
    this.logo = show.logo;
    this.image = show.image;
    this.siteDetailsUrl = show.siteDetailsUrl;
    this.apiDetailsUrl = show.apiDetailsUrl;
    this.apiVideosUrl = show.apiVideosUrl;
  }
}
