// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Color;
import java.awt.Font;

import java.nio.file.Path;


/** A panel showing a list of files.
  * @author baltasarq
  */
public class PathList extends IndependentPathList {
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
    @Override
    protected Path pathFromEntryName(String fn)
    {
        return Path.of( this.dir.toString(), fn );
    }
    
    /** Adds a new path.
      * @param path the path to add to the end of the list.
      */
    @Override
    public void add(Path path)
    {
        this.setDir( path );
        super.add( path.getFileName() );
    }
    
    /** Inserts a new path in the list.
      * @param row the number of the row to insert the path into.
      * @param path the file to add to the end of the list.
      */
    @Override
    public void insert(int row, Path path)
    {
        this.setDir( path );
        super.insert( row, path.getFileName() );
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    @Override
    public void removePathAt(int row)
    {
        super.removePathAt( row );
        
        if ( this.getItemCount() == 0 ) {
            this.dir = null;
        }
    }
    
    /** Removes all the paths in the list. */
    @Override
    public void removeAll()
    {
        this.dir = null;
        super.removeAll();
    }
    
    /** @return the CWD (current working directory). */
    public Path getCWD()
    {
        return this.dir;
    }
    
    private Path dir;
}
