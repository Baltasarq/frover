// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.box;


import java.awt.Dialog;
import java.awt.Window;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/** Represents a dialog box.
  * @author baltasarq
  */
public class InputBox extends Dialog {
    private static final Dimension MIN_SIZE = new Dimension( 300, 200 );
    
    /** Creates a dialog with a title, a message, and an Ok button.
      * @param owner the given owner of this dialog.
      * @param title the given title of the dialog.
      * @param message the given message of the dialog.
      */
    public InputBox(Window owner, String title, String message)
    {
        super( owner, title, Dialog.ModalityType.APPLICATION_MODAL );
        this.message = message;
        this.edValue = new TextField();
        this.btOk = new Button( "Ok" );
        this.btCancel = new Button( "Cancel" );
        this.answer = null;
        
        this.build();
        this.btOk.addActionListener( (evt) -> this.doClose( true ) );
        this.btCancel.addActionListener( (evt) -> this.doClose( false ) );
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                InputBox.this.doClose( false );
            }
        });
    }
    
    /** Build the layout for this dialog. */
    private void build()
    {
        final var LY = new BorderLayout();
        final var LY_INNER = new BorderLayout();
        final var LY_BUTTONS = new GridLayout( 1, 4 );
        final var PNL_INNER = new Panel( LY_INNER );
        final var PNL_BUTTONS = new Panel( LY_BUTTONS );
        final var LBL_MESSAGE = new Label( this.message );
        final var FONT = Font.decode( "serif-16" );
                
        LBL_MESSAGE.setFont( FONT );
        LY.setHgap( 5 );
        LY.setVgap( 5 );
        LY_INNER.setHgap( 5 );
        LY_INNER.setVgap( 5 );
        LY_BUTTONS.setHgap( 5 );
        LY_BUTTONS.setVgap( 5 );
        this.setLayout( LY );
        
        PNL_INNER.add( LBL_MESSAGE, BorderLayout.NORTH  );
        PNL_INNER.add( this.edValue, BorderLayout.CENTER );
        
        PNL_BUTTONS.add( new Panel() );
        PNL_BUTTONS.add( new Panel() );
        PNL_BUTTONS.add( this.btCancel );
        PNL_BUTTONS.add( this.btOk );
        
        
        this.add( PNL_INNER, BorderLayout.CENTER );
        this.add( PNL_BUTTONS, BorderLayout.SOUTH );
        this.setMinimumSize( MIN_SIZE );
        this.pack();
    }
    
    /** Callback for the button being clicked.
      * @param accepted true if the user pressed ok, false otherwise.
      */
    private void doClose(boolean accepted)
    {
        if ( accepted ) {
            this.answer = this.edValue.getText().trim();
        }
        
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
    public String run()
    {
        this.setLocationByPlatform( true );
        super.setVisible( true );
        this.dispose();
        
        return this.getAnswer();
    }
    
    /** @return the value entered by the user. */
    public String getAnswer()
    {
        return this.answer;
    }
    
    private final Button btOk;
    private final Button btCancel;
    private final TextField edValue;
    private final String message;
    private String answer;
}
