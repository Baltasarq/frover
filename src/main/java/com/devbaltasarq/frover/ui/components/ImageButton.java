// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Button;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;


/** A Button supporting an image.
  * @author baltasarq
  */
public class ImageButton extends Button {
    public ImageButton()
    {
        this( "", null );
    }

    public ImageButton(String text) {
        this( text, null );
    }

    public ImageButton(String text, Image icon)
    {
        this.text = text;
        this.icon = icon;
        this.revalidate();
        this.repaint();
    }
    
    /** @return the text on this button.
     *          an empty string if there is none.
     */
    public String getText()
    {
        return this.text;
    }
    
    /** @return the image on this button, null if there is none. */
    public Image getIcon()
    {
        return this.icon;
    }
    
    /** Changes the text on the button.
      * @param newText the new text for the button.
      */
    public void setText(String newText)
    {
        if ( newText == null ) {
            newText = "";
        }
        
        this.text = newText;
        this.revalidate();
        this.repaint();
    }
    
    /** Change the icon on the button.
      * @param newIcon the new image for the icon.
      */
    public void setIcon(Image newIcon)
    {
        this.icon = newIcon;
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void paint(Graphics g)
    {
        final int WIDTH = g.getFontMetrics().stringWidth( this.getText() );
        
        super.paint( g );
        g.drawString( this.getText(), 0, 0 );
        g.drawImage( this.icon, WIDTH, 0, null );
    }

    private String text;
    private Image icon;
}
