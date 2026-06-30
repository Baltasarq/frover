// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


/** Represents a panel with capabilities for file selecting.
  * @author baltasarq
  */
public class FileChoicePanel extends JPanel {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.GRAY;
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    public FileChoicePanel()
    {
        this( FG, BG, FONT_MONO_16 );
    }
    
    public FileChoicePanel(Color fg, Color bg, Font font)
    {
        this.fileList = new PathList( fg, bg, font );
        this.newFileAction = () -> {};
        this.openFileAction = (p) -> {};
        this.openWithFileAction = (p) -> {};
        this.renameFileAction = (p) -> {};
        this.deleteFileAction = (p) -> {};
        this.copyFileAction = (p) -> {};
        this.moveFileAction = (p) -> {};
        
        this.build();
        this.buildListeners();
    }
    
    private void build()
    {
       final var LYB_1 = new BorderLayout();
       
       LYB_1.setHgap( 5 );
       LYB_1.setVgap( 5 );
       
       this.setLayout( LYB_1 );
       this.add( this.fileList, BorderLayout.CENTER );
       this.buildPopup();
    }
    
    private void buildPopup()
    {
        this.ppPopup = new JPopupMenu( "File" );
        this.ppPopup.setVisible( false );
        
        this.ppmNewFile = new JMenuItem( "New..." );
        this.ppmNewFile.addActionListener(
                            (evt) -> this.newFileAction.run() );
        
        this.ppmOpen = new JMenuItem( "Open" );
        this.ppmOpen.addActionListener(
                            (evt) -> this.openFileAction.accept(
                                            this.fileList.getSelectedPath() ) );
        
        this.ppmOpenWith = new JMenuItem( "Open with..." );
        this.ppmOpenWith.addActionListener(
                            (evt) -> this.openWithFileAction.accept(
                                            this.fileList.getSelectedPath() ) );
        
        this.ppmRename = new JMenuItem( "Rename" );
        this.ppmRename.addActionListener(
                            (evt) -> this.renameFileAction.accept(
                                            this.fileList.getSelectedPath() ) );
        
        this.ppmCopy = new JMenuItem( "Copy" );
        this.ppmCopy.addActionListener(
                            (evt) -> this.copyFileAction.accept(
                                            this.fileList.getSelectedPath() ) );
        
        this.ppmMove = new JMenuItem( "Move" );
        this.ppmMove.addActionListener(
                            (evt) -> this.moveFileAction.accept(
                                            this.fileList.getSelectedPath() ) );
        
        this.ppmDelete = new JMenuItem( "Delete" );
        this.ppmDelete.addActionListener(
                            (evt) -> this.deleteFileAction.accept(
                                            this.fileList.getSelectedPath() ) );
        
        this.ppPopup.add( this.ppmNewFile );
        this.ppPopup.add( this.ppmOpen );
        this.ppPopup.add( this.ppmOpenWith );
        this.ppPopup.add( this.ppmRename );
        this.ppPopup.add( this.ppmCopy );
        this.ppPopup.add( this.ppmMove );
        this.ppPopup.add( this.ppmDelete );
    }
        
