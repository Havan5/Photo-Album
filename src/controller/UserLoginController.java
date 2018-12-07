package controller;


import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Album;
import model.User;
import util.UiUserManager;

/**
 * @author: Havan Patel, and TPULLIS TRAVIS B
 */
public class UserLoginController {

	@FXML Button login;
	@FXML TextField username;
	public static User user;
	public static UiUserManager handler;
	
	/**
	 * start method to initialize the fields
	 */
	public void start() {
		if(handler == null) {
			handler = new UiUserManager();
		}
		try {
			handler.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		user = null;
	}

	/**
	 * handle all the action event for the buttons
	 * @param event of buttons
	 */
	@FXML
	protected void handleButtonAction(ActionEvent event) throws Exception {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.equals(login)) {
				if (username.getText().isEmpty()) {
					handler.showMessageDialog("Enter valid username");
				} else {
					if (username.getText().equals("admin")) {
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin_Login.fxml"));
							Parent root = loader.load();
							AdminLoginController adminLog = loader.getController();
							adminLog.start(handler);
							handler.changeView("Admin_Login.fxml", event, "Admin Login", root, 300, 550);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						for (String u : handler.getUsersName()) {
							if (u.equals(username.getText())) {
								user = handler.readUserData(u);
								break;
							}
						}
						if (user == null) {
							handler.showMessageDialog("Invalid User. Try Again");
							return;
						}
						//if file is deleted remove the photo as well
//						for(int i = 0; i < user.getAlbums().size(); i++) {
//							for(int j = 0; j < user.getAlbums().get(i).getPhotos().size(); j++) {
//								if(user.getAlbums().get(i).getPhotos().get(j).getPic() == null) {
//									user.getAlbums().get(i).getPhotos().remove(j);
//								}
//							}
//						}
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/User_Album.fxml"));
							Parent root = loader.load();
							UserAlbumController userAlbum = loader.getController();
							userAlbum.start(handler);
							handler.changeView("User_Album.fxml", event, "User Album", root, 874, 600);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
