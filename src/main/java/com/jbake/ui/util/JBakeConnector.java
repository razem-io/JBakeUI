package com.jbake.ui.util;

import com.jbake.ui.settings.Settings;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.jbake.app.ConfigUtil;
import org.jbake.app.Oven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
/**
 * Created by julianliebl on 07.03.2015.
 */
public class JBakeConnector {
    private final static Logger LOGGER = LoggerFactory.getLogger(JBakeConnector.class);
    
    private static JBakeConnector mStaticInstance;
    private static Server mServer;

    
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
        if (mServer != null) {            
            System.out.println("Stopping the server...");
            try {
                mServer.stop();
                mServer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                if (mServer != null) {
                    stopServerLocal();
                } else {
                    listener.onStatusChange(CommandStatus.STARTING);
                    
                    File mDestinationFolder = Settings.getInstance().getDestinationFolderPath();

                    final CompositeConfiguration config = getConfig();
                    if(config == null) {
                        return;
                    }

                    String port = config.getString(ConfigUtil.Keys.SERVER_PORT);
                    String path = mDestinationFolder.getAbsolutePath();

                    mServer = new Server();
                    SelectChannelConnector connector = new SelectChannelConnector();
                    connector.setPort(Integer.parseInt(port));
                    mServer.addConnector(connector);

                    ResourceHandler resource_handler = new ResourceHandler();
                    resource_handler.setDirectoriesListed(true);
                    resource_handler.setWelcomeFiles(new String[]{ "index.html" });

                    resource_handler.setResourceBase(path);

                    HandlerList handlers = new HandlerList();
                    handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
                    mServer.setHandler(handlers);

                    LOGGER.info("Serving out contents of: [{}] on http://localhost:{}/", path, port);
                    LOGGER.info("(To stop server hit CTRL-C)");

                    try {
                        mServer.start();
                        mServer.join();
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
    private void bakeLocal(CommandStatusListener listener) {
        runInBackground(new StatusTask() {
            @Override
            void execute() {
                listener.onStatusChange(CommandStatus.STARTING);

                File mSourceFolder = Settings.getInstance().getSourceFolderPath();
                File mDestinationFolder = Settings.getInstance().getDestinationFolderPath();
                File mJBakeSourceFolder = Settings.getInstance().getJBakeFolderPath();

                if (mSourceFolder == null) {
                    System.out.println("ERROR: Please select a valid source directory!");
                    return;
                }

                if (mDestinationFolder == null) {
                    System.out.println("ERROR: Please select a valid destination directory!");
                    return;
                }

                if (mJBakeSourceFolder == null) {
                    System.out.println("ERROR: Please select a valid JBake directory!");
                    return;
                }

                //folders
                if (mSourceFolder.list() == null || mSourceFolder.list().length == 0) {
                    //TODO: Implement template selection

                    ZipUtil.unpack(
                            new File(mJBakeSourceFolder.getAbsolutePath() + "/example_project_freemarker.zip"),
                            mSourceFolder
                    );
                }

                System.out.println("Starting to bake by using the following command:");

                final CompositeConfiguration config = getConfig();                
                if(config == null) {
                    return;
                }
                

                System.out.println("JBake " + config.getString(ConfigUtil.Keys.VERSION) + " (" + config.getString(ConfigUtil.Keys.BUILD_TIMESTAMP) + ") [http://jbake.org]");
                System.out.println();

                Oven oven = new Oven(mSourceFolder, mDestinationFolder, config, true);
                oven.setupPaths();
                oven.bake();
            }

            @Override
            void onFinished() {
                listener.onStatusChange(CommandStatus.FINISHED);
            }
        });
    }
        
    private CompositeConfiguration getConfig(){
        CompositeConfiguration config = null;
        
        try {
            config = ConfigUtil.load(Settings.getInstance().getSourceFolderPath());
        } catch (final ConfigurationException e) {
            System.out.println("Configuration error: " + e.getMessage());
        }

        return config;
    }
}
