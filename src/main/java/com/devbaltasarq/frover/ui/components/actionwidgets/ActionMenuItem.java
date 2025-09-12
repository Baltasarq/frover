// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components.actionwidgets;


import com.devbaltasarq.frover.ui.components.*;

import java.awt.MenuItem;


/** Represents a Widget of the view.
  * Apparently, Java's AWT does not care about common operations.
  * @author baltasarq
  */
public class ActionMenuItem extends ActionWidget {
    public ActionMenuItem(MenuItem mitem)
    {
        super( mitem );
    }
    
    /** Sets the visibility of the widgets for this action.
      * Menu items can't be made invisible, so we enable/disable them.
      * @param visible whether it is visible or not.
      */
    public void setVisible(boolean visible)
    {
        this.setEnabled( visible );
    }
    
    /** Sets whether this widget is enabled or not.
      * @param enabled whether its is enabled or not.
      */
    public void setEnabled(boolean enabled)
    {
        this.getWidget().setEnabled( enabled );
    }
    
    /** Sets the callback for this menu item.
      * @param f the Runnable object for the callback.
      */
    public void set(Runnable f)
    {
        this.getWidget().addActionListener( (evt) -> f.run() );
    }

    /** @return the menu item. */
    @Override
    public MenuItem getWidget()
    {
        return (MenuItem) super.getWidget();
    }
}
