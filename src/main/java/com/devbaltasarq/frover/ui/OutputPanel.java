// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;


/** Represents an output panel.
  * @author baltasarq
  */
public class OutputPanel extends Panel {
    public OutputPanel(int rows, int columns, Color fg, Color bg)
    {
        this( rows, columns, fg, bg, Font.decode( "monospaced-12" ) );
    }
    
    public OutputPanel(int rows, int columns, Font f)
    {
        this( rows, columns, Color.WHITE, Color.BLACK, f );
    }
    
    public OutputPanel(int rows, int columns, Color fg, Color bg, Font font)
    {
        this.setLayout( new BorderLayout() );

        this.taOutput = new TextArea( rows, columns );
        this.taOutput.setFont( font );
        this.taOutput.setBackground( bg );
        this.taOutput.setForeground( fg );
        this.taOutput.setEditable( false );
        
        this.add( this.taOutput, BorderLayout.CENTER );
    }

    /** Appends text to the output.
      * @param txt the text to output.
      */
    public void append(String txt)
    {
        this.taOutput.append( txt );
    }
    
    /** Inserts text in the output.
      * @param pos the position in which to insert the text.
      * @param txt the text to insert.
      */
    public void insert(int pos, String txt)
    {
        this.taOutput.insert( txt, pos );
    }
    
    /** Substitutes the whole text.
      * @param txt the text to replace the current place.
      */
    public void setText(String txt)
    {
        this.taOutput.setText( txt );
    }
    
    /** Remove the whole text contents. */
    public void clear()
    {
        this.taOutput.setText( "" );
    }

    private final TextArea taOutput;
}
