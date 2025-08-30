// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;


/** The business logic for a directory browser.
  * @author baltasarq
  */
public class DirBrowser {
    /** Creates a new DirBrowser pointing to the user's home directory.
      * @throws IOException if the user's path is null.
      */
    public DirBrowser() throws IOException
    {
        this( Paths.get( System.getProperty( "user.home", "./" ) ));
    }
    
    /** Creates a new DirBrowser pointing to the given path.
      * @param path
      * @throws IOException 
      */
    public DirBrowser(Path path) throws IOException
    {
        if ( path == null ) {
            throw new IOException( "Dirbrowser: path can't be null" );
        }
        
        this.path = path;
        this.entries = new ArrayList<>( 10 );
    }
    
    /** Reads the directory entries.
     * @return the entries, as a List of Entry objects.
     * @throws IOException when there are no files read.
     */
    public List<Entry> readDir() throws IOException
    {
        final File DIR = this.path.toFile();
        
        if ( DIR != null ) {
            this.entries.clear();

            if ( DIR.isDirectory() ) {
                final File[] FILES = DIR.listFiles();
                            
                if ( FILES != null ) {
                    for(File entry: FILES) {
                        if ( entry != null ) {
                            this.entries.add( Entry.from( entry ) );
                        }
                    }
                } else {
                    throw new IOException( "could not read dir: "
                                            + this.getDirectory().asCanonical() );
                }
            }
        }
        
        return new ArrayList( this.entries );
    }
    
    /** @return the current path. */
    public Path getPath()
    {
        return this.path;
    }
    
    /** @return the current path, as a Directory. */
    public Entry getDirectory()
    {
        return Entry.from( this.path.toFile() );
    }
    
    /** @return the total size for this drive. */
    public long getDriveSize()
    {
        return this.path.toFile().getTotalSpace();
    }
    
    /** @return the total size for this drive. */
    public long getDriveFreeSize()
    {
        return this.path.toFile().getUsableSpace();
    }
    
    private final Path path;
    private final List<Entry> entries;
}
