// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;

import com.devbaltasarq.frover.core.Config;
import com.devbaltasarq.frover.ui.components.NamedPathList;
import com.devbaltasarq.frover.ui.components.PathList;
import com.devbaltasarq.frover.ui.mainwindow.MainWindowView;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import java.awt.Color;
import javax.swing.JButton;


/** Applies the configuration to the view.
  * @author baltasarq
  */
public class UICfgBridge {
    private static final Logger LOG = Logger.getLogger( MainWindow.class.getName() );
    
    public UICfgBridge(MainWindowView view, Config config)
    {
        this.view = view;
        this.cfg = config;
    }
    
    /** @return the view object. */
    public MainWindowView getView()
    {
        return this.view;
    }
    
    /** @return the configuration object. */
    public Config getConfig()
    {
        return this.cfg;
    }
    
    private void applyCfgToWindow()
    {
        final var WINDOW = this.view.getWindow();
        String strWidth = this.cfg.get( Config.Key.WIDTH );
        String strHeight = this.cfg.get( Config.Key.HEIGHT );
        String strLeft = this.cfg.get( Config.Key.LEFT );
        String strTop = this.cfg.get( Config.Key.TOP );
        
        if ( !strWidth.equals( "-1" )
          && !strHeight.equals( "-1" ) )
        {
            WINDOW.setSize(
                Integer.parseInt( strWidth ),
                Integer.parseInt( strHeight ));
        }
        
        if ( !strLeft.equals( "-1" )
          && !strTop.equals( "-1" ) )
        {
            WINDOW.setLocation(
                Integer.parseInt( strLeft ),
                Integer.parseInt( strTop ));
        }
    }
    
    private void applyCfgToFavs()
    {
        final var FAV_DIR_LIST = this.view.getVisitedDirChoicePanel().getFavList();
        final String[] FAV_NAMES = this.cfg.getList( Config.Key.FAV_NAMES );
        final String[] FAV_DIRS = this.cfg.getList( Config.Key.FAV_DIRS );
        
        // Adds the favourites fav dirs
        if ( FAV_NAMES != null
          && FAV_DIRS != null )
        {
            int numFavs = FAV_NAMES.length;

            for(int i = 0; i < numFavs; ++i) {
                try {
                    FAV_DIR_LIST.add( new NamedPathList.NamedPath(
                                                FAV_NAMES[ i ],
                                            Path.of( FAV_DIRS[ i ] ) ) );
                } catch(IndexOutOfBoundsException exc) {
                    LOG.severe( "retrieving list of favs at: " + i );
                }
            }
        }
    }
    
    private void applyCfgToColors()
    {
        final PathList DIR_LIST = this.view.getDirChoicePanel().getDirList();
        final PathList FILE_LIST = this.view.getFileChoicePanel().getFileList();
        final var FAV_LIST = this.view.getVisitedDirChoicePanel().getFavList();
        final List<JButton> BUTTONS = this.view.getAllButtons();
        
        Color favBgColor = this.cfg.getColor( Config.Key.FAV_BG_COLOR );
        Color favFgColor = this.cfg.getColor( Config.Key.FAV_FG_COLOR );
        Color dirBrowserBgColor = this.cfg.getColor(
                                            Config.Key.DIR_BROWSER_BG_COLOR );
        Color dirBrowserFgColor = this.cfg.getColor( 
                                            Config.Key.DIR_BROWSER_FG_COLOR );
        Color fileBrowserBgColor = this.cfg.getColor(
                                            Config.Key.FILE_BROWSER_BG_COLOR );
        Color fileBrowserFgColor = this.cfg.getColor(
                                            Config.Key.FILE_BROWSER_FG_COLOR );
        Color btnsBgColor = this.cfg.getColor( Config.Key.BTNS_BG_COLOR );
        Color btnsFgColor = this.cfg.getColor( Config.Key.BTNS_FG_COLOR );
        
        DIR_LIST.setBackground( dirBrowserBgColor );
        DIR_LIST.setForeground( dirBrowserFgColor );
        
        FILE_LIST.setBackground( fileBrowserBgColor );
        FILE_LIST.setForeground( fileBrowserFgColor );
        
        FAV_LIST.setBackground( favBgColor );
        FAV_LIST.setForeground( favFgColor );
        
        for(var button: BUTTONS) {
            button.setBackground( btnsBgColor );
            button.setForeground( btnsFgColor );
        }
    }

    /** Applies the configuration to the UI. */
    public void applyCfgToUI()
    {
        this.applyCfgToWindow();
        this.applyCfgToColors();
        this.applyCfgToFavs();
    }
    
    private final Config cfg;
    private final MainWindowView view;
}
