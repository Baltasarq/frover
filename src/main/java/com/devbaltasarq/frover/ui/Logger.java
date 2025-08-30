// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;

import java.util.Calendar;


/** Logs info in a given OutputPanel.
  * @author baltasarq
  * @see OutputPanel
  */
public class Logger {
    public final static String ERROR_TAG = "ERR";
    public final static String INFO_TAG = "NFO";
    public final static String DEBUG_TAG = "DBG";
    
    /** Creates a new logger, given an existing text area.
      * @param textArea the text area to use for logging.
      * @see OutputPanel
      */
    public Logger(OutputPanel textArea)
    {
        this.output = textArea;
        this.output.setText( "" );
    }
    
    /** Logs any info to the output.
      * @param tag the tag, such as ERR.
      * @param txt the new log contents
      */
    public void log(String tag, String txt)
    {
        final Calendar NOW = Calendar.getInstance();
        
        this.output.append(
                        String.format( "%04d/%02d/%02d %02d:%02d:%02d - [%s] %s\n",
                                       NOW.get( Calendar.YEAR ),
                                       NOW.get( Calendar.MONTH ) + 1,
                                       NOW.get( Calendar.DAY_OF_MONTH ),
                                       NOW.get( Calendar.HOUR ),
                                       NOW.get( Calendar.MINUTE ),
                                       NOW.get( Calendar.SECOND ),
                                       tag,
                                       txt ));
    }
    
    /** Logs an error to the output.
      * @param txt the new log contents.
      */
    public void e(String txt)
    {
        this.log( ERROR_TAG, txt );
    }
    
    /** Logs debug info to the output.
      * @param txt the new log contents.
      */
    public void d(String txt)
    {
        this.log( DEBUG_TAG, txt );
    }
    
    /** Logs info to the output.
      * @param txt the new log contents.
      */
    public void i(String txt)
    {
        this.log( INFO_TAG, txt );
    }
    
    private final OutputPanel output;
}
