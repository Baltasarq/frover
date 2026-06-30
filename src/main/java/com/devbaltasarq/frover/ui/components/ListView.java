// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


/** A Basic list with a self-modification guard.
  * @author baltasarq
  */
public class ListView extends javax.swing.JList {
    public ListView()
    {
        this.selfModification = false;
    }
    
    /** @return whether this component is self modifying,
      * so its selection will probably change.
      */    
    public boolean isSelfModifying()
    {
        return this.selfModification;
    }
    
    /** Change the self modifying guard.
      * @param v the new value.
      */
    protected void setSelfModifying(boolean v)
    {
        this.selfModification = v;
    }
    
    private boolean selfModification;
}
