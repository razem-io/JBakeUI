package com.jbake.ui;


import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by julianliebl on 28.02.2015.
 */
public class Frontend {

    private JPanel mainPanel;
    private JTextField mSourceFolderTextField;
    private JTextField mDestinationTextField;
    private JButton mBakeButton;
    private JButton mStartServerButton;
    private JTextArea mLogTextArea;
    private JButton mSourceSelectButton;
    private JButton mDestinationSelectButton;
    private JTextField mJBakeFolderTextField;
    private JButton mJBakeSelectButton;

    File mJBakeSourceFolder;
    File mSourceFolder;
    File mDestinationFolder;

    Settings settings;
    
    public Frontend() {
        settings = Settings.getInstance();

        mJBakeSourceFolder = settings.getJBakeFolderPath();
        if(mJBakeSourceFolder == null){
            JOptionPane.showMessageDialog(mainPanel,"Please select a directory where the file 'jbake-core.jar' is located.", "JBake distribution folder not set.", JOptionPane.INFORMATION_MESSAGE);
            selectJBakeFolder();
            if(mJBakeSourceFolder == null){
                System.exit(1);
            }
            //TODO: Do not exit the gui. Show the dialog again instead!
        }else{
            mJBakeFolderTextField.setText(mJBakeSourceFolder.getAbsolutePath());
        }

        mSourceFolder = settings.getSourceFolderPath();
        if(mSourceFolder == null){
            JOptionPane.showMessageDialog(mainPanel,"Please select a directory where the source to bake is located.", "No source folder set.", JOptionPane.INFORMATION_MESSAGE);
            selectSourceFolder();
            if(mSourceFolder == null){
                System.exit(1);
            }
            //TODO: Do not exit the gui. Show the dialog again instead!
        }

        mDestinationFolder = settings.getDestinationFolderPath();
        if(mDestinationFolder == null){
            mDestinationFolder = new File(mSourceFolder.getAbsolutePath() + "/output");
            settings.setDestinationFolderPath(mDestinationFolder);
        }

        mSourceSelectButton.addActionListener(e -> selectSourceFolder());
        mDestinationSelectButton.addActionListener(e -> selectDestinationFolder());
        mJBakeSelectButton.addActionListener(e -> selectJBakeFolder());
        
        mSourceFolderTextField.setText(mSourceFolder.getAbsolutePath());
        mDestinationTextField.setText(mDestinationFolder.getAbsolutePath());
        
        mBakeButton.addActionListener(mBakeButtonListener);
        mStartServerButton.addActionListener(mStartServerButtonListener);
        
        PrintStream con = new PrintStream(new TextAreaOutputStream(mLogTextArea));
        System.setOut(con);
        System.setErr(con);

        DefaultCaret caret = (DefaultCaret)mLogTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        //TODO: This is just a fast shutdown hook for an early release... ;)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if(serverIsRunning){
                    serverProcess.destroy();
                }
            }
        });
    }
    
    private void clearLog(){
        mLogTextArea.setText("");        
    }
    
    private void selectJBakeFolder(){
        FileChooserUtil.showSelectDirectoryDialog(mainPanel, "Select JBake distribution  directory:", selectedFile -> {
            mJBakeSourceFolder = selectedFile;
            mJBakeFolderTextField.setText(mJBakeSourceFolder.getAbsolutePath());
            settings.setJBakeFolderPath(mJBakeSourceFolder);
        });
    }
    
    private void selectSourceFolder(){
        FileChooserUtil.showSelectDirectoryDialog(mainPanel, "Select source directory:", selectedFile -> {
            mSourceFolder = selectedFile;
            mSourceFolderTextField.setText(mSourceFolder.getAbsolutePath());
            settings.setSourceFolderPath(mSourceFolder);
        });
    }

    private void selectDestinationFolder(){
        FileChooserUtil.showSelectDirectoryDialog(mainPanel, "Select destination directory:", selectedFile -> {
            mDestinationFolder = selectedFile;
            mDestinationSelectButton.setText(mDestinationFolder.getAbsolutePath());
            settings.setDestinationFolderPath(mDestinationFolder);
        });
    }
    

    //TODO: post a stack question regarding this topic -> 
    // http://stackoverflow.com/questions/10365402/java-process-invoked-by-processbuilder-sleeps-forever
    private ActionListener mBakeButtonListener = e -> {
        System.out.println("Starting to bake by using the following command:");
        runInBackground(new StatusTask() {
            @Override
            void execute() {
                mBakeButton.setEnabled(false);
                clearLog();
                
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", mJBakeSourceFolder.getAbsolutePath() + "\\jbake-core.jar", "-b", mSourceFolder.getAbsolutePath(), mDestinationFolder.getAbsolutePath());
                pb.command().stream().forEach(commandPart -> System.out.print(commandPart + " "));
                System.out.println();
                    pb.directory(mSourceFolder);

                    try {
                        Process process = pb.start();

                        InputStream stdoutStream = process.getInputStream();
                        InputStream stderrStream = process.getErrorStream();

                        final BufferedReader stdoutReader = new BufferedReader(
                                new InputStreamReader(stdoutStream));
                        final BufferedReader stderrReader = new BufferedReader(
                                new InputStreamReader(stderrStream));                        
                        
                        String stdoutLine;
                        String stderrLine = null;
                        while ((stdoutLine = stdoutReader.readLine()) != null || (stderrLine = stderrReader.readLine()) != null) {
                            if(stdoutLine != null){
                                System.out.println(stdoutLine);
                            }
                            if(stderrLine != null){
                                System.out.println(stderrLine);
                            }
                        }

                        stdoutReader.close();
                        stdoutStream.close();
                        
                        stderrReader.close();
                        stderrStream.close();

                        process.waitFor();
                        process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            void onFinished() {
                mBakeButton.setEnabled(true);
            }
        });
    };
    
    private Process serverProcess = null;
    private boolean serverIsRunning = false;
    private ActionListener mStartServerButtonListener = e -> {
        runInBackground(new StatusTask() {
            @Override
            void execute() {
                clearLog();
                if(serverIsRunning){
                    System.out.println("Stopping the server...");
                    serverProcess.destroy();
                    System.out.println("Done!");
                    mStartServerButton.setText("Start server");
                }else{
                    serverIsRunning = true;
                    mStartServerButton.setText("Stop server");
                    System.out.println("Starting the server by using the following command:");
                    ProcessBuilder pb = new ProcessBuilder("java", "-jar", mJBakeSourceFolder.getAbsolutePath() + "\\jbake-core.jar", "-s", mDestinationFolder.getAbsolutePath());
                    pb.command().stream().forEach(commandPart -> System.out.print(commandPart + " "));
                    System.out.println();
                    pb.directory(mSourceFolder);
    
                    try {
                        serverProcess = pb.start();
    
                        InputStream stdoutStream = serverProcess.getInputStream();
                        InputStream stderrStream = serverProcess.getErrorStream();
    
                        final BufferedReader stdoutReader = new BufferedReader(
                                new InputStreamReader(stdoutStream));
                        final BufferedReader stderrReader = new BufferedReader(
                                new InputStreamReader(stderrStream));
    
                        String stdoutLine;
                        String stderrLine = null;
                        while ((stdoutLine = stdoutReader.readLine()) != null || (stderrLine = stderrReader.readLine()) != null) {
                            if(stdoutLine != null){
                                System.out.println(stdoutLine);
                            }
                            if(stderrLine != null){
                                System.out.println(stderrLine);
                            }
                        }
    
                        stdoutReader.close();
                        stdoutStream.close();
    
                        stderrReader.close();
                        stderrStream.close();

                        serverProcess.waitFor();
                        serverProcess.destroy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            void onFinished() {
                serverIsRunning = false;
            }
        });
    };
    
    private void runInBackground(StatusTask task){
        new Thread(task).start();      
    }
    
    private abstract static class StatusTask implements Runnable{
        @Override
        public void run() {
            execute();
            onFinished();
        }

        abstract void execute();
        abstract void onFinished();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JBake UI");
        frame.setContentPane(new Frontend().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(800, 600));
        frame.setVisible(true);
    }
}
