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

package top.theillusivec4.bombindl.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import top.theillusivec4.bombindl.data.FileManager;

public class BombinDLLogger {

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
