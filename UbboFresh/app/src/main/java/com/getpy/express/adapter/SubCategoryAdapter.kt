package com.getpy.express.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ItemCategoryRowBinding
import com.getpy.express.ui.categories.CategoriesFragment

class SubCategoryAdapter(
        val preference: PreferenceProvider,val context: Context,
        var mCategoriesList:ArrayList<MainAndSubCatDataModel>) : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int):ViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<ItemCategoryRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_category_row, viewGroup, false)
        return ViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(viewHolder:ViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        var url:String
        if(model.ImageLinkFlag!=null) {
            if (model.ImageLinkFlag.equals("R")) {
                url = UbboFreshApp.instance?.imageLoadUrl + model.PicURL.toString()
            } else {
                url = model.PicURL.toString()
            }
        }else
        {
            if(model.PicURL!=null)
            {
                url = UbboFreshApp.instance?.imageLoadUrl + model.PicURL.toString()
            }else
            {
                url = UbboFreshApp.instance?.imageLoadUrl.toString()
            }
        }
        viewHolder.mBinding.image.load(url) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }
        viewHolder.mBinding.textview.text=model?.Name

    }
    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList.size
        } else {
            0
        }
    }
    inner class ViewHolder(var mBinding: ItemCategoryRowBinding) :
        RecyclerView.ViewHolder(mBinding.root)
    {
        init {
            mBinding.textview.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.image.setOnClickListener(View.OnClickListener {
                UbboFreshApp.instance?.isComingfromCatOrMen=true
                UbboFreshApp.instance?.isSubCatPos=adapterPosition
                UbboFreshApp.instance?.mainAndSubCatDataModel=mCategoriesList.get(adapterPosition)

                val map=HashMap<String,String>()
                map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                map.put("ProductId", UbboFreshApp.instance?.mainAndSubCatDataModel?.SubCategoryId.toString())
                map.put("ProductName", UbboFreshApp.instance?.mainAndSubCatDataModel?.Name.toString())
                Analytics.trackEvent("SubCategory clicked", map)

                CategoriesFragment.runable?.let { Handler().postDelayed(it,10) }
            })
        }
    }
    companion object{
        var adapter:SubCategoriesAdapter?=null
    }
}