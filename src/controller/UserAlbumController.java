package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import application.Photos;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import util.UiUserManager;

/**
 * @author: Havan Patel, and TPULLIS TRAVIS B
 */
public class UserAlbumController {

	@FXML Button addImage, removeImage, logout, removeAlbum, renameAlbum, createAlbum, change, cancel, search;
	@FXML ListView<Album> albumListview;
	@FXML TextField albumName;
	@FXML ScrollPane scrollPane;
	@FXML Label userLable, fieldLable, albumSelected, totalPhotos, oldestPhoto, earliestPhoto, caption;
	@FXML TilePane tilePane = new TilePane();
	@FXML ComboBox<Album> moveCombo, copyCombo;
	private ObservableList<Album> myComboBoxData;
	private ObservableList<Album> obsAlbum;
	private List<Photo> tempPhotoList = new ArrayList<>();
	private User user;
	private UiUserManager handler;
	public Image tempImg;
	private int imageHighlighted, k, deleteIndex;

	/**
	 * start method to initialize the fields
	 * @param handler from login class
	 */
	public void start(UiUserManager handler) {
		this.handler = handler;
	}
	
	/**
	 * defalut javafx fxml method
	 */
	@FXML
	protected void initialize() {
		this.user = UserLoginController.user;
		userLable.setText(user.getUsername());
		updateUserAlbum();
		albumListview.getSelectionModel().selectFirst();
		if(user.getAlbums().size() == 0) {
			setRRII(false);
			setComboVisibility(false);
			removeImage.setVisible(false);
		}
		copyCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int index = albumListview.getSelectionModel().getSelectedIndex();
				Album selection = copyCombo.getSelectionModel().getSelectedItem();
				if (selection != null) {
					Photo p = new Photo(user.getAlbums().get(index).getPhotos().get(deleteIndex).getImage());
					for (Tag tag : user.getAlbums().get(index).getPhotos().get(deleteIndex).getTags()) {
						p.setTag(tag);
					}
					p.setCaption(user.getAlbums().get(index).getPhotos().get(deleteIndex).getCaption());
					p.setDate(new Date(
							user.getAlbums().get(index).getPhotos().get(deleteIndex).getImage().lastModified()));
					selection.addPhoto(p);
					updateUserAlbum();
				} else {
					return;
				}
			}
		});
		moveCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int index = albumListview.getSelectionModel().getSelectedIndex();
				Album selection = moveCombo.getSelectionModel().getSelectedItem();
				if (selection != null) {
					Photo p = new Photo(user.getAlbums().get(index).getPhotos().get(deleteIndex).getImage());
					for (Tag tag : user.getAlbums().get(index).getPhotos().get(deleteIndex).getTags()) {
						p.setTag(tag);
					}
					p.setCaption(user.getAlbums().get(index).getPhotos().get(deleteIndex).getCaption());
					p.setDate(new Date(
							user.getAlbums().get(index).getPhotos().get(deleteIndex).getImage().lastModified()));
					selection.addPhoto(p);
					user.getAlbums().get(index).getPhotos().remove(deleteIndex);
					user.getAlbums().get(index).getdates().remove(deleteIndex);
					tilePane.getChildren().clear();
					updateUserAlbum();
				} else {
					return;
				}
			}
		});
	}

	/**
	 * handle all the action event for the buttons
	 * @param event of buttons
	 */
	@FXML
	protected void handleButtonAction(ActionEvent event) throws Exception {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.equals(logout)) {
				logout(event);
			} else if (button.equals(createAlbum)) {
				if (!albumName.getText().isEmpty()) {
					Album album = new Album(albumName.getText());
					if (user.contains(album)) {
						Dialog("Album already exist.", 2);
						albumName.clear();
					} else {
						user.addAlbum(album);
						updateUserAlbum();
						albumListview.getSelectionModel().select(user.getAlbums().size() - 1);
						albumListview.refresh();
						albumListview.requestFocus();
						albumName.clear();
					}
				}
			} else if (button.equals(removeAlbum)) {
				Dialog("Are you sure you want to delete this album?", 1);
			} else if (button.equals(renameAlbum)) {
				fieldLable.setText("Rename Album:");
				albumName.setPromptText("Rename Album");
				setButtonVisibility(true);
			}else if (button.equals(cancel)) {
				fieldLable.setText("Add New Album:");
				albumName.clear();
				albumName.setPromptText("Album Name");
				createAlbum.setText("create");
				setButtonVisibility(false);
			}else if (button.equals(change)) {
				renameAlbum();
			}else if(button.equals(addImage)) {
				chooseImage(event);
			}else if(button.equals(removeImage)) {
				int index = albumListview.getSelectionModel().getSelectedIndex();
				showImages(index);
				user.getAlbums().get(index).getPhotos().remove(deleteIndex);
				user.getAlbums().get(index).getdates().remove(deleteIndex);
				tilePane.getChildren().clear();
				updateUserAlbum();
			}else if(button.equals(search)) {
				displaySearch("Search Image");
			}
		}
	}	

	/**
	 * choose image from the file
	 * @param event which button was clicked
	 * @throws Exception
	 */
	private void chooseImage(ActionEvent event) throws Exception {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Images");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		fc.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg", "*.gif"));
		//TODO Change this photo picker to one at a time
		File fileList = fc.showOpenDialog(stage);
		int index = albumListview.getSelectionModel().getSelectedIndex();
		if (fileList != null) {				
				Photo photo = new Photo(fileList);
				photo.setDate(new Date(fileList.lastModified()));
				user.getAlbums().get(index).addPhoto(photo);
		}
		showImages(index);
	}

	/**
	 * show the selected image to tilepane
	 * @param index which album it is
	 * @throws Exception
	 */
	private void showImages(int index) throws Exception {
		tempPhotoList.clear();
		tempPhotoList.addAll(user.getAlbums().get(index).getPhotos());
		Image[] image = new Image[tempPhotoList.size()];
		ImageView[] pic = new ImageView[tempPhotoList.size()];
		GridPane[] gridpane = new GridPane[tempPhotoList.size()];
		tilePane.setPrefColumns(3);
		tilePane.setPadding(new Insets(5.5, 5.5, 5.5, 5.5));
		tilePane.setVgap(5.5);
		tilePane.setHgap(5.5);
		for(int i = 0; i < tempPhotoList.size(); i++) {
			image[i] = tempPhotoList.get(i).getPic();
			pic[i] = new ImageView();
			pic[i].setFitWidth(scrollPane.getPrefWidth() / 3 - 13);
			pic[i].setFitHeight(scrollPane.getPrefHeight() / 3- 13);
			gridpane[i] = new GridPane();
			gridpane[i].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (imageHighlighted == -1) {
					} else {
						gridpane[imageHighlighted].setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0), 0, 0, 0, 0);");
					}

					if (event.getClickCount() == 2) {
						copyCombo.getSelectionModel().clearSelection();
						moveCombo.getSelectionModel().clearSelection();
						try {
							k = tilePane.getChildren().indexOf(event.getSource());
							displayImage("Image Display", k, index);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						setComboVisibility(true);
						removeImage.setDisable(false);			
						String s = tempPhotoList.get(tilePane.getChildren().indexOf(event.getSource())).getCaption();
						if(!s.equals("")) {
							caption.setVisible(true);
							caption.setText("Selected Photo Caption:- " + s);
						}else {
							caption.setVisible(false);							
						}
						copyCombo.getSelectionModel().clearSelection();
						moveCombo.getSelectionModel().clearSelection();
						deleteIndex = tilePane.getChildren().indexOf(event.getSource());
						scrollPane.setStyle("-fx-focus-color: transparent;");
						((GridPane) tilePane.getChildren().get(tilePane.getChildren().indexOf(event.getSource())))
								.setStyle("-fx-background-color: #C20020;"
										+ "-fx-effect: dropshadow(gaussian, rgba(32, 246, 8,1), 5,5, 0, 0);");
					}
					imageHighlighted = tilePane.getChildren().indexOf(event.getSource());					
				}
			});
			pic[i].setImage(image[i]);
			gridpane[i].add(pic[i],0,0);
			removeImage.setVisible(true);
		}
		if (imageHighlighted != -1) {
			setComboVisibility(true);
			String s = tempPhotoList.get(imageHighlighted).getCaption();
			if(!s.equals("")) {				
				caption.setVisible(true);
				caption.setText("Selected Photo Caption:- " + s);
			}else {
				caption.setVisible(false);
			}
			gridpane[imageHighlighted].setStyle("-fx-background-color: #42f4d9;"
					+ "-fx-effect: dropshadow(gaussian, rgba(32, 246, 8,1), 5,5, 0, 0);");
		}
		tilePane.getChildren().clear();
		tilePane.getChildren().addAll(gridpane);
		scrollPane.setContent(tilePane);
		user.getAlbums().get(index).sortDate();
		totalPhotos.setText("Total Photos:- " + tempPhotoList.size());
		oldestPhoto.setText("Oldest Photo:- " + user.getAlbums().get(index).getOldestDate());
		earliestPhoto.setText("Earliest Photo:- " + user.getAlbums().get(index).getEarliestDate());
	}
	
	/**
	 * display image in separete window
	 * @param title of window
	 * @param i which image index clicked on
	 * @param index which albums index it is
	 * @throws Exception
	 */
	private void displayImage(String title, int i, int index) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Image_Display.fxml"));
		Parent root = loader.load();
		DisplayImageController dic = loader.getController();
		Scene s = new Scene(root);
		Stage stage = new Stage();
		dic.start(user, i, index);
		stage.setScene(s);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				if (albumListview.getSelectionModel().getSelectedIndex() == -1) {
					try {
						showImages(albumListview.getSelectionModel().getSelectedIndex());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					viewList(albumListview.getSelectionModel().getSelectedIndex());
				}
			}
		});
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(Photos.primaryStage);
		stage.setResizable(false);
		stage.setTitle(title);
		stage.show();
	}

	/**
	 * display search window
	 * @param title of window
	 * @throws Exception
	 */
	private void displaySearch(String title) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Search_Display.fxml"));
		Parent root = loader.load();
		SearchController ds= loader.getController();
		Scene s = new Scene(root);
		Stage stage = new Stage();
		ds.start(user);
		stage.setScene(s);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				updateUserAlbum();
			}
		});
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(Photos.primaryStage);
		stage.setResizable(false);
		stage.setTitle(title);
		stage.show();
	}

	/**
	 * update the users listview of albums and other nodes
	 */
	private void updateUserAlbum() {
		obsAlbum = FXCollections.observableArrayList(user.getAlbums());
		albumListview.setItems(obsAlbum);
		albumListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Album>() {
			@Override
			public void changed(ObservableValue<? extends Album> observable, Album oldValue, Album newValue) {
				int index = albumListview.getSelectionModel().getSelectedIndex();
				removeImage.setDisable(true);
				caption.setVisible(false);
				setComboVisibility(false);
				imageHighlighted = -1;
				try {
					viewList(index);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});	
		myComboBoxData = FXCollections.observableArrayList(user.getAlbums());
		moveCombo.setItems(myComboBoxData);
		copyCombo.setItems(myComboBoxData);
		albumListview.refresh();
	}
	
	/**
	 * combo box visibility
	 * @param b to show or not
	 */
	private void setComboVisibility(boolean b) {
		moveCombo.setVisible(b);
		copyCombo.setVisible(b);
	}
	
	/**
	 * view the list of photos based on which album was clicked
	 * @param index of album clicked
	 */
	public void viewList(int index) {
		int size = 0;
		if (index < 0) {
			setRRII(false);
			removeImage.setVisible(false);
			albumSelected.setText("");
			totalPhotos.setText("");
			oldestPhoto.setText("");
			earliestPhoto.setText("");
			caption.setText("");
			return;
		}
		setRRII(true);
		albumSelected.setText("Album Selected:- " + user.getAlbums().get(index).getAlbumName());
		size = user.getAlbums().get(index).getPhotos().size();
		totalPhotos.setText("Total Photos:- " + size);
		if (size == 0) {
			tilePane.getChildren().clear();
			caption.setText("");
			caption.setVisible(false);
			removeImage.setVisible(false);
			setComboVisibility(false);
			oldestPhoto.setText("");
			earliestPhoto.setText("");
			return;
		} else {
			removeImage.setVisible(true);
			user.getAlbums().get(index).sortDate();
			oldestPhoto.setText("Oldest Photo:- " + user.getAlbums().get(index).getOldestDate());
			earliestPhoto.setText("Earliest Photo:- " + user.getAlbums().get(index).getEarliestDate());
			try {
				showImages(index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * rename the albums
	 */
	private void renameAlbum() {
		int index = albumListview.getSelectionModel().getSelectedIndex();
		if (!albumName.getText().isEmpty()) {
			Album newAl = new Album(albumName.getText());
			if (user.contains(newAl)) {
				try {
					Dialog("Album already exist.", 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				albumName.clear();
			} else {
//				List<Photo> p = new ArrayList<>();
//				p.addAll(albumListview.getSelectionModel().getSelectedItem().getPhotos());
//				user.getAlbums().remove(index);
//				newAl.getPhotos().addAll(p);
//				user.getAlbums().add(index, newAl);
				user.getAlbums().get(index).setName(albumName.getText());
				fieldLable.setText("Add New Album:");
				albumName.setPromptText("Album Name");
				createAlbum.setText("create");
				setButtonVisibility(false);
				albumName.clear();
				myComboBoxData.clear();
				myComboBoxData = FXCollections.observableArrayList(user.getAlbums());
				moveCombo.setItems(myComboBoxData);
				copyCombo.setItems(myComboBoxData);
				updateUserAlbum();
				albumListview.getSelectionModel().select(index);
			}
		}
	}

	/**
	 * pop up dialog for message
	 * @param message type
	 * @param select which dialog type you want 1) for removing album 2) pop up for duplicate album
	 * @throws Exception
	 */
	private void Dialog(String message, int select) throws Exception {
		if (select == 2) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
			return;
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText(null);
			alert.setContentText(message);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				if (select == 1) {
					deleteAlbum();
				}
			} else {
				alert.close();
			}
		}
	}

	/**
	 * remove album from the list
	 * @throws Exception
	 */
	private void deleteAlbum() throws Exception {
		int index = albumListview.getSelectionModel().getSelectedIndex();
		user.getAlbums().remove(index);
		updateUserAlbum();
		tilePane.getChildren().clear();
		if (index < 0)
			return;
		if (user.getAlbums().size() == 1) {
			albumListview.getSelectionModel().select(0);
		} else if (user.getAlbums().size() == index) {
			albumListview.getSelectionModel().select(index - 1);
		} else {
			albumListview.getSelectionModel().select(index);
		}
	}

	/**
	 * button visibility
	 * @param b to show or now
	 */
	private void setButtonVisibility(boolean b) {
		createAlbum.setVisible(!b);
		change.setVisible(b);
		cancel.setVisible(b);
		removeAlbum.setDisable(b);
	}

	/**
	 * visibility of nodes
	 * @param b to show or now
	 */
	private void setRRII(boolean b) {
		addImage.setVisible(b);
		removeAlbum.setVisible(b);
		renameAlbum.setVisible(b);
	}

	/**
	 * logout from user
	 * @param event of logout button
	 * @throws Exception
	 */
	private void logout(ActionEvent event) throws Exception {
		handler.save(user);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/User_Login.fxml"));
		Parent root = loader.load();
		UserLoginController login = loader.getController();
		login.start();
		handler.changeView("User_Login.fxml", event, "User Login", root, 400, 400);
	}
}
