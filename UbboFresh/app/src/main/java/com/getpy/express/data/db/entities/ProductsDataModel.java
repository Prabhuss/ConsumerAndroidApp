package com.getpy.express.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductsDataModel {

    public boolean isReloadCalled() {
        return isReloadCalled;
    }

    public void setReloadCalled(boolean reloadCalled) {
        isReloadCalled = reloadCalled;
    }

    public boolean isReloadCalled;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String status;
    private String message;

    public String getMobileNumber() {
        return PrimaryPhone;
    }

    public void setMobileNumber(String PrimaryPhone) {
        this.PrimaryPhone = PrimaryPhone;
    }

    public String PrimaryPhone;


    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    private int itemCount;
    private String Category;

    private String CreatedBY;

    private String VisibilityStatus;

    private String QuantityList;

    private String StockQuantity;

    private String discount;

    private String ModifiedDate;

    private String productName;

    private String BrandName;

    private String ImageLinkFlag;

    private String OfferCode;

    private String productOwner;

    private String CreeatedDate;

    private String Availability_Status;

    private String GST;

    private String mrp;

    private String cess;
    @PrimaryKey
    @NonNull
    private String CitrineProdId;

    private String mproductid;

    private String SellingPrice;

    private String productDesc;

    private String UOM;

    private String SubCategory;

    private String IsDelete;

    private String ProductIdId;

    private String productPicUrl;

    private String MerchantBranchId;

    public String getCategory ()
    {
        return Category;
    }

    public void setCategory (String Category)
    {
        this.Category = Category;
    }

    public String getCreatedBY ()
    {
        return CreatedBY;
    }

    public void setCreatedBY (String CreatedBY)
    {
        this.CreatedBY = CreatedBY;
    }

    public String getVisibilityStatus ()
    {
        return VisibilityStatus;
    }

    public void setVisibilityStatus (String VisibilityStatus)
    {
        this.VisibilityStatus = VisibilityStatus;
    }

    public String getQuantityList ()
    {
        return QuantityList;
    }

    public void setQuantityList (String QuantityList)
    {
        this.QuantityList = QuantityList;
    }

    public String getStockQuantity ()
    {
        return StockQuantity;
    }

    public void setStockQuantity (String StockQuantity)
    {
        this.StockQuantity = StockQuantity;
    }

    public String getDiscount ()
    {
        return discount;
    }

    public void setDiscount (String discount)
    {
        this.discount = discount;
    }

    public String getModifiedDate ()
    {
        return ModifiedDate;
    }

    public void setModifiedDate (String ModifiedDate)
    {
        this.ModifiedDate = ModifiedDate;
    }

    public String getProductName ()
    {
        return productName;
    }

    public void setProductName (String productName)
    {
        this.productName = productName;
    }

    public String getBrandName ()
    {
        return BrandName;
    }

    public void setBrandName (String BrandName)
    {
        this.BrandName = BrandName;
    }

    public String getImageLinkFlag ()
    {
        return ImageLinkFlag;
    }

    public void setImageLinkFlag (String ImageLinkFlag)
    {
        this.ImageLinkFlag = ImageLinkFlag;
    }

    public String getOfferCode ()
    {
        return OfferCode;
    }

    public void setOfferCode (String OfferCode)
    {
        this.OfferCode = OfferCode;
    }

    public String getProductOwner ()
    {
        return productOwner;
    }

    public void setProductOwner (String productOwner)
    {
        this.productOwner = productOwner;
    }

    public String getCreeatedDate ()
    {
        return CreeatedDate;
    }

    public void setCreeatedDate (String CreeatedDate)
    {
        this.CreeatedDate = CreeatedDate;
    }

    public String getAvailability_Status ()
    {
        return Availability_Status;
    }

    public void setAvailability_Status (String Availability_Status)
    {
        this.Availability_Status = Availability_Status;
    }

    public String getGST ()
    {
        return GST;
    }

    public void setGST (String GST)
    {
        this.GST = GST;
    }

    public String getMrp ()
    {
        return mrp;
    }

    public void setMrp (String mrp)
    {
        this.mrp = mrp;
    }

    public String getCess ()
    {
        return cess;
    }

    public void setCess (String cess)
    {
        this.cess = cess;
    }

    public String getCitrineProdId ()
    {
        return CitrineProdId;
    }

    public void setCitrineProdId (String CitrineProdId)
    {
        this.CitrineProdId = CitrineProdId;
    }

    public String getMproductid ()
    {
        return mproductid;
    }

    public void setMproductid (String mproductid)
    {
        this.mproductid = mproductid;
    }

    public String getSellingPrice ()
    {
        return SellingPrice;
    }

    public void setSellingPrice (String SellingPrice)
    {
        this.SellingPrice = SellingPrice;
    }

    public String getProductDesc ()
    {
        return productDesc;
    }

    public void setProductDesc (String productDesc)
    {
        this.productDesc = productDesc;
    }

    public String getUOM ()
    {
        return UOM;
    }

    public void setUOM (String UOM)
    {
        this.UOM = UOM;
    }

    public String getSubCategory ()
    {
        return SubCategory;
    }

    public void setSubCategory (String SubCategory)
    {
        this.SubCategory = SubCategory;
    }

    public String getIsDelete ()
    {
        return IsDelete;
    }

    public void setIsDelete (String IsDelete)
    {
        this.IsDelete = IsDelete;
    }

    public String getProductIdId ()
    {
        return ProductIdId;
    }

    public void setProductIdId (String ProductIdId)
    {
        this.ProductIdId = ProductIdId;
    }

    public String getProductPicUrl ()
    {
        return productPicUrl;
    }

    public void setProductPicUrl (String productPicUrl)
    {
        this.productPicUrl = productPicUrl;
    }

    public String getMerchantBranchId ()
    {
        return MerchantBranchId;
    }

    public void setMerchantBranchId (String MerchantBranchId)
    {
        this.MerchantBranchId = MerchantBranchId;
    }

}
