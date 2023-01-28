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

package top.theillusivec4.bombindl.download;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import top.theillusivec4.bombindl.data.UserPrefs;
import top.theillusivec4.bombindl.util.BDLogger;

public class DownloadExecutor {

  private final ThreadPoolExecutor threadPoolExecutor;
  private final Map<Download, Future<?>> futures;

  public DownloadExecutor() {
    int maxDownloads = UserPrefs.INSTANCE.getMaxDownloads();
    this.threadPoolExecutor =
        new ThreadPoolExecutor(maxDownloads, maxDownloads, 10, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());
    this.futures = new ConcurrentHashMap<>();
  }

  public void setMaxDownloads(int num) {
    BDLogger.log("Setting maximum simultaneous downloads to " + num + "...");
    int current = this.threadPoolExecutor.getCorePoolSize();

    try {
      if (num > current) {
        this.threadPoolExecutor.setMaximumPoolSize(num);
        this.threadPoolExecutor.setCorePoolSize(num);
      } else if (num < current) {
        this.threadPoolExecutor.setCorePoolSize(num);
        this.threadPoolExecutor.setMaximumPoolSize(num);
      }
    } catch (IllegalArgumentException e) {
      BDLogger.error("There was an error changing maximum simultaneous downloads.", e);
    }
  }

  public void beginDownload(Download download) {
    Future<?> future = this.threadPoolExecutor.submit(download);
    this.futures.put(download, future);
  }

  public void stopDownload(Download download) {
    Future<?> future = this.futures.remove(download);

    if (future != null) {
      future.cancel(true);
    }
  }
}
