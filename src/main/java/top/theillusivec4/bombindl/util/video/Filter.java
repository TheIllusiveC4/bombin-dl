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

package top.theillusivec4.bombindl.util.video;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import top.theillusivec4.bombindl.data.json.Video;
import top.theillusivec4.bombindl.util.BombinDLLogger;

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
      BombinDLLogger.error("There was an error formatting date " + video.publishDate + ".", e);
    }
    return true;
  }
}
