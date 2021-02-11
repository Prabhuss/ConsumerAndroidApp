package com.getpy.express.data.model;

import com.getpy.express.data.db.entities.ProductsDataModel;

import java.util.ArrayList;

public class ProductsResponse {
    private ArrayList<ProductsDataModel> data;

    private String heading;

    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public ArrayList<ProductsDataModel> getData ()
    {
        return data;
    }

    public void setData (ArrayList<ProductsDataModel> data)
    {
        this.data = data;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", status = "+status+"]";
    }
}
