// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


/** A path list in which each directory is identified by a name.
  * @author baltasarq
  */
public class NamedPathList extends IndependentPathList {
    /** Creates a new panel with a list of file/dir entries.
      * Foreground color is FG, background color is BG,
      * and font is MONO_FONT_16.
      */
    public NamedPathList()
    {
        this( FG, BG,  FONT_MONO_16 );
    }
    
    public NamedPathList(Color fg, Color bg, Font font)
    {
        this.setForeground( fg );
        this.setBackground( bg );
        this.setFont( font );
        this.paths = new ArrayList<>();
    }
    
    @Override
    public void add(Path path)
    {
        throw new Error( "plain NamedPathList::add(p) plain called" );
    }
    
    @Override
    public void insert(int row, Path path)
    {
        throw new Error( "plain NamedPathList::insert(i, p) plain called" );
    }
    
    /** Adds a new path.
      * @param name the name for this path.
      * @param path the path to add to the end of the list.
      */
    public void add(String name, Path path)
    {
        super.add( formatName( name ) );
        this.paths.add( path );
    }
    
    /** Inserts a new path in the list.
      * @param row the number of the row to insert the path into.
      * @param name the name for this path.
      * @param path the file to add to the end of the list.
      */
    public void insert(int row, String name, Path path)
    {
        super.add( formatName( name ), row );
        this.paths.add( row, path );
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    public void removePathAt(int row)
    {
        super.remove( row );
        this.paths.remove( row );
    }
    
    /** Returns a file entry with a path, given a name.
      * @param name the string with the id of the path.
      * @return a path object.
     */
    @Override
    protected Path pathFromEntryName(String name)
    {
        Path toret = null;
        
        name = formatName( name );
        
        for(int i = 0; i < this.paths.size(); ++i) {
            if ( name.equals( this.getItem( i ) ) ) {
                toret = this.paths.get( i );
                break;
            }
        }
        
        if ( toret == null ) {
            throw new Error( "unable to find path for name:" + name );
        }
        
        return toret;
    }
    
    /** Removes all the paths in the list. */
    @Override
    public void removeAll()
    {
        super.removeAll();
        this.paths.clear();
    }
    
    /** @return the list of strings for the names. */
    public List<String> getNames()
    {
        return new ArrayList<>( Arrays.asList( this.getItems() ));
    }
    
    /** @return the list of Path objects for the paths. */
    public List<Path> getPaths()
    {
        return new ArrayList<>( this.paths );
    }
    
    /** @return a correctly formatted name.
      * @param name a name for a path.
      */
    private static String formatName(String name)
    {
        name = name.trim();
        
        return Character.toUpperCase( name.charAt( 0 ) )
                + name.substring( 1 ).toLowerCase();
    }
    
    private List<Path> paths;
}
