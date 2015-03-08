package com.jbake.ui.controller;

import com.jbake.ui.FrontendApplication;
import com.jbake.ui.util.JBakeConnector;
import com.jbake.ui.util.TextAreaOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by julianliebl on 07.03.2015.
 */

public class FrontendController implements Initializable {
    private FrontendApplication mApplication;
    
    @FXML private Button    mJBakeButton;
    @FXML private Button    mStartServerButton;
    
    @FXML private TextField mSourceFolderTextField;
    @FXML private TextField mDestinationFolderTextField;
    @FXML private TextField mJBakeFolderTextField;
    
    @FXML private TextArea  mLogTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mApplication = FrontendApplication.getInstance();

        File sourceFolder = mApplication.getSourceFolder();
        File destinationFolderFolder = mApplication.getDestinationFolder();
        File jBakeFolder = mApplication.getJBakeFolder();
        
        mSourceFolderTextField.setText(sourceFolder == null ? "" : sourceFolder.getAbsolutePath());
        mDestinationFolderTextField.setText(sourceFolder == null ? "" : destinationFolderFolder.getAbsolutePath());
        mJBakeFolderTextField.setText(sourceFolder == null ? "" : jBakeFolder.getAbsolutePath());

        PrintStream con = new PrintStream(new TextAreaOutputStream(mLogTextArea));
        System.setOut(con);
        System.setErr(con);
    }
    
    private File showDirectoryChooser(String title, File initialDirectory){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(title);

        if(initialDirectory != null && initialDirectory.exists() && initialDirectory.isDirectory()){
            fileChooser.setInitialDirectory(initialDirectory);
        }
        
        return fileChooser.showDialog(FrontendApplication.getStage());
    }

    @FXML protected void handleSourceFolderTextFieldKeyEvent(KeyEvent event) {
        File file = new File(mSourceFolderTextField.getText());
        FrontendApplication.getInstance().setSourceFolder(file);
    }

    @FXML protected void handleDestinationFolderTextFieldKeyEvent(KeyEvent event) {
        File file = new File(mDestinationFolderTextField.getText());
        FrontendApplication.getInstance().setDestinationFolder(file);
    }

    @FXML protected void handleJBakeFolderTextFieldKeyEvent(KeyEvent event) {
        File file = new File(mJBakeFolderTextField.getText());
        FrontendApplication.getInstance().setJBakeSourceFolder(file);
    }

    @FXML protected void handleSourceFolderButtonAction(ActionEvent event) {
        File file = showDirectoryChooser("Select the source directory:", mApplication.getSourceFolder());
        
        if(file != null){
            mSourceFolderTextField.setText(file.getAbsolutePath());
            FrontendApplication.getInstance().setSourceFolder(file);
        }
    }
    
    @FXML protected void handleDestinationFolderButtonAction(ActionEvent event) {
        File file = showDirectoryChooser("Select the destination directory:", mApplication.getDestinationFolder());

        if(file != null){
            mDestinationFolderTextField.setText(file.getAbsolutePath());
            FrontendApplication.getInstance().setDestinationFolder(file);
        }
    }
    @FXML protected void handleJBakeFolderButtonAction(ActionEvent event) {
        File file = showDirectoryChooser("Select the JBake distribution directory:", mApplication.getJBakeFolder());

        if(file != null){
            mJBakeFolderTextField.setText(file.getAbsolutePath());
            FrontendApplication.getInstance().setJBakeSourceFolder(file);
        }
    }

    @FXML protected void handleSettingsButtonAction(ActionEvent event) {
        FrontendApplication.openSettings((Node)event.getSource());
    }

    @FXML protected void handleBakeButtonAction(ActionEvent event) {
        mLogTextArea.clear();
        JBakeConnector.bake(status -> {
           if(status.equals(JBakeConnector.CommandStatus.STARTING)){
               Platform.runLater(() -> mJBakeButton.setDisable(true));                       
           }else if(status.equals(JBakeConnector.CommandStatus.FINISHED)){
               Platform.runLater(() -> mJBakeButton.setDisable(false));
           }
        });
    }

    @FXML protected void handleStartServerButtonAction(ActionEvent event) {
        mLogTextArea.clear();
        JBakeConnector.toggleServer(status -> {
            if (status.equals(JBakeConnector.CommandStatus.STARTING)) {
                Platform.runLater(() -> mStartServerButton.setText("Stop server"));
            } else if (status.equals(JBakeConnector.CommandStatus.FINISHED)) {
                Platform.runLater(() -> mStartServerButton.setText("Start server"));
            }
        });
    }

    
}