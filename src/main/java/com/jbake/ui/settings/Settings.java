package com.jbake.ui.settings;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;

/**
 * Created by julianliebl on 01.03.2015.
 */
public class Settings {
    //FOLDERS
    private final String DB_JBAKE_FOLDER_KEY = "DB_JBAKE_FOLDER_KEY";
    private final String DB_SOURCE_FOLDER_KEY = "DB_SOURCE_FOLDER_KEY";
    private final String DB_DESTINATION_FOLDER_KEY = "DB_DESTINATION_FOLDER_KEY";
    
    //LOCALE
    private final String DB_LOCALE_COUNTRY = "DB_LOCALE_COUNTRY";
    private final String DB_LOCALE_LANGUAGE = "DB_LOCALE_LANGUAGE";
    
    private static Settings savedInstance;
    
    private DB db;
    private HTreeMap<String, String> settings;
    
    private Settings(){
        File data = new File("data");
        if(!data.exists() && !data.mkdirs()){
            System.out.println("Wasn't able to create settings folder. Settings will not be saved on exit.");
            db = DBMaker.newMemoryDB().closeOnJvmShutdown().make();
        }else{
            db = DBMaker.newFileDB(new File("data/settings")).closeOnJvmShutdown().make();
        }
        
        settings = db.createHashMap("settings")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                .makeOrGet();
    }
    
    public static Settings getInstance(){
        if(savedInstance == null){
            savedInstance = new Settings();
        }
        return savedInstance;
    }
    
    public static void close(){
        getInstance().db.commit();
        getInstance().db.close();
    }
    
    public void setJBakeFolder(File jBakeFolder){
        settings.put(DB_JBAKE_FOLDER_KEY, jBakeFolder.getAbsolutePath());
    }

    public File getJBakeFolderPath(){
        String jBakeFolderString = settings.get(DB_JBAKE_FOLDER_KEY);
        return jBakeFolderString == null ? null : new File(jBakeFolderString);
    }

    public void setSourceFolder(File sourceFolder){
        settings.put(DB_SOURCE_FOLDER_KEY, sourceFolder.getAbsolutePath());
    }

    public File getSourceFolderPath(){
        String sourceFolderString = settings.get(DB_SOURCE_FOLDER_KEY);
        return sourceFolderString == null ? null : new File(sourceFolderString);
    }

    public void setDestinationFolder(File destinationFolder){
        settings.put(DB_DESTINATION_FOLDER_KEY, destinationFolder.getAbsolutePath());
    }

    public File getDestinationFolderPath(){
        String destinationFolderString = settings.get(DB_DESTINATION_FOLDER_KEY);
        return destinationFolderString == null ? null : new File(destinationFolderString);
    }
    
    public String getLocaleCountry(){
        String countryString = settings.get(DB_LOCALE_COUNTRY);
        return countryString == null ? System.getProperty("user.country") : countryString;
    }

    public void setLocaleCountry(String countryString){
        settings.put(DB_LOCALE_COUNTRY, countryString);
    }

    public String getLocaleLanguage(){
        String languageString = settings.get(DB_LOCALE_LANGUAGE);
        return languageString == null ? System.getProperty("user.language") : languageString;
    }

    public void setLocaleLanguage(String languageString){
        settings.put(DB_LOCALE_LANGUAGE, languageString);
    }
}
