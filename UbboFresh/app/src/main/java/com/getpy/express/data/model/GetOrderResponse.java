package com.getpy.express.data.model;

public class GetOrderResponse {
    private DataResonse data;

    private String status;

    public DataResonse getData ()
    {
        return data;
    }

    public void setData (DataResonse data)
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
