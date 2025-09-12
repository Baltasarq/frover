// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components.actionwidgets;


import com.devbaltasarq.frover.ui.components.*;

import java.awt.Button;


/** Represents a Widget of the view.
  * Apparently, Java's AWT does not care about common operations.
  * @author baltasarq
  */
public class ActionButton extends ActionWidget {
    public ActionButton(Button button)
    {
        super( button );
    }
    
    /** Sets the visibility of the widgets for this action.
      * @param visible whether it is visible or not.
      */
    public void setVisible(boolean visible)
    {
        this.getWidget().setVisible( visible );
    }
    
    /** Sets whether this widget is enabled or not. */
    public void setEnabled(boolean enabled)
    {
        this.getWidget().setEnabled( enabled );
    }
    
    /** Sets the callback for this button.
      * @param f the Runnable object for the callback.
      */
    public void set(Runnable f)
    {
        this.getWidget().addActionListener( (evt) -> f.run() );
    }

    /** @return the button. */
    @Override
    public Button getWidget()
    {
        return (Button) super.getWidget();
    }
}
