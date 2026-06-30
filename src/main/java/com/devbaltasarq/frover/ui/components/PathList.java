// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultListModel;


/** A panel showing a list of files.
  * @author baltasarq
  */
public class PathList extends ListView implements PathChoice {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.GRAY;
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
        
        this.setForeground( fg );
        this.setBackground( bg );
        this.setFont( font );
        this.setModel( new DefaultListModel<String>() );
        this.lastSelectedIndex = 0;
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
    
    /** Sets the CWD for this file list, provided that the path is absolute.
      * Provided also that all files pertain to a given directory.
      * @param path the path to extract the directory from.
      */
    protected void setCwdIfNeeded(Path path)
    {
        if ( this.dir == null
          && path != null )
        {
            this.dir = path.getParent();
        }
    }
    
    /** @return the number of paths in the list. */
    public int count()
    {
        return super.getModel().getSize();
    }
    
    /** Modify the path at a given position.
      * @param pos the position.
      * @param path the new path.
      */
    public void modifyPathAt(int pos, Path path)
    {
        this.modifyPathAt( pos, path.toString() );
    }
    
    /** Modify the path at a given position.
      * @param pos the position.
      * @param path the verbatim new path.
      */
    public void modifyPathAt(int pos, String path)
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();
        
        this.setSelfModifying( true );
        MODEL.setElementAt( path, pos );
        this.setSelfModifying( false );
    }
    
    /** @return the path at a given pos in the list.
      * @param row the position to retrieve the file from.
      */
    @Override
    public Path getPathAt(int row)
    {
        return this.pathFromEntryName( (String) super.getModel().getElementAt( row ) );
    }
    
    /** @return the complete list of paths in the list. */
    public java.util.List<Path> getAllPaths()
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();
        final var STR_LIST = Arrays.asList( MODEL.toArray() ).stream().map( (elem) -> elem.toString() ).toList();
        final var TORET = new ArrayList<Path>( STR_LIST.size() );
        
        for(String STR: STR_LIST) {
            TORET.add( this.pathFromEntryName( STR ) );
        }
        
        return TORET;
    }
    
    /** Adds a listener for the case in which a file is double-clicked.
      * @param ACTION what to do...
      */
    public void addActionListener(final Runnable ACTION)
    {
        super.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                if ( me.getClickCount() >= 2 ) {
                    ACTION.run();
                }
            }
        });
    }
    
    /** Returns a file entry with an absolute path, given a name.
      * @param fn the string with the file name.
      * @return an path with an absolute path.
     */
    protected Path pathFromEntryName(String fn)
    {
        return Path.of( this.dir.toString(), fn );
    }
    
    /** Adds a new path.
      * @param path the path to add to the end of the list.
      */
    public void add(Path path)
    {        
        this.setCwdIfNeeded( path );
        this.add( path.getFileName().toString() );
    }
    
    /** Add verbatim element.
      * @param element the verbatim element to add.
      */
    public void add(String element)
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();

        this.setSelfModifying( true );
        MODEL.addElement( element );
        this.setSelfModifying( false );
    }
    
    /** Inserts a new path in the list.
      * @param row the number of the row to insert the path into.
      * @param path the file to add to the end of the list.
      */
    public void insert(int row, Path path)
    {
        this.setCwdIfNeeded( path );
        this.insert( row, path.getFileName().toString() );
    }
    
    /** Inserts a new element in the list.
      * @param row the number of the row to insert the path into.
      * @param element the verbatim element to insert.
      */
    public void insert(int row, String element)
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();
        
        this.setSelfModifying( true );
        MODEL.add( row, element );
        this.setSelfModifying( false );
    }
    
    /** Remove a path at a given row.
      * @param row the number of the row for the file to remove.
      */
    public void removePathAt(int row)
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();
        
        this.setSelfModifying( true );
        MODEL.remove( row );
        
        if ( this.getModel().getSize() == 0 ) {
            this.dir = null;
        }
        
        this.setSelfModifying( false );
    }
    
    /** Removes all the paths in the list. */
    public void removeAllPaths()
    {
        final var MODEL = (DefaultListModel<String>) super.getModel();
        
        this.dir = null;
        this.setSelfModifying( true );
        MODEL.removeAllElements();
        this.setSelfModifying( false );
    }
    
    /** @return the CWD (current working directory). */
    public Path getCWD()
    {
        return this.dir;
    }
    
    /** @return the last selected index, or 0 (first item), if there was no selection. */
    public int getLastSelectedIndex()
    {
        return this.lastSelectedIndex;
    }
    
    /** Returns the selected path in the list.
      * Sets the last selected index.
      * @return the selected path, or null if there's no selection.
      */
    public Path getSelectedPath()
    {
        Path toret = null;
        int selectedIndex = this.getSelectedIndex();
        
        if ( selectedIndex >= 0 ) {
            this.lastSelectedIndex = selectedIndex;
            toret = this.getPathAt( selectedIndex );
        }
        
        return toret;
    }

    
    private Path dir;
    private int lastSelectedIndex;
}
