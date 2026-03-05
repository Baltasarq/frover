// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/** A path list in which each directory is identified by a name.
  * @author baltasarq
  */
public class NamedPathList extends java.awt.List implements PathChoice {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.GRAY;
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    /** Creates a new panel with a list of dir entries,
      * in which each entry is identified by a name.
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
        this.namesToPaths = new HashMap<>();
    }
    
    @Override
    public void add(String path)
    {
        final Path PATH = Path.of( path );
        final String NAME = PATH.getFileName().toString();
        
        this.add( new NamedPath( NAME, PATH ) );
    }
    
    @Override
    public void add(String path, int row)
    {
        final Path PATH = Path.of( path );
        final String NAME = PATH.getFileName().toString();
        
        this.add( new NamedPath( NAME, PATH ) );
    }
    
    @Override
    public void remove(int row)
    {
        this.removePathAt( row );
    }
    
    /** @return the number of items in the list. */
    public int count()
    {
        return super.getItemCount();
    }
    
    /** @return the current list of names, sorted. */
    private List<String> sortNames()
    {
        final List<String> TORET = new ArrayList<>( this.namesToPaths.keySet() );
        
        TORET.sort(
                (s1, s2 ) -> s1.toLowerCase().compareTo(
                                        s2.toLowerCase() ) );
        
        return TORET;
    }
    
    /** Updates the list of items visible to the user,
      * because the list names has changed.
      */
    private void updateList()
    {
        final List<String> SORTED_NAMES = this.sortNames();
        
        super.removeAll();
        for(final String NAME: SORTED_NAMES) {
            super.add( NAME );
        }        
    }
    
    /** Adds a new name/path pair.
      * @param NAME_PATH the NamedPath object holding a name and its path.
      */
    public void add(final NamedPath NAME_PATH)
    {
        this.namesToPaths.put( NAME_PATH.getName(), NAME_PATH.getPath() );
        this.updateList();
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    public void removePathAt(int row)
    {
        final String NAME = this.getItem( row );
        
        super.remove( row );
        this.namesToPaths.remove( NAME );
    }
    
    /** Return the path at the given row.
      * @param row the row index of the item.
      * @return the corresponding path.
      */
    public Path getPathAt(int row)
    {
        assert row > 0 && row < this.count():  "invalid row: " + row;
        
        final String NAME = super.getItem( row );
        final Path TORET = this.namesToPaths.get( NAME );
        
        if ( TORET == null ) {
            throw new Error( "NamedPathList: path not found for: " + NAME );
        }
        
        return TORET;
    }

    /** Removes all the paths in the list. */
    @Override
    public void removeAll()
    {
        super.removeAll();
        this.namesToPaths.clear();
    }
    
    public List<NamedPath> getAll()
    {
        final var TORET = new ArrayList<NamedPath>( this.count() );
        
        for(String name: this.namesToPaths.keySet()) {
            TORET.add( new NamedPath( name, this.namesToPaths.get( name ) ) );
        }
        
        return TORET;
    }
    
    private Map<String, Path> namesToPaths;
    
    /** Represents a pair name/path. */
    public static class NamedPath {
        public NamedPath(String name, Path path)
        {
            this.name = formatName( name );
            this.path = path;
        }
        
        /** @return the name of the pair. */
        public String getName()
        {
            return this.name;
        }
        
        /** @return the path of the pair. */
        public Path getPath()
        {
            return this.path;
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
        
        final private String name;
        final private Path path;
    }
}
