// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;


/** Configuration for the directory browser.
  * @author baltasarq
  */
public class Cfg {   
    public enum OutputPanelVisibility {
        HIDDEN( false ),
        SHOWN( true );
        
        OutputPanelVisibility(boolean v)
        {
            this.val = v;
        }
        
        /** @return a bool value determining visibility. */
        public boolean get()
        {
            return this.val;
        }
        
        public static OutputPanelVisibility from(boolean v)
        {
            int pos = ( v ? 1 : 0 );
            return values()[ pos ];
        }
        
        private boolean val;
    }
    
    public enum HiddenFilesVisibility {
        HIDDEN( false ),
        SHOWN( true );
        
        HiddenFilesVisibility(boolean v)
        {
            this.val = v;
        }
        
        /** @return a bool value determining visibility. */
        public boolean get()
        {
            return this.val;
        }
        
        public static HiddenFilesVisibility from(boolean v)
        {
            int pos = ( v ? 1 : 0 );
            return values()[ pos ];
        }
        
        private boolean val;
    }
    
    protected Cfg()
    {
        this.setDefaults();
    }
    
    private void setDefaults()
    {
        this.showHiddenFiles = HiddenFilesVisibility.HIDDEN;
        this.viewOutput = OutputPanelVisibility.HIDDEN;
    }
    
    /** Modifies whether hidden files are shown or not.
      * @param v the new setting.
      * @see HiddenFilesVisibility
      */
    public void setShowHiddenFiles(HiddenFilesVisibility v)
    {
        this.showHiddenFiles = v;
    }
    
    /** @return whether hidden files are shown or not. */
    public HiddenFilesVisibility isShowingHiddenFiles()
    {
        return this.showHiddenFiles;
    }
    
    /** @return whether the output area is visible or not. */
    public OutputPanelVisibility isOutputVisible()
    {
        return this.viewOutput;
    }
    
    /** Changes the visibility of the output area.
      * @param v the new visibility value.
      */
    public void setViewOutput(OutputPanelVisibility v)
    {
        this.viewOutput = v;
    }
    
    public static Cfg get()
    {
        if ( cfgRepo == null ) {
            cfgRepo = new Cfg();
        }
        
        return cfgRepo;
    }
    
    private HiddenFilesVisibility showHiddenFiles;
    private OutputPanelVisibility viewOutput;
    private static Cfg cfgRepo;
}
