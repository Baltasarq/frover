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
        this.fileSelector = (p) -> {};
        
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
    }
    
    private void buildListeners()
    {
        this.fileList.addActionListener( (evt) -> doSelectFile() );
    }
    
    private void doSelectFile()
    {
        int pos = this.fileList.getSelectedIndex();
        
        if ( pos >= 0 ) {
            this.fileSelector.accept( this.fileList.getPathAt( pos ) );
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
        this.fileSelector = doIt;
    }
    
    /** @return the action invoked when a file is selected. */
    public Consumer<Path> getSelectFileAction()
    {
        return this.fileSelector;
    }
    
    
    private Consumer<Path> fileSelector;
    private final PathList fileList;
}
