// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

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
import com.devbaltasarq.frover.core.Config;
import com.devbaltasarq.frover.core.LogWriter;
import com.devbaltasarq.frover.core.Size;
import com.devbaltasarq.frover.core.Cfg;
import com.devbaltasarq.frover.core.CmdExecutor;
import com.devbaltasarq.frover.core.DirBrowser;
import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.entries.Directory;
import com.devbaltasarq.frover.core.entries.File;
import com.devbaltasarq.frover.ui.box.MessageBox;
import com.devbaltasarq.frover.ui.box.InputBox;
import com.devbaltasarq.frover.ui.box.AskBox;
import com.devbaltasarq.frover.ui.components.NamedPathList;
import com.devbaltasarq.frover.ui.components.PathList;
import com.devbaltasarq.frover.ui.mainwindow.MainWindowView;
import java.awt.TextField;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;


/** Control for MainWindow.
  * @author baltasarq
  */
public class MainWindow extends Browser {
    private static final Logger LOG = Logger.getLogger( MainWindow.class.getName() );
    private static final String MSG_STATUS_SHOW_HIDDEN = "Showing hidden files";
    private static final String MSG_STATUS_HIDE_HIDDEN = "Hiding hidden files";
    private static final List<String> WIN_SHELL = List.of(
                                "cmd",
                                "/c",
                                "start",
                                "cmd.exe" );

    private static final List<String> NIX_SHELL =  List.of(
                                "/usr/bin/xterm",
                                "-fa", "Monospace",
                                "-fs", "12" );
    
    public MainWindow(Path path)
    {
        this( new MainWindowView(), path );
    }
    
    public MainWindow(MainWindowView view, Path path)
    {
        super( view );
        
        final var VIEW = (MainWindowView) this.view;
        
        this.config = Config.restore( AppInfo.NAME );
        this.logViewer = new LogWriter( LOG, 
                                   (str) ->
                                        VIEW.getLogViewer().append( str + "\n" ));
        LOG.addHandler( this.logViewer );
        
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
        this.actionOpenShell = new Action( "open_shell", "Open shell" );
        
        this.build( path );
        this.applyConfig();
    }
    
    private void build(Path path)
    {   
        this.buildListeners();
        this.getView().getLogViewer().setVisible( this.cfg.isOutputVisible().get() );

        LOG.info( AppInfo.getFullName() + "\n" );
        
        try {
            this.dirBrowser = new DirBrowser( path );
        } catch(IOException exc) {
            try {
                this.dirBrowser = new DirBrowser();
            } catch(IOException exc2) {
                final String MSG = "booting failed";
                
                LOG.severe( MSG );
                final var MBOX = new MessageBox(
                                        this.getView().getFrame(),
                                        AppInfo.NAME,
                                        "ERROR: " + MSG );
                MBOX.run();
            }
        }
    }
    
    /** Starts the app by showing it. */
    public void run()
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
        
        this.getView().
                getDirChoicePanel().setCopyCWDAction(
                                        (t) -> this.doCopyCWD( t ) );
        this.getView().
                getFileChoicePanel().setSelectFileAction(
                                        (p) -> this.setStatus(
                                                p.getFileName().toString() ) );
        this.getView().
                getFileChoicePanel().setOpenFileAction(
                                        (p) -> this.doView( p ) );
        this.getView().
                getVisitedDirChoicePanel().setChangeDirAction(
                                        (p) -> this.doCd( p ));
        this.getView().
                getVisitedDirChoicePanel().setNewFavAction(
                                        () -> this.doNewFavDir() );
        this.getView().
                getVisitedDirChoicePanel().setRemoveFavAction(
                                        () -> this.doRemoveFavDir() );
        
        this.getView().getBtExe().addActionListener( (evt) -> this.doCmd() );
        
        this.getView().getEdCmd().addFocusListener( new FocusListener() {
            @Override
            public void focusLost(FocusEvent evt)
            {
                MainWindow.this.syncToCurrentDir();
            }
            
            @Override
            public void focusGained(FocusEvent evt)
            {
            }
        });
        
