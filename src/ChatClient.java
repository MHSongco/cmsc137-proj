
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class ChatClient extends Application {
    private BufferedReader reader;
    private PrintWriter writer;
    private TextArea messageArea;

    public void start(Stage stage) throws Exception {
        initNetwork();

        BorderPane pane = new BorderPane();
        messageArea = new TextArea();
        messageArea.setEditable(false);
        TextField inputField = new TextField();

        inputField.setOnAction(event -> {
            writer.println(inputField.getText());
            inputField.setText("");
        });
        pane.setCenter(messageArea);
        pane.setBottom(inputField);

        Scene scene = new Scene(pane, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Chat Client");
        stage.show();
    }

    private void initNetwork() throws IOException {
        Socket socket = new Socket("localhost", 12345);
        InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(socket.getOutputStream(), true);

        Thread readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    final String message = line;
                    javafx.application.Platform.runLater(() -> messageArea.appendText(message + "\n"));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        readerThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
