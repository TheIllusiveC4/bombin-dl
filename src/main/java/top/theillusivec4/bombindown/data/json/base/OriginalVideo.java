package top.theillusivec4.bombindown.data.json.base;

import com.google.gson.annotations.SerializedName;
import top.theillusivec4.bombindown.data.DataManager;
import top.theillusivec4.bombindown.data.json.Association;
import top.theillusivec4.bombindown.data.json.Category;
import top.theillusivec4.bombindown.data.json.Image;
import top.theillusivec4.bombindown.data.json.Show;
import top.theillusivec4.bombindown.data.json.Video;

public class OriginalVideo {

  @SerializedName("api_detail_url")
  public String apiDetailUrl;
  public Association[] associations;
  public String deck;
  @SerializedName("embed_player")
  public String embedPlayer;
  public String guid;
  public int id;
  @SerializedName("length_seconds")
  public int lengthSeconds;
  public String name;
  public boolean premium;
  @SerializedName("publish_date")
  public String publishDate;
  @SerializedName("site_detail_url")
  public String siteDetailUrl;
  public Image image;
  public String user;
  public String crew;
  public String hosts;
  @SerializedName("video_type")
  public String videoType;
  @SerializedName("video_show")
  public VideoShow videoShow;
  @SerializedName("video_categories")
  public Category[] videoCategories;
  @SerializedName("youtube_id")
  public String youtubeId;
  public String url;
  @SerializedName("low_url")
  public String lowUrl;
  @SerializedName("high_url")
  public String highUrl;
  @SerializedName("hd_url")
  public String hdUrl;
  @SerializedName("uhd_url")
  public String uhdUrl;

  public OriginalVideo(Video video) {
    this.apiDetailUrl = video.apiDetailUrl;
    this.associations = video.associations;
    this.deck = video.deck;
    this.embedPlayer = video.embedPlayer;
    this.guid = video.guid;
    this.id = video.id;
    this.lengthSeconds = video.lengthSeconds;
    this.name = video.name;
    this.premium= video.premium;
    this.publishDate = video.publishDate;
    this.siteDetailUrl = video.siteDetailUrl;
    this.image = video.image;
    this.user = video.user;
    this.crew = video.crew;
    this.hosts = video.hosts;
    this.videoType = video.videoType;
    this.videoShow = null;
    Show show = DataManager.getShow(video.videoShow);

    if (show != null) {
      this.videoShow = new VideoShow(show);
    }
    this.videoCategories = video.videoCategories;
    this.youtubeId = video.youtubeId;
    this.url = video.url;
    this.lowUrl = video.lowUrl;
    this.highUrl = video.highUrl;
    this.hdUrl = video.hdUrl;
    this.uhdUrl = video.uhdUrl;
  }
}
