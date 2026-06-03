// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


import java.util.ArrayList;
import java.util.List;


/** A cross-platform path to terminal. */
public class TermPath {
    private static final List<String> WIN_SHELL = List.of(
                                "cmd",
                                "/c",
                                "start",
                                "cmd.exe" );

    private static final List<String> NIX_SHELL =  List.of(
                                "/usr/bin/xterm",
                                "-fa", "Monospace",
                                "-fs", "12" );
    
    public TermPath(Config cfg)
    {
        this.setDefaultTermPath();
        
        if ( cfg != null ) {
            final String TERM_PATH = cfg.get( Config.Key.TERM_PATH );
            
            if ( TERM_PATH != null ) {
                this.cmds = new ArrayList<>( 1 );
                this.cmds.add( TERM_PATH );
            }
        }
    }
    
    private void setDefaultTermPath()
    {
        final var OS_NAME = System.getProperty( "os.name" ).toLowerCase();
        this.cmds = NIX_SHELL;
        
        if ( OS_NAME.contains( "windows" ) ) {
            this.cmds = WIN_SHELL;
        }
    }
    
    public List<String> getCmds()
    {
        return this.cmds;
    }
    
    public static TermPath build(Config cfg)
    {
        assert instance != null: "tried to invoke build() more than once";
        
        if ( instance == null ) {
            instance = new TermPath( cfg );
        }
        
        return instance;
    }
    
    public static TermPath get()
    {
        assert instance != null: "tried to invoke get() before build";
        return instance;
    }
    
    private List<String> cmds;
    private static TermPath instance = null;
}
