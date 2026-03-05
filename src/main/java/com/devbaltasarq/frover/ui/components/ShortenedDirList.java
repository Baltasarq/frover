// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/** A dir list in which the paths appear shortened.
  * @author baltasarq
  */
public class ShortenedDirList extends PathList {
    public static final int DEFAULT_MAX_PATH_LEN = 14;
    
    public ShortenedDirList(Color fg, Color bg, Font font)
    {
        this( fg, bg, font, DEFAULT_MAX_PATH_LEN );
    }
    
    public ShortenedDirList(Color fg, Color bg, Font font, int maxNumVisibleChars)
    {
        super( fg, bg, font );
        this.realPaths = new ArrayList<>();
        this.maxVisibleChars = maxNumVisibleChars;
    }
    
    /** @return the max number of chars to be shown for each path. */
    public int getMaxVisibleChars()
    {
        return this.maxVisibleChars;
    }
    
    @Override
    protected void setCwdIfNeeded(Path path)
    {
    }
    
    /** @return the path at a given pos in the list.
      * @param row the position to retrieve the file from.
      */
    @Override
    public Path getPathAt(int row)
    {
        return this.realPaths.get( row );
    }
    
    /** @return the complete list of paths in the list. */
    @Override
    public List<Path> getAllPaths()
    {
        return new ArrayList<>( this.realPaths );
    }
    
    /** Shortens a given path.
      * @param path a given path.
      * @return a shortened equivalent, as a String.
      */
    private String shortenPath(Path path)
    {
        final int LIMIT = this.getMaxVisibleChars();
        String toret = path.toString();
        int nameLen = toret.length();
        
        if ( nameLen > LIMIT ) {
            toret = "\u2026" + toret.substring( nameLen - LIMIT );
        }
        
        return toret;
    }
    
    /** Adds a new path.
      * @param path the path to add to the end of the list.
      */
    @Override
    public void add(Path path)
    {
        super.add( this.shortenPath( path ) );
        this.realPaths.add( path );
    }
    
    /** Inserts a new path in the list.
      * @param row the number of the row to insert the path into.
      * @param path the file to add to the end of the list.
      */
    @Override
    public void insert(int row, Path path)
    {
        super.add( this.shortenPath( path ), row );
        this.realPaths.add( row, path );
    }
    
    @Override
    public void remove(int row)
    {
        this.removePathAt( row );
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    @Override
    public void removePathAt(int row)
    {
        super.remove( row );
        
        if ( this.getItemCount() == 0 ) {
            this.realPaths.clear();
        }
    }
    
    /** Removes all the paths in the list. */
    @Override
    public void removeAll()
    {
        super.removeAll();
        this.realPaths.clear();
    }
    
    private final List<Path> realPaths;
    private final int maxVisibleChars;
}
