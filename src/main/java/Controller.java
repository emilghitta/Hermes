import com.gluonhq.charm.glisten.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.Engine;
import model.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller extends Hermes implements Initializable {
    private Utils utill= new Utils();
    private Engine eng = new Engine();
    private String dirNN;
    private String dropdownInput;
    protected HashMap<String,String> builds = new HashMap<>();
    private ObservableList<String> channelList = FXCollections.observableArrayList("Latest Nightly","Beta","Release","ESR","DevEd");

    //Hardcoded for now
    private ObservableList<String> buildNumberList = FXCollections.observableArrayList("build1");
    private ObservableList<String> osVersionList = FXCollections.observableArrayList("win64","win32","win64-aarch64","mac","linux-x86_64","linux-i686");
    private ObservableList<String> fileTypeWindows64List = FXCollections.observableArrayList("archive","Firefox Setup exe","Firefox Setup msi");
    private ObservableList<String> fileTypeWindows32List = FXCollections.observableArrayList("archive","Firefox Installer.exe","Firefox Setup exe","Firefox Setup msi");
    private ObservableList<String> fileTypeWindowsArmList = FXCollections.observableArrayList("archive","Firefox Setup exe");
    private ObservableList<String> fileTypeLinuxList = FXCollections.observableArrayList("archive");
    private ObservableList<String> fileTypeMacList = FXCollections.observableArrayList("dmg","pkg");

    @FXML
    private Button saveToButton;

    @FXML
    private Label downloadPathLabel;

    @FXML
    private Button downloadButton;

    @FXML
    private ComboBox<String> channelDropdown;

    @FXML
    private TextField buildVersion;

    @FXML
    private ComboBox<String> buildNumber;

    @FXML
    private ComboBox<String> osVersion;

    @FXML
    private ComboBox<String> installerType;

    @FXML
    private CheckBox unarchiveBuilds;

    @FXML
    private CheckBox autoOpenBuilds;

    @FXML
    private TextField buildLocale;

    @FXML
    private Label errorMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        channelDropdown.setItems(channelList);
        osVersion.setItems(osVersionList);


        /*
        Creating the event listener for the saveToButton. On Click we are creating a directoryChooser which extracts the Stage from main in order to display it
        when needed. The primary is a field variable from main which gets instantiated after within the start() method.

        We are also:
        1. Initializing the dirNN with the selectedDirectory.toString() value.
        2. Setting the text and displaying the downloadPathLabel which displays the selected path as label to the GUI.
        3. Enabling the downloadButton.

        This was surrounded with a try catch to avoid NullPointerException for dirNN in case the selected directory was NULL (in case users closed the directory
        picker window).
         */
        saveToButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File selectedDirectory = directoryChooser.showDialog(primary);
                try {
                    dirNN = selectedDirectory.toString();
                    downloadPathLabel.setText(dirNN);
                    downloadPathLabel.setVisible(true);
                    downloadButton.setDisable(false);
                } catch (NullPointerException e) {
                    System.out.println("User has clicked Cancel");
                }
            }
        });

        /*
        Creating the event listener for channelDropdown. The event is fired when an item is selected.
        1. Disables and clears both buildVersion & buildNumber if LatestNightly is selected.
        2. Enables and adds all items to buildNumber if selection != nightly.
        3. Enables the osVersion dropdown list if channelDropdown contains "Latest Nightly".
         */
        channelDropdown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                osVersion.setValue("");
                autoOpenBuilds.setSelected(false);
                buildVersion.setText("");
                buildLocale.setText("");

                if (channelDropdown.getValue().contains("Latest Nightly")) {
                    buildVersion.setText("");
                    buildVersion.setDisable(true);
                    buildNumber.getItems().clear();
                    buildNumber.setDisable(true);
                    osVersion.setDisable(false);
                } else {
                    buildVersion.setDisable(false);
                    buildNumber.getItems().clear();
                    buildNumber.getItems().addAll(buildNumberList);
                    buildNumber.setDisable(false);
                    osVersion.setDisable(true);
                }
                installerType.setDisable(true);
            }
        });

        /*
        Creating the event listener for osVersion. This even is fired when a osVersion is selected from the combo list.
        1. We are clearing the combo box each time the event is triggered so the combo box is cleared each time so it is prepared for the items re-population.
        2. We are populating the combo box based on the osVersion with relevant items.
         */
        osVersion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                installerType.getItems().clear();
                buildLocale.setDisable(false);
                installerType.setDisable(false);
                autoOpenBuilds.setDisable(true);

                if (osVersion.getValue().contains("linux-x86_64") || osVersion.getValue().contains("linux-i686")) {
                    installerType.getItems().addAll(fileTypeLinuxList);
                } else if (osVersion.getValue().contains("mac")) {
                    installerType.getItems().addAll(fileTypeMacList);
                } else if (osVersion.getValue().contains("win64")) {
                    installerType.getItems().addAll(fileTypeWindows64List);
                } else if (osVersion.getValue().contains("win64-aarch64")) {
                    installerType.getItems().addAll(fileTypeWindowsArmList);
                } else {
                    installerType.getItems().addAll(fileTypeWindows32List);
                }
            }
        });

        /*
        Creating the event listener for installerType which gets fired after selecting an installer type from the dropdown menu.
        1. We are disabling the unarchiveBuilds checkbox if the installer type != archive or the selection is null.
        2. We are enabling the unarchiveBuilds checkbox if the installer type == archive and the selection is != null.
         */
        installerType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (installerType.getValue() == null || !installerType.getValue().contains("archive")) {
                    unarchiveBuilds.setDisable(true);
                    unarchiveBuilds.setSelected(false);
                    autoOpenBuilds.setDisable(false);
                } else if (installerType.getValue().contains("archive")) {
                    unarchiveBuilds.setDisable(false);
                    autoOpenBuilds.setDisable(true);
                    autoOpenBuilds.setSelected(false);
                }
            }
        });

        buildNumber.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });

        buildNumber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                osVersion.setDisable(false);
            }
        });

        unarchiveBuilds.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (unarchiveBuilds.isSelected()) {
                    autoOpenBuilds.setDisable(false);
                } else {
                    autoOpenBuilds.setDisable(true);
                    autoOpenBuilds.setSelected(false);
                }

            }
        });

        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                builds.clear();

                dropdownInput = channelDropdown.getValue().trim();

