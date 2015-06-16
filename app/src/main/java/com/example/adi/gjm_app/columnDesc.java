package com.example.adi.gjm_app;

/**
 * Created by Adi on 6/8/2015.
 */
public class columnDesc {

    public boolean primaryKey=false;
    public String name;
    public String type;
    public int length = 0;

    public columnDesc(String _name, String _type)
    {
        this.name = _name;
        this.type = _type;
    }

    public columnDesc(String _name, String _type, boolean _primary)
    {
        this.primaryKey = _primary;
        this.name = _name;
        this.type = _type;
    }
}
