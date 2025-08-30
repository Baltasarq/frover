// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core.entries;


import java.nio.file.Path;

import com.devbaltasarq.frover.core.Entry;


/** Represents a given file.
  * @author baltasarq
  */
public class File extends Entry {
    public File(Path path)
    {
        super( path );
    }
}
