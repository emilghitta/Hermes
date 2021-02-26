import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Updater;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Hermes extends Application {
    protected DirectoryChooser directoryChooser = new DirectoryChooser();
    protected Stage primary;
    private final Properties configProp = new Properties();


    @Override
    public void start(Stage primaryStage) throws Exception{
        initProperties();
        try{
            if(!configProp.getProperty("version").equals(Updater.getLatestVersion())){
                new UpdateInfo(Updater.getWhatsNew());
            }else{
                Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
                primaryStage.setTitle("Hermes " + configProp.getProperty("version"));
                primaryStage.setScene(new Scene(root, 387, 480));
                primaryStage.getIcons().add(new Image("/Hermes.png"));
                primaryStage.show();
                primaryStage.setMinWidth(primaryStage.getWidth());
                primaryStage.setMinHeight(primaryStage.getHeight());
                primary = primaryStage;
                configProp.setProperty("version",Updater.getLatestVersion());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initProperties(){
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("appVersion.properties");
        try{
            configProp.load(in);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}


