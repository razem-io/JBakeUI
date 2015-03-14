package com.jbake.ui.util;

import com.jbake.ui.settings.Settings;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julianliebl on 07.03.2015.
 */
public class JBakeConnector {
    private static JBakeConnector mStaticInstance;
    private static Process mServerProcess;

    
    private JBakeConnector(){}
    
    private static JBakeConnector getInstance(){
        if(mStaticInstance == null){
            mStaticInstance = new JBakeConnector();
        }
        return mStaticInstance;
    }    
    
    private void runInBackground(StatusTask task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private abstract static class StatusTask implements Runnable {
        @Override
        public void run() {
            execute();
            onFinished();
        }

        abstract void execute();

        abstract void onFinished();
    }
    
    public enum CommandStatus{
        STARTING, FINISHED        
    }
    
    public interface CommandStatusListener{
        void onStatusChange(CommandStatus status);
    }
    
    public static void stopServer(){
        getInstance().stopServerLocal();
    }
    
    private void stopServerLocal(){
        if (mServerProcess != null) {
            System.out.println("Stopping the server...");
            mServerProcess.destroyForcibly();
            mServerProcess = null;
            System.out.println("Done!");
        }
    }
    
    public static void toggleServer(CommandStatusListener listener){
        getInstance().toggleServerLocal(listener);        
    }
    
    private void toggleServerLocal(CommandStatusListener listener){
        runInBackground(new StatusTask() {
            @Override
            void execute() {
                if (mServerProcess != null) {
                    stopServerLocal();
                } else {
                    listener.onStatusChange(CommandStatus.STARTING);
                    
                    File mDestinationFolder = Settings.getInstance().getDestinationFolderPath();
                    File mJBakeSourceFolder = Settings.getInstance().getJBakeFolderPath();
                    
                    System.out.println("Starting the server by using the following command:");
                    ProcessBuilder pb = new ProcessBuilder("java", "-jar", "jbake-core.jar", "-s", mDestinationFolder.getAbsolutePath());
                    pb.command().stream().forEach(commandPart -> System.out.print(commandPart + " "));
                    System.out.println();
                    pb.directory(mJBakeSourceFolder);

                    try {
                        mServerProcess = pb.start();

                        InputStream stdoutStream = mServerProcess.getInputStream();
                        InputStream stderrStream = mServerProcess.getErrorStream();

                        final BufferedReader stdoutReader = new BufferedReader(
                                new InputStreamReader(stdoutStream));
                        final BufferedReader stderrReader = new BufferedReader(
                                new InputStreamReader(stderrStream));

                        String stdoutLine;
                        String stderrLine = null;
                        while ((stdoutLine = stdoutReader.readLine()) != null || (stderrLine = stderrReader.readLine()) != null) {
                            if (stdoutLine != null) {
                                System.out.println(stdoutLine);
                            }
                            if (stderrLine != null) {
                                System.out.println(stderrLine);
                            }
                        }

                        stdoutReader.close();
                        stdoutStream.close();

                        stderrReader.close();
                        stderrStream.close();

                        if(mServerProcess != null){
                            mServerProcess.waitFor();
                            mServerProcess.destroy();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            void onFinished() {
                listener.onStatusChange(CommandStatus.FINISHED);
            }
        });        
    }
    
    public static void bake(CommandStatusListener listener){
        getInstance().bakeLocal(listener);
    }
    
    //TODO: remove dependency on Settings with chained commands
    private void bakeLocal(CommandStatusListener listener){
        runInBackground(new StatusTask() {
            @Override
            void execute() {
                listener.onStatusChange(CommandStatus.STARTING);
                
                File mSourceFolder      = Settings.getInstance().getSourceFolderPath();
                File mDestinationFolder = Settings.getInstance().getDestinationFolderPath();
                File mJBakeSourceFolder = Settings.getInstance().getJBakeFolderPath();
                
                if(mSourceFolder == null){
                    System.out.println("ERROR: Please select a valid source directory!");
                    return;
                }

                if(mDestinationFolder == null){
                    System.out.println("ERROR: Please select a valid destination directory!");
                    return;
                }

                if(mJBakeSourceFolder == null){
                    System.out.println("ERROR: Please select a valid JBake directory!");
                    return;
                }

                System.out.println("Starting to bake by using the following command:");
                
                List<String> args = new ArrayList<>();
                //default
                args.add("java");
                args.add("-jar");

                //locale
                String localeCountry = Settings.getInstance().getLocaleCountry();
                if (localeCountry != null) args.add("-Duser.country=" + localeCountry);
                String localeLanguage = Settings.getInstance().getLocaleLanguage();
                if (localeLanguage != null) args.add("-Duser.language=" + localeLanguage);

                //JBake
                args.add("jbake-core.jar");

                //folders
                if (mSourceFolder.list() == null || mSourceFolder.list().length == 0) {
                    //TODO: Implement template selection

                    ZipUtil.unpack(
                            new File(mJBakeSourceFolder.getAbsolutePath() + "/example_project_freemarker.zip"),
                            mSourceFolder
                    );
                }

                args.add("-b");
                args.add(mSourceFolder.getAbsolutePath());
                args.add(mDestinationFolder.getAbsolutePath());

                ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[args.size()]));
                pb.directory(mJBakeSourceFolder);
                pb.command().stream().forEach(commandPart -> System.out.print(commandPart + " "));

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
                        if (stdoutLine != null) {
                            System.out.println(stdoutLine);
                        }
                        if (stderrLine != null) {
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
                listener.onStatusChange(CommandStatus.FINISHED);
            }
        });
        
        
        
    }
}
