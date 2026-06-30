// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.mainwindow;


import com.devbaltasarq.frover.ui.browser.BrowserView;
import com.devbaltasarq.frover.ui.components.DirChoicePanel;
import com.devbaltasarq.frover.ui.components.VisitedDirChoicePanel;
import com.devbaltasarq.frover.ui.components.FileChoicePanel;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;


/** App's main window
  * @author baltasarq
  */
public class MainWindowView extends BrowserView {
    private static final Dimension MIN_SIZE = new Dimension( 600, 450 );
    private static final Dimension START_SIZE = new Dimension( 620, 460 );
    private static final String ETQ_ICON_APP = "icon.png";
    private static final String ETQ_ICON_DIR_UP = "dir_up.png";
    
    /** Relation of buttons in the view. */
    public enum Buttons {
                    BtHelp, BtRename, BtCopy,
                    BtDelete, BtView, BtCopyCWDPath,
                    BtDirUp, BtExe,
                    BtNewFav, BtDelFav };
    
    public MainWindowView()
    {
        this( new JFrame() );
    }
    
    public MainWindowView(final JFrame FRAME)
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
        final var LY_CMDBAR = new BorderLayout();
        final var PNL_TOOLBAR = new JPanel( LY_TOOLBAR );
        final var PNL_CMDBAR = new JPanel( LY_CMDBAR );
        
        this.pnlMain = new JSplitPane();
        this.pnlEntries = new JSplitPane();
        
        this.mainVsOutputPanel = new JSplitPane();
        this.buttons = new JButton[ Buttons.values().length ];
        
        this.buildIcons();
        this.getFrame().setJMenuBar( this.buildMenu() );
        
        LYB.setHgap( 5 );
        LYB.setVgap( 5 );
        LY_TOOLBAR.setHgap( 5 );
        LY_TOOLBAR.setVgap( 5 );
        LY_CMDBAR.setHgap( 5 );
        LY_CMDBAR.setVgap( 5 );
        
        this.pnlMain.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
        this.pnlEntries.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
        
        this.pnlMain.setLeftComponent( this.buildFavDirectoryChoice() );
        this.pnlMain.setRightComponent( this.pnlEntries );
        
        this.pnlEntries.setLeftComponent( this.buildDirChoice() );
        this.pnlEntries.setRightComponent( this.buildFileChoice() );
        
        this.buttons[ Buttons.BtDirUp.ordinal() ] = this.pnlChoiceDir.getBtUp();
        this.buttons[ Buttons.BtCopyCWDPath.ordinal() ] =
                                this.pnlChoiceDir.getBtCopyCWDPath();
        
        this.mainVsOutputPanel.setOrientation(JSplitPane.VERTICAL_SPLIT );
        this.mainVsOutputPanel.setBottomComponent( this.buildOutputViewer() );
        this.mainVsOutputPanel.setTopComponent( this.pnlMain );
        this.buttons[ Buttons.BtNewFav.ordinal() ] = this.pnlChoiceFavDir.getBtNew();
        this.buttons[ Buttons.BtDelFav.ordinal() ] = this.pnlChoiceFavDir.getBtRemove();
                
        PNL_CMDBAR.add( this.mainVsOutputPanel, BorderLayout.CENTER );
        PNL_CMDBAR.add( this.buildCmdBar(), BorderLayout.PAGE_END );
        
        PNL_TOOLBAR.add( PNL_CMDBAR, BorderLayout.CENTER );
        PNL_TOOLBAR.add( this.buildToolbar(), BorderLayout.PAGE_END );        
        
        this.getWindow().add( PNL_TOOLBAR, BorderLayout.CENTER );
        this.getWindow().add( this.buildStatusBar(), BorderLayout.PAGE_END );
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
    
    private JPanel buildOutputViewer()
    {
        final var LY_TOOLBAR = new BorderLayout();
        final var PANEL = new JPanel( LY_TOOLBAR );
        final var FONT = new Font( Font.MONOSPACED, Font.PLAIN, 12 );

        this.logViewer = new JTextArea();
        this.logViewer.setEditable( false );
        this.logViewer.setFont( FONT );
        this.logViewer.setBackground( Color.BLACK );
        this.logViewer.setForeground( Color.WHITE );
        this.logViewer.setColumns( 80 );
        this.logViewer.setRows( 5 );
        
        PANEL.add( this.logViewer, BorderLayout.CENTER );
        return PANEL;
    }
    
