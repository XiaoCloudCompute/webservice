package edu.xiao.webservice.bean;

import edu.xiao.webservice.model.Picture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

public class PictureResponseBean {
    String file_name;
    UUID id;
    String url;
    String upload_date;
    String user_id;

    static public PictureResponseBean createBeanFromPicture(Picture picture) {
        PictureResponseBean res = new PictureResponseBean();
        res.setId(picture.getId());
        res.setFile_name(picture.getFileName());
        res.setUrl(picture.getUrl());
        res.setUser_id(picture.getUserId());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        res.setUpload_date(df.format(picture.getUpdateDate()));
        return res;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
