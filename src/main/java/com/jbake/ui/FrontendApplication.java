package com.jbake.ui;

import com.jbake.ui.settings.Settings;
import com.jbake.ui.util.JBakeConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

/**
 * Created by julianliebl on 07.03.2015.
 */
public class FrontendApplication extends Application {
    private static FrontendApplication mStaticInstance;
    private Stage mStage;

    private File mJBakeSourceFolder;
    private File mSourceFolder;
    private File mDestinationFolder;

    private Settings mSettings;
    
    public static void main(String[] args) {
        Application.launch(FrontendApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        mStaticInstance = this;

        mSettings = Settings.getInstance();
        
        mJBakeSourceFolder  = mSettings.getJBakeFolderPath();
        mSourceFolder       = mSettings.getSourceFolderPath();
        mDestinationFolder  = mSettings.getDestinationFolderPath();


        // get the ui started
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Frontend.fxml"));
        
        mStage = stage;
        setUserAgentStylesheet(STYLESHEET_MODENA);
        stage.setTitle("JBake UI");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/jbake-logo.png")));
        stage.setScene(new Scene(root, 800, 600));
        
        stage.setOnCloseRequest(event -> {
            JBakeConnector.stopServer();
            Settings.close();
        });
        
        stage.show();
    }
    
    public FrontendApplication(){
          
    }
    
    public static FrontendApplication getInstance(){
        return mStaticInstance;        
    }
    
    public static Stage getStage(){
        return getInstance().mStage;
    }
    
    public File getSourceFolder(){
        return mSourceFolder;
    }
    
    public static void openSettings(Node node){
        try {
            Parent settingRoot = FXMLLoader.load(FrontendApplication.class.getResource("/fxml/Settings.fxml"));
            final Stage dialog = new Stage(StageStyle.TRANSPARENT);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(node.getScene().getWindow());
            dialog.setScene(new Scene(settingRoot, 400, 300));
            dialog.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setSourceFolder(File folder){
        mSourceFolder = folder;
        mSettings.setSourceFolder(mSourceFolder);
    }

    public File getDestinationFolder() {
        return mDestinationFolder;
    }

    public void setDestinationFolder(File destinationFolder) {
        mDestinationFolder = destinationFolder;
        mSettings.setDestinationFolder(mDestinationFolder);
    }

    public File getJBakeFolder() {
        return mJBakeSourceFolder;
    }

    public void setJBakeSourceFolder(File JBakeSourceFolder) {
        mJBakeSourceFolder = JBakeSourceFolder;
        mSettings.setJBakeFolder(mJBakeSourceFolder);
    }
    
    
}
