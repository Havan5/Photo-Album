package controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;

/**
* @author: Havan Patel
*/
public class SearchController {
	
	@FXML ComboBox<String> tagType, tagValue;
	@FXML Button addToList, searchByDate, addResult, search;
	@FXML TableView<Tag> tableView;
	@FXML TableColumn<Tag, String> typeCol, valCol;
	@FXML TableColumn<Tag, Tag> removeCol;
	@FXML DatePicker dateFrom, dateTo;
	@FXML ScrollPane scrollPane;
	@FXML TilePane tilePane = new TilePane();
	@FXML Text dateerror;
	@FXML Label caption;
	private ObservableList<Tag> ObsList;
	private User u;
	private List<Tag> tags;
	private List<String> tagT;
	private List<String> tagV;
	private List<Photo> tempPhotoList = new ArrayList<>();
	private int imageHighlighted = -1;


	/**
	 * start method to initialize the fields
	 * @param user logged in
	 */
	public void start(User user) {
		this.u = user;
		tags = new ArrayList<Tag>();
		tagT = new ArrayList<String>();
		tagV = new ArrayList<String>();
		ObsList = FXCollections.observableArrayList(tags);

		for (int j = 0; j < u.getAlbums().size(); j++) {
			for (int k = 0; k < u.getAlbums().get(j).getPhotos().size(); k++) {
				for (int l = 0; l < u.getAlbums().get(j).getPhotos().get(k).getTags().size(); l++) {
					if (!tagT.contains(u.getAlbums().get(j).getPhotos().get(k).getTags().get(l).getTagName())
							&& !tagV.contains(u.getAlbums().get(j).getPhotos().get(k).getTags().get(l).getTagValue())) {
						tagT.add(u.getAlbums().get(j).getPhotos().get(k).getTags().get(l).getTagName());
						tagV.add(u.getAlbums().get(j).getPhotos().get(k).getTags().get(l).getTagValue());
					}
				}
			}
		}
		updateList();
	}

	/**
	 * default initialize methods called by javafx fxml
	 */
	@FXML
	protected void Initialize() {
		if(tilePane.getChildren().isEmpty()) {
			caption.setText("Caption: ");
		}
	}
	
