package controller;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.User;
import util.UiUserManager;

/**
 * @author: Havan Patel
 */
public class AdminLoginController {

	@FXML Button logout, removeUser, addUser, cancel, save;
	@FXML ListView<String> userList;
	@FXML TextField username;
	@FXML Label usernameLable, addNewUserLable;
	@FXML Stage stage;
	@FXML Pane pane;
	private ObservableList<String> obsList;
	private UiUserManager handler;
	
	/**
	 * to initialize the fields
	 * @param handler from login class
	 */
	public void start(UiUserManager handler) {
		this.handler = handler;
		try {
			populateList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userList.getSelectionModel().selectFirst();
		if (userList.getItems().size() == 0) {
			removeUser.setDisable(true);
		}
	}
	
	/**
	 * all the action event for the button
	 * @param event of buttons
	 * @throws Exception
	 */
	@FXML
	protected void handleButtonAction(ActionEvent event) throws Exception {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.equals(addUser)) {
				makeButtonsVisible(true);
			} else if (button.equals(cancel)) {
				makeButtonsVisible(false);
			} else if (button.equals(removeUser)) {
				removeDialog("Are you sure you want to remove this user?");
			} else if (button.equals(save)) {
				addUser();
			} else if (button.equals(logout)) {
				handler.save();
				handler.saveUserInAdmin();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/User_Login.fxml"));
				Parent root = loader.load();
				UserLoginController login = loader.getController();
				login.start();
				handler.changeView("User_Login.fxml", event, "User Login", root, 400, 400);
			}
		}
	}
	
	/**
	 * add new user to admin
	 * @throws Exception
	 */
	private void addUser() throws Exception {
		if (username.getText().isEmpty()) {
			handler.showMessageDialog("Enter valid username");
		} else {
			User user = new User(username.getText());

			if (handler.equal(user.getUsername())) {
				handler.showMessageDialog("Username already exist");
				username.clear();
			} else {
				handler.addUsers(user);
				handler.getUsersName().add(user.getUsername());
				populateList();
				userList.getSelectionModel().selectLast();
				makeButtonsVisible(false);
				removeUser.setDisable(false);
			}
		}
	}

	/**
	 * delete user from admin and delete user's file
	 * @throws Exception
	 */
	private void deleteUser() throws Exception {
		int index = userList.getSelectionModel().getSelectedIndex();
		handler.deleteUserFile(index);
		populateList();
		if (index < 0) {
			removeUser.setDisable(true);
			return;
		}
		if(userList.getItems().size() == 0) {
			removeUser.setDisable(true);
		}
		if (userList.getItems().size() == 1) {
			userList.getSelectionModel().select(0);
		} else if (userList.getItems().size() == index) {
			userList.getSelectionModel().select(index - 1);
		} else {
			userList.getSelectionModel().select(index);
		}
	}

	/**
	 * populate the listview of usernames
	 * @throws Exception
	 */
	private void populateList() throws Exception {
		obsList = FXCollections.observableArrayList(handler.getUsersName());
		userList.setItems(obsList);
		userList.refresh();
	}

	/**
	 * when to make buttons visible
	 * @param bool to make buttons visible or not
	 */
	public void makeButtonsVisible(boolean bool) {
		removeUser.setVisible(!bool);
		addUser.setVisible(!bool);
		save.setVisible(bool);
		cancel.setVisible(bool);
		usernameLable.setVisible(bool);
		addNewUserLable.setVisible(bool);
		username.setVisible(bool);
		username.clear();
	}

	/**
	 * pop up dialog when removing a user
	 * @param message type
	 * @throws Exception if can't delete the file
	 */
	public void removeDialog(String message) throws Exception {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText(message);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			deleteUser();
		} else {
			alert.close();
		}
	}

}
