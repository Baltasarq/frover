// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import com.devbaltasarq.frover.core.Cfg;
import com.devbaltasarq.frover.core.DirBrowser;
import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.entries.Directory;
import com.devbaltasarq.frover.ui.components.PathList;
import com.devbaltasarq.frover.ui.browser.BrowserView;

import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/** Represents the controller for a window with a panel of directories and files.
  * @author baltasarq
  */
public abstract class Browser {
    protected Browser(BrowserView view)
    {
        this( view, Cfg.get() );
    }
    
    protected Browser(BrowserView view, Cfg cfg)
    {
        this.view = view;
        this.cfg = cfg;
        
        this.view.getDirChoicePanel().setChangeDirAction(
                                                (p) -> this.cd( p ) );
    }
    
    /** Activate the whole UI to show current dir.
      * @throws IOException if something goes wrong while reading the dir.
      */
    protected void syncToCurrentDir() throws IOException
    {
        this.syncToCurrentDir( DirBrowser.ALPHA_SORTER );
    }
    
    /** Activate the whole UI to show current dir.
      * @param SORTER the comparator of paths, for sorting.
      * @throws IOException if something goes wrong while reading the dir.
      */
    protected void syncToCurrentDir(final Comparator<? super Path> SORTER)
            throws IOException
    {
        final boolean SHOW_HIDDEN = this.cfg.isShowingHiddenFiles().get();
        final List<Entry> ENTRIES = this.getDirBrowser().readDir( SORTER );
        final var DIRS = new ArrayList<Path>( 5 );
        final var FILES = new ArrayList<Path>( 5 );
        final Path DIR = this.getDirBrowser().getDirectory().getPath();
        List<Path> targetList;
        
        for(Entry e: ENTRIES) {
            if ( e != null ) {
                final Path PATH = e.getPath();
                targetList = FILES;

                if ( e instanceof Directory ) {
                    targetList = DIRS;
                }

                if ( !e.isHidden()
                  || SHOW_HIDDEN )
                {
                    targetList.add( PATH );
                }
            }
        }

        DIRS.sort( SORTER );
        FILES.sort( SORTER );

        this.view.getDirChoicePanel().syncToDir( DIR, DIRS );
        this.view.getFileChoicePanel().syncToFiles( DIR, FILES );
    }
    
    /** @return the directory browser for the current dir. */
    public DirBrowser getDirBrowser()
    {
        return this.dirBrowser;
    }
    
    
    
    /** @return the chosen entry (dir or file), or false if nothing is selected. */
    public Entry getChosenEntry()
    {
        final PathList DIR_CHOICE = this.view.getDirChoicePanel().getDirList();
        final PathList FILE_CHOICE = this.view.getFileChoicePanel().getFileList();
        PathList targetList = FILE_CHOICE;
        Path path = null;
        Entry toret = null;
        
        // Determine the list
        if ( DIR_CHOICE.isFocusOwner() ) {
            targetList = DIR_CHOICE;
        }
        
        // Determine the path
        int pos = targetList.getSelectedIndex();
        
        if ( pos >= 0 ) {
            path = targetList.getPathAt( pos );
            toret = Entry.from( path.toFile() );
        }
        
        return toret;
    }
    
    /** Changes directory to the given path.
      * @param PATH the given path to change to.
      * @return true if the directory could be changed, false otherwise.
      */
    public boolean cd(final Path PATH)
    {
        boolean toret;
        
        try {
            this.dirBrowser = new DirBrowser( PATH );
            toret = true;
            this.syncToCurrentDir();
        } catch(IOException exc) {
            toret = false;
        }
        
        return toret;
    }
   
    protected DirBrowser dirBrowser;
    protected final Cfg cfg;
    protected final BrowserView view;
}
