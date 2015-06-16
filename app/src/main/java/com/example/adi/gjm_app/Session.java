package com.example.adi.gjm_app;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Adi on 5/11/2015.
 */
public class Session implements Serializable {
    private String Username, baseUrl;
    private int updateStatus; //0:need update; 1:unknown; 2:no-need update
    private int userId;

    //search and filter
    private ArrayList filterKey = new ArrayList();
    private ArrayList searchKey = new ArrayList();
    private String[] filterParam = {"merek"};
    private String[] searchParam = {"itemcode", "name"};

    public String getUsername()
    {
        return this.Username;
    }
    public void setUsername(String _username)
    {
        this.Username = _username;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public int getUpdateStatus()
    {
        return this.updateStatus;
    }
    public void setUpdateStatus(int _updateStatus)
    {
        this.updateStatus = _updateStatus;
    }

    public ArrayList getFilterKey() {
        return filterKey;
    }
    public void setFilterKey(ArrayList filterKey) {
        this.filterKey = filterKey;
    }
    public ArrayList getSearchKey() {
        return searchKey;
    }
    public void setSearchKey(ArrayList searchKey) {
        this.searchKey = searchKey;
    }

    //build query
    public String buildSearchQuery()
    {
        String query = "(";
        for(int i=0; i<this.searchKey.size(); i++)
        {
            if(i>0)
            {
                query += " or ";
            }

            for(int j=0; j<searchParam.length; j++) {
                if(j>0)
                {
                    query += " or ";
                }

                query += searchParam[j] +" like '%" + searchKey.get(i) + "%'";
            }
        }
        query += ")";

        return query;
    }
    public String buildFilterQuery()
    {
        String query = "(";
        for(int i=0; i<this.filterKey.size(); i++)
        {
            if(i>0)
            {
                query += " or ";
            }

            for(int j=0; j<filterParam.length; j++) {
                if(j>0)
                {
                    query += " or ";
                }

                query += filterParam[j] +" like '" + filterKey.get(i) + "'";
            }
        }
        query += ")";

        return query;
    }

    //reset
    public void resetSearch()
    {
        this.searchKey = new ArrayList();
    }
    public void resetFilter()
    {
        this.filterKey = new ArrayList();
    }
    public void resetSearchFilter()
    {
        resetFilter();
        resetSearch();
    }

}
