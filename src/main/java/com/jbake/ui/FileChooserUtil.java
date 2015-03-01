package com.jbake.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by julianliebl on 01.03.2015.
 */
public class FileChooserUtil {
    public interface DirectorySelectionListener{
        void onSelected(File directory);
    }

    public static void showSelectDirectoryDialog(Component parent, String title, DirectorySelectionListener listener){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(title);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            listener.onSelected(chooser.getSelectedFile());
        }
    }
}
