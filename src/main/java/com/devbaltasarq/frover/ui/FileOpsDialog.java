// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui;


import com.devbaltasarq.frover.ui.fileopsdialog.FileOpsDialogView;
import com.devbaltasarq.frover.core.Entry;
import com.devbaltasarq.frover.core.DirBrowser;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;


/** Allows the copying files.
  * @author baltasarq
  */
public class FileOpsDialog extends Browser {
    /** Create a new copy file dialog.
      * @param owner the frame (MainWindow) for this dialog.
      * @param fileToCopy an Entry.File representing the file to copy.
      * @throws IOException if the file's directory cannot be read.
      */
    public FileOpsDialog(Frame owner, Entry fileToCopy)
            throws IOException
    {
        this( new FileOpsDialogView( owner ), fileToCopy );
    }
    
    /** Create a new copy file dialog.
      * @param view a given copy dialog interface.
      * @param entryToCopy an Entry representing the file to copy.
      * @throws IOException if the file's directory cannot be read.
      */
    public FileOpsDialog(FileOpsDialogView view, Entry entryToCopy)
            throws IOException
    {
        super( view );
        
        this.target = null;
        this.entryToCopy = entryToCopy;
        this.dirBrowser = new DirBrowser( Path.of( entryToCopy.getParentPath() ) );
        
        // Sync and listen
        this.getView().getEdOrgFile().setText( entryToCopy.asCanonical() );
        this.getView().getEdFileName().setText( entryToCopy.getFileName());
        this.buildListeners();
        this.internalSyncToCurrentDir();
    }
    
    private void buildListeners()
    {
        this.getView().getWindow().addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                FileOpsDialog.this.close();
            }
        });
        
        this.getView().getEdFileName().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent evt)
            {
                FileOpsDialog.this.syncEdEndFile();
                
                if ( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
                    FileOpsDialog.this.accept();
                }
                else
                if ( evt.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                    FileOpsDialog.this.close();
                }
            }

            @Override
            public void keyPressed(KeyEvent evt)
            {
            }
            
            @Override
            public void keyTyped(KeyEvent evt)
            {   
            }
        });
        
        this.getView().getBtCancel().addActionListener( (evt) -> this.close() );
        this.getView().getBtOk().addActionListener( (evt) -> this.accept() );
    }
    
    /** When closing the dialog, without doing anything. */
    private void close()
    {
        this.getView().getWindow().setVisible( false );
    }
    
    /** When closing the dialog, and the action will be carried out. */
    private void accept()
    {
        this.target = this.buildTargetPath();
        this.getView().getWindow().setVisible( false );
    }
    
    /** Avoids the call of an overridable method from the ctor. */
    private void internalSyncToCurrentDir() throws IOException
    {
        super.syncToCurrentDir();
        this.syncEdEndFile();
    }
    
    /** @return the file to be copied. */
    public Entry getEntryToCopy()
    {
        return this.entryToCopy;
    }
    
    /** @return the corresponding interface. */
    public final FileOpsDialogView getView()
    {
        return (FileOpsDialogView) super.view;
    }
    
    /** Shows the dialog and blocks.
      * @return the target path, if ok was selected, null otherwise.
      * @see CopyDialog::getTargetPath
      */
    public Path show()
    {
        this.getView().getWindow().setVisible( true );
        return this.getTargetPath();
    }
    
    /** @return the dialog's answer.
      * A path corresponding to the target if "Ok" is selected,
      * null otherwise.
      */
    public Path getTargetPath()
    {
        return this.target;
    }
    
    /** @return the target obtained combining the dir and the file name. */
    private Path buildTargetPath()
    {
        final String FILE_NAME = this.getView().getEdFileName().getText();
        final Path PATH = this.getDirBrowser().getPath();
        
        return Path.of( PATH.toString(), FILE_NAME );
    }
    
    /** Syncs the change of the directory with the Entry
      * in which the complete final path and file name is shown.
      */
    private void syncEdEndFile()
    {
        this.getView().getEdEndFile().setText( this.buildTargetPath().toString() );
    }
    
    @Override
    public void syncToCurrentDir() throws IOException
    {
        this.internalSyncToCurrentDir();
    }
    
    private Path target;
    private final Entry entryToCopy;
}