    private JPanel buildToolbar()
    {
        final var FONT = Font.decode( "monospaced-14" );
        int btHelpPos = Buttons.BtHelp.ordinal();
        int btRenamePos = Buttons.BtRename.ordinal();
        int btCopyPos = Buttons.BtCopy.ordinal();
        int btDeletePos = Buttons.BtDelete.ordinal();
        int btViewPos = Buttons.BtView.ordinal();
        
        this.buttons[ btHelpPos ] = new JButton( "F1 Help" );
        this.buttons[ btHelpPos ].setFont( FONT );
        this.buttons[ btHelpPos ].setForeground( Color.BLUE );
        this.buttons[ btHelpPos ].setBackground( Color.WHITE );

        this.buttons[ btRenamePos ] = new JButton( "F2 Rename" );
        this.buttons[ btRenamePos ].setFont( FONT );
        this.buttons[ btRenamePos ].setForeground( Color.BLUE );
        this.buttons[ btRenamePos ].setBackground( Color.WHITE );

        this.buttons[ btCopyPos ] = new JButton( "Ctrl+C Copy" );
        this.buttons[ btCopyPos ].setFont( FONT );
        this.buttons[ btCopyPos ].setForeground( Color.BLUE );
        this.buttons[ btCopyPos ].setBackground( Color.WHITE );
        
        this.buttons[ btDeletePos ] = new JButton( "Ctrl+Del Delete" );
        this.buttons[ btDeletePos ].setFont( FONT );
        this.buttons[ btDeletePos ].setForeground( Color.BLUE );
        this.buttons[ btDeletePos ].setBackground( Color.WHITE );
        
        this.buttons[ btViewPos ] = new JButton( "F5 View" );
        this.buttons[ btViewPos ].setFont( FONT );
        this.buttons[ btViewPos ].setForeground( Color.BLUE );
        this.buttons[ btViewPos ].setBackground( Color.WHITE );
        
        // Relate all buttons that will appear in the toolbar.
        final JButton[] BUTTONS = {
            this.buttons[ btHelpPos ], this.buttons[ btRenamePos ],
            this.buttons[ btCopyPos ], this.buttons[ btDeletePos ],
            this.buttons[ btViewPos ]
        };
        
        final var GRID = new GridLayout( 1, BUTTONS.length );

        GRID.setHgap( 10 );
        this.pnlToolbar = new JPanel( GRID );
        this.pnlToolbar.setFocusable( false );
        
        for(final JButton BT: BUTTONS) {
            this.pnlToolbar.add( BT );
        }

        return this.pnlToolbar;
    }
    
    private JPanel buildFavDirectoryChoice()
    {
        this.pnlChoiceFavDir = new VisitedDirChoicePanel(
                                    Color.WHITE,
                                    Color.BLACK,
                                    VisitedDirChoicePanel.FONT_MONO_16 );
        return this.pnlChoiceFavDir;
    }
    
    private JPanel buildFileChoice()
    {      
        this.pnlChoiceFile = new FileChoicePanel();
        return this.pnlChoiceFile;
    }
    
    private JPanel buildDirChoice()
    {
        this.pnlChoiceDir = new DirChoicePanel();
        return this.pnlChoiceDir;
    }
    
    private JMenuBar buildMenu()
    {        
        this.mbMainMenu = new JMenuBar();
        this.mFile = new JMenu( "File" );
        this.opQuit = new JMenuItem( "Quit" );
        this.mFile.add( opQuit );
        
        this.mEdit = new JMenu( "Edit" );
        this.opNew = new JMenuItem( "New" );
        this.opRename = new JMenuItem( "Rename" );
        this.opCopy = new JMenuItem( "Copy" );
        this.opMove = new JMenuItem( "Move" );
        this.opDelete = new JMenuItem( "Delete" );
        this.opView = new JMenuItem( "View" );
        this.opRefresh = new JMenuItem( "Refresh" );
        
        this.mEdit.add( this.opNew );
        this.mEdit.add( this.opView );
        this.mEdit.add( this.opRename );
        this.mEdit.add( this.opCopy );
        this.mEdit.add( this.opMove );
        this.mEdit.add( this.opDelete );
        
        this.mView = new JMenu( "View" );
        this.opViewOutput = new JMenuItem( "View output" );
        this.opShowHidden = new JMenuItem( "Show hidden" );
        this.mView.add( this.opRefresh );
        this.mView.add( this.opViewOutput );
        this.mView.add( this.opShowHidden );
        
        this.mTools = new JMenu( "Tools" );
        this.opOpenShell = new JMenuItem( "Open in shell" );
        this.mTools.add( this.opOpenShell );
        
        this.mHelp = new JMenu( "Help" );
        this.opAbout = new JMenuItem( "About" );
        this.opHelp = new JMenuItem( "Help" );
        this.mHelp.add( this.opHelp );
        this.mHelp.add( this.opAbout );
        
        this.mbMainMenu.add( this.mFile );
        this.mbMainMenu.add( this.mEdit );
        this.mbMainMenu.add( this.mView );
        this.mbMainMenu.add( this.mTools );
        this.mbMainMenu.add( this.mHelp );
        
        return this.mbMainMenu;
    }
    
    private JPanel buildCmdBar()
    {
        final var FONT = new Font( Font.MONOSPACED, Font.PLAIN, 14 );
        final var FONT_LBL = new Font( Font.MONOSPACED, Font.BOLD, 18 );
        final var LY_CMD = new BorderLayout();
        final var PNL_CMD_BAR = new JPanel( LY_CMD );
        final var LBL_PROMPT = new JLabel( ">" );
        final int btExePos = Buttons.BtExe.ordinal();
        
        LBL_PROMPT.setFont( FONT_LBL );
        LY_CMD.setHgap( 5 );
        LY_CMD.setVgap( 5 );
        
        this.edCmd = new JTextField();
        this.edCmd.setBackground(Color.BLACK );
        this.edCmd.setForeground( Color.WHITE );
        this.edCmd.setFont( FONT );
        
        this.buttons[ btExePos ] = new JButton( "DoIt" );
        
        PNL_CMD_BAR.add( LBL_PROMPT, BorderLayout.LINE_START );
        PNL_CMD_BAR.add( this.edCmd, BorderLayout.CENTER );
        PNL_CMD_BAR.add( this.buttons[ btExePos ], BorderLayout.LINE_END );
        
        return PNL_CMD_BAR;
    }
    
