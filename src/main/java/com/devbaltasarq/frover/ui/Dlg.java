// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import javax.swing.JOptionPane;
import javax.swing.JFrame;


/** A shortcut to JOptionPane.
  * @author baltasarq
  */
public class Dlg {
    /** Shows an info dialog.
      * @param owner the owner of the dialog.
      * @param title the title for the dialog.
      * @param message the message to show on the dialog.
      */
    public static void showInfo(JFrame owner, String title, String message)
    {
        JOptionPane.showConfirmDialog(
                        owner,
                                    message,
                                    title,
                                    JOptionPane.OK_OPTION );
    }
    
    /** Shows a confirmation dialog, asking yes or no.
      * @param owner the owner of the dialog.
      * @param title the title for the dialog.
      * @param message the message to show on the dialog.
      * @return 
      */
    public static boolean askYesNo(JFrame owner, String title, String message)
    {
        return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                                    owner,
                                    message,
                                    title,
                                    JOptionPane.YES_NO_OPTION );
    }
    
     /** Shows a confirmation dialog, asking to choose between two options,
       * which must be given as text.
       * @param owner the owner of the dialog.
       * @param title the title for the dialog.
       * @param message the message to show on the dialog.
       * @param yesText the text for the positive option.
       * @param noText the text for the negative option.
       * @return 
       */
    public static boolean askYesNo(
                            JFrame owner,
                            String title,
                            String message,
                            String yesText,
                            String noText)
    {
        return ( 0 == JOptionPane.showOptionDialog(
                                            owner,
                                            message,
                                            title,
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            new String[]{ yesText, noText },
                                            yesText ) );
    }
}
