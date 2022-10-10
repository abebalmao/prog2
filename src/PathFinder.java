import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Optional;

public class PathFinder extends Application {

    private BorderPane borderPane = new BorderPane();
    private FlowPane buttonPane = new FlowPane();
    private Pane backgroundPane;
    private Button findPathButton;
    private Button showConnectionButton;
    private Button newPlaceButton;
    private Button newConnectionButton;
    private Button changeConnectionButton;
    private TextField nameField;
    private TextField timeField;
    private boolean changes;
    private boolean newPlaceActive;
    private Place place1;
    private Place place2;
    private ListGraph<Place> listOfPlaces = new ListGraph<Place>();
    private Stage exitStage;
    private int total;

    @Override
    public void start(Stage primaryStage) {

        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu");
        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile");
        MenuItem newMapMenuItem = new MenuItem("New Map");
        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem saveImageMenuItem = new MenuItem("Save Image");
        MenuItem exitMenuItem = new MenuItem("Exit");

        newMapMenuItem.setId("menuNewMap");
        openMenuItem.setId("menuOpenFile");
        saveMenuItem.setId("menuSaveFile");
        saveImageMenuItem.setId("menuSaveImage");
        exitMenuItem.setId("menuExit");

        newMapMenuItem.setOnAction(e -> newMap(primaryStage));
        openMenuItem.setOnAction(e -> openFile());
        saveMenuItem.setOnAction(e -> saveToFile());
        saveImageMenuItem.setOnAction(e -> saveImage());
        exitMenuItem.setOnAction(new ExitItemHandler());

        fileMenu.getItems().addAll(newMapMenuItem,
                openMenuItem,
                saveMenuItem,
                saveImageMenuItem,
                exitMenuItem
        );
        menuBar.getMenus().add(fileMenu);

        findPathButton = new Button("Find Path");
        showConnectionButton = new Button("Show Connection");
        newPlaceButton = new Button("New Place");
        newConnectionButton = new Button("New Connection");
        changeConnectionButton = new Button("Change Connection");

        findPathButton.setId("btnFindPath");
        newPlaceButton.setId("btnNewPlace");
        newConnectionButton.setId("btnNewConnection");
        showConnectionButton.setId("btnShowConnection");
        changeConnectionButton.setId("btnChangeConnection");

        findPathButton.setOnAction(e -> findPath());
        newPlaceButton.setOnAction(e -> newPlaceButton());
        newConnectionButton.setOnAction(new ButtonHandler());
        showConnectionButton.setOnAction(new ButtonHandler());
        changeConnectionButton.setOnAction(new ButtonHandler());

        buttonPane.getChildren().addAll(
                findPathButton,
                showConnectionButton,
                newPlaceButton,
                newConnectionButton,
                changeConnectionButton
        );

        backgroundPane = new Pane();
        backgroundPane.setId("outputArea");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, buttonPane);
        borderPane.setTop(vbox);
        borderPane.setCenter(backgroundPane);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(new ExitHandler());
        primaryStage.show();
        exitStage = primaryStage;
        changes = false;
    }

    public void loadMap() {
        for (Place p1 : listOfPlaces.getNodes()) {
            backgroundPane.getChildren().add(p1);
            for (Place p2 : listOfPlaces.getNodes()) {
                if (listOfPlaces.getEdgeBetween(p1, p2)!= null ) {
                    Line line = new Line(p1.getCenterX(),
                            p1.getCenterY(),
                            p2.getCenterX(),
                            p2.getCenterY()
                    );
                    line.setFill(Color.BLACK);
                    line.setDisable(true);

                    backgroundPane.getChildren().add(line);

                }
            }
        }


    }

    public void resetMapAndObjects() {
        listOfPlaces.clear();
        backgroundPane.getChildren().clear();
        place1 = null;
        place2 = null;
    }

    public void newMap(Stage primaryStage) {

        if (changes("Unsaved changes, continue anyway?")) {
            resetMapAndObjects();
            initializeMap(primaryStage);
        }
        if (!changes) {
            resetMapAndObjects();
            initializeMap(primaryStage);
        }
    }

    public void initializeMap(Stage primaryStage) {
        Label map = new Label();
        Image image = new Image("file:europa.gif");
        ImageView iv = new ImageView(image);
        map.setGraphic(iv);
        backgroundPane.getChildren().add(map);
        primaryStage.sizeToScene();
        //changes = true;
    }

    public boolean openFileChecker() {
        if (changes) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Warning!");
            alert.setContentText("Unsaved changes");
            Optional<ButtonType> result = alert.showAndWait();
            if (!result.isEmpty() && result.get().equals(ButtonType.OK)) {
                changes = false;
                return false;
            }
      
        }
        return true;
    }
    public void openFile() {
        if (changes("Unsaved changes")) { // change this to revert
            resetMapAndObjects();
            try {
                FileReader fileReader = new FileReader("europa.graph");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String firstRow = bufferedReader.readLine(); // läser bara första raden
                String input = bufferedReader.readLine();
                String[] placeArray = input.split(";");


                for (int i = 0; i < placeArray.length; i += 3) {
                    String name = placeArray[i];
                    double x = Double.parseDouble(placeArray[i + 1]);
                    double y = Double.parseDouble(placeArray[i + 2]);
                    Place place = new Place(name, x, y);
                    //place.setId(name);
                    place.setOnMouseClicked(new PlaceHandler());
                    listOfPlaces.add(place);

                }
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] fileContent = line.split(";");
                    Place p1 = null;
                    Place p2 = null;

                    for (Place p : listOfPlaces.getNodes()) {
                        if (p.getName().equals(fileContent[0]))
                            p1 = p;
                        if (p.getName().equals(fileContent[1]))
                            p2 = p;
                    }
                    if (p1 != null && p2 != null) {
                        listOfPlaces.connect(p1, p2, fileContent[2], Integer.parseInt(fileContent[3]));
                    }

                }
                Label map = new Label();
                Image image = new Image(firstRow);
                ImageView iv = new ImageView(image);
                map.setGraphic(iv);
                backgroundPane.getChildren().add(map);
                loadMap();
                changes = true;
            } catch(FileNotFoundException ex){
                alertError("No Such File");
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void saveToFile() {

        File file = new File("europa.graph");
        String output;
        place1 = null;
        place2 = null;
        try {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("file:europa.gif");

            for(Place p : listOfPlaces.getNodes()) {
                output = p.getName() + ";" +
                        p.getCenterX() + ";" +
                        p.getCenterY() + ";";
                printWriter.print(output);

            }
            printWriter.println();
            for(Place p : listOfPlaces.getNodes()) {
                for (Edge e : listOfPlaces.getEdgesFrom(p)) {
                    output = p.getName() + ";" +
                            e.getDestination() + ";" +
                            e.getName()+ ";" +
                            e.getWeight();
                    printWriter.println(output);
                }
            }
            fileWriter.close();
            printWriter.close();
            changes = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveImage() {
        WritableImage snapshot = borderPane.snapshot(null, null);
        File file = new File("capture.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException e) {
            alertError("Capture unsuccessfull");
        }
    }

    public boolean changes(String contentText) {
        if (changes) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(contentText);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                changes = false;
                return true;
            }
        }
        return true; // change this to false if openFileChecker
    }

    public void newPlaceButton() {
        if (!newPlaceActive) {
            borderPane.setCursor(Cursor.CROSSHAIR);
            newPlaceButton.setDisable(true);
            borderPane.setOnMouseClicked(new ClickHandler());
        } else {
            resetPlaceButton(); // redudant
        }
    }

    public void resetPlaceButton() {
        borderPane.setCursor(Cursor.DEFAULT);
        newPlaceButton.setDisable(false);
        borderPane.setOnMouseClicked(null);
    }

    public void newConnection(ActionEvent e) {
        if (place1 == null || place2 == null) {
            alertError("Two places must be selected");
        } else if (listOfPlaces.getEdgeBetween(place1, place2) != null) {
            alertError("Connection already exist between ", place1, place2);
        } else {
            //Optional<ButtonType> result = createConnectionDialog();
            Optional<ButtonType> result = dialogConnection(e);
            if (result.isEmpty()) {
                alertError("Empty dialog");
                //result = createConnectionDialog(); // xd
            } else {
                listOfPlaces.connect(
                        place1,
                        place2,
                        nameField.getText(),
                        Integer.parseInt(timeField.getText()));

                Line line = new Line(
                        place1.getCenterX(),
                        place1.getCenterY(),
                        place2.getCenterX(),
                        place2.getCenterY());
                line.setDisable(true);
                backgroundPane.getChildren().add(line);
                changes = true;
            }
        }
    }

    public void showConnection(ActionEvent e) {
        if (place1 == null || place2 == null) {
            alertError("Two places must be selected");
        } else if (listOfPlaces.getEdgeBetween(place1, place2) == null) {
            alertError("No connection between ", place1, place2);
        } else {
            dialogConnection(e);
        }
    }

    public void changeConnection(ActionEvent e) {
        if (place1 == null || place2 == null) {
            alertError("Two places must be selected");
        } else if (listOfPlaces.getEdgeBetween(place1, place2) == null) {
            alertError("No connection between ", place1, place2);
        } else {
            Optional<ButtonType> result = dialogConnection(e);
            if (result.isEmpty()) {
                alertError("Empty dialog");
            } else {
                listOfPlaces.getEdgeBetween(place1, place2).setWeight(Integer.parseInt(timeField.getText()));
                listOfPlaces.getEdgeBetween(place2, place1).setWeight(Integer.parseInt(timeField.getText()));
                changes = true;
            }
        }
    }

    public Optional<ButtonType> dialogConnection(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Connection from " + place1.getName() + " to " + place2.getName());
        alert.getDialogPane().setContent(createConnectionDialog(e));

        return alert.showAndWait();
    }

    public GridPane createConnectionDialog(ActionEvent e) {
        GridPane connectionGrid = new GridPane();
        connectionGrid.setHgap(10);
        connectionGrid.setVgap(10);
        connectionGrid.setPadding(new Insets(20, 150, 10, 10));

        nameField = new TextField();
        timeField = new TextField();

        if (e.getSource() != newConnectionButton) {
            nameField.setText(listOfPlaces.getEdgeBetween(place1, place2).getName());
            timeField.setText(String.valueOf(listOfPlaces.getEdgeBetween(place1, place2).getWeight()));
            nameField.setEditable(false);
            if (e.getSource() == showConnectionButton) {
                timeField.setEditable(false);
            }
        }
        connectionGrid.add(new Label("Name:"), 0, 0);
        connectionGrid.add(nameField, 1, 0);
        connectionGrid.add(new Label("Time:"), 0, 1);
        connectionGrid.add(timeField, 1, 1);

        return connectionGrid;
    }

    public void findPath() {
        if (place1 == null || place2 == null) {
            alertError("Two places must be selected");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            TextArea textArea = new TextArea();
            total = 0;
            alert.setHeaderText("The Path from " + place1.getName() + " to " + place2.getName());
            alert.getDialogPane().setContent(textArea);

            StringBuilder sbEdge = new StringBuilder();
            StringBuilder stringBuilder = new StringBuilder();

            for (Edge e : listOfPlaces.getPath(place1, place2)) {
                sbEdge.append(e.toString()).append("\n");
                total += e.getWeight();
            }

            stringBuilder.append("Total ").append(total).append("\n");
            stringBuilder.append(sbEdge);
            textArea.appendText(String.valueOf(stringBuilder));
            alert.showAndWait();
        }
    }

    public void alertError(String contentText, Place place1, Place place2) {
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText + place1.getName() + " and " + place2.getName());
        alert.showAndWait();
    }

    public void alertError(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText);
        alert.showAndWait();
    }

    public class ButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {

            if (e.getSource() == newConnectionButton) {
                newConnection(e);
            } else if (e.getSource() == showConnectionButton) {
                showConnection(e);
            } else if (e.getSource() == changeConnectionButton) {
                changeConnection(e);
            }

        }
    }

    public class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent e) {
            //changes("Unsaved changes, exit anyways?")

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Unsaved changes, exit anyways?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                System.out.println("exit");
                changes = false;
                System.exit(0);
            } else {
                e.consume();
            }

            /*
            if (changes("Unsaved changes, exit anyways?")) {
                resetMapAndObjects();
                changes = false;
                System.exit(0);
            }

             */
        }
    }

    public class ExitItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Unsaved changes, exit anyways?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                System.out.println("exitItem");
                changes = false;
                exitStage.fireEvent(new WindowEvent(exitStage, WindowEvent.WINDOW_CLOSE_REQUEST));

            }
            /*
            if (changes("Unsaved changes, exit anyways?")) {
                exitStage.fireEvent(new WindowEvent(exitStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }

             */
        }
    }

    public class PlaceHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            Place p = (Place) e.getSource();
            if (p.isSelected()) {
                p.setSelected(false);
                if (p.equals(place1)) {
                    place1 = null;
                } else {
                    place2 = null;
                }

            } else if (place1 == null) {
                place1 = p;
                p.setSelected(true);
            } else if (place2 == null && p != place1) {
                place2 = p;
                p.setSelected(true);
            }

        }
    }

    public class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            TextInputDialog textInput = new TextInputDialog();
            textInput.setContentText("Name of place");
            Optional<String> result = textInput.showAndWait();
            String name = textInput.getEditor().getText();
            double x = e.getX();
            double y = e.getY();
            if (result.isPresent()) {
                Place place = new Place(name, x, y);
                place.setOnMouseClicked(new PlaceHandler());
                listOfPlaces.add(place);
                backgroundPane.getChildren().add(place);
            }
            resetPlaceButton();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

