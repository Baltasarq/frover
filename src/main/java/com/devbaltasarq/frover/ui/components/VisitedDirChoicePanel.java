// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.BorderLayout;

import java.nio.file.Path;
import java.util.function.Consumer;


/** This is a small wrapper around a DirList
  * @see DirList
  * @author baltasarq
  */
public class VisitedDirChoicePanel extends Panel {
    public static final Color FG = Color.WHITE;
    public static final Color BG = Color.GRAY;
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    public VisitedDirChoicePanel()
    {
        this( FG, BG, FONT_MONO_16 );
    }
    
    public VisitedDirChoicePanel(Color fg, Color bg, Font font)
    {
        this.btNew = new Button( "+" );
        this.btRemove = new Button( "-" );
        this.favDirList = new NamedPathList( fg, bg, font );
        this.histDirList = new ShortenedDirList( fg, bg, font );
        this.dirChanger = (p) -> {};
        this.btNewClicker = () -> {};
        
        this.build();
        this.favDirList.addItemListener(
                            (evt) -> this.doDirSelected( this.favDirList ) );
        this.histDirList.addItemListener(
                            (evt) -> this.doDirSelected( this.histDirList ) );
        this.btNew.addActionListener(
                            (evt) -> this.doNewFavDir() );
        this.btRemove.addActionListener(
                            (evt) -> this.doRemoveFavDir() );
    }
    
    private void build()
    {
        final var LYB = new BorderLayout();
        final var LYG = new GridLayout( 2, 1 );
        final var LYP = new GridLayout( 1, 2 );
        final var PANEL_MAIN = new Panel( LYB );
        final var PANEL_GRID = new Panel( LYG );
        final var PANEL_BUTTONS = new Panel( LYP );
        
        LYB.setHgap( 5 );
        LYB.setVgap( 5 );
        LYG.setHgap( 5 );
        LYG.setVgap( 5 );
        LYP.setHgap( 10 );
        LYP.setVgap( 10 );
        
        PANEL_BUTTONS.add( this.btNew );
        PANEL_BUTTONS.add( this.btRemove );
        
        PANEL_GRID.add( this.favDirList );
        PANEL_GRID.add( this.histDirList );
        PANEL_MAIN.add( PANEL_BUTTONS, BorderLayout.NORTH );
        PANEL_MAIN.add( PANEL_GRID, BorderLayout.CENTER );
        this.setLayout( new BorderLayout() );
        this.add( PANEL_MAIN, BorderLayout.CENTER );
    }
    
    /** Add a new directory to the history panel. 
      * @param path the new Path to add.
      */
    public void addDirToHistory(Path path)
    {
        this.getHistList().insert( 0, path );
    }
    
    /** Removes a given favourite directory. */
    public void removeFavDir()
    {
        int row = this.getFavList().getSelectedIndex();
        
        if ( row >= 0 ) {
            this.getFavList().removePathAt( row );
        }
    }
    
    /** Add a new directory to the panel. **/
    private void doNewFavDir()
    {
        this.btNewClicker.run();
    }
    
    /** Remove a directory on the panel. **/
    private void doRemoveFavDir()
    {
        this.btRemoveClicker.run();
    }
    
    /** Change directory from the dir list. */
    private void doDirSelected(PathChoice list)
    {
        int dirPos = list.getSelectedIndex();
        
        if ( dirPos >= 0 ) {
            this.dirChanger.accept( list.getPathAt( dirPos ) );
        }
    }
    
    /** Changes the listener for the directory change.
      * @param doIt the new function to invoke when the directory changes.
      */
    public void setChangeDirAction(Consumer<Path> doIt)
    {
        this.dirChanger = doIt;
    }
    
    /** @return the action invoked when the directory is changed. */
    public Consumer<Path> getChangeDirAction()
    {
        return this.dirChanger;
    }
    
    /** Changes the listener for the new fav button.
      * @param doIt the new function to invoke.
      */
    public void setNewFavAction(Runnable doIt)
    {
        this.btNewClicker = doIt;
    }
    
    /** @return the action invoked when the new fav button is clicked. */
    public Runnable getNewFavAction()
    {
        return this.btNewClicker;
    }
    
    /** Changes the listener for the remove fav button.
      * @param doIt the new function to invoke.
      */
    public void setRemoveFavAction(Runnable doIt)
    {
        this.btRemoveClicker = doIt;
    }
    
    /** @return the action invoked when the remove fav button is clicked. */
    public Runnable getRemoveFavAction()
    {
        return this.btRemoveClicker;
    }
    
    /** @return the list of fav directories. */
    public NamedPathList getFavList()
    {
        return this.favDirList;
    }
    
    /** @return the list of history directories. */
    public ShortenedDirList getHistList()
    {
        return this.histDirList;
    }    

    private Consumer<Path> dirChanger;
    private Runnable btNewClicker;
    private Runnable btRemoveClicker;
    private final Button btNew;
    private final Button btRemove;
    private final NamedPathList favDirList;
    private final ShortenedDirList histDirList;
}