	/**
	 * handle all the action event for the buttons
	 * @param event of buttons
	 */
	@FXML
	protected void handleButtonAction(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.equals(addToList)) {
				String type = tagType.getSelectionModel().getSelectedItem();
				String value = tagValue.getSelectionModel().getSelectedItem();
				if (tagType.getSelectionModel().getSelectedItem() == null
						|| tagValue.getSelectionModel().getSelectedItem() == null) {
					dateerror.setVisible(true);
					dateerror.setFill(Color.RED);
					dateerror.setText("To add you must select both tag. Try again");
					return;
				}

				for(Tag t : tags) {
					if(t.getTagName().equals(type) && t.getTagValue().equals(value)) {
						dateerror.setVisible(true);
						dateerror.setFill(Color.RED);
						dateerror.setText("can't add same tag twice. Try again");
						return;
					}
				}
				dateerror.setVisible(false);
				Tag temp = new Tag(type, value);
				ObsList.add(temp);
				tags.add(temp);
				tagType.getSelectionModel().clearSelection();
				tagValue.getSelectionModel().clearSelection();
				updateList();
			} else if(button.equals(search)) {
				tempPhotoList.clear();
				tilePane.getChildren().clear();
				Photo p = null;
				for(int j = 0; j < u.getAlbums().size(); j++) {
					for(int k = 0; k < u.getAlbums().get(j).getPhotos().size(); k++) {
						for(int l = 0; l < u.getAlbums().get(j).getPhotos().get(k).getTags().size(); l++) {
							for(Tag current : tableView.getItems()) {								
								if(current.getTagName().equals(u.getAlbums().get(j).getPhotos().get(k).getTags().get(l).getTagName())&&
										current.getTagValue().equals(u.getAlbums().get(j).getPhotos().get(k).getTags().get(l).getTagValue())) {
									if(tempPhotoList.contains(u.getAlbums().get(j).getPhotos().get(k))) {
										continue;
									}else {			
										p = u.getAlbums().get(j).getPhotos().get(k);
										tempPhotoList.add(p);
									}
								}
							}
						}
					}
				}
				if(p != null) {
					showImages(tempPhotoList);
				}else {
					caption.setText("Caption: ");
				}
			}else if(button.equals(searchByDate)) {
				tempPhotoList.clear();
				tilePane.getChildren().clear();
				if(dateFrom.getValue() == null || dateTo.getValue() == null || dateFrom.getValue().isAfter(dateTo.getValue())) {
					dateerror.setVisible(true);
					dateerror.setFill(Color.RED);
					dateerror.setText("Incorrect date input try again");
					caption.setText("Caption: ");
					return;
				}
				LocalDate localDateFrom = dateFrom.getValue();
				LocalDate localDateto = dateTo.getValue();
				Date df = Date.from(localDateFrom.atStartOfDay(ZoneId.systemDefault()).toInstant());	
				Date dt = Date.from(localDateto.atStartOfDay(ZoneId.systemDefault()).toInstant());
				dateerror.setVisible(false);
				for(Photo p : getAllPhotos()) {
					if(df.compareTo(p.getCalendar().getTime()) * p.getCalendar().getTime().compareTo(dt) >= 0) {
						tempPhotoList.add(p);
					}
				}
				
				if(tempPhotoList != null) {
				showImages(tempPhotoList);	
				}
			}else if(button.equals(addResult)) {
				if(!tilePane.getChildren().isEmpty() || !tempPhotoList.isEmpty()) {
					dateerror.setVisible(true);
					dateerror.setFill(Color.BLUE);
					dateerror.setText("Album created successfully");
					Random rand = new Random(); 
					int r1 = rand.nextInt(50); 
					int r2 = rand.nextInt(50);
					Album al = new Album("Album created from search " + r1 + "" + r2);
					for(Photo p : tempPhotoList) {
						Photo p1 = new Photo(p.getImage());
						for (Tag tag : p.getTags()) {
							p1.setTag(tag);
						}
						p1.setCaption(p.getCaption());
						p1.setDate(new Date(p.getImage().lastModified()));
						al.addPhoto(p1);
					}
					u.addAlbum(al);
				}else {
					dateerror.setVisible(true);
					dateerror.setFill(Color.RED);
					dateerror.setText("Can't create album. Result is empty");
				}
			} 
		}
	}
	
	/**
	 * return all the list of photo in the albums
	 * @return list of photos
	 */
	public List<Photo> getAllPhotos() {
		List<Photo> photos = new ArrayList<Photo>();
		List<Album> albums = u.getAlbums();
		for (int a = 0; a < albums.size(); a++) {
			for (Photo p : albums.get(a).getPhotos()) {
				if (!photos.contains(p)) {
					photos.add(p);
				}
			}
		}
		return photos;
	}

	/**
	 * show images from the search results
	 * @param list of photos from result
	 */
	private void showImages(List<Photo> list) {
		Image[] image = new Image[list.size()];
		ImageView[] pic = new ImageView[list.size()];
		GridPane[] gridpane = new GridPane[list.size()];
		tilePane.setPrefColumns(3);
		tilePane.setPadding(new Insets(5.5, 5.5, 5.5, 5.5));
		tilePane.setVgap(5.5);
		tilePane.setHgap(5.5);
		for (int i = 0; i < list.size(); i++) {
			image[i] = list.get(i).getPic();
			pic[i] = new ImageView();
			pic[i].setFitWidth(scrollPane.getPrefWidth() - 5);
			pic[i].setFitHeight(scrollPane.getPrefHeight() / 2 + 5);
			gridpane[i] = new GridPane();
			gridpane[i].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (imageHighlighted == -1) {
					} else {
						gridpane[imageHighlighted].setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0), 0, 0, 0, 0);");
					}
					String s = tempPhotoList.get(tilePane.getChildren().indexOf(event.getSource())).getCaption();
					caption.setText("Caption: " + s);
					scrollPane.setStyle("-fx-focus-color: transparent;");
					((GridPane) tilePane.getChildren().get(tilePane.getChildren().indexOf(event.getSource())))
							.setStyle("-fx-background-color: #42f4d9;"
									+ "-fx-effect: dropshadow(gaussian, rgba(32, 246, 8,1), 5,5, 0, 0);");
					imageHighlighted = tilePane.getChildren().indexOf(event.getSource());
				}
			});
			pic[i].setImage(image[i]);
			gridpane[i].add(pic[i], 0, 0);
		}
		tilePane.getChildren().clear();
		tilePane.getChildren().addAll(gridpane);
		scrollPane.setContent(tilePane);
	}

	/**
	 * update the list of search result accordingly. 
	 * refreshes the pages 
	 */
	private void updateList() {
		tagType.setItems(FXCollections.observableArrayList(tagT));
		tagValue.setItems(FXCollections.observableArrayList(tagV));

		typeCol.setCellValueFactory(new Callback<CellDataFeatures<Tag, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Tag, String> t) {
				return new SimpleStringProperty(t.getValue().getTagName());
			}
		});

		valCol.setCellValueFactory(new Callback<CellDataFeatures<Tag, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Tag, String> t) {
				return new SimpleStringProperty(t.getValue().getTagValue());
			}
		});

		removeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		removeCol.setCellFactory(param -> new TableCell<Tag, Tag>() {
			private final Button deleteButton = new Button("Remove");

			@Override
			protected void updateItem(Tag tag, boolean empty) {
				super.updateItem(tag, empty);
				if (tag == null) {
					setGraphic(null);
					return;
				}
				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> {
					ObsList.remove(tag);
					tags.remove(tag);
				});
			}
		});
		tableView.setItems(ObsList);
		tableView.refresh();
	}
	
	
}
