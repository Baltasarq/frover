// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import java.awt.Dialog;
import java.awt.Window;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.Font;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/** Represents a dialog box.
  * @author baltasarq
  */
public class AskBox extends Dialog {
    private static final Dimension MIN_SIZE = new Dimension( 300, 200 );
    
    /** Creates a dialog with a title, a message, and a Yes/No buttons.
      * @param owner the given owner of this dialog.
      * @param title the given title of the dialog.
      * @param message the explanation part of the question.
      * @param question the question itself.
      * @param positiveLabel the label of the positive button.
      * @param negativeLabel the label of the negative button.
      */
    public AskBox(Window owner,
                    String title,
                    String message,
                    String question,
                    String positiveLabel,
                    String negativeLabel)
    {
        super( owner, title, Dialog.ModalityType.APPLICATION_MODAL );
        this.message = message;
        this.question = question;        
        this.btYes = new Button( positiveLabel );
        this.btNo = new Button( negativeLabel );
        this.answer = null;
        
        this.build();
        this.btYes.addActionListener( (evt) -> this.doClose( true ) );
        this.btNo.addActionListener( (evt) -> this.doClose( false ) );
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                AskBox.this.doClose( false );
            }
        });
    }
    
    /** Build the layout for this dialog. */
    private void build()
    {
        final var LY_MAIN = new BorderLayout();
        final var LY_BUTTONS = new GridLayout( 1, 2 );
        final var LY_MESSAGES = new GridLayout( 2, 1 );
        final var LBL_QUESTION = new Label( this.question );
        final var LBL_MESSAGE = new Label( this.message );
        final var PNL_BUTTONS = new Panel( LY_BUTTONS );
        final var PNL_MESSAGES = new Panel( LY_MESSAGES );
        final var FONT = Font.decode( "serif-16" );
        
        LBL_QUESTION.setFont( FONT );
        LBL_MESSAGE.setFont( FONT );
        
        LY_MAIN.setHgap( 5 );
        LY_MAIN.setVgap( 5 );
        LY_BUTTONS.setHgap( 5 );
        LY_BUTTONS.setVgap( 5 );
        LY_MESSAGES.setHgap( 5 );
        LY_MESSAGES.setVgap( 5 );
        
        this.setLayout( LY_MAIN );
        
        PNL_MESSAGES.add( LBL_MESSAGE );
        PNL_MESSAGES.add( LBL_QUESTION );
        
        PNL_BUTTONS.add( this.btNo );
        PNL_BUTTONS.add( this.btYes );
        
        this.add( PNL_MESSAGES, BorderLayout.CENTER );
        this.add( PNL_BUTTONS, BorderLayout.SOUTH );
        this.setMinimumSize( MIN_SIZE );
    }
    
    /** Callback for the button being clicked. */
    private void doClose(boolean answer)
    {
        super.setVisible( false );
        this.answer = answer;
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
    
    /** Makes the dialog visible, blocking until Yes or No are clicked.
      * @return true if user clicked on the Yes button, false otherwise.
      */
    public boolean run()
    {
        this.setLocationByPlatform( true );
        super.setVisible( true );
        this.dispose();
        
        return this.answer;
    }
    
    /** @return the answer, true if "Yes" was clicked, false otherwise. */
    public boolean getAnswer()
    {
        if ( this.answer == null ) {
            throw new Error( "AskBox: the answer is not ready, call run() before" );
        }
        
        return this.answer;
    }
    
    private Boolean answer;
    private final Button btYes;
    private final Button btNo;
    private final String message;
    private final String question;
}
