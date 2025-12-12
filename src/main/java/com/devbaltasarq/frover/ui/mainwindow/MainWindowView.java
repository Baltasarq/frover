// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.mainwindow;


import com.devbaltasarq.frover.ui.browser.BrowserView;
import com.devbaltasarq.frover.ui.components.DirChoicePanel;
import com.devbaltasarq.frover.ui.components.FileChoicePanel;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Menu;
import java.awt.Button;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;


/** App's main window
  * @author baltasarq
  */
public class MainWindowView extends BrowserView {
    private static final Dimension MIN_SIZE = new Dimension( 500, 400 );
    private static final Dimension START_SIZE = new Dimension( 620, 460 );
    private static final String ETQ_ICON_APP = "icon.png";
    private static final String ETQ_ICON_DIR_UP = "dir_up.png";
    
    public MainWindowView()
    {
        this( new Frame() );
    }
    
    public MainWindowView(final Frame FRAME)
    {
        super( FRAME );
        
        this.getFrame().setLocationByPlatform( true );
        this.build();
        this.getWindow().setSize( START_SIZE );
        this.getWindow().setMinimumSize( MIN_SIZE );
    }
    
    private void build()
    {
        final var LYB = (BorderLayout) this.getWindow().getLayout();
        final var LY_TOOLBAR = new BorderLayout();
        final var LY_OUTPUT = new BorderLayout();
        final var PNL_OUTPUT = new Panel( LY_TOOLBAR );
        final var PNL_TOOLBAR = new Panel( LY_OUTPUT );
        final var PNL_MAIN = new Panel( new GridLayout( 1, 2 ) );
        
        this.buildIcons();
        this.getFrame().setMenuBar( this.buildMenu() );
        
        LYB.setHgap( 5 );
        LYB.setVgap( 5 );
        LY_TOOLBAR.setHgap( 5 );
        LY_TOOLBAR.setVgap( 5 );
        LY_OUTPUT.setHgap( 5 );
        LY_OUTPUT.setVgap( 5 );
        
        PNL_MAIN.add( this.buildDirChoice() );
        PNL_MAIN.add( this.buildFileChoice() );
        
        PNL_OUTPUT.add( this.buildLogViewer(), BorderLayout.SOUTH );
        PNL_OUTPUT.add( PNL_MAIN, BorderLayout.CENTER );
        
        PNL_TOOLBAR.add( PNL_OUTPUT, BorderLayout.CENTER );
        PNL_TOOLBAR.add( this.buildToolbar(), BorderLayout.SOUTH );
        
        this.getWindow().add( PNL_TOOLBAR, BorderLayout.CENTER );
        this.getWindow().add( this.buildStatusBar(), BorderLayout.SOUTH );
        this.getWindow().setIconImage( this.iconApp );
        this.getWindow().pack();
    }
    
        /** Retrieves icons from jar for future use */
    private void buildIcons()
    {
        try {
            URL url;
            
            url = ClassLoader.getSystemResource( ETQ_ICON_APP );
            this.iconApp = Toolkit.getDefaultToolkit().getImage( url );
            
            url = ClassLoader.getSystemResource( ETQ_ICON_DIR_UP );
            this.iconDirUp = Toolkit.getDefaultToolkit().getImage( url );
        } catch(Exception exc)
        {
            System.err.println( "Could not load icon `" + ETQ_ICON_APP + "` from resources.");
        }
    }
    
    private Panel buildLogViewer()
    {
        final var LY_TOOLBAR = new BorderLayout();
        final var PANEL = new Panel( LY_TOOLBAR );
        final var FONT = new Font( Font.MONOSPACED, Font.PLAIN, 12 );

        this.logViewer = new TextArea();
        this.logViewer.setFont( FONT );
        this.logViewer.setBackground( Color.BLACK );
        this.logViewer.setForeground( Color.WHITE );
        this.logViewer.setColumns( 80 );
        this.logViewer.setRows( 5 );
        
        PANEL.add( this.logViewer, BorderLayout.CENTER );
        return PANEL;
    }
    
