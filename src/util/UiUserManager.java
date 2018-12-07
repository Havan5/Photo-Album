package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import controller.UserLoginController;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.User;

/**
 * @author: Havan Patel, and TPULLIS TRAVIS B
 */
public class UiUserManager {
	private List<User> users;
	private List<String> usersName;
	private User userData;
	public static final String storeDir = "data";

	/**
	 * Constructor to initialize the fields
	 */
	public UiUserManager(){
		users = new ArrayList<User>();
		usersName = new ArrayList<String>();
		userData = new User("");
	}

	/**
	 * Changes view of the screen user want to go to
	 * @param fxmlView fxml view 
	 * @param event get the action event
	 * @param viewTitle title of the window
	 * @param root get the parent the scene
	 * @param w width of the window
	 * @param h height of the window
	 * @throws Exception if saving fails
	 */
	public void changeView(String fxmlView, ActionEvent event, String viewTitle, Parent root, double w, double h)
			throws Exception {
		Scene s = new Scene(root);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((screenBounds.getWidth() - w) / 2);
		stage.setY((screenBounds.getHeight() - h) / 2);
		stage.setScene(s);
		stage.setTitle(viewTitle);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (fxmlView.equals("Admin_Login.fxml")) {
					try {
						save();
						saveUserInAdmin();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (fxmlView.equals("User_Album.fxml")) {
					try {
						save(UserLoginController.user);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * Show the error pop-up message
	 * @param message type
	 */
	public void showMessageDialog(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * add user to list
	 * @param user you want to add to list
	 */
	public void addUsers(User user) {
		this.users.add(user);
	}

	/**
	 * get the list of users
	 * @return list of users
	 */
	public List<User> getUsers() {
		return users;
	}
	
	/**
	 * get the usernames from the admin file
	 * @return list of usernames
	 */
	public List<String> getUsersName() {
		return usersName;
	}
		
	/**
	 * check to see if user is already in admin
	 * @param user name
	 * @return true or false
	 */
	public boolean equal(String user) {
		for(String u : usersName) {
			if(u.equalsIgnoreCase(user)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * read the specific user data
	 * @return user
	 */
	public User getUserData() {
		return userData;
	}

	/**
	 * make separate file for each user for saving
	 * @throws Exception saving fails to file
	 */
	public void save() throws Exception {
		for (int i = 0; i < users.size(); i++) {
			ObjectOutputStream oo = new ObjectOutputStream(
					new FileOutputStream(storeDir + File.separator + users.get(i).getUsername()));
			oo.writeObject(users.get(i));
			oo.close();
		}
	}

	/**
	 * save usernames to admin file
	 * @throws Exception saving fails to a admin file
	 */
	public void saveUserInAdmin() throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + "admin"));
		oos.writeObject(getUsersName());
		oos.close();
	}

	/**
	 * read the list of username string from admin file
	 * @throws Exception if can't read a file
	 */
	@SuppressWarnings("unchecked")
	public void read() throws Exception {
		File file = new File(storeDir + "/" + "admin");
		if (file.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			usersName = (List<String>) ois.readObject();
			ois.close();
		}
	}
	
	/**
	 * read the user data from the user file
	 * @param s which user file to read
	 * @return user data
	 * @throws Exception if can't read the user file
	 */
	public User readUserData(String s) throws Exception {
		File file = new File(storeDir + "/" + s);
		if (file.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			userData = (User) ois.readObject();
			ois.close();
		}
		return userData;
	}
	
	/**
	 * save data in user's file
	 * @param u get user name to save in user's file
	 * @throws Exception if can't save to user file
	 */
	public void save(User u) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + u.getUsername()));
		oos.writeObject(u);
		oos.flush();
		oos.close();
	}

	/**
	 * remove user and its data from user file and admin file
	 * @param index of listview to delete user file
	 */
	public void deleteUserFile(int index) {
		File file = new File(storeDir + "/" + getUsersName().get(index));
		file.delete();
		getUsersName().remove(index);
	}
}
