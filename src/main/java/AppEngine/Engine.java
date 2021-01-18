package AppEngine;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.html.parser.Parser;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Engine {
    public  String targetURL;
    public  String fileOutput;
    private static String archivesLink = "https://archive.mozilla.org/pub/firefox/";
    private static String archivesLinkDevEd = "https://archive.mozilla.org/pub/devedition/";
    private String latestNightlyPath = "https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central/";
    private String latestNightlyLocalePath ="https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central-l10n/";
    private HashMap<String, String> buildPath = new HashMap<String, String>();
    private String fileOutputType;
    public HashMap<String, Boolean> checkboxes = new HashMap<>();

    /*
           We are using Java NIO package to handle networking input-output.
     */

    //We need to execute this part in a different Thread
    public void initiateDownload(String targetURL, String fileOutput, String fileOutputType, String filename, String directory) throws IOException {
        System.out.println(osCheck());
        // We are creating a stream to read content from the URL.
        ReadableByteChannel readChannel = Channels.newChannel(new URL(targetURL).openStream());

        //We are transferring the downloaded content to a file on the local system based on the file type extension.
        FileOutputStream fileOS = new FileOutputStream(directory + osCheck() + filename + fileOutputType);

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
            fxPath = directory + osCheck() + fileName +fileOutputType;
            System.out.println(fxPath);
        }
        System.out.println(fxPath);

        //Need to test this in windows before removing! But since we are using desktop we could remove run.exec from msi
        if(fileOutputType.contains(".msi")){
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("msiexec /i " +fxPath);
        }else{
            try{
                Desktop desktop = null;
                if(Desktop.isDesktopSupported()){
                    desktop = Desktop.getDesktop();
                }
                desktop.open(new File(fxPath));
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
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
            if(locale.contains("en-US")){
                buildPath.put("latestNightlyPath", latestNightlyPath +"firefox-" + parseHtml(latestNightlyPath) + "." + locale + "." + osSelection +  msiExeInstallerPathBuilder(builds.get("latestNightly"),fileType));
            }else{
                buildPath.put("latestNightlyPath",latestNightlyLocalePath + "firefox-" + parseHtml(latestNightlyPath) + "." + locale + "." + osSelection + msiExeInstallerPathBuilder(builds.get("latestNightly"),fileType));
            }
            finalPath = buildPath.get("latestNightlyPath");
            initiateDownload(finalPath,fileOutput,fileOutputType,fileName,directory);


        }

    }

    //Need to rethink this, make it much simple based on received params and evaluate them accordingly
    private String msiExeInstallerPathBuilder(String builds,String fileType){
        if(fileType.contains("Firefox Setup exe")){
            if(builds.contains("Nightly")){
                return fileOutputType = ".installer.exe";
            }else{
                fileOutputType = ".exe";
                return "Firefox%20Setup%20" + builds + ".exe";
            }

        }else if(fileType.contains("Firefox Setup msi")){
            if(builds.contains("Nightly")){
                return fileOutputType = ".installer.msi";
            }else{
                fileOutputType = ".msi";
                return "Firefox%20Setup%20" + builds + ".msi";
            }

        }else if(fileType.contains("Firefox Installer.exe")){
            if(builds.contains("Nightly")){
                return fileOutputType = ".installer.exe";
            }else{
                fileOutputType = ".exe";
                return "Firefox%20Installer.exe";
            }

        }else{
            if(builds.contains("Nightly")) {
                if(fileType.contains("dmg")) {
                    return fileOutputType = ".dmg";
                }else if(fileType.contains("pkg")) {
                    return fileOutputType = ".pkg";
                }else{
                    return fileOutputType = ".zip";
                }
            }else {
                if (fileType.contains("dmg")) {
                    fileOutputType = ".dmg";
                    return "Firefox%20" + builds + fileOutputType;
                } else if (fileType.contains("pkg")) {
                    fileOutputType = ".pkg";
                    return "Firefox%20" + builds + fileOutputType;
                } else {
                    fileOutputType = ".zip";
                    return "firefox-" + builds + ".zip";
                }
            }
            }
        }

    public String parseHtml(String build) throws IOException {
        Document doc = Jsoup.connect(build).get();
        Elements content = doc.select("td").eq(617);

        String nightlyCurrentVersion = content.text().replaceFirst("firefox-","");
        return nightlyCurrentVersion = nightlyCurrentVersion.replaceFirst(".en-US.win64.zip","");

    }

    public String parseHtmlBuildVersion(String build) throws IOException {
        Document buildNumber = Jsoup.connect(build).get();
        Elements con = buildNumber.select("a");

        String builds = con.text();
        String item[] = builds.replace("..", "").split("/ ");

        System.out.println(item[1]);


        System.out.println(builds);
        return con.text();
    }



    public String osCheck(){
        String OS = System.getProperty("os.name");
        if(OS.contains("Mac OS X")){
            return "/";
        }
        return "\\";
    }
}
