import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Hermes extends Application {
    protected DirectoryChooser directoryChooser = new DirectoryChooser();
    protected Stage primary;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hermes");
        primaryStage.setScene(new Scene(root, 387, 480));
        primaryStage.getIcons().add(new Image("/Hermes.png"));
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primary = primaryStage;


    }

    public static void main(String[] args) {
        launch(args);
    }
}


