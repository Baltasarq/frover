// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>

package com.devbaltasarq.frover.ui;


import java.nio.file.Path;
import java.io.IOException;
import java.util.Comparator;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.MenuShortcut;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import com.devbaltasarq.frover.core.AppInfo;
import com.devbaltasarq.frover.core.DirBrowser;
import com.devbaltasarq.frover.core.Size;
import com.devbaltasarq.frover.core.Cfg;


/** Control for MainWindow.
  * @author baltasarq
  */
public class MainWindowCtrl extends DirBrowserCtrl {
    public MainWindowCtrl(Path path)
    {
        this( new MainWindowView(), path );
    }
    
    public MainWindowCtrl(MainWindowView view, Path path)
    {
        super( view );
        
        this.log = new Logger( this.getView().getOutput() );
        
        this.actionAbout = new Action( "about", "About" );
        this.actionQuit = new Action( "quit", "Quit" );
        this.actionHelp = new Action( "help", "Help" );
        this.actionRename = new Action( "rename", "Rename" );
        this.actionShowHidden = new Action( "show_hidden", "Show hidden" );
        this.actionCopy = new Action( "copy", "Copy" );
        this.actionMove = new Action( "move", "Move" );
        this.actionDelete = new Action( "delete", "Delete" );
        this.actionView = new Action( "view", "View" );
        this.actionViewOutput = new Action( "view_output", "View output" );
        
        this.buildListeners();
        this.getView().getOutput().setVisible( this.cfg.isOutputVisible().get() );

        this.log.i( AppInfo.getFullName() + "\n" );
        this.cd( path );
    }
    
    /** Starts the app by showing it. */
    public void start()
    {
        this.getView().getWindow().setVisible( true );
    }
    
    /** @return the directory browser for the current dir. */
    public DirBrowser getDirBrowser()
    {
        return this.dirBrowser;
    }
    
    /** @return the corresponding view. */
    public final MainWindowView getView()
    {
        return (MainWindowView) this.view;
    }

    private void buildListeners()
    {
        this.buildActions();
        
        this.getView().getDirChoicePanel().setChangeDirAction(
                                                (p) -> this.cd( p ) );
        this.getView().getDirChoicePanel().setCopyCWDAction(
                                                (t) -> this.doCopyCWD( t ) );
        this.getView().getFileChoicePanel().setSelectFileAction(
                                                (p) -> this.doView( p ) );
        
        this.getView().getWindow().addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                MainWindowCtrl.this.actionQuit.doIt();
            }
            
