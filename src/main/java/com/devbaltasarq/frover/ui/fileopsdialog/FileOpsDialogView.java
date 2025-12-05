// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.fileopsdialog;


import com.devbaltasarq.frover.ui.browser.BrowserView;
import com.devbaltasarq.frover.ui.components.DirChoicePanel;
import com.devbaltasarq.frover.ui.components.FileChoicePanel;
import com.devbaltasarq.frover.ui.components.TitledPanel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;


/** The view of the copy dialog.
  * @author baltasarq
  */
public class FileOpsDialogView extends BrowserView {
    public static final Dimension MIN_SIZE = new Dimension( 400, 300 );
    public static final Dimension START_SIZE = new Dimension( 420, 320 );
    public static final Font FONT_MONO_16 = Font.decode( "monospaced-16" );
    
    public FileOpsDialogView(Frame owner)
    {
        super( new Dialog( owner, true ) );
        
        this.dirChoicePanel = new DirChoicePanel();
        this.fileChoicePanel = new FileChoicePanel();
        this.edOrgFile = new TextField();
        this.edEndFile = new TextField();
        this.edFileName = new TextField();
        this.btOk = new Button( "Ok" );
        this.btCancel = new Button( "Cancel" );
        
        this.build();
    }
    
    private void build()
    {
        final var LY_INTERNAL = new BorderLayout( 1, 2 );
        final var LY_MAIN = new BorderLayout( 1, 2 );
        final var INTERNAL_PANEL = new Panel( LY_INTERNAL );
        
        LY_INTERNAL.setHgap( 5 );
        LY_INTERNAL.setVgap( 5 );
        LY_MAIN.setHgap( 5 );
        LY_MAIN.setVgap( 5 );
                
        INTERNAL_PANEL.add( this.buildOrgFile(), BorderLayout.NORTH );
        INTERNAL_PANEL.add( this.buildBrowser(), BorderLayout.CENTER );
        INTERNAL_PANEL.add( this.buildTargetFileName(), BorderLayout.SOUTH );
        
        // Arrange the external panel
        this.getWindow().setLayout( LY_MAIN );
        this.getWindow().add( INTERNAL_PANEL, BorderLayout.CENTER );
        this.getWindow().add( this.buildButtons(), BorderLayout.SOUTH );
        
        // Show
        this.getWindow().setMinimumSize( MIN_SIZE );
        this.getWindow().setSize( START_SIZE );
        this.getWindow().pack();
        this.getWindow().setModalExclusionType( Dialog.ModalExclusionType.NO_EXCLUDE );
    }
    
    private Panel buildBrowser()
    {
        final var LY = new GridLayout( 1, 2 );
        final var TORET = new TitledPanel( "To directory" );
        
        LY.setHgap( 5 );
        LY.setVgap( 5 );
        
        TORET.getContentsPanel().setLayout( LY );
        TORET.getContentsPanel().add( this.getDirChoicePanel() );
        TORET.getContentsPanel().add( this.getFileChoicePanel() );
        
        return TORET;
    }
    
    private Panel buildOrgFile()
    {
        final var LY = new BorderLayout();
        final var TORET = new TitledPanel( "File" );
        
        LY.setHgap( 5 );
        LY.setVgap( 5 );
        TORET.getContentsPanel().setLayout( LY );
        TORET.getContentsPanel().add( this.edOrgFile, BorderLayout.CENTER );
        
        this.edOrgFile.setColumns( 80 );
        this.edOrgFile.setEditable( false );
        this.edOrgFile.setFont( FONT_MONO_16 );
        
        return TORET;
    }
    
    private Panel buildTargetFileName()
    {
        final var LY = new BorderLayout();
        final var TORET = new TitledPanel( "Final file name" );
        
        LY.setHgap( 5 );
        LY.setVgap( 5 );
        TORET.getContentsPanel().setLayout( LY );
        TORET.getContentsPanel().add( this.edFileName, BorderLayout.CENTER );
        TORET.getContentsPanel().add( this.edEndFile, BorderLayout.SOUTH );
        
        this.edFileName.setColumns( 80 );
        this.edFileName.setFont( FONT_MONO_16 );
        
        this.edEndFile.setColumns( 80 );
        this.edEndFile.setFont( FONT_MONO_16 );
        this.edEndFile.setEditable( false );
        
        return TORET;
    }
    
    private Panel buildButtons()
    {
        final var LY = new GridLayout( 1, 4, 5, 5 );
        final var TORET = new Panel( LY );
        
        TORET.add( new Panel() );
        TORET.add( new Panel() );
        TORET.add( this.btCancel );
        TORET.add( this.btOk );
        
        return TORET;
    }
    
    /** @return the dialog for this view. */
    public Dialog getDialog()
    {
        return (Dialog) this.getWindow();
    }
    
    /** @return the panel for selecting directories. */
    @Override
    public DirChoicePanel getDirChoicePanel()
    {
        return this.dirChoicePanel;
    }
    
    /** @return the panel for selecting files. */
    @Override
    public FileChoicePanel getFileChoicePanel()
    {
        return this.fileChoicePanel;
    }
    
    /** @return the text field for the path of the original file. */
    public TextField getEdOrgFile()
    {
        return this.edOrgFile;
    }
    
    /** @return the text field for the target file name. */
    public TextField getEdFileName()
    {
        return this.edFileName;
    }
    
    /** @return the text field for the path of the target file. */
    public TextField getEdEndFile()
    {
        return this.edEndFile;
    }
    
    /** @return the button for Ok. */
    public Button getBtOk()
    {
        return this.btOk;
    }
    
    /** @return the button for Cancel. */
    public Button getBtCancel()
    {
        return this.btCancel;
    }
    
    private final DirChoicePanel dirChoicePanel;
    private final FileChoicePanel fileChoicePanel;
    private final TextField edOrgFile;
    private final TextField edEndFile;
    private final TextField edFileName;
    private final Button btOk;
    private final Button btCancel;
}
