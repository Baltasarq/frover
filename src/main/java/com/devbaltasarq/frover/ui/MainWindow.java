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
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.devbaltasarq.frover.core.AppInfo;
import com.devbaltasarq.frover.core.Config;
import com.devbaltasarq.frover.core.LogWriter;
import com.devbaltasarq.frover.core.Size;
import com.devbaltasarq.frover.core.ViewSettings;
import com.devbaltasarq.frover.core.CmdExecutor;
import com.devbaltasarq.frover.core.DirBrowser;
import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.entries.Directory;
import com.devbaltasarq.frover.core.entries.File;
import com.devbaltasarq.frover.core.OpenerEngine;
import com.devbaltasarq.frover.core.TermPath;
import com.devbaltasarq.frover.core.entries.Extension;
import com.devbaltasarq.frover.ui.dlg.InputDlg;
import com.devbaltasarq.frover.ui.components.NamedPathList;
import com.devbaltasarq.frover.ui.components.PathList;
import com.devbaltasarq.frover.ui.mainwindow.MainWindowView;
import javax.swing.JSplitPane;


/** Control for MainWindow.
  * @author baltasarq
  */
public class MainWindow extends Browser {
    private static final Logger LOG = Logger.getLogger( MainWindow.class.getName() );
    private static final String MSG_STATUS_SHOW_HIDDEN = "Showing hidden files";
    private static final String MSG_STATUS_HIDE_HIDDEN = "Hiding hidden files";
    
    public MainWindow(Path path)
    {
        this( new MainWindowView(), path );
    }
    
