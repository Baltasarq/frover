// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;


/** This involves various selection widgets,
  * like a DirList, a Choice of top dirs...
  * @see DirList
  * @author baltasarq
  */
public class DirChoicePanel extends JPanel {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.GRAY;
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    public DirChoicePanel()
    {
        this( FG, BG, FONT_MONO_16 );
    }
    
    public DirChoicePanel(Color fg, Color bg, Font font)
    {
        this.dirList = new PathList( fg, bg, font );
        this.topDirs = new JComboBox();
        this.btUp = new JButton( ".." );
        this.edCWD = new JTextField();
        this.btCopyCWD = new JButton( "Copy" );
        
        this.dirChanger = (p) -> {};
        this.cwdCopier = (p) -> {};
        this.lastSelectedIndex = 0;
        
        this.syncing = false;
        this.build();
        this.buildListeners();
    }
    
    private void build()
    {
        // Panels & layouts
        final Font FONT = this.dirList.getFont();
        final var LYB_1 = new BorderLayout();
        final var LYB_2 = new BorderLayout();
        final var LYB_3 = new BorderLayout();
        final var PNL_FOR_UP = new JPanel( LYB_2 );
        final var PNL_FOR_CWD = new JPanel( LYB_3 );
        
        LYB_1.setHgap( 5 );
        LYB_1.setVgap( 5 );
        LYB_2.setHgap( 5 );
        LYB_2.setVgap( 5 );
        LYB_3.setHgap( 5 );
        LYB_3.setVgap( 5 );
        
        // Top directories
        this.topDirs.setFont( FONT );
        this.topDirs.setEditable( false );
               
        // Button up
        this.btUp.setFont( FONT );
        PNL_FOR_UP.add( this.btUp, BorderLayout.NORTH );     
        PNL_FOR_UP.add( this.dirList, BorderLayout.CENTER );
        
        // CWD
        final var PNL_FOR_COPY = new JPanel( new BorderLayout() );
        this.edCWD.setFont( FONT );
        
        PNL_FOR_COPY.add( this.edCWD, BorderLayout.CENTER );
        PNL_FOR_COPY.add( this.btCopyCWD, BorderLayout.EAST );
        PNL_FOR_CWD.add( PNL_FOR_COPY, BorderLayout.NORTH );
        PNL_FOR_CWD.add( PNL_FOR_UP, BorderLayout.CENTER );
        
        // Build & return
        this.setLayout( LYB_1 );
        this.add( this.topDirs, BorderLayout.NORTH );
        this.add( PNL_FOR_CWD, BorderLayout.CENTER );
    }
    
    private void buildListeners()
    {
        this.btCopyCWD.addActionListener( (evt) -> this.doCopyCWD() );
        this.dirList.addActionListener( () -> this.doDirSelected() );
        this.dirList.addItemListener( (evt) -> this.updateDirMarked() );
        this.btUp.addActionListener( (evt) -> this.doGoUpDirSelected());
        this.topDirs.addItemListener( (evt) -> this.doTopDirSelected() );        
        this.edCWD.addKeyListener( new KeyListener() {
                        @Override
                        public void keyTyped(KeyEvent e)
                        {
                        }

                        @Override
                        public void keyPressed(KeyEvent e)
                        {
                            final DirChoicePanel SELF = DirChoicePanel.this;
                            
                            if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                                e.consume();

                                SELF.dirChanger.accept(
                                        Path.of( SELF.edCWD.getText() ));
                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent e)
                        {
                        }
                    });

    }
    
    /** Invokes the cwd copier with the contents of the CWD text field. */
    private void doCopyCWD()
    {
        this.cwdCopier.accept( this.edCWD.getText() );
    }
    
    /** Change directory from the dir list. */
    private void doDirSelected()
    {
        if ( !this.syncing ) {
            int dirPos = this.getDirList().getSelectedIndex();

            if ( dirPos >= 0 ) {
                final Path PATH = this.getDirList().getPathAt( dirPos );
                this.dirChanger.accept( PATH );
                this.lastSelectedIndex = dirPos;
            }
        }
    }
    
    /** Change the directory marked. */
    private void updateDirMarked()
    {
        if ( !this.syncing ) {
            int dirPos = this.getDirList().getSelectedIndex();

            if ( dirPos >= 0 ) {
                this.lastSelectedIndex = dirPos;
            }
        }
    }
    
