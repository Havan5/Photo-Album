package model;
/**
* @author: Havan Patel
*/

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class Album implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Photo> photos;
	private List<Calendar> dates;
	private String albumName;

	/**
	 * album default constructor
	 * @param albumName string
	 */
	public Album(String albumName) {
		this.albumName = albumName;
		this.photos = new ArrayList<Photo>();
		this.dates = new ArrayList<Calendar>();
	}

	/**
	 * add photo to list
	 * @param photo Photo
	 */
	public void addPhoto(Photo photo) {
		dates.add(photo.getCalendar());
		this.photos.add(photo);
	}

	/**
	 * set album name
	 * @param name string
	 */
	public void setName(String name) {
		this.albumName = name;
	}

	/**
	 * get oldest date of photo in a album
	 * @return String of date
	 */
	public String getOldestDate() {
		if (dates.size() > 0) {
			SimpleDateFormat n = new SimpleDateFormat("MM/dd/yyyy");
			return n.format(dates.get(0).getTime());
		}
		return "";
	}

	/**
	 * get earliest date of photo in a album
	 * @return String of date
	 */
	public String getEarliestDate() {
		if (dates.size() > 0) {
			SimpleDateFormat n = new SimpleDateFormat("MM/dd/yyyy");
			return n.format(dates.get(dates.size() - 1).getTime());
		}
		return "";
	}

	/**
	 * sort the dates
	 */
	public void sortDate() {
		dates.sort(new Comparator<Calendar>() {
			@Override
			public int compare(Calendar c0, Calendar c1) {
				return c0.compareTo(c1);
			}
		});
	}

	/**
	 * get all the dates
	 * @return List of calendar
	 */
	public List<Calendar> getdates() {
		return dates;
	}

	/**
	 * total phots is album
	 * @return int size
	 */
	public int totalPhotos() {
		return photos.size();
	}

	/**
	 * get Album name
	 * @return String album name
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * get all photo list in a album
	 * @return list of photo
	 */
	public List<Photo> getPhotos() {
		return photos;
	}

	/**
	 * to string for album
	 */
	public String toString() {
		return getAlbumName();
	}
}
