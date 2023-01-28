package top.theillusivec4.bombindown.util.video;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import top.theillusivec4.bombindown.data.json.Video;
import top.theillusivec4.bombindown.util.BombinDownLogger;

public class Filter {

  public String membership;
  public Date fromDate;
  public Date untilDate;

  public Filter() {
    this.membership = "-";
  }

  public Filter(String membership, Date fromDate, Date untilDate) {
    this.membership = membership;
    this.fromDate = fromDate;
    this.untilDate = untilDate;
  }

  public boolean apply(Video video) {
    return isValidMember(video) && isValidDate(video);
  }

  public Collection<Video> apply(Collection<Video> videos) {

    if (membership.equals("-") && fromDate == null && untilDate == null) {
      return videos;
    } else {
      List<Video> result = new ArrayList<>();

      for (Video video : videos) {

        if (isValidMember(video) && isValidDate(video)) {
          result.add(video);
        }
      }
      return result;
    }
  }

  private boolean isValidMember(Video video) {
    return membership.equals("-") || (membership.equals("Premium") && video.premium) ||
        (membership.equals("Free") && !video.premium);
  }

  private boolean isValidDate(Video video) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    try {
      Date date = df.parse(video.publishDate);

      if (fromDate != null && date.before(fromDate)) {
        return false;
      }

      if (untilDate != null && date.after(untilDate)) {
        return false;
      }
    } catch (ParseException e) {
      BombinDownLogger.error("There was an error formatting date " + video.publishDate + ".", e);
    }
    return true;
  }
}
