import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Engine extends BuildGiver {
    public  String targetURL;
    public  String fileOutput;


    public void initiateDownload(String targetURL, String fileOutput) throws IOException {
        //We are using Java NIO package to handle networking input-output.

        // We are creating a stream to read content from the URL.
        ReadableByteChannel readChannel = Channels.newChannel(new URL("https://archive.mozilla.org/pub/firefox/candidates/85.0b4-candidates/build1/win64/en-US/firefox-85.0b4.zip").openStream());

        //We are transferring the downloaded content to a file on the local system.
        FileOutputStream fileOS = new FileOutputStream("D://te//firefox.zip");

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
        initiateDownload(targetURL,fileOutput);
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
        String fxPath = "D://te//firefox//firefox.exe";
        Runtime run = Runtime.getRuntime();
        Process proc = run.exec(fxPath);

    }

    public void pathBuilder(){

    }

    public void quit(){
        System.exit(0);
    }
}
