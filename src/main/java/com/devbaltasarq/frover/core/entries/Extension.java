// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core.entries;


import com.devbaltasarq.frover.core.Entry;


/** Represents the extension of a file path.
  * @author baltasarq
  */
public class Extension {
    private Extension(String ext)
    {
        this.ext = ext.trim().toLowerCase();
    }
    
    /** @return the extension itself. */
    public String get()
    {
        return this.ext;
    }
       
    @Override
    public int hashCode()
    {
        return this.ext.hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if ( this == obj ) {
            return true;
        }
        
        boolean toret = false;
        
        if ( obj instanceof Extension ext ) {
            toret = this.get().equals( ext.get() );
        }
        
        return toret;
    }
    
    /** @return an Extension object from a given String path.
      * @param strPath a given path, as a String.
      */
    public static Extension from(String strPath)
    {
        String toret = "";
        
        int posDotExt = strPath.lastIndexOf( "." );
        toret = strPath.substring( posDotExt + 1 );        
        return new Extension( toret );
    }
    
    /** @return an Extension object from a given String path.
      * @param entry a given path, as an Entry.
      */
    public static Extension from(Entry entry)
    {
        return Extension.from( entry.getFileName() );
    }
    
    private String ext;
}