    private Panel buildToolbar()
    {
        var font = Font.decode( "monospaced-14" );
        
        this.btHelp = new Button( "F1 Help" );
        this.btHelp.setFont( font );
        this.btHelp.setForeground( Color.BLUE );
        this.btHelp.setBackground( Color.WHITE );

        this.btRename = new Button( "F2 Rename" );
        this.btRename.setFont( font );
        this.btRename.setForeground( Color.BLUE );
        this.btRename.setBackground( Color.WHITE );

        this.btCopy = new Button( "Ctrl+C Copy" );
        this.btCopy.setFont( font );
        this.btCopy.setForeground( Color.BLUE );
        this.btCopy.setBackground( Color.WHITE );
        
        this.btDelete = new Button( "Ctrl+Del Delete" );
        this.btDelete.setFont( font );
        this.btDelete.setForeground( Color.BLUE );
        this.btDelete.setBackground( Color.WHITE );
        
        this.btView = new Button( "F5 View" );
        this.btView.setFont( font );
        this.btView.setForeground( Color.BLUE );
        this.btView.setBackground( Color.WHITE );
        
        final Button[] BUTTONS = {
            this.btHelp, this.btRename, this.btCopy,
            this.btDelete, this.btView
        };

        this.pnlToolbar = new Panel( new GridLayout( 1, BUTTONS.length ) );
        this.pnlToolbar.add( this.btHelp );
        this.pnlToolbar.add( this.btRename );
        this.pnlToolbar.add( this.btCopy );
        this.pnlToolbar.add( this.btDelete );
        this.pnlToolbar.add( this.btView );
        
        return this.pnlToolbar;
    }
    
    private Panel buildFileChoice()
    {      
        this.pnlChoiceFile = new FileChoicePanel();
        return this.pnlChoiceFile;
    }
    
    private Panel buildDirChoice()
    {
        this.pnlChoiceDir = new DirChoicePanel();
        return this.pnlChoiceDir;
    }
    
    private MenuBar buildMenu()
    {        
        this.mbMainMenu = new MenuBar();
        this.mFile = new Menu( "File" );
        this.opQuit = new MenuItem( "Quit" );
        this.mFile.add( opQuit );
        
        this.mEdit = new Menu( "Edit" );
        this.opNew = new MenuItem( "New" );
        this.opRename = new MenuItem( "Rename" );
        this.opCopy = new MenuItem( "Copy" );
        this.opMove = new MenuItem( "Move" );
        this.opDelete = new MenuItem( "Delete" );
        this.opView = new MenuItem( "View" );
        this.opRefresh = new MenuItem( "Refresh" );
        this.opShowHidden = new MenuItem( "Show hidden" );
        
        this.mEdit.add( this.opNew );
        this.mEdit.add( this.opView );
        this.mEdit.add( this.opRename );
        this.mEdit.add( this.opCopy );
        this.mEdit.add( this.opMove );
        this.mEdit.add( this.opDelete );
        this.mEdit.add( this.opShowHidden );
        
        this.mView = new Menu( "View" );
        this.opViewOutput = new MenuItem( "View output" );
        this.mView.add( this.opRefresh );
        this.mView.add( this.opViewOutput );
        
        this.mTools = new Menu( "Tools" );
        this.opOpenShell = new MenuItem( "Open in shell" );
        this.mTools.add( this.opOpenShell );
        
        this.mHelp = new Menu( "Help" );
        this.opAbout = new MenuItem( "About" );
        this.opHelp = new MenuItem( "Help" );
        this.mHelp.add( this.opHelp );
        this.mHelp.add( this.opAbout );
        
        this.mbMainMenu.add( this.mFile );
        this.mbMainMenu.add( this.mEdit );
        this.mbMainMenu.add( this.mView );
        this.mbMainMenu.add( this.mTools );
        this.mbMainMenu.add( this.mHelp );
        
        return this.mbMainMenu;
    }
    
    private TextField buildStatusBar()
    {
        this.sbStatus = new TextField();
        this.sbStatus.setEditable( false );
        
        return this.sbStatus;
    }
        
