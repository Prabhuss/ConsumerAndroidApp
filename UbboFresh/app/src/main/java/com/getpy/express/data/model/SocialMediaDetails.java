package com.getpy.express.data.model;

public class SocialMediaDetails {
    private String PlatformName;

    private String Createdby;

    private String PlatformURL;

    private String CreatedDate;

    private String id;

    private String ModifiedDate;

    private String MerchantBranchId;

    public String getPlatformName ()
    {
        return PlatformName;
    }

    public void setPlatformName (String PlatformName)
    {
        this.PlatformName = PlatformName;
    }

    public String getCreatedby ()
    {
        return Createdby;
    }

    public void setCreatedby (String Createdby)
    {
        this.Createdby = Createdby;
    }

    public String getPlatformURL ()
    {
        return PlatformURL;
    }

    public void setPlatformURL (String PlatformURL)
    {
        this.PlatformURL = PlatformURL;
    }

    public String getCreatedDate ()
    {
        return CreatedDate;
    }

    public void setCreatedDate (String CreatedDate)
    {
        this.CreatedDate = CreatedDate;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getModifiedDate ()
    {
        return ModifiedDate;
    }

    public void setModifiedDate (String ModifiedDate)
    {
        this.ModifiedDate = ModifiedDate;
    }

    public String getMerchantBranchId ()
    {
        return MerchantBranchId;
    }

    public void setMerchantBranchId (String MerchantBranchId)
    {
        this.MerchantBranchId = MerchantBranchId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [PlatformName = "+PlatformName+", Createdby = "+Createdby+", PlatformURL = "+PlatformURL+", CreatedDate = "+CreatedDate+", id = "+id+", ModifiedDate = "+ModifiedDate+", MerchantBranchId = "+MerchantBranchId+"]";
    }
}
