// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import java.nio.file.Path;
import java.io.IOException;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MenuShortcut;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import com.devbaltasarq.frover.core.AppInfo;
import com.devbaltasarq.frover.core.Size;
import com.devbaltasarq.frover.core.Cfg;
import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.entries.Directory;
import com.devbaltasarq.frover.core.entries.File;
import com.devbaltasarq.frover.ui.tools.Action;
import com.devbaltasarq.frover.ui.dirbrowser.DirBrowserCtrl;
import com.devbaltasarq.frover.ui.tools.Logger;
import com.devbaltasarq.frover.ui.box.MessageBox;
import com.devbaltasarq.frover.ui.box.InputBox;
import com.devbaltasarq.frover.ui.box.AskBox;
import com.devbaltasarq.frover.ui.components.PathList;
import com.devbaltasarq.frover.ui.mainwindow.MainWindowView;


/** Control for MainWindow.
  * @author baltasarq
  */
public class MainWindow extends DirBrowserCtrl {
    public MainWindow(Path path)
    {
        this( new MainWindowView(), path );
    }
    
    public MainWindow(MainWindowView view, Path path)
    {
        super( view );
        
        this.log = new Logger( this.getView().getOutput() );
        
        this.actionAbout = new Action( "about", "About" );
        this.actionQuit = new Action( "quit", "Quit" );
        this.actionHelp = new Action( "help", "Help" );
        this.actionNew = new Action( "new", "New" );
        this.actionRename = new Action( "rename", "Rename" );
        this.actionShowHidden = new Action( "show_hidden", "Show hidden" );
        this.actionCopy = new Action( "copy", "Copy" );
        this.actionMove = new Action( "move", "Move" );
        this.actionDelete = new Action( "delete", "Delete" );
        this.actionView = new Action( "view", "View" );
        this.actionRefresh = new Action( "refresh", "Refresh" );
        this.actionViewOutput = new Action( "view_output", "View output" );
        
        this.buildListeners();
        this.getView().getOutput().setVisible( this.cfg.isOutputVisible().get() );

        this.log.i( AppInfo.getFullName() + "\n" );
        this.doCd( path );
    }
    
    /** Starts the app by showing it. */
    public void show()
    {
        this.getView().getWindow().setVisible( true );
    }
    
    /** @return the corresponding view. */
    public final MainWindowView getView()
    {
        return (MainWindowView) this.view;
    }
    
