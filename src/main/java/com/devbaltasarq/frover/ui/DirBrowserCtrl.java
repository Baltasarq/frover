// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import com.devbaltasarq.frover.core.Cfg;
import com.devbaltasarq.frover.core.DirBrowser;
import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.entries.Directory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/** Represents the controller for a window with a panel of directories and files.
  * @author baltasarq
  */
public abstract class DirBrowserCtrl {
    protected DirBrowserCtrl(DirBrowserView view)
    {
        this.view = view;
        this.cfg = Cfg.get();
    }
    
    /** Activate the whole UI to show current dir.
      * @param DIR the directory to sync.
      * @param ENTRIES its subdirectories.
      * @param SORTER the comparator of paths, for sorting.
      */
    protected void syncToDir(final Path DIR,
                             final List<Entry> ENTRIES,
                             final Comparator<? super Path> SORTER)
    {
        final boolean SHOW_HIDDEN = this.cfg.isShowingHiddenFiles().get();
        final var DIRS = new ArrayList<Path>( 5 );
        final var FILES = new ArrayList<Path>( 5 );
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
   
    protected DirBrowser dirBrowser;
    protected final Cfg cfg;
    protected final DirBrowserView view;
}
