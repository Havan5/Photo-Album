package model;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.scene.image.Image;

/**
 * @author: Havan Patel
 */
public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Tag> tags;
	private String caption;
	private Calendar date;
	private File photo;

	/**
	 * photo default constructor
	 * @param photo file
	 */
	public Photo(File photo) {
		this.photo = photo;
		tags = new ArrayList<Tag>();
		date = Calendar.getInstance();
		date.set(Calendar.MILLISECOND, 0);
		caption = "";
	}

	/**
	 * set caption to photo
	 * @param caption string
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * add tag to list
	 * @param tag Tag
	 */
	public void setTag(Tag tag) {
		this.tags.add(tag);
	}

	/**
	 * set date to photo
	 * @param date Date
	 */
	public void setDate(Date date) {
		this.date.setTime(date);
	}

	/**
	 * add tag to list
	 * @param s1 tag type string
	 * @param s2 tag value string
	 */
	public void addTag(String s1, String s2) {
		tags.add(new Tag(s1, s2));
	}

	/**
	 * return image
	 * @return Image from file
	 */
	public Image getPic() {
		return new Image(photo.toURI().toString());
	}

	/**
	 * get caption of photo
	 * @return caption as string
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * 
	 * @return tags list
	 */
	public List<Tag> getTags() {
		return tags;
	}
	
	/**
	 * return file of image
	 * @return File of image
	 */
	public File getImage() {
		return photo;
	}

	/**
	 * get calendar of photo
	 * @return Calendar
	 */
	public Calendar getCalendar() {
		return date;
	}

	/**
	 * get Date of photo
	 * @return String date
	 */
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(date.getTime());
	}

	/**
	 * get time of photo
	 * @return String time
	 */
	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
		return sdf.format(date.getTime());
	}
}
