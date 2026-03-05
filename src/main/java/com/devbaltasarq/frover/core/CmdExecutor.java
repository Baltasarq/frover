// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/** Executes a command in the OS' shell.
  * @author baltasarq
  */
public class CmdExecutor {
    public CmdExecutor(Path cwd, String cmd, Consumer<String> log)
    {
        this.cwd = cwd;
        this.cmd = cmd;
        this.log = log;
    }
    
    private List<String> buildCommand()
    {
        final var CMD = new ArrayList<String>();
        
        if ( !isThisWindows() ) {
            CMD.addAll( List.of( "/bin/sh", "-c" ) );
        } else {
            CMD.addAll( List.of( "cmd.exe", "/c" ) );
        }
        
        CMD.add( this.cmd );
        return CMD;
    }
    
    public void run()
    {
        final var BUILDER = new ProcessBuilder();
        
        try {
            final Path TEMP_PATH = Files.createTempFile( "process-", ".out" );
            final List<String> CMDS = this.buildCommand();
            final String CMD = CMDS.stream().collect( Collectors.joining( " " ) );
            var TEMP_FILE = TEMP_PATH.toFile();
            int exitCode = 0;
            
            this.log.accept( "Executing: `" + CMD + "`" );
            
            final Process PROCESS = BUILDER.directory( this.cwd.toFile() )
                .redirectOutput( TEMP_FILE )
                .command( CMDS )
                .start();
            
            try {
                exitCode = PROCESS.waitFor();
            } catch(InterruptedException exc) {
                this.log.accept( "Interrupted!" );
            } finally {
                PROCESS.destroy(); // Clean up
                if ( PROCESS.isAlive() ) {
                    PROCESS.destroyForcibly();
                }
            }
            
           
            Files.lines( TEMP_PATH ).forEach( this.log );
            this.log.accept( "\nExit code: " + exitCode + "\n" );
        } catch(IOException exc) {
            this.log.accept( "[ERR] launching: `"
                                + this.cmd + "`: " + exc.getMessage() );
        }
    }
    
    /** @returns whether this is a Windows operating system. */
    private static boolean isThisWindows()
    {
        if ( isWindows == null ) {
            isWindows = System.getProperty( "os.name" )
                                    .toLowerCase().startsWith( "windows" );
        }
        
        return isWindows;
    }
    
    private final Consumer<String> log;
    private final Path cwd;
    private final String cmd;
    private static Boolean isWindows = null;
}
