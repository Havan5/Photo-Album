package controller;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Photo;
import model.Tag;
import model.User;

/**
* @author: Havan Patel
*/
public class DisplayImageController {
	
	@FXML Button backward, forward, cancel, capButton, tagButton, addCap;
	@FXML ImageView imageview;
	@FXML Label date, time;
	@FXML ListView<Tag> tagList;
	@FXML Text caption;
	@FXML TextArea capArea;
	private ObservableList<Tag> obsTag;
	private List<Photo> photos;
	private User u;
	private int index;
	private int i = 0;

	/**
	 * start mehtod to initialize the fields
	 * @param u logged in
	 * @param i which photo index it is
	 * @param index which album index it is
	 */
	public void start(User u, int i, int index) {
		this.u = u;
		this.index = index;
		this.photos = u.getAlbums().get(index).getPhotos();
		this.i = i;
		updatePhotoDetails();
	}

	/**
	 * action events for the buttons
	 * @param event of buttons
	 */
	@FXML
	protected void handleButtonAction(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.equals(forward)) {
				i++;
				setVisiblity(false);
				updatePhotoDetails();

			} else if (button.equals(backward)) {
				i--;
				setVisiblity(false);
				updatePhotoDetails();
			} else if (button.equals(capButton)) {
				setVisiblity(true);
				capArea.setText(photos.get(i).getCaption());
			} else if (button.equals(cancel)) {
				setVisiblity(false);
				updatePhotoDetails();
			} else if (button.equals(addCap)) {
				photos.get(i).setCaption(capArea.getText());
				setVisiblity(false);
				updatePhotoDetails();
			}else if(button.equals(tagButton)) {
				Dialog<User> dialog = new Dialog<>();
				dialog.setTitle("Edit Tag");
				dialog.setHeaderText("Edit Tag type / Tag value");
				dialog.setResizable(true);

				Label label1 = new Label("Tag Type: ");
				Label label2 = new Label("Tag Value: ");
				TextField text1 = new TextField();
				TextField text2 = new TextField();
				
				GridPane grid = new GridPane();
				grid.setHgap(10); //horizontal gap in pixels => that's what you are asking for
				grid.setVgap(10); //vertical gap in pixels
				grid.setPadding(new Insets(10, 10, 10, 10));
				grid.add(label1, 1, 1);
				grid.add(text1, 2, 1);
				grid.add(label2, 1, 2);
				grid.add(text2, 2, 2);
				dialog.getDialogPane().setContent(grid);		

				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

				Optional<User> result = dialog.showAndWait();
				String editTagError = checkForError(text1.getText(), text2.getText());
				
				if (result.isPresent()) {
					if (editTagError.equals("Tag type/name exist")) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText("Tag type/name exist");
						alert.setContentText("Due to error tag will not be saved");
						alert.showAndWait();
					} else if(editTagError.equals("Can't set the fields to empty fields")){
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText("Can't set the fields to empty fields");
						alert.setContentText("Due to error tag will not be saved");
						alert.showAndWait();
					}else {
						photos.get(i).addTag(text1.getText(), text2.getText());
						updatePhotoDetails();
					}
				}
			}
				
		}
	}

	private void setVisiblity(boolean b) {
		capArea.setVisible(b);
		addCap.setVisible(b);
		cancel.setVisible(b);
	}

	/**
	 * update and populate the photo details
	 */
	public void updatePhotoDetails() {
		imageview.setImage(photos.get(i).getPic());
		caption.setText("Caption: " + photos.get(i).getCaption());
		date.setText("Date Taken:- " + photos.get(i).getDate());
		time.setText("Time Taken:- " + photos.get(i).getTime());
		if(u.getAlbums().get(index).getPhotos().get(i).getTags().size() != 0) {
		obsTag = FXCollections.observableArrayList(u.getAlbums().get(index).getPhotos().get(i).getTags());
		tagList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
			@Override
			public ListCell<Tag> call(ListView<Tag> param) {
				return new TagRow();
			}
		});
		tagList.setItems(obsTag);
		}else {
			tagList.getItems().clear();
		}
		backward.setDisable(i == 0);
		forward.setDisable(i == photos.size() - 1);
	}
	
	/**
	 * add button to listview of tag cells
	 * @author HP
	 *
	 */
	private class TagRow extends ListCell<Tag> {
        HBox hbox = new HBox();
		Button edit = new Button("Edit");
		Button delete = new Button("Delete");
		Label tagLabel = new Label();
		
		public TagRow() {
			super();
			tagLabel.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(tagLabel, Priority.ALWAYS);
			hbox.getChildren().addAll(tagLabel, edit, delete);
			hbox.setSpacing(5);
			edit.setVisible(false);
			delete.setVisible(false);
			setGraphic(hbox);
		}

		@Override
		protected void updateItem(Tag tag, boolean empty) {
			super.updateItem(tag, empty);
			
			if(tag != null) {
				edit.setVisible(true);
				delete.setVisible(true);
				tagLabel.setText(tag.toString());
				
				edit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						editTag(tag);
					}
				});
				
				delete.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						deleteTag(tag);
					}
				});
			}
		}
	}
	
	/**
	 * edit tag of the photo
	 * @param tag from the tag cell
	 */
	private void editTag(Tag tag) {
		Dialog<User> dialog = new Dialog<>();
		dialog.setTitle("Edit Tag");
		dialog.setHeaderText("Edit Tag type / Tag value (ex: value1, value2)");
		dialog.setResizable(true);

		Label label1 = new Label("Tag Type: ");
		Label label2 = new Label("Tag Value: ");
		TextField text1 = new TextField();
		text1.setText(tag.getTagName());

		TextField text2 = new TextField();
		text2.setText(tag.getTagValue());
		
		GridPane grid = new GridPane();
		grid.setHgap(10); //horizontal gap in pixels => that's what you are asking for
		grid.setVgap(10); //vertical gap in pixels
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.add(label1, 1, 1);
		grid.add(text1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(text2, 2, 2);
		dialog.getDialogPane().setContent(grid);
		
		

		dialog.getDialogPane().getButtonTypes().addAll( ButtonType.OK);

		Optional<User> result = dialog.showAndWait();
		String editTagError = checkForError(text1.getText(), text2.getText());
		
		if (result.isPresent()) {
			if (editTagError.equals("Can't set the fields to empty fields")) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("Can't leave the fields to empty fields");
				alert.setContentText("Due to error tag will not be saved");
				alert.showAndWait();
			} else {
				Photo p = photos.get(i);
				for (int j = 0; j < p.getTags().size(); j++) {
					if (p.getTags().get(j).equals(tag)) {
						p.getTags().get(j).setTagName(text1.getText());
						p.getTags().get(j).setTagValue(text2.getText());
					}
				}
				updatePhotoDetails();
			}
		}
	}
	
	/**
	 * check if the tag input is valid or not
	 * @param s1 tag type
	 * @param s2 tag value
	 * @return string
	 */
	private String checkForError(String s1, String s2) {
		if (s1.equals("") || s2.equals("")) {
			return "Can't set the fields to empty fields";
		}
		for (Tag t : photos.get(i).getTags()) {
			if(t.getTagName().equals(s1)) {
				return "Tag type/name exist";
			}
			if (t.getTagName().equals(s1) && t.getTagValue().equals(s2)) {
				return "Tag type/name exist";
			}
		}
		// no error found
		return "Success";
	}

	/**
	 * delete the tag from the photo and listview
	 * @param tag
	 */
	private void deleteTag(Tag tag) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this tag?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Photo p = photos.get(i);
			for(int j = 0; j < p.getTags().size(); j++) {
				if(p.getTags().get(j).equals(tag)) {
					p.getTags().remove(j);
					updatePhotoDetails();
				}
			}
		} else {
			alert.close();
		}
	}
}
