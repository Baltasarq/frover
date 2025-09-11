// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core.entries;


import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.DirBrowser;


/** Represents a given directory.
  * @author baltasarq
  */
public class Directory extends Entry {
    public static String SEPARATOR = java.io.File.separator;

    public Directory(Path path) throws IOException
    {        
        super( path );
        
        if ( !path.toFile().isDirectory() ) {
            throw new IOException(
                        "Directory: path is not a dir: " + path.toFile() );
        }
    }
    
    @Override
    public void copy(Entry target) throws IOException
    {
        final DirBrowser DIR_BROWSER = new DirBrowser( this );
        
        if ( this.exists() ) {
            for(Entry entry: DIR_BROWSER.readDir()) {
                if ( entry instanceof Directory ) {
                    // Create the target subdirectory
                    final var SUB_DIR = new Directory(
                                            Path.of(
                                                target.asCanonical(),
                                                entry.getFileName() ) );
                    
                    SUB_DIR.create();
                    entry.copy( SUB_DIR );
                } else {
                    entry.copy( target );
                }
            }
        } else {
            throw new IOException( "mssing org file: `" + this.toString() + "`" );
        }
    }
    
    @Override
    public void move(Entry target) throws IOException
    {
        if ( this.exists() ) {
            this.copy( target );
            
            if ( !this.getFile().delete() ) {
                throw new IOException( "error removing: `" + this.toString() + "`" );
            }
        } else {
            throw new IOException( "mssing org file: `" + this.toString() + "`" );
        }
    }
    
    @Override
    public void create() throws IOException
    {
        if ( !this.getFile().mkdirs() ) {
            throw new IOException(
                            "could not create dirs: `"
                            + this.asCanonical()
                            + "`" );
        }
        
        return;
    }
}
