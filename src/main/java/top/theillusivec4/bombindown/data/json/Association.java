package top.theillusivec4.bombindown.data.json;

import com.google.gson.annotations.SerializedName;

public class Association {

  public String guid;
  @SerializedName("site_details_url")
  public String siteDetailsUrl;
  @SerializedName("api_details_url")
  public String apiDetailsUrl;
  public String name;
  public int id;
}