    private void buildListeners()
    {
        this.fileList.addActionListener( () -> doOpenFile() );
        this.fileList.addListSelectionListener( (evt) -> doSelectFile() );
        
        this.fileList.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me)
            {            
                final var SELF = FileChoicePanel.this;
                
                if ( SwingUtilities.isRightMouseButton( me )
                  && !SELF.fileList.isSelfModifying() )
                {
                    SELF.showPopup( me.getX(), me.getY() );
                }
            }
        });
    }
    
    private void doSelectFile()
    {
        final Path SELECTED_PATH = this.fileList.getSelectedPath();
        
        if ( SELECTED_PATH != null
          && !this.fileList.isSelfModifying() )
        {
            this.fileSelectAction.accept( SELECTED_PATH );
        }
    }
    
    private void doOpenFile()
    {
        int pos = this.fileList.getSelectedIndex();
        
        if ( pos >= 0 ) {
            this.openFileAction.accept( this.fileList.getPathAt( pos ) );
        }
    }
    
    /** Syncs this panel with the files inside a given directory.
      * @param DIR the given directory. 
      * @param FILES the files inside the directory. 
      */
    public void syncToFiles(final Path DIR, final List<Path> FILES)
    {
        if ( this.fileList.isSelfModifying() ) {
            return;
        }
        
        final PathList FILE_LIST = this.getFileList();
        final int SELECTED_INDEX = this.fileList.getSelectedIndex();

        // Fill the path list wuth all files
        FILE_LIST.removeAllPaths();
        FILE_LIST.setCWD( DIR );
        for(Path path: FILES) {
            FILE_LIST.add( path );
        }
        
        // Re-select the last index
        if ( SELECTED_INDEX >= 0
          && SELECTED_INDEX < FILE_LIST.count() )
        {
            FILE_LIST.setSelectedIndex( SELECTED_INDEX );
            FILE_LIST.ensureIndexIsVisible( SELECTED_INDEX );
        }
    }
    
    /** @return the list of files. */
    public PathList getFileList()
    {
        return this.fileList;
    }
       
    /** Changes the listener for the file selection.
      * @param doIt the new function to invoke when a file is selected.
      */
    public void setSelectFileAction(Consumer<Path> doIt)
    {
        this.fileSelectAction = doIt;
    }
    
    /** Changes the listener for the file opener.
      * @param doIt the new function to invoke when a file is open.
      */
    public void setOpenFileAction(Consumer<Path> doIt)
    {
        this.openFileAction = doIt;
    }
    
    /** Changes the listener for new file creation.
      * @param doIt the new function to invoke when a file is created.
      */
    public void setNewFileAction(Runnable doIt)
    {
        this.newFileAction = doIt;
    }    
    
    /** Changes the listener for the file opener with another program.
      * @param doIt the new function to invoke when a file is open.
      */
    public void setOpenWithFileAction(Consumer<Path> doIt)
    {
        this.openWithFileAction = doIt;
    }    

    /** Changes the listener for file deletion.
      * @param doIt the new function to invoke when a file is to be deleted.
      */
    public void setDeleteFileAction(Consumer<Path> doIt)
    {
        this.deleteFileAction = doIt;
    }
    
    /** Changes the listener for file renaming.
      * @param doIt the new function to invoke when a file is to be renamed.
      */
    public void setRenameFileAction(Consumer<Path> doIt)
    {
        this.renameFileAction = doIt;
    }    
    
    /** Changes the listener for file copying.
      * @param doIt the new function to invoke when a file is to be copied.
      */
    public void setCopyFileAction(Consumer<Path> doIt)
    {
        this.copyFileAction = doIt;
    }
    
    /** Changes the listener for file moving.
      * @param doIt the new function to invoke when a file is to be moved.
      */
    public void setMoveFileAction(Consumer<Path> doIt)
    {
        this.moveFileAction = doIt;
    }
    
    /** Sets the selected entry in the list, by the given index.
      * @param index the entry to select.
      */
    public void setSelectedIndex(int index)
    {
        if ( !this.fileList.isSelfModifying() ) {
            this.fileList.setSelectedIndex( index );
        }
    }
    
    /** @return the popup for the file browser. */
    public JPopupMenu getPopup()
    {
        return this.ppPopup;
    }
    
    /** Shows the popup for the file choice panel.
      * @param x the x clicked position for the popup.
      * @param y the y clicked position for the popup.
      */
    public void showPopup(int x, int y)
    {
        boolean fileSelected = this.fileList.getSelectedIndex() >= 0;
        
        this.ppmNewFile.setEnabled( true );
        this.ppmOpen.setEnabled( fileSelected );
        this.ppmOpenWith.setEnabled( fileSelected );
        this.ppmRename.setEnabled( fileSelected );
        this.ppmCopy.setEnabled( fileSelected );
        this.ppmMove.setEnabled( fileSelected );
        this.ppmDelete.setEnabled( fileSelected );
        
        this.ppPopup.show( this.fileList, x, y );
    }

    
    private final PathList fileList;
    private Runnable newFileAction;
    private Consumer<Path> fileSelectAction;
    private Consumer<Path> openFileAction;
    private Consumer<Path> openWithFileAction;
    private Consumer<Path> renameFileAction;
    private Consumer<Path> copyFileAction;
    private Consumer<Path> moveFileAction;
    private Consumer<Path> deleteFileAction;
    private JPopupMenu ppPopup;
    private JMenuItem ppmNewFile;
    private JMenuItem ppmOpen;
    private JMenuItem ppmRename;
    private JMenuItem ppmCopy;
    private JMenuItem ppmMove;
    private JMenuItem ppmDelete;
    private JMenuItem ppmOpenWith;
}
