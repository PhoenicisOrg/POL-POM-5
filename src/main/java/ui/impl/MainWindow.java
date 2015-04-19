package ui.impl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

import static utils.Localisation.Translate;

public class MainWindow extends Application {
    private static ui.api.EventHandler eventHandler;
    private Stage stage;

    public ToolBar generateToolbar() {

        ImageView runImage = new ImageView(this.getClass().getResource("toolbar/run.png").toExternalForm());
        runImage.setFitWidth(16);
        runImage.setFitHeight(16);

        ImageView stopImage = new ImageView(this.getClass().getResource("toolbar/stop.png").toExternalForm());
        stopImage.setFitWidth(16);
        stopImage.setFitHeight(16);

        ImageView installImage = new ImageView(this.getClass().getResource("toolbar/install.png").toExternalForm());
        installImage.setFitWidth(16);
        installImage.setFitHeight(16);

        ImageView removeImage = new ImageView(this.getClass().getResource("toolbar/delete.png").toExternalForm());
        removeImage.setFitWidth(16);
        removeImage.setFitHeight(16);

        ImageView configureImage = new ImageView(this.getClass().getResource("toolbar/configure.png").toExternalForm());
        configureImage.setFitWidth(16);
        configureImage.setFitHeight(16);


        Button run = new Button(Translate("Run"), runImage);
        run.setContentDisplay(ContentDisplay.LEFT);

        Button stop = new Button(Translate("Stop"), stopImage);
        stop.setContentDisplay(ContentDisplay.LEFT);

        Button install = new Button(Translate("Install"), installImage);
        install.setContentDisplay(ContentDisplay.LEFT);

        Button remove = new Button(Translate("Remove"), removeImage);
        remove.setContentDisplay(ContentDisplay.LEFT);

        Button configure = new Button(Translate("Configure"), configureImage);
        configure.setContentDisplay(ContentDisplay.LEFT);

        TextField searchField = new TextField();

        ToolBar toolbar = new ToolBar();
        toolbar.getItems().addAll(
                run,
                stop,
                new Separator(),
                install,
                remove,
                new Separator(),
                configure,
                new Separator(),
                searchField
        );

        return toolbar;
    }


    public MenuBar generateMenuBar() {
        final Menu fileMenu = new Menu("File");
        final Menu toolsMenu = new Menu("Tools");
        MenuItem openScript = new MenuItem("Run a local script");
        openScript.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a script");
            File scriptToRun = fileChooser.showOpenDialog(stage);

            eventHandler.runLocalScript(scriptToRun);
        });
        toolsMenu.getItems().addAll(openScript);

        final Menu helpMenu = new Menu("Help");

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, toolsMenu, helpMenu);
        menuBar.useSystemMenuBarProperty().set(true);

        return menuBar;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        VBox topContainer = new VBox();
        topContainer.getChildren().add(this.generateMenuBar());
        topContainer.getChildren().add(this.generateToolbar());

        BorderPane pane = new BorderPane();
        pane.setTop(topContainer);

        Scene scene = new Scene(pane, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("PlayOnLinux");
        primaryStage.show();
    }


    public static void defineStaticEventHandler(ui.api.EventHandler eventHandler) {
        MainWindow.eventHandler = eventHandler;
    }
}
