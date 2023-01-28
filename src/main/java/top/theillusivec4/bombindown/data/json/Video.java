package top.theillusivec4.bombindown.data.json;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import top.theillusivec4.bombindown.data.json.base.OriginalVideo;

public class Video implements Comparable<Video> {

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
  public String videoShow;
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

  public Video(OriginalVideo originalVideo) {
    this.apiDetailUrl = originalVideo.apiDetailUrl;
    this.associations = originalVideo.associations;
    this.deck = originalVideo.deck;
    this.embedPlayer = originalVideo.embedPlayer;
    this.guid = originalVideo.guid;
    this.id = originalVideo.id;
    this.lengthSeconds = originalVideo.lengthSeconds;
    this.name = originalVideo.name;
    this.premium= originalVideo.premium;
    this.publishDate = originalVideo.publishDate;
    this.siteDetailUrl = originalVideo.siteDetailUrl;
    this.image = originalVideo.image;
    this.user = originalVideo.user;
    this.crew = originalVideo.crew;
    this.hosts = originalVideo.hosts;
    this.videoType = originalVideo.videoType;
    this.videoShow = originalVideo.videoShow == null ? null : "2340-" + originalVideo.videoShow.id;
    this.videoCategories = originalVideo.videoCategories;
    this.youtubeId = originalVideo.youtubeId;
    this.url = originalVideo.url;
    this.lowUrl = originalVideo.lowUrl;
    this.highUrl = originalVideo.highUrl;
    this.hdUrl = originalVideo.hdUrl;
    this.uhdUrl = originalVideo.uhdUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Video video = (Video) o;
    return guid.equals(video.guid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guid);
  }

  @Override
  public int compareTo(Video o) {
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
