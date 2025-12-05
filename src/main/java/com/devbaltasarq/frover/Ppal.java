// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover;


import java.nio.file.Path;

import com.devbaltasarq.frover.ui.MainWindow;
import com.devbaltasarq.frover.core.AppInfo;


/** Entry point to the frover app.
  * @author baltasarq
  */
public class Ppal {
    public static void main(String[] args) {
        // Prepare antialising
        try {
            System.setProperty( "swing.aatext", "true" );
            System.setProperty( "awt.useSystemAAFontSettings", "on" );
        } catch(Exception exc) {
            System.err.println( "[ERR] Error setting antialising: "
                                + exc.getMessage() );
        }
        
        var path = Path.of( System.getProperty( "user.home", "./" ));
        
        System.out.println( AppInfo.getFullName() );
        System.out.println( "User path: `" + path + "`" );
        
        if ( args.length > 0 ) {
            path = Path.of( args[ 0 ] );
            System.out.println( "Setting given path: `" + path + "`" );
        }
        
        var mainWin = new MainWindow( path );
        mainWin.run();
    }
}