            public void keyPressed(KeyEvent e)
            {
                if ( e.getKeyCode() == KeyEvent.VK_F1 ) {
                    
                }
            }
        });
        
        this.buildMenuListeners();
    }
    
    private void buildActions()
    {
        // View output
//        this.actionViewOutput.add( this.view.getAbout() );
        this.actionAbout.set( () -> this.doAbout());
        
        // Quit
//        this.actionQuit.add( this.view.getOpQuit() );
        this.actionQuit.set( () -> System.exit( 0 ) );
        
        // Help
//        this.actionHelp.add( this.view.getHelpButton() );
//        this.actionHelp.add( this.view.getOpHelp() );
        this.actionHelp.set( () -> this.doHelp() );
        
        // Rename
//        this.actionRename.add( this.view.getRenameButton() );
//        this.actionRename.add( this.view.getOpRename() );
        this.actionRename.set( () -> this.doRename() );
        
        // Copy
//        this.actionCopy.add( this.view.getCopyButton() );
//        this.actionCopy.add( this.view.getOpCopy() );       
        this.actionCopy.set( () -> this.doCopy() );
        
        // Move
//        this.actionMove.add( this.view.getMoveButton() );
//        this.actionMove.add( this.view.getOpMove() );       
        this.actionMove.set( () -> this.doMove() );
        
        // Delete
//        this.actionDelete.add( this.view.getDeleteButton() );
//        this.actionDelete.add( this.view.getOpDelete() );       
        this.actionDelete.set( () -> this.doDelete() );
        
        // View
//        this.actionView.add( this.view.getViewButton() );
//        this.actionView.add( this.view.getOpView() );       
        //this.actionView.set( () -> this.doView() );
        
        // Show hidden
//        this.actionShowHidden.add( this.view.getShowHiddenButton() );
//        this.actionShowHidden.add( this.view.getOpShowHidden() );       
        this.actionShowHidden.set( () -> this.doShowHidden());
        
        // View output
//        this.actionViewOutput.add( this.view.getOpViewOutput() );
        this.actionViewOutput.set( () -> this.doViewOutput());
    }
    
    private void buildMenuListeners()
    {
        final var OP_QUIT = this.getView().getOpQuit();
        final var OP_RENAME = this.getView().getOpRename();
        final var OP_COPY = this.getView().getOpCopy();
        final var OP_DELETE = this.getView().getOpDelete();
        final var OP_VIEW = this.getView().getOpView();
        final var OP_SHOW_HIDDEN = this.getView().getOpShowHidden();
        final var OP_VIEW_OUTPUT = this.getView().getOpViewOutput();
        final var OP_ABOUT = this.getView().getAbout();
        
        OP_QUIT.setShortcut( new MenuShortcut( KeyEvent.VK_Q ) );
        OP_RENAME.setShortcut( new MenuShortcut( KeyEvent.VK_F2 ) );
        OP_COPY.setShortcut( new MenuShortcut( KeyEvent.VK_C ) );
        OP_DELETE.setShortcut( new MenuShortcut( KeyEvent.VK_DELETE ) );
        OP_VIEW.setShortcut( new MenuShortcut( KeyEvent.VK_F5 ) );
        OP_SHOW_HIDDEN.setShortcut( new MenuShortcut( KeyEvent.VK_F6 ) );

        // OpQuit
        OP_QUIT.addActionListener( (ActionEvent e) -> {
            this.actionQuit.doIt();
        });
        
        // OpView
        OP_VIEW.addActionListener( (ActionEvent e) -> {
            this.actionView.doIt();
        });
        
        // OpShowHidden
        OP_SHOW_HIDDEN.addActionListener( (ActionEvent e) -> {
            this.actionShowHidden.doIt();
        });
        
        // OpViewOutput
        OP_VIEW_OUTPUT.addActionListener( (ActionEvent e) -> {
            this.actionViewOutput.doIt();
        });
        
        // OpAbout
        OP_ABOUT.addActionListener(  (ActionEvent e) -> {
            this.actionAbout.doIt();
        });
        
        // OpDelete
        OP_DELETE.addActionListener(  (ActionEvent e) -> {
            this.actionDelete.doIt();
        });
    }
    
    private void syncToCurrentDir()
    {
        final Path CWD = this.dirBrowser.getDirectory().getPath();
        final Comparator<? super Path> SORTER = (p1, p2) -> p1.compareTo( p2 );
        String status = "Ready";
        
        try {
            super.syncToDir( CWD, this.dirBrowser.readDir(), SORTER );
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
        this.log.i( "launching help" );
    }
    
    /** Rename action */
    public void doRename()
    {
        this.log.i( "renaming..." );
    }
    
    /** Copy action */
    public void doCopy()
    {
        this.log.i( "copying..." );
    }
    
    /** Move action */
    public void doMove()
    {
        this.log.i( "moving..." );
    }
    
    /** Move action */
    public void doDelete()
    {
        final PathList DIR_CHOICE = this.getView().getDirChoicePanel().getDirList();
        final PathList FILE_CHOICE = this.getView().getFileChoicePanel().getFileList();
        final String LBL_DELETE = this.actionDelete.getLabel();
        PathList targetList = FILE_CHOICE;
        String status = "Ready";
        Path path;
        
        // Determine the list
        if ( DIR_CHOICE.isFocusOwner() ) {
            targetList = DIR_CHOICE;
        }
        
        // Determine the path
        int pos = targetList.getSelectedIndex();
        
        if ( pos >= 0 ) {
            path = targetList.getPathAt( pos );
            
            final var ASK_BOX = new AskBox(
                                this.getView().getFrame(),
                                LBL_DELETE,
                                LBL_DELETE + " `" + path.getFileName(),
                                "Are you sure?",
                                LBL_DELETE,
                                "Cancel" );
            if ( ASK_BOX.run() ) {
                // Delete it
                if ( path.toFile().delete() ) {
                    status = "deleted: `" + path.getFileName() + "`";
                    this.syncToCurrentDir();
                    this.log.i( status );
                } else {
                    status = "unable to delete: `" + path.getFileName() + "`";
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
    
    /** View action
      * @param FILE the file selected.
      */
    public void doView(final Path FILE)
    {
        final Desktop DESKTOP = Desktop.getDesktop();

        try {
            this.log.i( "view `" + FILE.toString() + "`" );
            DESKTOP.open( FILE.toFile() );
        } catch(IOException exc)
        {
            final String ERR_MSG = "Problem opening: `" + FILE.toString() + "`";

            this.log.e( ERR_MSG );
            this.setStatus( ERR_MSG );
        }
    }
    
    /** Show hidden action. */
    public void doShowHidden()
    {
        boolean val = !this.cfg.isShowingHiddenFiles().get();
        
        this.log.i( "showHiddenFiles `" + val + "`" );
        this.cfg.setShowHiddenFiles( Cfg.HiddenFilesVisibility.from( val ) );
        this.syncToCurrentDir();
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
    
    /** Changes directory to the given path.
      * @param path the given path to change to.
      */
    private void cd(final Path PATH)
    {
        try {
            this.dirBrowser = new DirBrowser( PATH );
            this.log.i( "cd `" + PATH + "`" );
            this.syncToCurrentDir();
        } catch(IOException exc) {
            this.log.e( "cd `" + PATH + "`: cannot change to" );
        }
        
        return;
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
    private final Action actionRename;
    private final Action actionCopy;
    private final Action actionMove;
    private final Action actionDelete;
    private final Action actionView;
    private final Action actionShowHidden;
    private final Action actionViewOutput;
    
    private final Logger log;
}