        this.getView().getEdCmd().addKeyListener( new KeyListener() {
            @Override
            public void keyReleased(KeyEvent evt)
            {
                if ( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
                    MainWindow.this.doCmd();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent evt)
            {                
            }
            
            @Override
            public void keyTyped(KeyEvent evt)
            {   
            }
        });
        
        this.getView().getFrame().addWindowListener( new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent we)
            {
                MainWindow.this.syncToCurrentDir();
            }
            
            @Override
            public void windowClosing(WindowEvent we)
            {
                MainWindow.this.doQuit();
            }});
        
        final var KEY_MAN = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        KEY_MAN.addKeyEventDispatcher( new KeyboardDispatcher() );

        this.buildMenuListeners();
    }
    
    private void buildActions()
    {
        // View output
        this.actionAbout.add( this.getView().getOpAbout() );
        this.actionAbout.set( () -> this.doAbout() );
        
        // Help
        this.actionHelp.add( this.getView().getHelpButton() );
        this.actionHelp.add( this.getView().getOpHelp() );
        this.actionHelp.set( () -> this.doHelp() );
        
        // View output
        this.actionViewOutput.add( this.getView().getOpViewOutput() );
        this.actionViewOutput.set( () -> this.doViewOutput() );
        
        // Open in shell
        this.actionOpenShell.add( this.getView().getOpShell() );
        this.actionOpenShell.set( () -> this.doOpenInShell() );
        
        // Quit
        this.actionQuit.add( this.getView().getOpQuit() );
        this.actionQuit.set( () -> this.doQuit() );
        
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
    }
    
    private void buildMenuListeners()
    {
        final var OP_QUIT = this.getView().getOpQuit();
        final var OP_COPY = this.getView().getOpCopy();
        final var OP_MOVE = this.getView().getOpMove();
        final var OP_DELETE = this.getView().getOpDelete();
        final var OP_NEW = this.getView().getOpNew();
        final var OP_SHELL = this.getView().getOpShell();
        final var OP_REFRESH = this.getView().getOpRefresh();
        
        OP_QUIT.setShortcut( new MenuShortcut( KeyEvent.VK_Q ) );
        OP_NEW.setShortcut( new MenuShortcut( KeyEvent.VK_N ) );
        OP_COPY.setShortcut( new MenuShortcut( KeyEvent.VK_C ) );
        OP_MOVE.setShortcut( new MenuShortcut( KeyEvent.VK_C, true ) );
        OP_DELETE.setShortcut( new MenuShortcut( KeyEvent.VK_DELETE ) );
        OP_REFRESH.setShortcut( new MenuShortcut( KeyEvent.VK_F5 ) );
        OP_SHELL.setShortcut( new MenuShortcut( KeyEvent.VK_F4 ) );
    }
    
    @Override
    public void syncToCurrentDir()
    {
        String status = "Ready";
        
        try {
            super.syncToCurrentDir();
            LOG.info( "sync `" + this.dirBrowser.getDirectory().asCanonical() + "`" );
        } catch(IOException exc)
        {
            LOG.severe( exc.getMessage() );
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
    
    /** Quit action **/
    public void doQuit()
    {
        this.getView().getFrame().setVisible( false );
        this.getView().getFrame().dispose();
        this.saveConfig();
        System.exit( 0 );
    }
    
    /** Help action */
    public void doHelp()
    {
        final Desktop DESKTOP = Desktop.getDesktop();

        try {
            LOG.info( "launching browser for: `" + AppInfo.WIKI_WEB + "`" );
            DESKTOP.browse( new java.io.File( AppInfo.WIKI_WEB ).toURI() );
        } catch(IOException | UnsupportedOperationException exc)
        {
            final String ERR_MSG = "Problem browsing: `"
                                        + AppInfo.WIKI_WEB + "`";

            LOG.severe( ERR_MSG + ": " + exc.getMessage() );
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
        
        LOG.info( "creating new..." );
        
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
        
        LOG.info( "\tcreating new " + entryType );
        
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
                LOG.info( "\t" + status );
                this.syncToCurrentDir();
            } catch(IOException exc) {
                status = "creating `" + entryName + "`: " + exc.getMessage();
                LOG.severe( status );
            }
        } else {
            status = "cancelled by user";
            LOG.info( "\t" + status );
        }
        
        this.setStatus( status );
    }
    
    /** Rename action */
    public void doRename()
    {
        final Entry ENTRY = this.getChosenEntry();
        String status = "renaming...";

        LOG.info( status );
        
        if ( ENTRY != null ) {
            final String FILE_NAME = ENTRY.getFileName();
            final var INPUT_BOX = new InputBox(
                                        this.view.getWindow(),
                                        this.actionRename.getLabel(),
                                        "New name for: `" + FILE_NAME + "`",
                                        FILE_NAME );
            String newName = INPUT_BOX.run();
            
            LOG.info( "\trenaming from: `" + ENTRY.asCanonical() + "`" );

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
                        LOG.info( "\trenamed to: `" + newName + "`" );
                        this.syncToCurrentDir();
                    } catch(IOException exc) {
                        status = "renaming to `" + newName + "`" + exc.getMessage();
                        LOG.severe( "\t" + status );
                    }
                } else {
                    status = "cancelled by user";
                    LOG.severe( "\t + status ");
                }
            } else {
                status = "cancelled by user";
                LOG.severe( "\t" + status );
            }
        } else {
            status = "missing file selection";
            LOG.severe( "\t" + status );
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
                LOG.info( "\tcopying from: `" + ENTRY.asCanonical()
                                + "` to `" + TARGET.asCanonical()
                                + "`" );

                ENTRY.copy( TARGET );
                status = "copied: `" + ENTRY + "`";
                LOG.info( "\t" + status );
                this.syncToCurrentDir();
            } else {
                status = "cancelled by user";
                LOG.severe( "\t" + status );
            }
        } catch(IOException exc) {
            status = "while " + VERB + ": `" + ENTRY + "`: " + exc.getMessage();
            LOG.severe( "\t" + status );
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
                LOG.info( "\tmoving from: `" + ENTRY.asCanonical()
                                + "` to `" + TARGET.asCanonical()
                                + "`" );

                ENTRY.move( TARGET );
                status = "moved: `" + ENTRY + "`";
                LOG.info( "\t" + status );
                this.syncToCurrentDir();
            } else {
                status = "cancelled by user";
            }
        } catch(IOException exc) {
            status = "while " + VERB + ": `" + ENTRY + "`: " + exc.getMessage();
            LOG.severe( "\t" + status );
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
        
        LOG.info( "prepare to " + verb );
        
        if ( ENTRY != null ) {
            LOG.info( "\t" + verb + ": `" + ENTRY.asCanonical() + "`" );

            try {
                final FileOpsDialog DLG_COPY = new FileOpsDialog(
                                                   this.getView().getFrame(),
                                                   ENTRY );

                Path targetPath = DLG_COPY.show();

                if ( targetPath != null ) {
                    toret = Entry.from( targetPath.toFile() );
                } else {
                    LOG.info( "\tcancelled by user" );
                }
            } catch(IOException exc) {
                LOG.severe( "\twhile " + verb + ": `" + ENTRY + "`: " + exc.getMessage() );
            }
        } else {
            LOG.info( "\tmissing file selection ");
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
                    LOG.info( status );
                } else {
                    status = "unable to delete: `" + FILE_NAME + "`";
                    LOG.severe( status );
                }
            } else {
                status = "cancelled by the user";
                LOG.info( status );
            }
        } else {
            status = "no dir/file selected";
            LOG.severe( status );
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
            
            LOG.info( status );
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
            LOG.info( "view `" + FILE.toString() + "`" );
            DESKTOP.open( FILE.toFile() );
        } catch(IllegalArgumentException | IOException exc)
        {
            status = "problem opening: `" + FILE.toString() + "`: " + exc.toString();
            LOG.severe( status );
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
        
        LOG.info( "showHiddenFiles `" + val + "`" );
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
            LOG.info( "cd `" + PATH + "`" );
            
            this.getView().getVisitedDirChoicePanel().addDirToHistory( PATH );
        } else {
            LOG.severe( "cd `" + PATH + "`: cannot change to" );
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
        
        this.showOutput( val );
    }
    
    /** Show/hide the output.
      * @param visible true if the output is to be visible, false otherwise.
      */
    private void showOutput(boolean visible)
    {
        this.getView().getLogViewer().setVisible( visible );
        this.getView().getLogViewer().revalidate();
        this.cfg.setViewOutput( Cfg.OutputPanelVisibility.from( visible ) );
    }
    
    /** Execute a command. */
    public void doCmd()
    {
        final TextField ED_CMD = this.getView().getEdCmd();
        final var EXE_ENGINE = new CmdExecutor(
                                       this.dirBrowser.getPath(),
                                       ED_CMD.getText(),
                                       this.logViewer.getWriter() );
        
        new Thread(() -> {
            EXE_ENGINE.run();
        }).start();
        ED_CMD.setText( "" );
        this.showOutput( true );
    }
    
    /** Adds a new favourite dir to the favs panel. */
    public void doNewFavDir()
    {
        final var DIR = this.getDirBrowser().getDirectory();
        final var DLG_INPUT = new InputBox(
                        this.getView().getWindow(),
                        AppInfo.NAME,
                        "New favourite directory",
                        DIR.getFileName() );
        
        String name = DLG_INPUT.run();
        
        if ( !name.isBlank() ) {
            final var NAMED_PATH =
                        new NamedPathList.NamedPath( name, DIR.getPath() );
            this.getView().getVisitedDirChoicePanel().getFavList().add( NAMED_PATH );
            LOG.info( String.format(
                                    "storing favourite dir with name: '%s' -> '%s'",
                                    name,
                                    DIR ));
        } else {
            LOG.info( "adding favourite dir to panel: " + DIR );
        }
    }
    
    /** Removes a favourite dir on the favs panel. */
    public void doRemoveFavDir()
    {
        this.getView().getVisitedDirChoicePanel().removeFavDir();
    }
    
    /** Open in shell. */
    public void doOpenInShell()
    {
        LOG.info( "opening shell in: " + this.dirBrowser.getDirectory() );
        final Entry PATH = this.dirBrowser.getDirectory();
        String status = "opening shell in: " + PATH;
        List<String> cmd = NIX_SHELL;
        final var OS_NAME = System.getProperty( "os.name" ).toLowerCase();
        
        if ( OS_NAME.contains( "windows" ) ) {
            cmd = WIN_SHELL;
        }
        
        try {
            final var PROCESS = new ProcessBuilder( cmd )
                                    .directory( PATH.getFile() );

            PROCESS.start();
        } catch(IOException exc)
        {
            status = "problem " + status + ": " + exc.getMessage();
            LOG.severe( status );
        }
        
        this.setStatus( status );
    }
    
    private void setStatus(String msg)
    {
        final boolean SHOW_HIDDEN = this.cfg.isShowingHiddenFiles().get();
        final Entry ENTRY = this.getChosenEntry();
        final var STATUS = this.getView().getStatusBar();
        final Size FREE = new Size( this.dirBrowser.getDriveFreeSize() );
        final Size TOTAL = new Size( this.dirBrowser.getDriveSize() );
        String size = "N/A";
        
        if ( ENTRY instanceof File f) {
            try {
                size = new Size( Files.size( f.getPath() ) ).toString();
            } catch(IOException | SecurityException exc)
            {
                size = "[ERR]";
            }
        }
        
        final String TXT = String.format(
                                    "Free: %s / %s | %s | %s | %s",
                                    FREE.toString(),
                                    TOTAL.toString(),
                                    SHOW_HIDDEN ? MSG_STATUS_SHOW_HIDDEN
                                            : MSG_STATUS_HIDE_HIDDEN,
                                    "Size: " + size,
                                    msg );
        
        STATUS.setText( TXT );
    }
    
    /** Applies the configuration to the app. */
    private void applyConfig()
    {
        final var MAIN_VIEW = ( (MainWindowView) this.view );
        final var WIN = MAIN_VIEW.getWindow();
        final var FAV_DIR_LIST = MAIN_VIEW.getVisitedDirChoicePanel().getFavList();
        
        String strWidth = this.config.get( Config.Key.WIDTH );
        String strHeight = this.config.get( Config.Key.HEIGHT );
        String strLeft = this.config.get( Config.Key.LEFT );
        String strTop = this.config.get( Config.Key.TOP );
        final String[] FAV_NAMES = this.config.getList( Config.Key.FAV_NAMES );
        final String[] FAV_DIRS = this.config.getList( Config.Key.FAV_DIRS );
        
        if ( !strWidth.equals( "-1" )
          && !strHeight.equals( "-1" ) )
        {
            WIN.setSize(
                Integer.parseInt( strWidth ),
                Integer.parseInt( strHeight ));
        }
        
        if ( !strLeft.equals( "-1" )
          && !strTop.equals( "-1" ) )
        {
            WIN.setLocation(
                Integer.parseInt( strLeft ),
                Integer.parseInt( strTop ));
        }
        
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
    
    /** Saves the settings to the config. */
    private void saveConfig()
    {
        final var WIN = ( (MainWindowView) this.view ).getWindow();
        final var FAV_DIR_LIST = this.getView().getVisitedDirChoicePanel().getFavList();
        final List<NamedPathList.NamedPath> FAV_PAIRS = FAV_DIR_LIST.getAll();
        final List<String> STR_FAV_NAMES = FAV_PAIRS.stream()
                                                .map( p -> p.getName() ).toList();
        final List<String> STR_FAV_DIRS = FAV_PAIRS.stream()
                                                .map(
                                                  p -> p.getPath().toString() )
                                                .toList();
        
        this.config.add( Config.Key.WIDTH, "" + WIN.getWidth() );
        this.config.add( Config.Key.HEIGHT, "" + WIN.getHeight() );
        this.config.add( Config.Key.LEFT, "" + WIN.getLocation().x );
        this.config.add( Config.Key.TOP, "" + WIN.getLocation().y );
        this.config.addList(Config.Key.FAV_NAMES,
                                STR_FAV_NAMES.toArray( String[]::new ) );
        this.config.addList( Config.Key.FAV_DIRS,
                                STR_FAV_DIRS.toArray( String[]::new ) );
        this.config.save();
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
    private final Action actionOpenShell;

    private final LogWriter logViewer;
    private final Config config;
    
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
                    case KeyEvent.VK_F4 -> {
                        MainWindow.this.actionOpenShell.doIt();
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
