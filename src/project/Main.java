package project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private Set<String> savedFiles = new HashSet<>();
    private HBox shortcutsRow = new HBox(20);
    private VBox shortcutsColumn = new VBox(20);
    private MediaPlayer mediaPlayer;
    private static final String CORRECT_PASSWORD = "1234";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/assets/nature.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

        Image profileImage = new Image(getClass().getResourceAsStream("/assets/default_profile_pic.png"));
        ImageView profileImageView = new ImageView(profileImage);
        profileImageView.setFitHeight(100);
        profileImageView.setFitWidth(100);

        Label welcomeLabel = new Label("Welcome RU24-2!");
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(new Font(20));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.getStyleClass().add("password-field");
        passwordField.setPrefWidth(200);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");

        HBox passwordRow = new HBox(10, passwordField, loginButton);
        passwordRow.setAlignment(Pos.CENTER);

        Label errorLabel = new Label("Wrong Password!");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        VBox loginBox = new VBox(10, profileImageView, welcomeLabel, passwordRow, errorLabel);
        loginBox.setAlignment(Pos.CENTER);

        StackPane loginRoot = new StackPane(backgroundImageView, loginBox);
        Scene loginScene = new Scene(loginRoot, 500, 400);

        loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Scene homeScene = createHomeScene(primaryStage, loginScene);
        homeScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        loginButton.setOnAction(e -> {
            if (passwordField.getText().equals(CORRECT_PASSWORD)) {
                errorLabel.setVisible(false);
                primaryStage.setScene(homeScene);
            } else {
                errorLabel.setVisible(true);
            }
        });

        primaryStage.setScene(loginScene);
        primaryStage.setOnCloseRequest(e -> stopVideo());
        primaryStage.show();
    }

    private Scene createHomeScene(Stage stage, Scene loginScene) {
        StackPane root = new StackPane();

        Image background = new Image(getClass().getResourceAsStream("/assets/homepage.jpg"));
        ImageView backgroundView = new ImageView(background);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(stage.widthProperty());
        backgroundView.fitHeightProperty().bind(stage.heightProperty());

        shortcutsRow.setPadding(new Insets(10));
        shortcutsRow.setAlignment(Pos.TOP_LEFT);
        shortcutsRow.setSpacing(20); 
        
        shortcutsColumn.setAlignment(Pos.TOP_LEFT);
        shortcutsRow.getChildren().add(shortcutsColumn);

        VBox shortcut1 = createShortcut("Trash", "/assets/trash-icon.png");
        VBox shortcut2 = createShortcut("Notepad", "/assets/notepad-icon.png");
        VBox shortcut3 = createShortcut("ChRUme", "/assets/chrome.png");
        shortcutsColumn.getChildren().addAll(shortcut1, shortcut2, shortcut3);
        
        MenuBar taskBar = new MenuBar();
        taskBar.setStyle("-fx-background-color: black;");

        Menu windowMenu = new Menu();
        Image windowImg = new Image(getClass().getResourceAsStream("/assets/window-icon.png"));
        ImageView windowIcon = new ImageView(windowImg);
        windowIcon.setFitHeight(20);
        windowIcon.setFitWidth(20);
        windowMenu.setGraphic(windowIcon);

        MenuItem logoutItem = new MenuItem("Logout", new ImageView(new Image(getClass().getResourceAsStream("/assets/logout3-icon.png"), 16, 16, true, true)));
        logoutItem.setOnAction(e -> stage.setScene(loginScene));

        MenuItem shutdownItem = new MenuItem("Shutdown", new ImageView(new Image(getClass().getResourceAsStream("/assets/shutdown4-icon.png"), 16, 16, true, true)));
        shutdownItem.setOnAction(e -> stage.close());

        windowMenu.getItems().addAll(logoutItem, shutdownItem);

        Menu notepadMenu = new Menu();
        Image notepadImg = new Image(getClass().getResourceAsStream("/assets/notepad-icon.png"));
        ImageView notepadIcon = new ImageView(notepadImg);
        notepadIcon.setFitHeight(20);
        notepadIcon.setFitWidth(20);
        notepadMenu.setGraphic(notepadIcon);

        MenuItem openNotepadItem = new MenuItem("Open Notepad");
        openNotepadItem.setOnAction(e -> openNotepad());
        notepadMenu.getItems().add(openNotepadItem);

        taskBar.getMenus().addAll(windowMenu, notepadMenu);

        BorderPane homeLayout = new BorderPane();
        BorderPane.setAlignment(shortcutsColumn, Pos.TOP_LEFT);
        homeLayout.setBottom(taskBar);
        homeLayout.setLeft(shortcutsRow);
        shortcutsColumn.setPadding(new Insets(10));

        root.getChildren().addAll(backgroundView, homeLayout);

        return new Scene(root, 800, 600);
    }
    
    private void addShortcutToDesktop(VBox shortcut) {
        VBox lastColumn = (VBox) shortcutsRow.getChildren().get(shortcutsRow.getChildren().size() - 1);

        if (lastColumn.getChildren().size() >= 5) {
            VBox newColumn = new VBox(10);
            newColumn.setAlignment(Pos.TOP_LEFT);
            newColumn.setPadding(new Insets(10));
            newColumn.setSpacing(20);
            shortcutsRow.getChildren().add(newColumn);
            newColumn.getChildren().add(shortcut);
        } else {
            lastColumn.getChildren().add(shortcut);
        }
    }

    private VBox createShortcut(String name, String imagePath) {
        Image iconImg = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView icon = new ImageView(iconImg);
        icon.setFitHeight(50);
        icon.setFitWidth(50);

        Label label = new Label(name);
        label.setTextFill(Color.BLACK);

        VBox shortcut = new VBox(5, icon, label);
        shortcut.setAlignment(Pos.CENTER);

        if ("Notepad".equals(name)) {
            shortcut.setOnMouseClicked(e -> openNotepad());
        }

        if ("ChRUme".equals(name)) {
        	shortcut.setOnMouseClicked(e -> openChrume());
        }
        
        return shortcut;
    }

    private void createTextFileShortcut(String filename) {
        Image iconImg = new Image(getClass().getResource("/assets/notepad-icon.png").toExternalForm());
        ImageView icon = new ImageView(iconImg);
        icon.setFitHeight(50);
        icon.setFitWidth(50);

        Label label = new Label(filename); 
        label.setTextFill(Color.BLACK);

        VBox fileShortcut = new VBox(5, icon, label);
        fileShortcut.setAlignment(Pos.CENTER);

        fileShortcut.setOnMouseClicked(e -> {
            // Read file content
            String content = "";
            try {
                content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error opening file");
                alert.setHeaderText(null);
                alert.setContentText("Could not read file: " + filename);
                alert.showAndWait();
                return;
            }

            // Create notepad window
            Stage noteStage = new Stage();

            TextArea textArea = new TextArea(content);

            MenuBar menuBar = new MenuBar();
            Menu fileMenu = new Menu("File");
            MenuItem saveItem = new MenuItem("Save");

            saveItem.setOnAction(saveEvent -> {
                try (FileWriter writer = new FileWriter(filename)) {
                    writer.write(textArea.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Save Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to save file: " + filename);
                    alert.showAndWait();
                }
            });

            fileMenu.getItems().add(saveItem);
            menuBar.getMenus().add(fileMenu);

            VBox layout = new VBox(menuBar, textArea);
            VBox.setVgrow(textArea, Priority.ALWAYS);
            
            Scene scene = new Scene(layout, 600, 400);
            noteStage.setScene(scene);
            noteStage.setTitle(filename);
            noteStage.show();
        });
        
        addShortcutToDesktop(fileShortcut);
    }

    private void openNotepad() {
        Stage noteStage = new Stage();
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        TextArea textArea = new TextArea();
        MenuItem saveItem = new MenuItem("Save");

        saveItem.setOnAction(e -> {
            // Count existing .txt files
            int count = 1;
            for (String saved : savedFiles) {
                if (saved.endsWith(".txt")) count++;
            }

            // Default name suggestion
            String defaultFilename = "text" + count + ".txt";
            TextInputDialog dialog = new TextInputDialog(defaultFilename);
            dialog.setTitle("Save File");
            dialog.setHeaderText("Save File");
            dialog.setContentText("Rename File:");

            dialog.showAndWait().ifPresent(filename -> {
                if (!filename.toLowerCase().endsWith(".txt")) {
                    filename += ".txt";
                }

                String nameOnly = filename.replaceFirst("\\.txt$", "");

                if (!nameOnly.matches("[a-zA-Z0-9]+")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("File name is Invalid!");
                    alert.setHeaderText(null);
                    alert.setContentText("File name must be alphanumeric!");
                    alert.showAndWait();
                } else {
                    File file = new File(filename);

                    if (file.exists() || savedFiles.contains(filename)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("File with that name already exists!");
                        alert.setHeaderText(null);
                        alert.setContentText("A file with that name has already been made");
                        alert.showAndWait();
                    } else {
                        try (FileWriter writer = new FileWriter(file)) {
                            writer.write(textArea.getText());
                            savedFiles.add(filename);
                            createTextFileShortcut(filename);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        });

        fileMenu.getItems().add(saveItem);
        menuBar.getMenus().add(fileMenu);

        VBox layout = new VBox(menuBar, textArea);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        Scene noteScene = new Scene(layout, 600, 400);
        noteStage.setTitle("Notepad");
        noteStage.setScene(noteScene);
        noteStage.show();
    }
    
    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    private void openChrume() {
    	Stage chrumeStage = new Stage();
    	TextField searchBar = new TextField();
    	searchBar.setPrefWidth(300);
    	
    	Button searchBtn = new Button("Search");
    	
    	HBox hb = new HBox(10, searchBar, searchBtn);
    	hb.setAlignment(Pos.CENTER);
    	
    	GridPane gpChrume = new GridPane();
    	gpChrume.setAlignment(Pos.CENTER);
    	gpChrume.setHgap(10);
    	gpChrume.setVgap(10);
    	
    	searchBtn.setOnAction(e -> {
    		String input = searchBar.getText().trim();
    		stopVideo();
    		gpChrume.getChildren().clear();
    		
    		//Checked site existence
    		if (input.isEmpty()) {
    			gpChrume.getChildren().clear();
    			
    	        // Create the search row centered horizontally
    	        HBox searchRow = new HBox(10, searchBar, searchBtn);
    	        searchRow.setAlignment(Pos.CENTER);

    	        // VBox to keep search bar at top, with empty space below
    	        VBox layout = new VBox(10, searchRow, new VBox());
    	        layout.setAlignment(Pos.TOP_CENTER);
    	        VBox.setVgrow(layout.getChildren().get(1), Priority.ALWAYS); // empty VBox fills remaining space

    	        gpChrume.getChildren().add(layout);
    	        GridPane.setHgrow(layout, Priority.ALWAYS);
    	        GridPane.setVgrow(layout, Priority.ALWAYS);
    	    } 
    			
    		else if (input.equalsIgnoreCase("RUtube.net")) {
    			//stop any previous video
				stopVideo();
				gpChrume.getChildren().clear();
				
				//root layout
				BorderPane root = new BorderPane();
				root.setStyle("-fx-background-color: #2f2f2f;");
				
				//Youtube Logo
				Image logoImage = new Image(getClass().getResource("/assets/youtube-logo.png").toExternalForm());
				ImageView logoView = new ImageView(logoImage);
				logoView.setFitHeight(30);
				logoView.setPreserveRatio(true);
				
				//Label "RUtube"
				Label rutubeLabel = new Label("RUtube");
				rutubeLabel.setTextFill(Color.WHITE);
				rutubeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
				
				HBox topBar = new HBox (10, logoView, rutubeLabel);
				topBar.setAlignment(Pos.CENTER_LEFT);
				topBar.setPadding(new Insets(10));
				
				//Add top bar to BorderPane
				root.setTop(topBar);
				
				//Load Video
				Media media = new Media(getClass().getResource("/assets/DiamondJack.mp4").toExternalForm());
				mediaPlayer = new MediaPlayer(media);
				MediaView mediaView = new MediaView(mediaPlayer);
				mediaView.setFitWidth(600);
				mediaView.setPreserveRatio(true);
				
				//Play and Pause Button
				Button playButton = new Button("Play");
				Button pauseButton = new Button("Pause");
				playButton.setOnAction(ev -> mediaPlayer.play());
				pauseButton.setOnAction(ev -> mediaPlayer.pause());
				
				HBox controls = new HBox(10, playButton, pauseButton);
				controls.setAlignment(Pos.CENTER);
				
				VBox centerContent = new VBox(10, mediaView, controls);
				centerContent.setAlignment(Pos.CENTER);
				
				root.setCenter(centerContent);
				
				VBox fullPage = new VBox (10);
				fullPage.setPadding(new Insets(10));
				
				// Search bar row (kept at top)
				HBox searchRow = new HBox(10, searchBar, searchBtn);
				searchRow.setAlignment(Pos.CENTER);

				// Add to fullPage
				fullPage.getChildren().addAll(searchRow, root);
				VBox.setVgrow(root, Priority.ALWAYS); // Let root expand
				
				ScrollPane scrollPane = new ScrollPane(fullPage);
			    scrollPane.setFitToWidth(true);
			    scrollPane.setFitToHeight(true);

				gpChrume.getChildren().clear();
				gpChrume.getChildren().add(scrollPane);

				GridPane.setHgrow(scrollPane, Priority.ALWAYS);
				GridPane.setVgrow(scrollPane, Priority.ALWAYS);
		     
			} 
			else if (input.equalsIgnoreCase("RUtify.net")) {
				stopVideo();
				gpChrume.setStyle("-fx-background-color: black;");
				gpChrume.getChildren().clear();
				
				BorderPane root = new BorderPane();
				root.setStyle("-fx-background-color: black;");
				
				//Logo and label
				Image logoImage = new Image(getClass().getResource("/assets/spotify-logo.png").toExternalForm());
				ImageView logoView = new ImageView(logoImage);
				logoView.setFitHeight(30);
				logoView.setPreserveRatio(true);
				
				Label rutifyLabel = new Label("RUtify");
				rutifyLabel.setTextFill(Color.WHITE);
				rutifyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
				
				HBox topBar = new HBox(10, logoView, rutifyLabel);
				topBar.setAlignment(Pos.CENTER_LEFT);
				topBar.setPadding(new Insets(10));
				
				root.setTop(topBar);
				
				//Audio
				Media media = new Media(getClass().getResource("/assets/PromQueen.mp3").toExternalForm());
				mediaPlayer = new MediaPlayer(media);
				
				//Slider
				Slider musicSlider = new Slider();
				musicSlider.setPrefWidth(200);
				musicSlider.setMaxWidth(400);
				musicSlider.setDisable(true);
				
				mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
					if(media.getDuration().toMillis() > 0) {
						musicSlider.setValue((newTime.toMillis() / media.getDuration().toMillis()) * 100);
					}
				});
				
				//Buttons
				Button playButton = new Button("Play");
				Button pauseButton = new Button("Pause");
				playButton.setOnAction(ev -> mediaPlayer.play());
				pauseButton.setOnAction(ev -> mediaPlayer.pause());
				
				HBox controls = new HBox(10, playButton, pauseButton);
				controls.setAlignment(Pos.CENTER);
				
				VBox centerContent = new VBox(15, musicSlider, controls);
				centerContent.setAlignment(Pos.CENTER);
				
				root.setCenter(centerContent);
				
				//Search Bar
				HBox searchRow = new HBox(10, searchBar, searchBtn);
				searchRow.setAlignment(Pos.CENTER);
				
				VBox fullPage = new VBox(10, searchRow, root);
				fullPage.setPadding(new Insets(10));
				VBox.setVgrow(root, Priority.ALWAYS);
				
				gpChrume.getChildren().add(fullPage);
				GridPane.setHgrow(fullPage, Priority.ALWAYS);
				GridPane.setVgrow(fullPage, Priority.ALWAYS);
			} 
			else if (input.equalsIgnoreCase("stockimages.net")) {
				stopVideo();
				gpChrume.setStyle("-fx-background-color: black;");
				gpChrume.getChildren().clear();
				
				VBox imageList = new VBox(20);
				imageList.setPadding(new Insets(20));
				imageList.setStyle("-fx-background-color: black;");
		
				String[] catImages = {
					"/assets/cat-image1.jpg", 
					"/assets/cat-image2.jpg", 
					"/assets/cat-image3.jpeg", 
					"/assets/cat-image4.jpeg"
				};
				
				for (String path : catImages) {
					Image image = new Image(getClass().getResourceAsStream(path));
					ImageView imageView = new ImageView(image);
					imageView.setFitWidth(300);
					imageView.setPreserveRatio(true);
					
					Button downloadButton = new Button("Download");
					downloadButton.setOnAction(ev -> {
						int count = 1;
						for (String saved: savedFiles) {
							if(saved.endsWith(".jpg")) count++;
						}
						String defaultFilename = "cat" + count + ".jpg";
						TextInputDialog dialog = new TextInputDialog(defaultFilename);
						dialog.setTitle("Save Image");
						dialog.setHeaderText("Save image");
						dialog.setContentText("Rename file:");
						
						dialog.showAndWait().ifPresent(filename -> {
							if(!filename.toLowerCase().endsWith(".jpg")) {
								filename += ".jpg";
							}
							String nameOnly = filename.replaceFirst("\\.jpg$", "");
							if (!nameOnly.matches("[a-zA-Z0-9]+")) {
								Alert alert = new Alert(Alert.AlertType.ERROR);
								alert.setTitle("File name is invalid");
								alert.setHeaderText(null);
								alert.setContentText("Filename must be alphanumeric!");
								alert.showAndWait();
							} else if(savedFiles.contains(filename)) {
								Alert alert = new Alert(Alert.AlertType.ERROR);
								alert.setTitle("File with that name already exist");
								alert.setHeaderText(null);
								alert.setContentText("A file with that name has already been made");
								alert.showAndWait();
							} else {
								savedFiles.add(filename);
								
								final String savedFilename = filename;  // ← Make effectively final
							    
								VBox shortcut = new VBox(5);
								shortcut.setAlignment(Pos.CENTER);
								
								ImageView shortcutIcon = new ImageView(new Image(getClass().getResourceAsStream(path)));
								shortcutIcon.setFitHeight(50);
								shortcutIcon.setPreserveRatio(true);
								
								Label shortcutLabel = new Label(filename);
								shortcutLabel.setStyle("-fx-text-fill: black; -fx-font-size: 10;");
								
								shortcut.getChildren().addAll(shortcutIcon, shortcutLabel);
								shortcut.setCursor(Cursor.HAND);
								
								shortcut.setOnMouseClicked(event -> {
									Stage editorStage = new Stage();
									editorStage.setTitle(savedFilename);
									
									Image imageShortcut = new Image(getClass().getResourceAsStream(path));
									ImageView imageViewShortcut = new ImageView(imageShortcut);
									imageViewShortcut.setPreserveRatio(true);
									
									StackPane sp = new StackPane(imageViewShortcut);
									sp.setAlignment(Pos.CENTER);
									
									ScrollPane scrollPane = new ScrollPane(sp);
								    scrollPane.setPannable(true);
								    scrollPane.setFitToWidth(true);
								    scrollPane.setFitToHeight(true);
								    
								    Slider zoomSlider = new Slider(0.1, 3, 1);
								    
								    zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
								        imageViewShortcut.setFitWidth(imageShortcut.getWidth() * newVal.doubleValue());
								        imageViewShortcut.setFitHeight(imageShortcut.getHeight() * newVal.doubleValue());
								    });
								    
								    Button rotateButton = new Button("Rotate");
								    rotateButton.setOnAction(events -> {
								        imageViewShortcut.setRotate((imageViewShortcut.getRotate() + 90) % 360);
								    });
								    
								    Label zoomLbl = new Label("Zoom");
								    
								    ToolBar toolBar = new ToolBar();
								    toolBar.getItems().add(zoomSlider);
								    toolBar.getItems().add(zoomLbl);
								    toolBar.getItems().add(rotateButton);
								    
								    VBox layout = new VBox(toolBar, scrollPane);
								    VBox.setVgrow(scrollPane, Priority.ALWAYS);
								    
								    Scene editorScene = new Scene(layout, 800, 600);
								    editorStage.setScene(editorScene);
								    editorStage.show();
								});
								
								addShortcutToDesktop(shortcut);
							}
						});
					});
					
					HBox imageBox = new HBox(20, imageView, downloadButton);
					imageBox.setAlignment(Pos.CENTER);
					imageBox.setPadding(new Insets(10));
					imageList.getChildren().add(imageBox);
				}
				
				ScrollPane scrollPane = new ScrollPane(imageList);
				scrollPane.setFitToWidth(true);
				scrollPane.setStyle("-fx-background:black; -fx-border-color: transparent;");
				
				GridPane.setHgrow(scrollPane, Priority.ALWAYS);
				GridPane.setVgrow(scrollPane, Priority.ALWAYS);
				
				gpChrume.getChildren().add(scrollPane);
				
			} 
			else {
				stopVideo();
				
				gpChrume.getChildren().clear();
				
			    // Search bar row
			    HBox searchRow = new HBox(10, searchBar, searchBtn);
			    searchRow.setAlignment(Pos.CENTER);
			    
			    Text domainUnfound1 = new Text("This site can't be reached");
		    	domainUnfound1.getStyleClass().add("domainUnfound1");
		    	
		    	Text domainUnfound2 = new Text("domain.com does not exist, try checking your spelling");
		    	domainUnfound2.getStyleClass().add("domainUnfound2");
		    	
			    // VBox for domain unfound labels
			    VBox domainLabels = new VBox(10, domainUnfound1, domainUnfound2);
			    domainLabels.setAlignment(Pos.CENTER);
			    
			    // Main VBox layout containing search bar at top and domain labels centered below
			    VBox layout = new VBox(20, searchRow, domainLabels);
			    layout.setAlignment(Pos.TOP_CENTER);
			    layout.setPadding(new Insets(10));
			    
			    // Allow domainLabels VBox to grow if needed
			    VBox.setVgrow(domainLabels, Priority.ALWAYS);
			    
			    gpChrume.getChildren().add(layout);
			    GridPane.setHgrow(layout, Priority.ALWAYS);
			    GridPane.setVgrow(layout, Priority.ALWAYS);
			}
    	});
    	
    	VBox layout = new VBox(hb, gpChrume);
    	VBox.setVgrow(gpChrume, Priority.ALWAYS);
    	
    	Scene chrumeScene = new Scene(layout, 800, 400);
    	chrumeScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    	chrumeStage.setTitle("ChRUme");
    	chrumeStage.setScene(chrumeScene);
    	chrumeStage.show();
    	chrumeStage.setOnCloseRequest(e -> stopVideo());
    }
}







