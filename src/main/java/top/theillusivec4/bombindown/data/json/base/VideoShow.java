package top.theillusivec4.bombindown.data.json.base;

import com.google.gson.annotations.SerializedName;
import top.theillusivec4.bombindown.data.json.Image;
import top.theillusivec4.bombindown.data.json.Logo;
import top.theillusivec4.bombindown.data.json.Show;

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