    public MainWindow(MainWindowView view, Path path)
    {
        super( view );
        
        final var VIEW = (MainWindowView) this.view;
        
        this.isBuild = true;
        this.config = Config.restore( AppInfo.NAME );
        this.openerEngine = OpenerEngine.build( this.config );
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
        
        this.termPath = TermPath.build( this.config );
        this.build( path );
        new UICfgBridge( VIEW, this.config ).applyCfgToUI();
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
                Dlg.showInfo(
                                this.getView().getFrame(),
                                AppInfo.NAME,
                                "ERROR: " + MSG );
            }
        }
    }
    
    /** Starts the app by showing it. */
    public void run()
    {
        this.getView().getWindow().setVisible( true );
        this.showOutput( false );
        this.isBuild = false;
    }
    
    /** @return the corresponding view. */
    public final MainWindowView getView()
    {
        return (MainWindowView) this.view;
    }
    
    private void buildListeners()
    {
        final JSplitPane SPLIT_PANEL = this.getView().getSplitPanel();
        final JButton BtExe =
                    this.getView().getButton( MainWindowView.Buttons.BtExe );

        SPLIT_PANEL.addPropertyChangeListener(
                        JSplitPane.DIVIDER_LOCATION_PROPERTY,
                        (pce) -> this.doMainDividerMovement() );
        
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
                                        (p) -> this.doView( Entry.from( p ) ) );
        this.getView().
                getVisitedDirChoicePanel().setChangeDirAction(
                                        (p) -> this.doCd( p ));
        this.getView().
                getVisitedDirChoicePanel().setNewFavAction(
                                        () -> this.doNewFavDir() );
        this.getView().
                getVisitedDirChoicePanel().setRemoveFavAction(
                                        () -> this.doRemoveFavDir() );
        
        BtExe.addActionListener( (evt) -> this.doCmd() );
        
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
        final var BT_HELP =
                    this.getView().getButton( MainWindowView.Buttons.BtHelp );
        final var BT_RENAME =
                    this.getView().getButton( MainWindowView.Buttons.BtRename );
        final var BT_COPY =
                    this.getView().getButton( MainWindowView.Buttons.BtCopy );
        final var BT_DELETE =
                    this.getView().getButton( MainWindowView.Buttons.BtDelete );
        final var BT_VIEW =
                    this.getView().getButton( MainWindowView.Buttons.BtView );

        // View output
        this.actionAbout.add( this.getView().getOpAbout() );
        this.actionAbout.set( () -> this.doAbout() );
        
        // Help
        this.actionHelp.add( BT_HELP );
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
        this.actionRename.add( BT_RENAME );
        this.actionRename.add( this.getView().getOpRename() );
        this.actionRename.set( () -> this.doRename() );
        
        // Copy
        this.actionCopy.add( BT_COPY );
        this.actionCopy.add( this.getView().getOpCopy() );       
        this.actionCopy.set( () -> this.doCopy() );
  /*      
        // Move
        this.actionMove.add( BT_MOVE );
        this.actionMove.add( this.getView().getOpMove() );       
        this.actionMove.set( () -> this.doMove() );
    */    
        // Delete
        this.actionDelete.add( BT_DELETE );
        this.actionDelete.add( this.getView().getOpDelete() );       
        this.actionDelete.set( () -> this.doDelete() );
        
        // View
        this.actionView.add( BT_VIEW );
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
        final var OP_RENAME = this.getView().getOpRename();
        final var OP_DELETE = this.getView().getOpDelete();
        final var OP_NEW = this.getView().getOpNew();
        final var OP_SHELL = this.getView().getOpShell();
        final var OP_VIEW = this.getView().getOpView();
        
        OP_QUIT.setAccelerator(
                        KeyStroke.getKeyStroke(
                                        KeyEvent.VK_Q,
                                        InputEvent.CTRL_DOWN_MASK ) );
        OP_NEW.setAccelerator(
                        KeyStroke.getKeyStroke(
                                        KeyEvent.VK_N,
                                        InputEvent.CTRL_DOWN_MASK ) );
        OP_COPY.setAccelerator(
                        KeyStroke.getKeyStroke(
                                        KeyEvent.VK_C,
                                        InputEvent.CTRL_DOWN_MASK ) );
        OP_MOVE.setAccelerator(
                        KeyStroke.getKeyStroke(
                                        KeyEvent.VK_C,
                                        InputEvent.SHIFT_DOWN_MASK
                                        | InputEvent.CTRL_DOWN_MASK ) );
        OP_DELETE.setAccelerator(
                       KeyStroke.getKeyStroke(
                                        KeyEvent.VK_DELETE,
                                        InputEvent.CTRL_DOWN_MASK ) );
        OP_RENAME.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ) );
        OP_VIEW.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ) );
        OP_SHELL.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, 0 ) );
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
        String entryName = "file.txt";
        
        LOG.info( "creating new..." );
        
        if ( DIR_CHOICE.isFocusOwner() ) {
            createDir = true;
            entryType = "directory";
            entryName = "newDir";
        }

        msg += entryType;
        
        final var DLG = new InputDlg(
                                this.getView().getFrame(),
                                this.actionNew.getLabel() + " - " + AppInfo.NAME,
                                msg,
                                entryName,
                                0,
                                entryName.lastIndexOf( "." ) );
        entryName = DLG.run();
        LOG.info( "\tcreating new " + entryType );

        String status = "";
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
            final var INPUT_BOX = new InputDlg(
                                        this.getView().getFrame(),
                                        this.actionRename.getLabel(),
                                        "New name for: `" + FILE_NAME + "`",
                                        FILE_NAME,
                                        0,
                                        FILE_NAME.indexOf( '.' ) );
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
            toret = Dlg.askYesNo(
                            this.getView().getFrame(),
                            "Overwrite? - " + AppInfo.NAME,
                            "File already exists: `"
                                    + ENTRY.getFileName() + "`"
                            + "\nDo you want to overwrite it?",
                            "Overwrite",
                            "Cancel" );
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
            
            final var resultDlg = Dlg.askYesNo(
                                    this.getView().getFrame(),
                                    LBL_DELETE,
                                    LBL_DELETE + " `" + FILE_NAME
                                    + "\nAre you sure?",
                                    LBL_DELETE,
                                    "Cancel" );
            if ( resultDlg ) {
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
            this.doView( ENTRY );
        } else {
            String status = "missing selection";
            
            LOG.info( status );
            this.setStatus( status );
        }
    }
    
    /** View action
      * @param FILE the file selected.
      */
    public void doView(final Entry FILE)
    {
        final Desktop DESKTOP = Desktop.getDesktop();
        String status = "view " + FILE;

        try {
            final Extension EXT = Extension.from( FILE );
            final File APP = this.openerEngine.getAppFor( EXT );
            
            LOG.info( "view `" + FILE.toString() + "`" );
            
            if ( APP != null ) {
                this.openerEngine.open(
                                    APP,
                                    FILE,
                                    this.logViewer.getWriter() );
            } else {
                DESKTOP.open( FILE.getPath().toFile() );
            }
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
        this.cfg.setShowHiddenFiles(ViewSettings.HiddenFilesVisibility.from( val ) );
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
        Dlg.showInfo(
                        this.getView().getFrame(),
                        AppInfo.NAME,
                        AppInfo.getFullName() );
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
    
    public void doMainDividerMovement()
    {
        if ( !isBuild ) {
            int height = this.getView().getWindow().getHeight();
            int dividerLocation = this.getView().getSplitPanel().getDividerLocation();
    System.out.println( "---Divided moved" );
            if ( height > 0 ) {
                double pos = dividerLocation / height;

                if ( pos >= 0.9 ) {
                    this.showOutput( false );
                } else {
                    this.showOutput( true );
                }
            }
    System.out.println( "---End of Divided moved" );
        }
    }
    
    /** Show/hide the output.
      * @param visible true if the output is to be visible, false otherwise.
      */
    public void showOutput(boolean visible)
    {
        final var SPLIT_PANEL = this.getView().getSplitPanel();

        this.getView().getLogViewer().setVisible( visible );
        this.getView().getLogViewer().revalidate();
        this.cfg.setViewOutput(ViewSettings.OutputPanelVisibility.from( visible ) );
        
        isBuild = true;
        
        if ( !visible ) {
            SPLIT_PANEL.setDividerLocation( 1.0 );
        } else {
            SPLIT_PANEL.setDividerLocation( 0.8 );
        }
        
        isBuild = false;
    }
    
    /** Execute a command. */
    public void doCmd()
    {
        final var ED_CMD = this.getView().getEdCmd();
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
        final var DLG_INPUT = new InputDlg(
                        this.getView().getFrame(),
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
        final Entry PATH = this.dirBrowser.getDirectory();
        String status = "opening shell in: " + PATH;
        
        LOG.info( "opening shell in: " + PATH );
        
        List<String> cmd = TermPath.get().getCmds();
        
        LOG.info( "term path is: " + String.join( "\n", cmd ) );
                
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
        int numDirs = this.getView().getDirChoicePanel().getDirList().count();
        int numFiles = this.getView().getFileChoicePanel().getFileList().count();
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
                                    "Free: %s / %s | %s | %s | %d dirs | %d files | %s",
                                    FREE.toString(),
                                    TOTAL.toString(),
                                    SHOW_HIDDEN ? MSG_STATUS_SHOW_HIDDEN
                                            : MSG_STATUS_HIDE_HIDDEN,
                                    "Size: " + size,
                                    numDirs,
                                    numFiles,
                                    msg );
        
        STATUS.setText( TXT );
    }
    
    /** Saves the settings to the config. */
    private void saveConfig()
    {
        final var WIN = ( (MainWindowView) this.view ).getWindow();
        final var FAV_DIR_LIST = this.getView().getVisitedDirChoicePanel().getFavList();
        final var FILE_ASSOCS = this.openerEngine.all();
        final List<NamedPathList.NamedPath> FAV_PAIRS = FAV_DIR_LIST.getAll();
        final List<String> STR_FAV_NAMES = FAV_PAIRS.stream()
                                                .map( p -> p.getName() ).toList();
        final List<String> STR_FAV_DIRS = FAV_PAIRS.stream()
                                                .map(
                                                  p -> p.getPath().toString() )
                                                .toList();
        final List<String> STR_EXTS = FILE_ASSOCS.stream()
                                                .map( p -> p.getKey().get() )
                                                .toList();
        final List<String> STR_APPS = FILE_ASSOCS.stream()
                                                .map(
                                                  p -> p.getValue().asCanonical() )
                                                .toList();
        
        this.config.add( Config.Key.WIDTH, "" + WIN.getWidth() );
        this.config.add( Config.Key.HEIGHT, "" + WIN.getHeight() );
        this.config.add( Config.Key.LEFT, "" + WIN.getLocation().x );
        this.config.add( Config.Key.TOP, "" + WIN.getLocation().y );
        this.config.addList(Config.Key.FAV_NAMES,
                                STR_FAV_NAMES.toArray( String[]::new ) );
        this.config.addList( Config.Key.FAV_DIRS,
                                STR_FAV_DIRS.toArray( String[]::new ) );
        this.config.addList( Config.Key.EXTENSIONS,
                                STR_EXTS.toArray( String[]::new ) );
        this.config.addList(Config.Key.APP_PATHS,
                                STR_APPS.toArray( String[]::new ) );
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

    private final TermPath termPath;
    private final LogWriter logViewer;
    private final Config config;
    private final OpenerEngine openerEngine;
    private boolean isBuild;
    
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
