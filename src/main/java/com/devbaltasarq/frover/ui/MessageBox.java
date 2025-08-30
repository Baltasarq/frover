// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import java.awt.Dialog;
import java.awt.Window;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/** Represents a dialog box.
  * @author baltasarq
  */
public class MessageBox extends Dialog {
    private static final Dimension MIN_SIZE = new Dimension( 300, 200 );
    
    /** Creates a dialog with a title, a message, and an Ok button.
      * @param owner the given owner of this dialog.
      * @param title the given title of the dialog.
      * @param message the given message of the dialog.
      */
    public MessageBox(Window owner, String title, String message)
    {
        super( owner, title, Dialog.ModalityType.APPLICATION_MODAL );
        this.message = message;        
        this.btOk = new Button( "Ok" );
        
        this.build();
        this.btOk.addActionListener( (evt) -> this.doClose() );
        this.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                MessageBox.this.doClose();
            }
        });
    }
    
    /** Build the layout for this dialog. */
    private void build()
    {
        final BorderLayout LY = new BorderLayout();
        final Label LBL_MESSAGE = new Label( this.message );
        final Font FONT = Font.decode( "serif-16" );
        
        LBL_MESSAGE.setFont( FONT );
        LY.setHgap( 5 );
        LY.setVgap( 5 );
        this.setLayout( LY );
        
        this.add( LBL_MESSAGE, BorderLayout.CENTER );
        this.add( this.btOk, BorderLayout.SOUTH );
        this.setMinimumSize( MIN_SIZE );
    }
    
    /** Callback for the button being clicked. */
    private void doClose()
    {
        super.setVisible( false );
    }
    
    /** Mandates calling run, instead of simply setVisible().
      * @param v the new value for visibility.
      */
    @Override
    public void setVisible(boolean v)
    {
        if ( v ) {
            this.run();
        } else {
            super.setVisible( v );
        }
        
        return;
    }
    
    /** Makes the dialog visible, blocking until "Ok" is clicked. */
    public void run()
    {
        this.setLocationByPlatform( true );
        super.setVisible( true );
        this.dispose();
    }
    
    private final Button btOk;
    private final String message;
}
