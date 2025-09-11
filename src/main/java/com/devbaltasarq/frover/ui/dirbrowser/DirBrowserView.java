// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.dirbrowser;


import com.devbaltasarq.frover.ui.components.DirChoicePanel;
import com.devbaltasarq.frover.ui.components.FileChoicePanel;
import java.awt.Window;


/** A view of a window with a panel for directories and files.
  * @author baltasarq
  */
public abstract class DirBrowserView {
    protected DirBrowserView(Window view)
    {
        this.view = view;
        this.view.setLocationByPlatform( true );
    }
    
    /** The view stored.
      * @return a Frame or a Dialog.
      */
    public Window getWindow()
    {
        return this.view;
    }
    
    public abstract DirChoicePanel getDirChoicePanel();
    public abstract FileChoicePanel getFileChoicePanel();
    
    private final Window view;
}
