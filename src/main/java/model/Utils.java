package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Utils {
    protected String fileOutputType;
    protected String fileOutput;
    public String fileNN, dirNN;
    public HashMap<String, Boolean> checkBoxes = new HashMap<>();
    public ObservableList<String> buildNumberList = FXCollections.observableArrayList();
    String OS = System.getProperty("os.name");

    /*
    We are making use of different unzipping clases based on the os check.
    1. We are using Archiver class to handle the untaring by passing the archiveFormat: tar & the compression type as bz2 if the os == Linux
    2. We are using ZipFile (lingala) class to handle the zip extraction if the os == Windows
     */
    protected void unzip(String fileName, String directory, String fileOutputType) throws IOException {
        String source = directory + osCheck() + fileName + fileOutputType;
        String os = System.getProperty("os.name");

        if(!os.contains("Windows")){
            File archive = new File(source);
            File dest = new File(directory);

            Archiver archiver = ArchiverFactory.createArchiver("tar","bzip2");
            archiver.extract(archive,dest);
        }else{
            try{
                ZipFile zipFile = new ZipFile(source);
                zipFile.extractAll(directory);
            }catch(ZipException e){
                e.printStackTrace();
            }
        }
    }

    /*
        The launchFirefox() is providing us the ability to execute the downloaded & maybe extracted.
        1. We are setting the fxPath based on the unarchive checkbox selection because after the extraction the path changes.
        2. If the fileOutputType is .msi we need to run it like msiexec because an MSI file is not standalone. If not, it will generate a
        CreateProcess error=193, %1 is not a valid Win32 application error.
        3. We are using the Desktop from AWT library. The Desktop class allows a Java application to launch associated applications registered on the native desktop to handle a URI or a file.
        This helps us greatly for pkg or dmg files.
     */

    protected void launchFirefox(String fileOutputType, String directory, String fileName) throws IOException {
        String fxPath = "";
        if(checkBoxes.get("unarchiveBuilds")) {
            if(!OS.contains("Linux")){
                fxPath = directory + "\\firefox\\firefox" + fileOutputType;
            }else{
                fxPath = directory + "/firefox/firefox-bin";
            }
        } else if (!checkBoxes.get("unarchiveBuilds")) {
            fxPath = directory + osCheck() + fileName + fileOutputType;
            System.out.println(fxPath);
        }

        //Need to test this in windows before removing! But since we are using desktop we could remove run.exec from msi
        if (fileOutputType.contains(".msi")) {
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("msiexec /i " + fxPath);
        } else if(OS.contains("Linux")) {
            //We are running exec on Linux platforms to open the files
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec(" " + fxPath);
        }
        else{
                try {
                    //This does not work in Linux (because this is from the AWT library)
                    Desktop desktop = null;
                    if (Desktop.isDesktopSupported()) {
                        desktop = Desktop.getDesktop();
                    }
                    desktop.open(new File(fxPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    //Maybe rethink this
        protected String installerPathBuilder(String builds,String fileType, String osSelection){
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
                    }else if(osSelection.contains("linux-x86_64") || osSelection.contains("linux-i686")){
                        return fileOutputType = ".tar.bz2";
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
                        if(osSelection.contains("linux-x86_64") || osSelection.contains("linux-i686")){
                            fileOutputType = ".tar.bz2";
                            return "firefox-" + builds + ".tar.bz2";
                        }else{
                            fileOutputType = ".zip";
                            return "firefox-" + builds + ".zip";
                        }
                    }
                }
            }
        }

    protected String parseHtml(String build) throws IOException {
            Document doc = Jsoup.connect(build).get();
            Elements content = doc.select("td").eq(617);

            String nightlyCurrentVersion = content.text().replaceFirst("firefox-","");
            return nightlyCurrentVersion = nightlyCurrentVersion.replaceFirst(".en-US.win64.zip","");
        }

    public void parseHTMLBuildVersion(String build, ComboBox buildNumberDrop) throws IOException {
        try{
             /*
            If a timeout occurs, an SocketTimeoutException will be thrown. The default timeout is 30 seconds.
            The timeout specifies the combined maximum duration of the connection time and the time to read the full response.
             */
            buildNumberList.clear();


            Document buildNumber = Jsoup.connect(build).timeout(4000).get();
            Elements con = buildNumber.select("a");

            String builds = con.text();
            String item[] = builds.replace("..", "").split("/");

            for(int i = 0; i < item.length; i++){
                buildNumberList.add(item[i].trim());
            }
            buildNumberDrop.getItems().addAll(buildNumberList);
        } catch (HttpStatusException e) {
            System.out.println("Resource not found");
        }
    }

    public String buildPathForBuildVersioN(String build, com.gluonhq.charm.glisten.control.TextField buildVersion){
        if(!build.contains("DevEd") || !build.contains("Latest Nightly")){
            return "https://archive.mozilla.org/pub/firefox/candidates/" + buildVersion.getText() + "-candidates/";
        }else if(build.contains("DevEd")){
            return "https://archive.mozilla.org/pub/devedition/candidates/" + buildVersion.getText() + "-candidates/";
        }else{
            return "";
        }
    }

    protected String osCheck(){
        if(OS.contains("Mac OS X") || OS.contains("Linux")){
            return "/";
        }else{
            return "\\";
        }
        }

    public boolean checkForInavlidString(String toVerify, String invalidCharacter){
        return toVerify.contains(invalidCharacter);

    }
    }
