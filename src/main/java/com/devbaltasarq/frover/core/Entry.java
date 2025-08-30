// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


import java.nio.file.Path;
import java.io.IOException;

import com.devbaltasarq.frover.core.entries.Directory;
import com.devbaltasarq.frover.core.entries.File;


/** Represents an entry, mostly files and directories.
  * @author baltasarq
  */
public class Entry {
    public Entry(Path path)
    {
        this.p = path;
        this.f = path.toFile();
    }
    
    /** @return the path to the file/dir/whatever's parent. */
    public String getParentPath()
    {
        return toCanonicalPath( this.p.getParent() );
    }
    
    /** @return the path corresponding to the root. */
    public Path getRoot()
    {
        return this.p.getRoot();
    }
    
    /** @return just the file name of this entry. */
    public String getFileName()
    {
        return this.f.getName();
    }
    
    /** @return the path to the file/dir/whatever. */
    public Path getPath()
    {
        return this.p;
    }
    
    /** @return the file to the file/dir/whatever. */
    public java.io.File getFile()
    {
        return this.f;
    }
    
    /** @return a string with the path to this file. */
    public String asCanonical()
    {
        return toCanonicalPath( this.getPath() );
    }
    
    /** @return whether this file or dir or whatever is hidden or not. */
    public boolean isHidden()
    {
        return this.f.isHidden();
    }
    
    @Override
    public boolean equals(Object other)
    {
        boolean toret = false;
        
        if ( other instanceof Entry e2 ) {
            toret = this.getPath() == e2.getPath();
        }
        
        return toret;
    }
    
    @Override
    public int hashCode()
    {
        int toret = 0;
        
        if ( this.p != null ) {
            toret = this.p.hashCode();
        }
        
        return toret;
    }
    
    @Override
    public String toString()
    {
        return this.getFileName();
    }
    
    private static String toCanonicalPath(Path path)
    {
        final java.io.File FILE = path.toFile();
        String toret = FILE.getAbsolutePath();
        
        try {
            toret = FILE.getCanonicalPath();
        } catch(IOException exc) {
            // Do nothing, we use the absolute path.
        }
        
        return toret;
    }
    
    /** @return the corresponding Entry to the given f.
      * @param f a java.io.File.
      * @see com.devbaltasarq.frover.core.entries.File
      * @see com.devbaltasarq.frover.core.entries.Directory
      * @see com.devbaltasarq.frover.core.Entry
      */
    public static Entry from(java.io.File f)
    {
        Entry toret;
        
        if ( f.isDirectory() ) {
            try {
                toret = new Directory( f.toPath() );
            } catch(IOException exc) {
                toret = null;       // ignored -- can't be thrown
            }
        } else {
            toret = new File( f.toPath() );
        }
        
        return toret;
    }
    
    private final Path p;
    private final java.io.File f;
}
