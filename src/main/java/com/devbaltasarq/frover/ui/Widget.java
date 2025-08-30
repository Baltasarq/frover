// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>

package com.devbaltasarq.frover.ui;


/** Represents a Widget of the view.
  * Apparently, Java's AWT does not care about common operations.
  * @author baltasarq
  */
public interface Widget {
    /** Sets the visibility of the widgets for this action.
      * @param visible whether it is visible or not.
      */
    public void setVisible(boolean visible);
}
