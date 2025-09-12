// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core.entries;


import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.FileOutputStream;

import com.devbaltasarq.frover.core.Entry;


/** Represents a given file.
  * @author baltasarq
  */
public class File extends Entry {
    public File(Path path)
    {
        super( path );
    }
    
    @Override
    public void copy(Entry target) throws IOException
    {
        if ( this.exists() ) {
            target = this.adaptTargetIfDir( target );
            
            Files.copy(
                    this.getPath(),
                    target.getPath(),
                    StandardCopyOption.REPLACE_EXISTING );
        } else {
            throw new IOException( "mssing org file: " + this.toString() );
        }
    }
    
    @Override
    public void move(Entry target) throws IOException
    {
        if ( this.exists() ) {
            target = this.adaptTargetIfDir( target );
            
            Files.move(
                    this.getPath(),
                    target.getPath(),
                    StandardCopyOption.REPLACE_EXISTING );
        } else {
            throw new IOException( "mssing org file: " + this.toString() );
        }
    }
    
    /** Creates a target file if the target is a dir.
      * So, if the org file is temp.txt, and the target is foo/,
      * then the target becomes foo/temp.txt.
      * @param target the given target, which could be a dir.
      * @return the new Entry reflecting changes, if any.
      */
    private Entry adaptTargetIfDir(Entry target)
    {
        Entry toret = target;
        
        // Determine target
        if ( target.exists()
          && target instanceof Directory )
        {
            // Adds the name of the org file.
            toret = Entry.from( Path.of(
                                   target.asCanonical(),
                                    this.getFileName() ).toFile() );
        }
        
        return toret;
    }
    
    public void create() throws IOException
    {
        try {
            final Path CREATED_FILE = Files.createFile( this.getPath() );
            
            try (final var C_OUT = new FileOutputStream( CREATED_FILE.toFile() ) )
            {
                C_OUT.write( ' ' );
            }
        } catch(IOException exc) {
            throw new IOException(
                            "error creating file or already exists: `"
                            + this.asCanonical()
                            + "`" );            
        }
        
        return;
    }
}
