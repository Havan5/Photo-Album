package application;

import controller.UserLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Photos extends Application {
	public static Stage primaryStage;
	@Override
	/**
	 * main method
	 */
	public void start(Stage primaryStage) {
	    Photos.primaryStage = primaryStage;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/User_Login.fxml"));
			Pane root = (Pane) loader.load();
			UserLoginController login = loader.getController();
			login.start();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Log In");
			primaryStage.setResizable(false);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