//                //Not Sure if this works
//                primary.getScene().setCursor(javafx.scene.Cursor.cursor(String.valueOf(Cursor.WAIT_CURSOR)));
                if (dropdownInput.contains("Latest Nightly")) {
                    System.out.println("Nightly");
                    builds.put("latestNightly", "Nightly");
                    utill.fileNN = "LatestNightly" + "-" + buildLocale.getText();
                } else if (dropdownInput.contains("Beta")) {
                    if(!eng.checkForInavlidString(buildVersion.getText(),"b")){
                        setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                    }else{
                        System.out.println("Beta");
                        builds.put("betaVersion", buildVersion.getText());
                        utill.fileNN = buildVersion.getText() + "-" + buildNumber.getValue() + "-" + buildLocale.getText();
                    }

                } else if (dropdownInput.contains("Release")) {
                    if (eng.checkForInavlidString(buildVersion.getText(), "b")) {
                        setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                    } else {
                        System.out.println("Release");
                        builds.put("releaseVersion", buildVersion.getText());
                        utill.fileNN = buildVersion.getText() + "-" + buildNumber.getValue() + "-" + buildLocale.getText();
                    }

                } else if (dropdownInput.contains("ESR")) {
                    if(eng.checkForInavlidString(buildVersion.getText(),"b")){
                        setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                    }else{
                        System.out.println("ESR");
                        builds.put("esrVersion", buildVersion.getText());
                        utill.fileNN = buildVersion.getText() + "-" + buildNumber.getValue() + "-" + buildLocale.getText();
                    }

                } else if (dropdownInput.contains("DevEd")) {
                    if(!eng.checkForInavlidString(buildVersion.getText(),"b")){
                        setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                    }else{
                        System.out.println("DevEd");
                        builds.put("devedVersion", buildVersion.getText());
                        utill.fileNN = buildVersion.getText() + "-" + buildNumber.getValue() + "-" + buildLocale.getText();
                    }

                }

                downloadPathLabel.setText(dirNN + "\\" + utill.fileNN);
                eng.osSelection = osVersion.getValue();
                eng.typeOfFile = installerType.getValue();
                eng.buildNumber = buildNumber.getValue();

                eng.checkBoxes.put("unarchiveBuilds", unarchiveBuilds.isSelected());
                eng.checkBoxes.put("autoOpenBuilds", autoOpenBuilds.isSelected());

                System.out.println(buildNumber.getValue());
                System.out.println(utill.fileNN);
                System.out.println(dirNN);

                try {
                    eng.pathFoundation(builds, eng.buildNumber, eng.osSelection, buildLocale.getText(), eng.typeOfFile, utill.fileNN, dirNN);
                    setErrorMessage("SUCCESS!!");
                } catch (NullPointerException | IOException ioException) {
                    System.out.println(ioException);
                    setErrorMessage("Build Download Error: Please double check you Build Version or Locale");


                }
//                primary.getScene().setCursor(javafx.scene.Cursor.cursor(String.valueOf(Cursor.DEFAULT_CURSOR)));
            }
        });



    }
    private void setErrorMessage(String message){
            if(message.contains("SUCCESS!!")){
                errorMessage.setText(message);
                errorMessage.setTextFill(javafx.scene.paint.Color.web("00FF00"));
            }else{
                errorMessage.setText("Build Download Error: Please double check you Build Version or Locale");
                errorMessage.setTextFill(javafx.scene.paint.Color.web("#FF0000"));
            }
        errorMessage.setVisible(true);
    }
    }


