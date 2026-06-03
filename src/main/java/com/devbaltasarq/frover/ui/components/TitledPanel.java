// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JLabel;


/** A Panel with a title.
  * This is a panel with a BorderLayout and a label at north.
  * @author baltasarq
  */
public class TitledPanel extends JPanel {
    public static final Font FONT_SERIF_18 = Font.decode( "serif-bold-18" );
    
    public TitledPanel(String title)
    {
        this( title, FONT_SERIF_18 );
    }
    
    public TitledPanel(String title, Font font)
    {
        this.build( title, font );
    }
    
    private void build(String title, Font font)
    {
        final BorderLayout LY = new BorderLayout();
        
        LY.setHgap( 10 );
        LY.setVgap( 10 );
        
        this.lblTitle = this.buildTitleLabel( title, font );
        this.pnlContents = new JPanel();
        
        this.setLayout( LY );
        this.add( this.lblTitle, BorderLayout.NORTH );
        this.add( this.pnlContents, BorderLayout.CENTER );
    }
    
    private JLabel buildTitleLabel(String title, Font font)
    {
        final var TORET = new JLabel( title );
        
        TORET.setFont( font );
        
        return TORET;
    }
    
    /** @return the current title. */
    public String getTitle()
    {
        return this.lblTitle.getText();
    }
    
    /** Changes the title of the panel. */
    public void setTitle(String title)
    {
        this.lblTitle.setText( title );
    }
    
    /** @return the label that holds the title. */
    public JLabel getTitleLabel()
    {
        return this.lblTitle;
    }
    
    /** @return the panel that holds the contents. */
    public JPanel getContentsPanel()
    {
        return this.pnlContents;
    }
    
    private JPanel pnlContents;
    private JLabel lblTitle;
}