    /** Go to the previous directory */
    private void doGoUpDirSelected()
    {
        if ( !this.syncing ) {
            this.dirChanger.accept( this.dirList.getCWD().getParent() );
        }
    }
    
    /** Go to the selected top dirs. */
    private void doTopDirSelected()
    {
        if ( !this.syncing ) {
            final String DIR_SELECTED = (String) this.topDirs.getSelectedItem();

            if ( DIR_SELECTED != null
              && !DIR_SELECTED.isBlank() )
            {
                this.dirChanger.accept( Path.of( DIR_SELECTED ) );
            }
        }
    }
    
    /** Changes the listener for the directory change.
      * @param doIt the new function to invoke when the directory changes.
      */
    public void setChangeDirAction(Consumer<Path> doIt)
    {
        this.dirChanger = doIt;
    }
    
    /** @return the action invoked when the directory is changed. */
    public Consumer<Path> getChangeDirAction()
    {
        return this.dirChanger;
    }
    
    /** Changes the listener for the CWD copier.
      * @param doIt the new function to invoke when the CWD is copied..
      */
    public void setCopyCWDAction(Consumer<String> doIt)
    {
        this.cwdCopier = doIt;
    }
    
    /** @return the action invoked when the CWD is copied. */
    public Consumer<String> getCopyCWDAction()
    {
        return this.cwdCopier;
    }
    
    /** @return the list of directories. */
    public PathList getDirList()
    {
        return this.dirList;
    }
    
    /** @return the text field with the desired dir. */
    public JTextField getEdCWD()
    {
        return this.edCWD;
    }
    
    /** @return the button for going up to parent directory. */
    public JButton getBtUp()
    {
        return this.btUp;
    }
    
    /** @return the button for copying the current path. */
    public JButton getBtCopyCWDPath()
    {
        return this.btCopyCWD;
    }
    
    /** @return a choice with the top directories. */
    public JComboBox getTopDirs()
    {
        return this.topDirs;
    }
    
    /** Syncs all the widgets to the given dir in the dir browser.
      * @param DIR the directory to sync contents with.
      * @param SUB_DIRECTORIES the subdir entries of the directory above.
      * @see com.devbaltasarq.frover.core.DirBrowser
      * @see com.devbaltasarq.frover.core.Cfg
     */
    public void syncToDir(final Path DIR, final List<Path> SUB_DIRECTORIES)
    {
        final JComboBox CH_TOP_DIRS = this.getTopDirs();
        final PathList DIR_LIST = this.getDirList();
        
        this.syncing = true;
        
        // Fill the top dirs choice
        CH_TOP_DIRS.setEnabled( false );
        CH_TOP_DIRS.setEditable( false );
        CH_TOP_DIRS.removeAllItems();
        CH_TOP_DIRS.addItem( DIR.toString() );
        Path climber = DIR;
        
        while( !climber.equals( DIR.getRoot() ) ) {
            climber = climber.getParent();
            CH_TOP_DIRS.insertItemAt( climber.toString(), 0 );
        }
        
        // Select last one
        CH_TOP_DIRS.setSelectedIndex( CH_TOP_DIRS.getItemCount() - 1 );   
        
        // Fill the CWD text field
        this.getEdCWD().setText( DIR.toString() );
                
        // Copy entries to lists
        DIR_LIST.removeAll();
        DIR_LIST.setCWD( DIR );
        for(Path p: SUB_DIRECTORIES) {
            DIR_LIST.add( p );
        }
        
        if ( this.lastSelectedIndex >= 0
          && this.lastSelectedIndex < DIR_LIST.count() )
        {
            DIR_LIST.select( this.lastSelectedIndex );
            DIR_LIST.makeVisible( this.lastSelectedIndex );
        }
        
        CH_TOP_DIRS.setEnabled( true );
        this.syncing = false;
    }
    
    private final PathList dirList;
    private final JTextField edCWD;
    private final JButton btUp;
    private final JButton btCopyCWD;
    private final JComboBox topDirs;
    
    private int lastSelectedIndex;
    private boolean syncing;
    private Consumer<Path> dirChanger;
    private Consumer<String> cwdCopier;
}
