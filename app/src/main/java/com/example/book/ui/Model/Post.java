package com.example.book.ui.Model;

import com.example.book.ui.extra.Enums;

import java.util.List;

public class Post {
    private String bookName;
    private String bookPrice;
    private String imageUrl;

    private List<String> authors;
    private String description;
    private String condition;
    private Enums.BookCategory bookCategory;


    private Enums.PostType postType;

    private String uploadDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageUpload.class)
    }

    public Post(String bookName, String bookPrice, String imageUrl, List<String> authors, String description, String condition, String uploadDate, Enums.BookCategory bookCategory,String userId, Enums.PostType post_type) {
        this.userId = userId;
        this.bookName = bookName;
        this.bookPrice = bookPrice;
        this.imageUrl = imageUrl;
        this.authors = authors;
        this.description = description;
        this.condition = condition;
        this.uploadDate = uploadDate; // Set the upload date to the current date
        this.bookCategory = bookCategory;
        this.postType = post_type;
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
    public Enums.PostType getPostType() {
        return postType;
    }

    public void setPostType(Enums.PostType postType) {
        this.postType = postType;
    }
    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
