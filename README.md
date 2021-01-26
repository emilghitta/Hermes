# Description
Hermes is a simple JavaFX application which automates Firefox build downloads.

# How to install

1. First of all you need to download and install the `latest java SE` on your pc. The Hermes jar file (like any other jar file) needs to be executed by the JVM.
2. Head over to the `release page`.
 
 ![Alt text](https://github.com/emilghitta/Hermes/blob/master/src/main/resources/githubPictures/releases.png "Release Pages") 

3. Get the latest `jar` file.
 ![Alt text](https://github.com/emilghitta/Hermes/blob/master/src/main/resources/githubPictures/latestRelease.png "LatestReleases")
 
# How to open Hermes

The launching process for Hermes on Windows platforms is straight forward (you just have to double click the Hermes file). Unfortunately, for now, things are looking different
for Ubuntu and macOS machines.

* Ubuntu:

You might encounter the following error message `... is not marked as executable. If this was downloaded or copied from an untrusted source, it may be dangerous to run. For more details, read about the executable bit.`
In order to bypass this error message you need to:

-Right click on the jar file -> properties and check the `Allow executing file as program` checkbox.

 ![Alt text](https://github.com/emilghitta/Hermes/blob/master/src/main/resources/githubPictures/LinuxPerm.png "Linux Permissions")

OR

-Open a terminal and type `chmod +x pathToHermes/Hermes.jar` 


* macOS

You might encounter the `Hermes.jar can't be opened because it is from an unidentified developer`.

In order to bypass this error message you need to:

-Right click on the jar file -> open. 

This will open the same prompt but with the possibilty of opening the file:

 ![Alt text](https://github.com/emilghitta/Hermes/blob/master/src/main/resources/githubPictures/untrustedBypass.png "Untrusted")

# How it works

A simple workflow:

1. Set a download location by clicking the "Save to" button. This will open a directory picker.
2. Specify the appropiate channel.
3. Specify the version you want to download. Note: You need to input it like (84.0b4 for beta builds or 78.4.0esr for esr builds).
4. Select a build for that particular version. The build dropdown menu updates dynamically based on the inputed Version number. This is being performed using Jsoup.
5. Select a locale in the form of (eg: `en-US` or `ar`).
6. Choose an installer type.
7. Optional you can set Hermes to auto extract the archives when download is completed or/and automatically open the builds when download is completed.
8. Hit the download button.


# Future implementations
* `Batch download`. In order to download multiple builds at once.
* `Auto app updater`. Currently there is no auto updater implemented (you will have to manually check for newer versions on the github releases).
* `Avoiding archive extraction overriding`. Currently, if you download and automatically extract an archive it will override the existing firefox folder inside the specified path.
