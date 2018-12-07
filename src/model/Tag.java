package model;

import java.io.Serializable;

/**
 * @author: Havan Patel
 */
public class Tag implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tagName; // tagType
	private String tagValue; // tagDescription

	/**
	 * default constructor
	 * @param tagType string
	 * @param tagDescription string
	 */
	public Tag(String tagType, String tagDescription) {
		this.tagName = tagType;
		this.tagValue = tagDescription;
	}

	/**
	 * set the tag type
	 * @param tagType string
	 */
	public void setTagName(String tagType) {
		this.tagName = tagType;
	}

	/**
	 * set the tag value
	 * @param tagDescription tag value string
	 */
	public void setTagValue(String tagDescription) {
		this.tagValue = tagDescription;
	}

	/**
	 * get tag name
	 * @return string
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * get tag value
	 * @return string
	 */
	public String getTagValue() {
		return tagValue;
	}

	/**
	 * to string for tags
	 */
	public String toString() {
		return getTagName() + " : " + getTagValue();
	}
}
