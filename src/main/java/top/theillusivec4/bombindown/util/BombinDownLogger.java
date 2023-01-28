package top.theillusivec4.bombindown.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import top.theillusivec4.bombindown.data.FileManager;

public class BombinDownLogger {

  private static final Logger LOGGER = Logger.getLogger(Constants.ID);

  public static void load() {
    FileHandler fh;

    try {
      FileManager.ROOT.mkdir();
      fh = new FileHandler(FileManager.ROOT + "/latest.log");
      LOGGER.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);
    } catch (SecurityException | IOException e) {
      e.printStackTrace();
    }
    log("Initialized logger.");
  }

  public static void log(String msg) {
    LOGGER.info(msg);
  }

  public static void warn(String msg) {
    LOGGER.log(Level.WARNING, msg);
  }

  public static void warn(String msg, Exception e) {
    warn(msg);
    LOGGER.log(Level.WARNING, e.getMessage(), e);
  }

  public static void error(String msg) {
    LOGGER.log(Level.SEVERE, msg);
  }

  public static void error(String msg, Exception e) {
    error(msg);
    LOGGER.log(Level.SEVERE, e.getMessage(), e);
  }
}
