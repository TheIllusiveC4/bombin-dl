package top.theillusivec4.bombindown.download;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import top.theillusivec4.bombindown.data.UserPrefs;
import top.theillusivec4.bombindown.util.BombinDownLogger;

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
    BombinDownLogger.log("Setting maximum simultaneous downloads to " + num + "...");
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
      BombinDownLogger.error("There was an error changing maximum simultaneous downloads.", e);
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
