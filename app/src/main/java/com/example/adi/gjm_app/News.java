package com.example.adi.gjm_app;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adi on 4/22/2015.
 */
public class News implements Serializable {

    private String title, content, date;
    private Date formattedDate;
    private int id;

    public News(int id, String title, String content, String date)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        setFormattedDate(date);
    }

    public int getId()
    {
        return this.id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return this.title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return this.content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    //date
    public String getDate()
    {
        return this.date;
    }
    public String getLastUpdate()
    {
        return "last update : "+this.date;
    }
    public void setDate(String _date)
    {
        this.date = _date;
    }

    private void setFormattedDate(String currDate){
        DateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formattedDate = dateFormat.parse(currDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getFormattedDate(String format)
    {
        DateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
