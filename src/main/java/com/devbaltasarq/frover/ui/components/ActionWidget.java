// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.awt.Button;
import java.awt.MenuItem;

import com.devbaltasarq.frover.ui.components.actionwidgets.ActionButton;
import com.devbaltasarq.frover.ui.components.actionwidgets.ActionMenuItem;


/** Represents a Widget of the view.
  * Apparently, Java's AWT does not care about common operations.
  * @author baltasarq
  */
public abstract class ActionWidget {
    public ActionWidget(Object widget)
    {
        this.widget = widget;
    }
    
    /** Sets the visibility of the widgets for this action.
      * @param visible whether it is visible or not.
      */
    public abstract void setVisible(boolean visible);
    
    /** Sets whether this widget is enabled or not.
      * @param enabled whether the widget will be enabled or not.
      */
    public abstract void setEnabled(boolean enabled);
    
    /** Sets the callback for all related widgets.
      * @param f the Runnable object for the callback.
      */
    public abstract void set(Runnable f);
    
    /** @return the widget. */
    public Object getWidget()
    {
        return this.widget;
    }
    
    public static ActionWidget from(Object obj)
    {
        ActionWidget toret = null;
        
        switch ( obj ) {
            case Button button -> toret = new ActionButton( button );
            case MenuItem menuItem -> toret = new ActionMenuItem( menuItem );
            default -> {
                throw new Error( "No ActionWidget for " + obj.getClass().getSimpleName() );
            }
        }
        
        return toret;
    }
    
    private final Object widget;
}
