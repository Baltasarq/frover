// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;


/** Represents a panel with capabilities for file selecting.
  * @author baltasarq
  */
public class FileChoicePanel extends Panel {
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
        this.fileOpener = (p) -> {};
        
        this.build();
        this.buildListeners();
        this.lastSelectedIndex = 0;
    }
    
    private void build()
    {
       final var LYB_1 = new BorderLayout();
       
       LYB_1.setHgap( 5 );
       LYB_1.setVgap( 5 );
       
       this.setLayout( LYB_1 );
       this.add( this.fileList, BorderLayout.CENTER );
    }
    
    private void buildListeners()
    {
        this.fileList.addActionListener( (evt) -> doOpenFile() );
        this.fileList.addItemListener( (evt) -> doSelectFile() );
    }
    
    private void doSelectFile()
    {
        int pos = this.fileList.getSelectedIndex();
        
        if ( pos >= 0 ) {
            this.fileSelector.accept( this.fileList.getPathAt( pos ) );
            this.lastSelectedIndex = pos;
            System.out.println( "File position selected: " + this.lastSelectedIndex );
        }
    }
    
    private void doOpenFile()
    {
        int pos = this.fileList.getSelectedIndex();
        
        if ( pos >= 0 ) {
            this.fileOpener.accept( this.fileList.getPathAt( pos ) );
        }
    }
    
    /** Syncs this panel with the files inside a given directory.
      * @param DIR the given directory. 
      * @param FILES the files inside the directory. 
      */
    public void syncToFiles(final Path DIR, final List<Path> FILES)
    {
        final PathList FILE_LIST = this.getFileList();
        
        FILE_LIST.removeAll();
        FILE_LIST.setCWD( DIR );
        for(Path path: FILES) {
            FILE_LIST.add( path );
        }
        
        if ( this.lastSelectedIndex >= 0
          && this.lastSelectedIndex < FILE_LIST.count() )
        {
            FILE_LIST.select( this.lastSelectedIndex );
            FILE_LIST.makeVisible( this.lastSelectedIndex );
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
    public void setOpenFileAction(Consumer<Path> doIt)
    {
        this.fileOpener = doIt;
    }
    
    /** @return the action invoked when a file is selected. */
    public Consumer<Path> getOpenFileAction()
    {
        return this.fileOpener;
    }
    
    /** Changes the listener for the file selection.
      * @param doIt the new function to invoke when a file is selected.
      */
    public void setSelectFileAction(Consumer<Path> doIt)
    {
        this.fileSelector = doIt;
    }
    
    /** @return the action invoked when a file is selected. */
    public Consumer<Path> getSelectFileAction()
    {
        return this.fileSelector;
    }
    
    /** Sets the selected entry in the list, by the given index.
      * @param index the entry to select.
      */
    public void setSelectedIndex(int index)
    {
        this.fileList.select( index );
    }
    
    private int lastSelectedIndex;
    private Consumer<Path> fileOpener;
    private Consumer<Path> fileSelector;
    private final PathList fileList;
}
