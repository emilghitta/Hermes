package AppEngine;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

public class Engine {
    public  String targetURL;
    public  String fileOutput;
    private static String archivesLink = "https://archive.mozilla.org/pub/firefox/";
    private static String archivesLinkDevEd = "https://archive.mozilla.org/pub/devedition/";
    private HashMap<String, String> buildPath = new HashMap<String, String>();
    private String fileOutputType;
    public HashMap<String, Boolean> checkboxes = new HashMap<>();

    /*
           We are using Java NIO package to handle networking input-output.
     */

    //We need to execute this part in a different Thread
    public void initiateDownload(String targetURL, String fileOutput, String fileOutputType, String filename, String directory) throws IOException {

        // We are creating a stream to read content from the URL.
        ReadableByteChannel readChannel = Channels.newChannel(new URL(targetURL).openStream());

        //We are transferring the downloaded content to a file on the local system based on the file type extension.
        FileOutputStream fileOS = new FileOutputStream(directory + "\\" + filename + fileOutputType);

        //We are going to copy the contents read from the readChannel object to the file destination using writeChannel object.
        FileChannel writeChannel = fileOS.getChannel();
        writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

        //We need to close the FileChannel
        writeChannel.close();

        if(checkboxes.get("unarchiveBuilds")){
            unzip(filename,directory,fileOutputType);
        }

        if(checkboxes.get("autoOpenBuilds") && checkboxes.get("unarchiveBuilds")){
            unzip(filename,directory,fileOutputType);
            fileOutputType = ".exe";
            launchFirefox(fileOutputType,directory,filename);
        }else if(checkboxes.get("autoOpenBuilds") && !checkboxes.get("unarchiveBuilds")){
            launchFirefox(fileOutputType,directory,filename);
        }

    }

    /*
    Add a check to verify and run this only if the file is an archive. Otherwise just open it.
    Maybe integrate this and launchFirefox() in a UI check to verify if the user really want this to happen.
     */
    public void unzip(String fileName, String directory, String fileOutputType) throws IOException {

        String source = directory + "\\" + fileName + fileOutputType;
        String destination = directory;

        try{
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);

        }catch (ZipException e){
            e.printStackTrace();
        }
    }

    public void launchFirefox( String fileOutputType, String directory, String fileName) throws IOException {
        String fxPath = "";
        if(checkboxes.get("unarchiveBuilds")){
            fxPath = directory+ "\\firefox\\firefox" + fileOutputType;
        }else if(!checkboxes.get("unarchiveBuilds")){
            fxPath = directory + "\\"+ fileName +fileOutputType;
            System.out.println(fxPath);
        }
        System.out.println(fxPath);

        if(fileOutputType.contains(".msi")){
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("msiexec /i " +fxPath);
        }else{
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec(fxPath);

        }

    }
    /*
        Create the final resource path based on received arguments.
     */

    public void pathFoundation(HashMap<String,String> builds, String osSelection,String locale, String fileType, String fileName, String directory) throws IOException {
        String finalPath;

        if(builds.get("betaVersion") != null){
           buildPath.put("betaPath", archivesLink + "candidates/" + builds.get("betaVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("betaVersion"),fileType));
            finalPath = buildPath.get("betaPath");
            //Build 1 ? 2 ?

            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else if(builds.get("releaseVersion") != null){
            buildPath.put("releasePath", archivesLink + "candidates/" + builds.get("releaseVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("releaseVersion"),fileType));
            finalPath = buildPath.get("releasePath");
            //Build 1 ? 2 ?


            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else if(builds.get("esrVersion") != null){
            buildPath.put("esrPath", archivesLink + "candidates/" + builds.get("esrVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("esrVersion"),fileType));
            finalPath = buildPath.get("esrPath");


            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else if(builds.get("devedVersion") != null){
            buildPath.put("devedPath", archivesLinkDevEd + "candidates/" + builds.get("devedVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("devedVersion"),fileType));
            finalPath = buildPath.get("devedPath");
            //Build 1 ? 2 ?

            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else{
            //Rethink this! Hardcoded for now
            buildPath.put("latestNightlyPath",archivesLink + builds.get("latestNightly") +"firefox-86.0a1.en-US.win64.zip");
            finalPath = buildPath.get("latestNightlyPath");
            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);

        }

    }

    private String msiExeInstallerPathBuilder(String builds,String fileType){
        if(fileType.contains("Firefox Setup exe")){
            fileOutputType = ".exe";
            return "Firefox%20Setup%20" + builds + ".exe";
        }else if(fileType.contains("Firefox Setup msi")){
            fileOutputType = ".msi";
            return "Firefox%20Setup%20" + builds + ".msi";
        }else if(fileType.contains("Firefox Installer.exe")){
            fileOutputType = ".exe";
            return "Firefox%20Installer.exe";
        }else{
            fileOutputType = ".zip";
            return "firefox-" + builds + ".zip";
        }
    }
}
