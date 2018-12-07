package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Havan Patel
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private List<Album> albums;

	/**
	 * user constructor
	 * @param username string
	 */
	public User(String username) {
		this.username = username;
		albums = new ArrayList<Album>();
	}

	/**
	 * 
	 * @return return user name
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * set username
	 * @param s string
	 */
	public void setUsername(String s) {
		username = s;
	}

	/**
	 * add album to list
	 * @param album get album to add to list
	 */
	public void addAlbum(Album album) {
		albums.add(album);
	}

	/**
	 * get the album the user wants
	 * @param albumName String
	 * @return album
	 */
	public Album getAlbum(String albumName) {
		for (Album album : albums) {
			if (album.getAlbumName().equals(albumName))
				return album;
		}
		return null;
	}
	
	/**
	 * check to see there is no duplicate album
	 * @param album get album
	 * @return bool 
	 */
	public boolean contains(Album album) {
		for(int i = 0; i < albums.size();i++) {
			if(albums.get(i).getAlbumName().equals(album.getAlbumName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * return list of albums
	 * @return list of albums
	 */
	public List<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * to string method
	 */
	public String toString() {
		return getUsername();
	}
}
