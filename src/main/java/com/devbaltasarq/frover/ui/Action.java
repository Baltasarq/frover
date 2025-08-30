// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import java.util.List;
import java.util.ArrayList;


/** An Action that involves multiple widgets.
  * @author baltasarq
  */
public class Action {
    /** Creates a new action.
      * @param name a name for this action.
      * @param label a label for this action, to show in the ui.
      */
    public Action(String name, String label)
    {
        this.name = name.trim().toLowerCase();
        this.label = label;
        this.widgets = new ArrayList( 3 );
        this.doIt = null;
    }
    
    /** @return the name of this action. */
    public String getName()
    {
        return this.name;
    }
    
    /** @return the label of this action. */
    public String getLabel()
    {
        return this.label;
    }
    
    /** Adds a new widget to the list of observables.
      * @param widget the new widget to observe.
      */
    public void add(Widget widget)
    {
        this.widgets.add( widget );
    }
    
    /** Sets the visibility of the widgets for this action.
      * @param visible whether it is visible or not.
      */
    public void setVisible(boolean visible)
    {
        for(Widget widget: widgets) {
            widget.setVisible( visible );
        }
    }
    
    /** Sets the actual action to do for this action.
      * @param f the function to carry out.
      */
    public void set(Runnable f)
    {
        this.doIt = f;
    }
    
    /** @return the Runnable object as action. */
    public Runnable get()
    {
        return this.doIt;
    }
    
    /** Triggers the action. */
    public void doIt()
    {
        if ( this.doIt != null ) {
            this.doIt.run();
        }
        
        return;
    }
    
    private final String name;
    private final String label;
    private final List<Widget> widgets;
    private Runnable doIt;
}