    private void buildListeners()
    {
        this.buildActions();
        
        this.getView().getDirChoicePanel().setCopyCWDAction(
                                                (t) -> this.doCopyCWD( t ) );
        this.getView().getFileChoicePanel().setSelectFileAction(
                                                (p) -> this.doView( p ) );
        
        this.getView().getFrame().addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                MainWindow.this.actionQuit.doIt();
            }});
        
        final var KEY_MAN = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        KEY_MAN.addKeyEventDispatcher( new KeyboardDispatcher() );

        this.buildMenuListeners();
    }
    
    private void buildActions()
    {
        // View output
        this.actionViewOutput.add( this.getView().getOpAbout() );
        this.actionAbout.set( () -> this.doAbout());
        
        // Quit
        this.actionQuit.add( this.getView().getOpQuit() );
        this.actionQuit.set( () -> System.exit( 0 ) );
        
        // Help
        this.actionHelp.add( this.getView().getHelpButton() );
        this.actionHelp.add( this.getView().getOpHelp() );
        this.actionHelp.set( () -> this.doHelp() );
        
        // New
        this.actionNew.add( this.getView().getOpNew() );
        this.actionNew.set( () -> this.doNew() );
        
        // Rename
        this.actionRename.add( this.getView().getRenameButton() );
        this.actionRename.add( this.getView().getOpRename() );
        this.actionRename.set( () -> this.doRename() );
        
        // Copy
        this.actionCopy.add( this.getView().getCopyButton() );
        this.actionCopy.add( this.getView().getOpCopy() );       
        this.actionCopy.set( () -> this.doCopy() );
        
        // Move
        this.actionMove.add( this.getView().getMoveButton() );
        this.actionMove.add( this.getView().getOpMove() );       
        this.actionMove.set( () -> this.doMove() );
        
        // Delete
        this.actionDelete.add( this.getView().getDeleteButton() );
        this.actionDelete.add( this.getView().getOpDelete() );       
        this.actionDelete.set( () -> this.doDelete() );
        
        // View
        this.actionView.add( this.getView().getViewButton() );
        this.actionView.add( this.getView().getOpView() );       
        this.actionView.set( () -> this.doView() );
        
        // Refresh
        this.actionRefresh.add( this.getView().getOpRefresh() );       
        this.actionRefresh.set( () -> this.doRefresh());
        
        // Show hidden
        this.actionShowHidden.add( this.getView().getOpShowHidden() );       
        this.actionShowHidden.set( () -> this.doShowHidden());
        
        // View output
        this.actionViewOutput.add( this.getView().getOpViewOutput() );
        this.actionViewOutput.set( () -> this.doViewOutput());
    }
    
    private void buildMenuListeners()
    {
        final var OP_QUIT = this.getView().getOpQuit();
        final var OP_COPY = this.getView().getOpCopy();
        final var OP_MOVE = this.getView().getOpMove();
        final var OP_DELETE = this.getView().getOpDelete();
        final var OP_NEW = this.getView().getOpNew();
        final var OP_REFRESH = this.getView().getOpRefresh();
        
        OP_QUIT.setShortcut( new MenuShortcut( KeyEvent.VK_Q ) );
        OP_NEW.setShortcut( new MenuShortcut( KeyEvent.VK_N ) );
        OP_COPY.setShortcut( new MenuShortcut( KeyEvent.VK_C ) );
        OP_MOVE.setShortcut( new MenuShortcut( KeyEvent.VK_C, true ) );
        OP_DELETE.setShortcut( new MenuShortcut( KeyEvent.VK_DELETE ) );
        OP_REFRESH.setShortcut( new MenuShortcut( KeyEvent.VK_F5 ) );
    }
    
    @Override
    public void syncToCurrentDir()
    {
        String status = "Ready";
        
        try {
            super.syncToCurrentDir();
            this.log.i( "sync `" + this.dirBrowser.getDirectory().asCanonical() + "`" );
        } catch(IOException exc)
        {
            this.log.e( exc.getMessage() );
            status = "ERROR: " + exc.getMessage();
        }
        
        // Status and title
        this.setStatus( status );
        this.setTitle();
    }
    
    /** Changes directory to the given path.
      * @param PATH the given path to change to.
      * @return true if the directory could be changed, false otherwise.
      */
    @Override
    public boolean cd(final Path PATH)
    {
        return this.doCd( PATH );
    }
    
    private void setTitle()
    {
        String dirName = this.dirBrowser.getDirectory().asCanonical();
        
        if ( dirName.length() > 30 ) {
            dirName = "..." + dirName.substring( dirName.length() - 27 );
        }
        
        this.getView().getFrame().setTitle( dirName + " - " + AppInfo.NAME );
    }
    
    /** Help action */
    public void doHelp()
    {
        final Desktop DESKTOP = Desktop.getDesktop();

        try {
            this.log.i( "launching browser for: `" + AppInfo.WIKI_WEB + "`" );
            DESKTOP.browse( new java.io.File( AppInfo.WIKI_WEB ).toURI() );
        } catch(IOException | UnsupportedOperationException exc)
        {
            final String ERR_MSG = "Problem browsing: `"
                                        + AppInfo.WIKI_WEB + "`";

            this.log.e( ERR_MSG + ": " + exc.getMessage() );
            this.setStatus( ERR_MSG );
        }
    }
    
    /** New action. */
    public void doNew()
    {
        final PathList DIR_CHOICE = this.getView().getDirChoicePanel().getDirList();
        String msg = "Create a new ";
        boolean createDir = false;
        String entryType = "file";
        
        log.i( "creating new..." );
        
        if ( DIR_CHOICE.isFocusOwner() ) {
            createDir = true;
            entryType = "directory";
        }

        msg += entryType;
        
        final InputBox INPUT_BOX = new InputBox(
                                this.getView().getWindow(),
                                this.actionNew.getLabel() + " - " + AppInfo.NAME,
                                msg,
                                "" );
        
        String entryName = INPUT_BOX.run();
        String status = "";
        
        log.i( "\tcreating new " + entryType );
        
        if ( entryName != null
          && !entryName.isEmpty() )
        {
            final Path PATH = Path.of(
                                this.getDirBrowser().getPath().toString(),
                                entryName );
            Entry entry = null;
            
            try {
                if ( createDir ) {
                    entry = new Directory( PATH );
                } else {
                    entry = new File( PATH );
                }
                
                entry.create();
                status = "created: " + entryName;
                log.i( "\t" + status );
                this.syncToCurrentDir();
            } catch(IOException exc) {
                status = "creating `" + entryName + "`: " + exc.getMessage();
                log.e( status );
            }
        } else {
            status = "cancelled by user";
            this.log.i( "\t" + status );
        }
        
        this.setStatus( status );
    }
    
    /** Rename action */
    public void doRename()
    {
        final Entry ENTRY = this.getChosenEntry();
        String status = "renaming...";

        this.log.i( status );
        
        if ( ENTRY != null ) {
            final String FILE_NAME = ENTRY.getFileName();
            final var INPUT_BOX = new InputBox(
                                        this.view.getWindow(),
                                        this.actionRename.getLabel(),
                                        "New name for: `" + FILE_NAME + "`",
                                        FILE_NAME );
            String newName = INPUT_BOX.run();
            
            this.log.i( "\trenaming from: `" + ENTRY.asCanonical() + "`" );

            if ( newName != null
              && !newName.isEmpty() )
            {
                // Set target
                final Entry NEW_ENTRY = Entry.from(
                                                Path.of(
                                                    ENTRY.getParentPath(),
                                                        newName ).toFile() );

                if ( this.askToContinueBecauseExists( NEW_ENTRY ) ) {
                    try {
                        ENTRY.renameTo( newName );
                        log.i( "\trenamed to: `" + newName + "`" );
                        this.syncToCurrentDir();
                    } catch(IOException exc) {
                        status = "renaming to `" + newName + "`" + exc.getMessage();
                        log.e( "\t" + status );
                    }
                } else {
                    status = "cancelled by user";
                    log.e( "\t + status ");
                }
            } else {
                status = "cancelled by user";
                log.e( "\t" + status );
            }
        } else {
            status = "missing file selection";
            log.e( "\t" + status );
        }
        
        this.setStatus( status );
    }
    
    /** Copy action */
    public void doCopy()
    {
        final Entry ENTRY = this.getChosenEntry();
        final String VERB = "copy";
        String status = "";
        
        try {
            final var TARGET = this.determineTargetForFileOp( VERB, ENTRY );
            
            if ( TARGET != null
              && this.askToContinueBecauseExists( TARGET ) )
            {
                log.i( "\tcopying from: `" + ENTRY.asCanonical()
                                + "` to `" + TARGET.asCanonical()
                                + "`" );

                ENTRY.copy( TARGET );
                status = "copied: `" + ENTRY + "`";
                log.i( "\t" + status );
                this.syncToCurrentDir();
            } else {
                status = "cancelled by user";
                log.e( "\t" + status );
            }
        } catch(IOException exc) {
            status = "while " + VERB + ": `" + ENTRY + "`: " + exc.getMessage();
            log.e( "\t" + status );
        }
        
        this.setStatus( status );
    }
    
    /** Move action */
    public void doMove()
    {
        final Entry ENTRY = this.getChosenEntry();
        final String VERB = "move";
        String status = "";
        
        try {
            final var TARGET = this.determineTargetForFileOp( VERB, ENTRY );
            
            if ( TARGET != null
              && this.askToContinueBecauseExists( TARGET ) )
            {
                log.i( "\tmoving from: `" + ENTRY.asCanonical()
                                + "` to `" + TARGET.asCanonical()
                                + "`" );

                ENTRY.move( TARGET );
                status = "moved: `" + ENTRY + "`";
                log.i( "\t" + status );
                this.syncToCurrentDir();
            } else {
                status = "cancelled by user";
            }
        } catch(IOException exc) {
            status = "while " + VERB + ": `" + ENTRY + "`: " + exc.getMessage();
            log.e( "\t" + status );
        }
        
        this.setStatus( status );
    }
    
    
    /** Determines the target for a copy or move.
      * @param verb the verb as a string, such as "copy" or "move"
      * @param ENTRY the original file.
      * @return the entry object for the target.
      */
    private Entry determineTargetForFileOp(String verb, final Entry ENTRY)
    {
        Entry toret = null;
        
        log.i( "prepare to " + verb );
        
        if ( ENTRY != null ) {
            log.i( "\t" + verb + ": `" + ENTRY.asCanonical() + "`" );

            try {
                final FileOpsDialog DLG_COPY = new FileOpsDialog(
                                                   this.getView().getFrame(),
                                                   ENTRY );

                Path targetPath = DLG_COPY.show();

                if ( targetPath != null ) {
                    toret = Entry.from( targetPath.toFile() );
                } else {
                    log.i( "\tcancelled by user" );
                }
            } catch(IOException exc) {
                log.e( "\twhile " + verb + ": `" + ENTRY + "`: " + exc.getMessage() );
            }
        } else {
            log.i( "\tmissing file selection ");
        }
        
        return toret;
    }
    
    /** Asks to continue because of the existence of the file.
      * @param ENTRY the entry that maybe exists.
      * @return true if the user chooses to continue, false otherwise
      */
    private boolean askToContinueBecauseExists(final Entry ENTRY)
    {
        boolean toret = true;
        
        if ( ENTRY.exists() ) {
            final AskBox ASKER = new AskBox(
                                        this.getView().getFrame(),
                                        "Overwrite? - " + AppInfo.NAME,
                                        "File already exists: `"
                                                + ENTRY.getFileName() + "`",
                                        "Do you want to overwrite it?",
                                        "Overwrite",
                                        "Cancel" );
            toret = ASKER.run();
        }        
        
        return toret;
    }
    
    /** Move action */
    public void doDelete()
    {
        final String LBL_DELETE = this.actionDelete.getLabel();
        final Entry ENTRY = this.getChosenEntry();
        String status;
        
        if ( ENTRY != null ) {
            final String FILE_NAME = ENTRY.getFileName();
            
            final var ASK_BOX = new AskBox(
                                this.getView().getFrame(),
                                LBL_DELETE,
                                LBL_DELETE + " `" + FILE_NAME,
                                "Are you sure?",
                                LBL_DELETE,
                                "Cancel" );
            if ( ASK_BOX.run() ) {
                // Delete it
                if ( ENTRY.getFile().delete() ) {
                    status = "deleted: `" + FILE_NAME + "`";
                    this.syncToCurrentDir();
                    this.log.i( status );
                } else {
                    status = "unable to delete: `" + FILE_NAME + "`";
                    this.log.e( status );
                }
            } else {
                status = "cancelled by the user";
                this.log.d( status );
            }
        } else {
            status = "no dir/file selected";
            this.log.e( status );
        }
        
        this.setStatus( status );
    }
    
    /** View action. */
    public void doView()
    {
        final Entry ENTRY = this.getChosenEntry();
                                        
        if ( ENTRY != null ) {
            this.doView( ENTRY.getPath() );
        } else {
            String status = "missing selection";
            
            log.i( status );
            this.setStatus( status );
        }
    }
    
    /** View action
      * @param FILE the file selected.
      */
    public void doView(final Path FILE)
    {
        final Desktop DESKTOP = Desktop.getDesktop();
        String status = "view " + FILE;

        try {
            this.log.i( "view `" + FILE.toString() + "`" );
            DESKTOP.open( FILE.toFile() );
        } catch(IOException exc)
        {
            status = "problem opening: `" + FILE.toString() + "`: " + exc.toString();
            this.log.e( status );
        }
        
        this.setStatus( status );        
    }
    
    /** Refresh action. */
    public void doRefresh()
    {
        this.syncToCurrentDir();
    }
    
    /** Show hidden action. */
    public void doShowHidden()
    {
        boolean val = !this.cfg.isShowingHiddenFiles().get();
        
        this.log.i( "showHiddenFiles `" + val + "`" );
        this.cfg.setShowHiddenFiles( Cfg.HiddenFilesVisibility.from( val ) );
        this.syncToCurrentDir();
    }
    
    /** Avoids calling an overridable method from ctor.
      * @param PATH the path to change to.
      * @return true if cd was successful, false otherwise.
      */
    private boolean doCd(final Path PATH)
    {
        boolean toret = super.cd( PATH );
        
        if ( toret ) {
            this.log.i( "cd `" + PATH + "`" );
        } else {
            this.log.e( "cd `" + PATH + "`: cannot change to" );
        }
        
        return toret;
    }
    
    /** The about action. */
    public void doAbout()
    {
        final MessageBox MBOX = new MessageBox(
                                        this.getView().getFrame(),
                                        AppInfo.NAME,
                                        AppInfo.getFullName() );
        
        MBOX.run();
    }
    
    /** Copies the CWD to the clipboard. */
    private void doCopyCWD(String text)
    {
        final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
        final Clipboard CLIPBOARD = TOOLKIT.getSystemClipboard();
        
        CLIPBOARD.setContents( new StringSelection( text ), null );
    }
        
    /** Show or hide the output. */
    public void doViewOutput()
    {
        boolean val = !this.cfg.isOutputVisible().get();
        
        this.cfg.setViewOutput( Cfg.OutputPanelVisibility.from( val ) );
        this.getView().getOutput().setVisible( val );
        this.getView().getOutput().revalidate();
    }
    
    private void setStatus(String msg)
    {
        final var STATUS = this.getView().getStatusBar();
        final Size FREE = new Size( this.dirBrowser.getDriveFreeSize() );
        final Size TOTAL = new Size( this.dirBrowser.getDriveSize() );
        final String TXT = String.format(
                                    "Free: %s / %s | %s",
                                    FREE.toString(),
                                    TOTAL.toString(),
                                    msg );
        
        STATUS.setText( TXT );
    }
    
    private final Action actionAbout;
    private final Action actionQuit;
    private final Action actionHelp;
    private final Action actionNew;
    private final Action actionRename;
    private final Action actionCopy;
    private final Action actionMove;
    private final Action actionDelete;
    private final Action actionView;
    private final Action actionShowHidden;
    private final Action actionRefresh;
    private final Action actionViewOutput;
    
    private final Logger log;
    
    private class KeyboardDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e)
        {
            if ( e.getID() == KeyEvent.KEY_PRESSED ) {
                switch ( e.getKeyCode() ) {
                    case KeyEvent.VK_F1 -> {
                        MainWindow.this.actionHelp.doIt();
                        return true;
                    }
                    case KeyEvent.VK_F2 -> {
                        MainWindow.this.actionRename.doIt();
                        return true;
                    }
                    case KeyEvent.VK_F5 -> {
                        MainWindow.this.actionView.doIt();
                        return true;
                    }
                    case KeyEvent.VK_F6 -> {
                        MainWindow.this.actionShowHidden.doIt();
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