    /** @return the frame of this window view. */
    public final Frame getFrame()
    {
        return (Frame) super.getWindow();
    }
    
    /** @return the Help >> about option. */
    public MenuItem getOpAbout()
    {
        return this.opAbout;
    }
    
    /** @return the File >> quit option. */
    public MenuItem getOpQuit()
    {
        return this.opQuit;
    }
    
    /** @return the Edit >> view option. */
    public MenuItem getOpView()
    {
        return this.opView;
    }
    
    /** @return the Edit >> new option. */
    public MenuItem getOpNew()
    {
        return this.opNew;
    }
    
    /** @return the Edit >> rename option. */
    public MenuItem getOpRename()
    {
        return this.opRename;
    }
    
    /** @return the Edit >> copy option. */
    public MenuItem getOpCopy()
    {
        return this.opCopy;
    }
    
    /** @return the Edit >> move option. */
    public MenuItem getOpMove()
    {
        return this.opMove;
    }
    
    /** @return the Edit >> move option. */
    public MenuItem getOpDelete()
    {
        return this.opDelete;
    }
    
    /** @return the Edit >> show hidden option. */
    public MenuItem getOpShowHidden()
    {
        return this.opShowHidden;
    }
    
    /** @return the View >> Refresh option. */
    public MenuItem getOpRefresh()
    {
        return this.opRefresh;
    }
    
    /** @return the Tools >> Open Shell option. */
    public MenuItem getOpShell()
    {
        return this.opOpenShell;
    }
    
    /** @return the View >> View output option. */
    public MenuItem getOpViewOutput()
    {
        return this.opViewOutput;
    }
    
    /** @return the menu item for help >> help. */
    public MenuItem getOpHelp()
    {
        return this.opHelp;
    }
    
    /** @return the text field that acts as a status bar. */
    public TextField getStatusBar()
    {
        return this.sbStatus;
    }
    
    /** @return the text area that acts as output. */
    public TextArea getLogViewer()
    {
        return this.logViewer;
    }
    
    /** @return the button in the toolbar for help. */
    public Button getHelpButton()
    {
        return this.btHelp;
    }
    
    /** @return the button in the toolbar for rename. */
    public Button getRenameButton()
    {
        return this.btRename;
    }
    
    /** @return the button in the toolbar for copy. */
    public Button getCopyButton()
    {
        return this.btCopy;
    }
    
    /** @return the button in the toolbar for move. */
    public Button getMoveButton()
    {
        return this.btDelete;
    }
    
    /** @return the button in the toolbar for view. */
    public Button getViewButton()
    {
        return this.btView;
    }
    
    /** @return the button in the toolbar for rename. */
    public Button getDeleteButton()
    {
        return this.btDelete;
    }
    
    /** @return the widget for the list of directories. */
    @Override
    public DirChoicePanel getDirChoicePanel()
    {
        return this.pnlChoiceDir;
    }
    
    /** @return the widget for the list of files. */
    @Override
    public FileChoicePanel getFileChoicePanel()
    {
        return this.pnlChoiceFile;
    }
        
    private FileChoicePanel pnlChoiceFile;
    private DirChoicePanel pnlChoiceDir;
    private Panel pnlToolbar;
    private TextField sbStatus;
    private TextArea logViewer;
    private Menu mFile;
    private Menu mEdit;
    private Menu mView;
    private Menu mTools;
    private Menu mHelp;
    private MenuBar mbMainMenu;
    private MenuItem opQuit;
    private MenuItem opView;
    private MenuItem opNew;
    private MenuItem opRename;
    private MenuItem opCopy;
    private MenuItem opMove;
    private MenuItem opDelete;
    private MenuItem opRefresh;
    private MenuItem opShowHidden;
    private MenuItem opViewOutput;
    private MenuItem opHelp;
    private MenuItem opAbout;
    private MenuItem opOpenShell;
    private Button btView;
    private Button btRename;
    private Button btCopy;
    private Button btDelete;
    private Button btHelp;
    private Image iconApp;
    private Image iconDirUp;
}
