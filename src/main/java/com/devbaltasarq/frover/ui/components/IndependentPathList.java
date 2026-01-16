// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.awt.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.nio.file.Path;


/** A panel showing a list of files.
  * @author baltasarq
  */
public abstract class IndependentPathList extends List {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.GRAY;
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    /** Creates a new panel with a list of file/dir entries.
      * Foreground color is FG, background color is BG,
      * and font is MONO_FONT_16.
      */
    protected IndependentPathList()
    {
        this( FG, BG,  FONT_MONO_16 );
    }
    
    protected IndependentPathList(Color fg, Color bg, Font font)
    {
        this.setForeground( fg );
        this.setBackground( bg );
        this.setFont( font );
    }
    
    /** Adds a new path.
      * @param path the path to add to the end of the list.
      */
    public void add(Path path)
    {
        int row = this.count();
        
        this.add( path.toString() );
    }
    
    /** Inserts a new path in the list.
      * @param row the number of the row to insert the path into.
      * @param path the file to add to the end of the list.
      */
    public void insert(int row, Path path)
    {
        this.add( path.toString(), row );
    }
    
    /** @return the number of paths in the list. */
    public int count()
    {
        return this.getItemCount();
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    public void removePathAt(int row)
    {
        this.remove( row );
    }
    
    /** Returns a file entry with an absolute path, given a dir and a name.
      * @param fn the string with the file name.
      * @return an path with an absolute path.
     */
    protected Path pathFromEntryName(String fn)
    {
        return Path.of( fn );
    }
    
    /** @return the path at a given pos in the list.
      * @param row the position to retrieve the file from.
      */
    public Path getPathAt(int row)
    {
        return this.pathFromEntryName( this.getItem( row ));
    }
    
    /** Removes all the paths in the list. */
    @Override
    public void removeAll()
    {
        super.removeAll();
    }
    
    /** @return the complete list of paths in the list. */
    public java.util.List<Path> getAllPaths()
    {
        final var STR_LIST = new ArrayList<String>( Arrays.asList( this.getItems() ) );
        final var TORET = new ArrayList<Path>( STR_LIST.size() );
        
        for(String STR: STR_LIST) {
            TORET.add( this.pathFromEntryName( STR ) );
        }
        
        return TORET;
    }
    
    /** Adds a listener for the case in which a file is selected.
      * @param action what to do...
      */
    public void addActionListener(Runnable action)
    {
        this.addActionListener( (evt) -> action.run() );
    }
}
