// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;


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
    
    /** Set the max number of chars to be shown for each path.
      * @param maxChars the new value for max chars.
      */
    public void setMaxVisibleChars(int maxChars)
    {
        if ( maxChars <= 0 ) {
            maxChars = 1;
        }
        
        this.maxVisibleChars = maxChars;
        this.updatePaths();
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
    
    /** Updates the entries so the shortening adapts to width. */
    private void updatePaths()
    {
        for(int i = 0; i < this.realPaths.size(); ++i) {
            final Path PATH = this.realPaths.get( i );
            
            super.modifyPathAt( i, this.shortenPath( PATH ) );
        }
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
      * @param path the path to insert at a given position in the list.
      */
    @Override
    public void insert(int row, Path path)
    {
        super.insert( row, this.shortenPath( path ) );
        this.realPaths.add( row, path );
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    @Override
    public void removePathAt(int row)
    {
        super.removePathAt( row );
        
        if ( this.getModel().getSize() == 0 ) {
            this.realPaths.clear();
        }
    }
    
    /** Removes all the paths in the list. */
    @Override
    public void removeAllPaths()
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();
        
        MODEL.removeAllElements();
        this.realPaths.clear();
    }
    
    private final List<Path> realPaths;
    private int maxVisibleChars;
}
