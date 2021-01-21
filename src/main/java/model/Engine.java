package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

public class Engine extends Utils {
    public String osSelection;
    public String typeOfFile;
    public String buildNumber;


    private final HashMap<String, String> buildPath = new HashMap<String, String>();


    /*
        We are using Java NIO package to handle networking input-output. This methods takes the necessary parameters and initiates the resource download.
     */
    public void initiateDownload(String targetURL, String fileOutput, String fileOutputType, String fileName, String directory) throws IOException {

        //We are creating a stream to read content from the given URL.
        ReadableByteChannel readChannel = Channels.newChannel(new URL(targetURL).openStream());

        //We are transferring the downloaded content to a file on the local system based on the file type extension.
        FileOutputStream fileOS = new FileOutputStream(directory + osCheck() + fileName + fileOutputType);

        //We are going to copy the contents read from the readChannel object to the file destination using writeChannel object.
        FileChannel writeChannel = fileOS.getChannel();
        writeChannel.transferFrom(readChannel,0,Long.MAX_VALUE);

        //We need to close the FileChannel
        writeChannel.close();

        if(checkBoxes.get("unarchiveBuilds") != null && checkBoxes.get("unarchiveBuilds")){
            unzip(fileName,directory,fileOutputType);
        }

        if(checkBoxes.get("autoOpenBuilds") && checkBoxes.get("unarchiveBuilds")){
            unzip(fileName,directory,fileOutputType);
            fileOutputType = ".exe";  //Need to rethink this for Linux compatibility
            launchFirefox(fileOutputType,directory,fileName);
        }else if(checkBoxes.get("autoOpenBuilds") && !checkBoxes.get("unarchiveBuilds")){
            launchFirefox(fileOutputType,directory,fileName);
        }

    }

    public void pathFoundation(HashMap<String,String> builds,String buildNumber, String osSelection,String locale, String fileType, String fileName, String directory) throws IOException {
        String finalPath;

        String archivesLink = "https://archive.mozilla.org/pub/firefox/";
        if(builds.get("betaVersion") != null){
            System.out.println("Beta Not Null");
            buildPath.put("betaPath", archivesLink + "candidates/" + builds.get("betaVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("betaVersion"),fileType,osSelection));
            finalPath = buildPath.get("betaPath");
            System.out.println(finalPath);

            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else if(builds.get("releaseVersion") != null){
            System.out.println("Release Not Null");
            buildPath.put("releasePath", archivesLink + "candidates/" + builds.get("releaseVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("releaseVersion"),fileType,osSelection));
            finalPath = buildPath.get("releasePath");

            System.out.println(finalPath);

            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else if(builds.get("esrVersion") != null){
            System.out.println("ESR Not Null");
            buildPath.put("esrPath", archivesLink + "candidates/" + builds.get("esrVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("esrVersion"),fileType,osSelection));
            finalPath = buildPath.get("esrPath");

            System.out.println(finalPath);

            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else if(builds.get("devedVersion") != null){
            System.out.println("Deved Not Null");
            String archivesLinkDevEd = "https://archive.mozilla.org/pub/devedition/";
            buildPath.put("devedPath", archivesLinkDevEd + "candidates/" + builds.get("devedVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("devedVersion"),fileType,osSelection));
            finalPath = buildPath.get("devedPath");

            System.out.println(finalPath);

            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);
        }else{
            String latestNightlyPath = "https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central/";
            if(locale.contains("en-US")){
                buildPath.put("latestNightlyPath", latestNightlyPath +"firefox-" + parseHtml(latestNightlyPath) + "." + locale + "." + osSelection +  installerPathBuilder(builds.get("latestNightly"),fileType,osSelection));
            }else{
                String latestNightlyLocalePath = "https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central-l10n/";
                buildPath.put("latestNightlyPath", latestNightlyLocalePath + "firefox-" + parseHtml(latestNightlyPath) + "." + locale + "." + osSelection + installerPathBuilder(builds.get("latestNightly"),fileType,osSelection));
            }
            finalPath = buildPath.get("latestNightlyPath");

            System.out.println(finalPath);
            initiateDownload(finalPath,fileOutput, fileOutputType ,fileName,directory);
        }

    }


}
