// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


import com.devbaltasarq.frover.core.entries.Extension;
import com.devbaltasarq.frover.core.entries.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;


/** The opener for files.
  * @author baltasarq
  */
public class OpenerEngine {
    private OpenerEngine()
    {
        this.fileAssociations = new HashMap<>();
    }
    
    /** Returns the associated app for a given extension.
      * @param ext a given Extension.
      * @return a File that represents the App, or null if not found.
      */
    public File getAppFor(Extension ext)
    {
        return this.fileAssociations.get( ext );
    }
    
    private void add(Extension ext, String appPath)
    {
        this.fileAssociations.put(
                                ext,
                                new File( Path.of( appPath ) ) );
    }
    
    public List<Map.Entry<Extension, File>> all()
    {
        return this.fileAssociations.entrySet().stream().toList();
    }
    
    /** Open a file with an app.
      * @param app the application to use to open a file.
      * @param fileSystemEntry the file to open.
      * @param log how to log the info.
      */
    public void open(File app, Entry fileSystemEntry, Consumer<String> log)
    {
        final String CMD = String.format( Locale.getDefault(),
                                    "%s \"%s\"", 
                                    app.asCanonical(),
                                    fileSystemEntry.asCanonical() );
        final var EXE = new CmdExecutor(
                                fileSystemEntry.getContainer().getPath(),
                                CMD,
                                log );
        EXE.run();
    }
    
    private static void applyConfig(Config cfg)
    {
        final String[] EXTS = cfg.getList( Config.Key.EXTENSIONS );
        final String[] PATHS = cfg.getList( Config.Key.APP_PATHS );
        int num = Math.min( EXTS.length, PATHS.length );
        
        for(int i = 0; i < num; ++i) {
            engine.add( Extension.from( EXTS[ i ] ), PATHS[ i ] );
        }
    }
    
    /** Creates the opener engine.
      * @param cfg the configuration object.
      * @return a unique OpenerEngine object.
      */
    public static OpenerEngine build(Config cfg)
    {
        if ( engine == null ) {
            engine = new OpenerEngine();
            applyConfig( cfg );
        }
        
        return engine;
    }
    
    private final Map<Extension, File> fileAssociations;
    private static OpenerEngine engine = null;
}
