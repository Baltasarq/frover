// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.awt.Color;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Map;


/** The configuration for the app
  * @author baltasarq
  */
public class Config {
    private static final Logger LOG = Logger.getLogger( Config.class.getSimpleName() );
    private static final String RECORD_SEPARATOR = "\u001C\u001E";
    private static final String LBL_FAV_HOME = "Home";
    private static final String STR_FMT_COLOR = "#%02x%02x%02x%02x";
    private static final int STR_COLOR_FMT_LEN = 9;

    public enum Key {
        APP_NAME,
        HOME_DIR,
        CONFIG_DIR,
        CONFIG_FILE_NAME,
        CONFIG_FILE_PATH,
        WIDTH,
        HEIGHT,
        TOP,
        LEFT,
        FAV_NAMES,
        FAV_DIRS,
        FAV_BG_COLOR,
        FAV_FG_COLOR,
        DIR_BROWSER_BG_COLOR,
        DIR_BROWSER_FG_COLOR,
        FILE_BROWSER_BG_COLOR,
        FILE_BROWSER_FG_COLOR,
        BTNS_BG_COLOR,
        BTNS_FG_COLOR        
    }
    
    private Config(String appName)
    {
        appName = appName.trim().toLowerCase();
        
        this.configValues = new EnumMap<>( Key.class );
        this.configValues.put( Key.APP_NAME, appName );
        this.configValues.put( Key.CONFIG_FILE_NAME, appName + ".cfg.json" );
        this.init();
    }
    
    /** Assigns all the default values. */
    private void init()
    {
        final var HOME_DIR = System.getProperty( "user.home" );

        this.configValues.put( Key.HOME_DIR, HOME_DIR );
        this.configValues.put( Key.CONFIG_DIR,
                                  this.buildConfigDirPath() );
        this.configValues.put( Key.CONFIG_FILE_PATH,
                                  this.buildConfigFilePath() );
        this.configValues.put( Key.LEFT, "-1" );
        this.configValues.put( Key.TOP, "-1" );
        this.configValues.put( Key.WIDTH, "-1" );
        this.configValues.put( Key.HEIGHT, "-1" );
        this.configValues.put( Key.FAV_NAMES, LBL_FAV_HOME );
        this.configValues.put( Key.FAV_DIRS, HOME_DIR );
        this.configValues.put( Key.FAV_BG_COLOR, strRGBFromColor( Color.BLACK ) );
        this.configValues.put( Key.FAV_FG_COLOR, strRGBFromColor( Color.WHITE ) );
        this.configValues.put( Key.DIR_BROWSER_BG_COLOR, strRGBFromColor( Color.GRAY ) );
        this.configValues.put( Key.DIR_BROWSER_FG_COLOR, strRGBFromColor( Color.WHITE ) );
        this.configValues.put( Key.FILE_BROWSER_BG_COLOR, strRGBFromColor( Color.GRAY ) );
        this.configValues.put( Key.FILE_BROWSER_FG_COLOR, strRGBFromColor( Color.WHITE ) );
        this.configValues.put( Key.BTNS_BG_COLOR, strRGBFromColor( Color.WHITE ) );
        this.configValues.put( Key.BTNS_FG_COLOR, strRGBFromColor( Color.BLUE ) );
    }
    
    /** Builds the config directory path.
      * Beware that it needs the home directory already stored.
      * @return a string with the directory.
      */
    private String buildConfigDirPath()
    {
        String strConfigDirName = AppInfo.NAME.toLowerCase();
        String strHomeDir = this.configValues.get( Key.HOME_DIR );
        
        assert strHomeDir != null: "buildConfigDirectory(): "
                                    + "home dir can't be null";
        
        var configDirParent = new File( strHomeDir, ".config" );
        var toret = new File( configDirParent, strConfigDirName );
        
        return toret.getAbsolutePath();
    }
    
    /** Builds the config file path.
      * Beware that it needs the CONFIG_DIR entry before being executed.
      * @return the complete path to the config file.
      */
    private String buildConfigFilePath()
    {
        String strConfigDir = this.get( Key.CONFIG_DIR );
        String strConfigFileName = this.get( Key.CONFIG_FILE_NAME );
        
        assert strConfigDir != null: "buildConfigFilePath(): "
                                    + "config file path dir can't be null";
        
        assert strConfigFileName != null: "buildConfigFilePath(): "
                                    + "config file name can't be null";
        
        final var TORET = new File( strConfigDir, strConfigFileName );
        return TORET.getAbsolutePath();
    }
    
    /** @return the associated value, or null if it does not exist.
      * @param key the key to ask for.
      */
    public String get(Key key)
    {
        return this.configValues.get( key );
    }
    
    /** @return the associated list values, or null if it does not exist.
      * @param key the key to ask for.
      */
    public String[] getList(Key key)
    {
        String value = this.configValues.get( key );
        String[] toret = null;
        
        if ( value != null ) {
            toret = value.split( RECORD_SEPARATOR );
        }
        
        return toret;
    }
    
