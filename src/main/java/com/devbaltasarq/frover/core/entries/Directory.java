// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core.entries;


import java.nio.file.Path;
import java.io.IOException;

import com.devbaltasarq.frover.core.Entry;


/** Represents a given directory.
  * @author baltasarq
  */
public class Directory extends Entry {
    public Directory(Path path) throws IOException
    {        
        super( path );
        
        if ( !path.toFile().isDirectory() ) {
            throw new IOException( "Directory: path is not a dir." );
        }
    }
    
    public static String SEPARATOR = java.io.File.separator;
}
