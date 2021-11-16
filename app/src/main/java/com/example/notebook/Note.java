package com.example.notebook;

public class Note {
    private int _id;
    private String title;
    private String content;
    private String date;
    private String author;
    private String imgP;

    public int get_id(){
        return _id;
    }

    public void set_id(int _id){
        this._id=_id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getContent() { return content; }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgP() { return imgP; }

    public void setImgP(String imgP) { this.imgP = imgP; }

    public Note(int _id,String title,String content,String date,String author,String imgP)
    {
        super();
        this._id = _id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.author = author;
        this.imgP = imgP;
    }

}
