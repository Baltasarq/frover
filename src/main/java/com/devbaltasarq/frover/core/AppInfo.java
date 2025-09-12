// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


/** AppInfo
  * @author baltasarq
  */
public class AppInfo {
    public static final String NAME = "frover";
    public static final String VERSION = "0.1 20250812";
    public static final String WEB = "http://github.com/baltasarq/frover";
    public static final String WIKI_WEB = "http://github.com/baltasarq/frover/wiki";
    
    public static String getFullName()
    {
        return NAME + " v" + VERSION;
    }
}
