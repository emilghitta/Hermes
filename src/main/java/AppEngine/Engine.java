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



    /*
           We are using Java NIO package to handle networking input-output.
     */

    //We need to execute this part in a different Thread
    public void initiateDownload(String targetURL, String fileOutput, String fileOutputType) throws IOException {

        // We are creating a stream to read content from the URL.
        ReadableByteChannel readChannel = Channels.newChannel(new URL(targetURL).openStream());

        //We are transferring the downloaded content to a file on the local system based on the file type extension.
        FileOutputStream fileOS = new FileOutputStream("D://te//Firefox" + fileOutputType);

        //We are going to copy the contents read from the readChannel object to the file destination using writeChannel object.
        FileChannel writeChannel = fileOS.getChannel();
        writeChannel.transferFrom(readChannel,0,Long.MAX_VALUE);


    }

    public void buildCheck(String build) throws IOException {
        if(build.contains("beta")){
            fileOutput = "https://archive.mozilla.org/pub/firefox/candidates/85.0b4-candidates/build1/win64/en-US/firefox-85.0b4.zip";
            pathCheck(targetURL);
        }else if(build.contains("rc")){
            fileOutput = "https://archive.mozilla.org/pub/firefox/candidates/84.0.1-candidates/build1/win64/en-US/firefox-84.0.1.zip";
            pathCheck(targetURL);
        }else if(build.contains("esr")){
            fileOutput = "https://archive.mozilla.org/pub/firefox/candidates/78.6.0esr-candidates/build1/win64/en-US/firefox-78.6.0esr.zip";
            pathCheck(targetURL);
        }else if(build.contains("nightly")){ //Always latest nightly or can we improve this to cover other, manually given, nightly builds.
            fileOutput = "https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central/firefox-86.0a1.en-US.win64.zip";
            pathCheck(targetURL);
        }else if(build.contains("deved")){
            fileOutput = "https://archive.mozilla.org/pub/devedition/candidates/85.0b4-candidates/build1/win64/en-US/firefox-85.0b4.zip";
            pathCheck(targetURL);
        }else{
            System.out.println("Invalid given build. Cannot continue :(");
        }

    }

    public void pathCheck(String targetURL) throws IOException {
        initiateDownload(targetURL,fileOutput,fileOutputType);
    }

    /*
    Add a check to verify and run this only if the file is an archive. Otherwise just open it.
    Maybe integrate this and launchFirefox() in a UI check to verify if the user really want this to happen.
     */
    public void unzip() throws IOException {
        String source = "D://te//firefox.zip";
        String destination = "D://te//";

        try{
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);

        }catch (ZipException e){
            e.printStackTrace();
        }
    }

    //Add a UI check to verify if the user really wants this to happen.
    public void launchFirefox() throws IOException {
        //Need to extract this from the file picker input
        String fxPath = "D://te//firefox//firefox.exe";

        Runtime run = Runtime.getRuntime();
        Process proc = run.exec(fxPath);

    }
    /*
        Create the final resource path based on received arguments.
     */

    public void pathFoundation(HashMap<String,String> builds, String osSelection,String locale, String fileType) throws IOException {
        String finalPath;

        if(builds.get("betaVersion") != null){
           buildPath.put("betaPath", archivesLink + "candidates/" + builds.get("betaVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("betaVersion"),fileType));
            finalPath = buildPath.get("betaPath");
            //Build 1 ? 2 ?

            initiateDownload(finalPath,fileOutput,fileOutputType);
        }else if(builds.get("releaseVersion") != null){
            buildPath.put("releasePath", archivesLink + "candidates/" + builds.get("releaseVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("releaseVersion"),fileType));
            finalPath = buildPath.get("releasePath");
            //Build 1 ? 2 ?


            initiateDownload(finalPath,fileOutput,fileOutputType);
        }else if(builds.get("esrVersion") != null){
            buildPath.put("esrPath", archivesLink + "candidates/" + builds.get("esrVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("esrVersion"),fileType));
            finalPath = buildPath.get("esrPath");


            initiateDownload(finalPath,fileOutput,fileOutputType);
        }else if(builds.get("devedVersion") != null){
            buildPath.put("devedPath", archivesLinkDevEd + "candidates/" + builds.get("devedVersion") + "-candidates/build1/" + osSelection +"/" + locale + "/" + msiExeInstallerPathBuilder(builds.get("devedVersion"),fileType));
            finalPath = buildPath.get("devedPath");
            //Build 1 ? 2 ?

            initiateDownload(finalPath,fileOutput,fileOutputType);
        }else{
            //Rethink this! Hardcoded for now
            buildPath.put("latestNightlyPath",archivesLink + builds.get("latestNightly") +"firefox-86.0a1.en-US.win64.zip");
            finalPath = buildPath.get("latestNightlyPath");
            initiateDownload(finalPath,fileOutput,fileOutputType);

            //initiate download in a separate thread? We need to populate the downloaded file with bits
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



    public void quit(){
        System.exit(0);
    }
}
