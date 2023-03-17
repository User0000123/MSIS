import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class View extends Application{
    public static Stage Interface;

    public static void main(String[] args) {
        View.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui_market.fxml")));
        stage.show();
        Interface = stage;
    }
}