    /** Adds a new value to the configuration.
      * @param key the key for the value.
      * @param value the new value.
      */
    public void add(Key key, String value)
    {
        this.configValues.put( key, value );
    }
    
    /** Adds a new list of values to the configuration.
      * @param key the key for the list of values.
      * @param list the new list of values, as a String[].
      */
    public void addList(Key key, String[] list)
    {
        String value = "";
        
        if ( list != null ) {
            value = String.join( RECORD_SEPARATOR, list );
        }
        
        this.configValues.put( key, value );
    }
    
    private void createFilesIfNotExisting() throws IOException
    {
        final var CONFIG_DIR = new File( this.get( Key.CONFIG_DIR ) );
        final var CONFIG_PATH = new File( this.get( Key.CONFIG_FILE_PATH ) );
        
        if ( !CONFIG_DIR.exists() ) {
            if ( !CONFIG_DIR.mkdirs() ) {
                throw new IOException(
                                "Config: unable to create config directory: "
                                + CONFIG_DIR );
            }
        }
        
        if ( !CONFIG_PATH.exists() ) {
            if ( !CONFIG_PATH.createNewFile() ) {
                throw new IOException(
                            "Config: unable to create config file: "
                            + CONFIG_PATH );
            }
        }
    }
    
    /** Saves the configuration to the config file. */
    public void save()
    {
        final String FILE_PATH = this.get( Key.CONFIG_FILE_PATH );
        final var GSON = new Gson();
        final String CONTENTS = GSON.toJson( this.configValues );
        
        try {
            Files.deleteIfExists( Path.of( FILE_PATH ) );
            Files.writeString( Path.of( FILE_PATH ), CONTENTS,
                            StandardOpenOption.CREATE );
            LOG.info( "config saved" );
        } catch(IOException exc) {
            LOG.warning( "unable to save config file: " + exc.getMessage() );
        }
    }
    
    /** @return a string in the STR_FMT_COLOR, i.e., #rrggbbaa
      * @param color a regular awt.Color.
      */
    private static String strRGBFromColor(Color color)
    {
        return String.format( STR_FMT_COLOR,
                                        color.getRed(),
                                        color.getGreen(),
                                        color.getBlue(),
                                        color.getTransparency() );
    }
    
    /** @return an awt.Color object from a given string
      * @param strColor a string in the STR_FMT_COLOR, i.e., #aarrggbb
      */
    private static Color colorFromstrARGB(String strColor)
                                                throws IllegalArgumentException
    {
        strColor = strColor.trim();
        
        // Chk start with the '#'
        if ( !strColor.startsWith( "#" ) ) {
            throw new IllegalArgumentException( "missing '#' prefix" );
        }
        
        // Check that we have four pairs, and of course, the leading '#'
        if ( strColor.length() < STR_COLOR_FMT_LEN ) {
            throw new IllegalArgumentException(
                        String.format( "format should be '%s', not '%s'",
                                STR_FMT_COLOR,
                                strColor ) );
        }
        
        // Now parse the components
        strColor = strColor.substring( 1 );
        
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 255;
            
        // Parse each two-digit hex segment and convert to decimal
        try {
            red = Integer.parseInt( strColor.substring( 0, 2 ), 16 );
            green = Integer.parseInt( strColor.substring( 2, 4 ), 16 );
            blue = Integer.parseInt( strColor.substring( 4, 6 ), 16 );
            alpha = Integer.parseInt( strColor.substring( 6, 8 ), 16 );
        } catch(NumberFormatException exc) {
            throw new IllegalArgumentException( "invalid color number in: " + strColor );
        }

        return new Color( red, green, blue, alpha );
    }
    
    public static Config restore(String appName)
    {
        if ( uniqueInstance != null ) {
            return uniqueInstance;
        }
        
        uniqueInstance = new Config( appName );
        final Gson GSON = new Gson();
        final String FILE_PATH = uniqueInstance.get( Key.CONFIG_FILE_PATH );
        
        try {    
            final String CONTENTS = Files.readString( Path.of( FILE_PATH ) );
            final var TYPE = new TypeToken<Map<String, String>>(){}.getType();
            final Map<String, String> MAP = GSON.fromJson( CONTENTS, TYPE );
            
            if ( MAP != null ) {            
                for(final Map.Entry<String, String> ENTRY: MAP.entrySet()) {
                    final String STR_KEY = ENTRY.getKey().trim().toUpperCase();
                    final Key KEY = Key.valueOf( STR_KEY );

                    if ( KEY != null ) {
                        uniqueInstance.add( KEY, ENTRY.getValue() );
                    } else {
                        LOG.warning( "Config.restore(): key not found: " + STR_KEY );
                    }
                }
            }
        } catch(IOException exc) {
            LOG.warning( "unable to locate/create/load config file: "
                            + exc.getMessage() );
        }

        try {
            uniqueInstance.createFilesIfNotExisting();
        } catch(IOException exc) {
            LOG.warning( "unable to locate/create directories: "
                            + exc.getMessage() );
        }

        return uniqueInstance;
    }
    
    private Map<Key, String> configValues;
    private static Config uniqueInstance = null;
}