    private JTextField buildStatusBar()
    {
        this.sbStatus = new JTextField();
        this.sbStatus.setEditable( false );
        this.sbStatus.setFocusable( false );
        
        return this.sbStatus;
    }
        
    /** @return the frame of this window view. */
    public final JFrame getFrame()
    {
        return (JFrame) super.getWindow();
    }
    
    /** @return the Help >> about option. */
    public JMenuItem getOpAbout()
    {
        return this.opAbout;
    }
    
    /** @return the File >> quit option. */
    public JMenuItem getOpQuit()
    {
        return this.opQuit;
    }
    
    /** @return the Edit >> view option. */
    public JMenuItem getOpView()
    {
        return this.opView;
    }
    
    /** @return the Edit >> new option. */
    public JMenuItem getOpNew()
    {
        return this.opNew;
    }
    
    /** @return the Edit >> rename option. */
    public JMenuItem getOpRename()
    {
        return this.opRename;
    }
    
    /** @return the Edit >> copy option. */
    public JMenuItem getOpCopy()
    {
        return this.opCopy;
    }
    
    /** @return the Edit >> move option. */
    public JMenuItem getOpMove()
    {
        return this.opMove;
    }
    
    /** @return the Edit >> move option. */
    public JMenuItem getOpDelete()
    {
        return this.opDelete;
    }
    
    /** @return the Edit >> show hidden option. */
    public JMenuItem getOpShowHidden()
    {
        return this.opShowHidden;
    }
    
    /** @return the View >> Refresh option. */
    public JMenuItem getOpRefresh()
    {
        return this.opRefresh;
    }
    
    /** @return the Tools >> Open Shell option. */
    public JMenuItem getOpShell()
    {
        return this.opOpenShell;
    }
    
    /** @return the View >> View output option. */
    public JMenuItem getOpViewOutput()
    {
        return this.opViewOutput;
    }
    
    /** @return the menu item for help >> help. */
    public JMenuItem getOpHelp()
    {
        return this.opHelp;
    }
    
    /** @return the text field that acts as a status bar. */
    public JTextField getStatusBar()
    {
        return this.sbStatus;
    }
    
    /** @return the text area that acts as output. */
    public JTextArea getLogViewer()
    {
        return this.logViewer;
    }
  
    /** @return the text field for a command. */
    public JTextField getEdCmd()
    {
        return this.edCmd;
    }
    
    /** @return the button, given the id.
      * @param bt the id of the button.
      * @see MainWindowView::Button
      */
    public JButton getButton(Buttons bt)
    {
        return this.buttons[ bt.ordinal() ];
    }
    
    /** @return a list with all the available buttons. */
    public List<JButton> getAllButtons()
    {
        return Arrays.asList( this.buttons );
    }
    
    /** @return the widget for the list of visited directories. */
    public VisitedDirChoicePanel getVisitedDirChoicePanel()
    {
        return this.pnlChoiceFavDir;
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
    
    /** @return the split panel between main and output. */
    public JSplitPane getSplitPanel()
    {
        return this.mainVsOutputPanel;
    }
    
    /** @return the main split panel, between favs and entries. */
    public JSplitPane getPnlMain()
    {
        return this.pnlMain;
    }
    
    /** @return the split panel between dirs and files. */
    public JSplitPane getPnlEntries()
    {
        return this.pnlEntries;
    }
    
    private JSplitPane pnlMain;
    private JSplitPane pnlEntries;
    private FileChoicePanel pnlChoiceFile;
    private DirChoicePanel pnlChoiceDir;
    private VisitedDirChoicePanel pnlChoiceFavDir;
    private JSplitPane mainVsOutputPanel;
    private JPanel pnlToolbar;
    private JTextField edCmd;
    private JTextField sbStatus;
    private JTextArea logViewer;
    private JMenu mFile;
    private JMenu mEdit;
    private JMenu mView;
    private JMenu mTools;
    private JMenu mHelp;
    private JButton[] buttons;
    private JMenuBar mbMainMenu;
    private JMenuItem opQuit;
    private JMenuItem opView;
    private JMenuItem opNew;
    private JMenuItem opRename;
    private JMenuItem opCopy;
    private JMenuItem opMove;
    private JMenuItem opDelete;
    private JMenuItem opRefresh;
    private JMenuItem opShowHidden;
    private JMenuItem opViewOutput;
    private JMenuItem opHelp;
    private JMenuItem opAbout;
    private JMenuItem opOpenShell;
    private Image iconApp;
    private Image iconDirUp;
}
