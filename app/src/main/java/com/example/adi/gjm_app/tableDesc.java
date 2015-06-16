package com.example.adi.gjm_app;

import java.util.ArrayList;

/**
 * Created by Adi on 6/8/2015.
 */
public class tableDesc {

    public String name;
    public ArrayList<columnDesc> column;

    public tableDesc(String _name)
    {
        this.name = _name;
        this.column = new ArrayList<columnDesc>();
    }

    public String queryBuilderCreateTable()
    {
        String query = "CREATE TABLE "+this.name+" (";
        for(int i=0; i<this.column.size(); i++)
        {
            if(this.column.get(i).primaryKey) {
                query += this.column.get(i).name + " " + this.column.get(i).type+" PRIMARY KEY AUTOINCREMENT NOT NULL";
            }else{
                query += this.column.get(i).name + " " + this.column.get(i).type;
            }

            if(i<this.column.size()-1)
            {
                query += ", ";
            }
        }
        query += ")";

        return query;
    }

    public String getPrimaryColumnName()
    {
        String result = "";
        for(int i=0; i<this.column.size(); i++) {
            if(this.column.get(i).primaryKey)
                result = this.column.get(i).name;
        }
        return result;
    }
}
