package com.example.book.ui.Model;

import com.example.book.ui.extra.Enums;

public class Post {
    private String bookName;
    private String bookPrice;
    private String imageUrl;
    private String author;
    private String description;
    private String condition;
    private Enums.BookCategory bookCategory;

    private String uploadDate;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageUpload.class)
    }

    public Post(String bookName, String bookPrice, String imageUrl, String author, String description, String condition, String uploadDate, Enums.BookCategory bookCategory) {
        this.bookName = bookName;
        this.bookPrice = bookPrice;
        this.imageUrl = imageUrl;
        this.author = author;
        this.description = description;
        this.condition = condition;
        this.uploadDate = uploadDate; // Set the upload date to the current date
        this.bookCategory = bookCategory;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getCondition() {
        return condition;
    }


    public String getUploadDate() {
        return uploadDate;
    }

    public Enums.BookCategory getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(Enums.BookCategory bookCategory) {
        this.bookCategory = bookCategory;
    }
}
