package com.commanderalchemy.myeconomy.database;

import java.util.Date;

import android.graphics.Bitmap;

/**
 * Datatable for Transactions.
 * @author Artur Olech
 *
 */
public class Transaction {
	
	// Columns
	private Long 		id;
	private String 		userID;
	private String		type;
	private Category 	category;
	private String 		title;
	private String		barCode;
	private Date 		date;
	private Bitmap  	img;
	private Double 		amount;
	

	/**
	 * Return Transaction ID
	 * @return
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Set Transaction ID
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Get UserID
	 * @return
	 */
	public String getUserID() {
		return userID;
	}
	
	/**
	 * Set UserID
	 * @param userID
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	/**
	 * Get Transaction Type (Income/Expense)
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Set Transaction Type (Income/Expense)
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Get Transaction Category
	 * @return
	 */
	public Category getCategory() {
		return category;
	}
	
	/**
	 * Set Transaction Category
	 * @param category
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	
	/**
	 * Get Transaction Title
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set Transaction Title
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get Transaction BarCode
	 * @return
	 */
	public String getBarCode() {
		return barCode;
	}
	
	/**
	 * Set Transaction BarCode
	 * @param barCode
	 */
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	
	/**
	 * Get Transaction Date
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Set Transaction Date
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Get Transaction Bitmap
	 * @return
	 */
	public Bitmap getImg() {
		return img;
	}
	
	/**
	 * Set Transaction Bitmap
	 * @param img
	 */
	public void setImg(Bitmap img) {
		this.img = img;
	}
	
	/**
	 * Get Transaction Amount
	 * @return
	 */
	public Double getAmount() {
		return amount;
	}
	
	/**
	 * Set Transaction Amount
	 * @param amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
