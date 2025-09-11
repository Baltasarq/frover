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
public class PathList extends List {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.WHITE;
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    /** Creates a new panel with a list of file/dir entries.
      * Foreground color is FG, background color is BG,
      * and font is MONO_FONT_16.
      */
    protected PathList()
    {
        this( FG, BG,  FONT_MONO_16 );
    }
    
    protected PathList(Color fg, Color bg, Font font)
    {
        this.dir = null;
        
        this.setForeground( Color.WHITE );
        this.setBackground( Color.GRAY );
        this.setFont( font );
    }
    
    /** Sets the CWD, since maybe there are no paths in this list...
      * @param path the new CWD.
      */
    public void setCWD(Path path)
    {
        if ( path == null ) {
            throw new Error( "PathList::setCWD(p): p cannot be null" );
        }
        
        this.dir = path;
    }
    
    /** Sets the dir for this file list, provided that the path is absolute.
      * Provided also that all files pertain to a given directory.
      * @param path the path to extract the directory from.
      */
    protected void setDir(Path path)
    {
        if ( this.dir == null
          && path != null )
        {
            this.dir = path.getParent();
        }
    }
    
    /** Returns a file entry with an absolute path, given a dir and a name.
      * @param fn the string with the file name.
      * @return an path with an absolute path.
     */
    protected Path pathFromDirAndEntryName(String fn)
    {
        return Path.of( this.dir.toString(), fn );
    }
    
    /** Adds a new path.
      * @param path the path to add to the end of the list.
      */
    public void add(Path path)
    {
        this.setDir( path );
        this.add( path.getFileName().toString() );
    }
    
    /** Inserts a new path in the list.
      * @param row the number of the row to insert the path into.
      * @param path the file to add to the end of the list.
      */
    public void insert(int row, Path path)
    {
        this.setDir( path );
        this.add( path.getFileName().toString(), row );
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
        
        if ( this.getItemCount() == 0 ) {
            this.dir = null;
        }
    }
    
    /** @return the path at a given pos in the list.
      * @param row the position to retrieve the file from.
      */
    public Path getPathAt(int row)
    {
        return this.pathFromDirAndEntryName( this.getItem( row ));
    }
    
    /** Removes all the paths in the list. */
    @Override
    public void removeAll()
    {
        this.dir = null;
        super.removeAll();
    }
    
    /** @return the complete list of paths in the list. */
    public java.util.List<Path> getAllPaths()
    {
        final var STR_LIST = new ArrayList<String>( Arrays.asList( this.getItems() ) );
        final var TORET = new ArrayList<Path>( STR_LIST.size() );
        
        for(String STR: STR_LIST) {
            TORET.add(this.pathFromDirAndEntryName( STR ) );
        }
        
        return TORET;
    }
    
    /** @return the CWD (current working directory). */
    public Path getCWD()
    {
        return this.dir;
    }
    
    /** Adds a listener for the case in which a file is selected.
      * @param action what to do...
      */
    public void addActionListener(Runnable action)
    {
        this.addActionListener( (evt) -> action.run() );
    }
    
    private Path dir;
}
