package com.getpy.fresh.adaters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.adapter.HomeCategoriesGridAdapter
import com.getpy.express.adapter.SubCategoryAdapter
import com.getpy.express.data.model.CategoriesExpModel
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragCatExpandRowBinding
import com.getpy.express.ui.categories.CategoriesFragment


class CategoriesExandAdapter(val preference: PreferenceProvider,val context: Context,
                             var mlist:ArrayList<CategoriesExpModel>) : RecyclerView.Adapter<CategoriesExandAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int):ViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<FragCatExpandRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.frag_cat_expand_row, viewGroup, false)
        return ViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(viewHolder:ViewHolder, i: Int) {
        val model = mlist.get(i)
        var url:String
        if(model.mMainAndSubCatDataModel?.ImageLinkFlag!=null) {
            if (model.mMainAndSubCatDataModel?.ImageLinkFlag.equals("R")) {
                url = UbboFreshApp.instance?.imageLoadUrl + model.mMainAndSubCatDataModel?.PicURL.toString()
            } else {
                url = model.mMainAndSubCatDataModel?.PicURL.toString()
            }
        }else
        {
            if(model.mMainAndSubCatDataModel?.PicURL!=null)
            {
                url = UbboFreshApp.instance?.imageLoadUrl + model.mMainAndSubCatDataModel?.PicURL.toString()
            }else
            {
                url = UbboFreshApp.instance?.imageLoadUrl.toString()
            }
        }
        viewHolder.mFragCatExpandRowBinding.imageview.load(url) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }
        viewHolder.mFragCatExpandRowBinding.textview.text=model.mMainAndSubCatDataModel?.Name
        //adapter= model.list?.let { HomeCategoriesGridAdapter(context,it) }
        //viewHolder.mFragCatExpandRowBinding.expandRecyclerview.adapter=adapter
        viewHolder.mFragCatExpandRowBinding.expandRecyclerview.itemAnimator=null
        //viewHolder.mFragCatExpandRowBinding.expandRecyclerview.setHasFixedSize(true)
        viewHolder.mFragCatExpandRowBinding.expandRecyclerview.layoutManager=
                LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        val adapter= model.list?.let { SubCategoryAdapter(preference,context, it) }
        viewHolder.mFragCatExpandRowBinding.expandRecyclerview.adapter=adapter
        val isExpandable=model.Expandable
        if(isExpandable)
        {
            viewHolder.mFragCatExpandRowBinding.expandLayout.visibility=  View.VISIBLE
            viewHolder.mFragCatExpandRowBinding.headerView.cardElevation= 0F

        }else
        {
            viewHolder.mFragCatExpandRowBinding.headerView.cardElevation= 10F
            viewHolder.mFragCatExpandRowBinding.expandLayout.visibility=  View.GONE
        }
        viewHolder.mFragCatExpandRowBinding.headerView.setOnClickListener(View.OnClickListener {
            val cmodel = mlist.get(i)
            if(cmodel.list!=null) {
                if (cmodel.list?.size!! > 0) {
                    cmodel.Expandable = !cmodel.Expandable
                    //setDynamicHeight(viewHolder.mFragCatExpandRowBinding.expandRecyclerview,3)
                    notifyItemChanged(i)
                } else {
                    UbboFreshApp.instance?.mainAndSubCatDataModel=cmodel.mMainAndSubCatDataModel

                    val map=HashMap<String,String>()
                    map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                    map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                    map.put("ProductId", UbboFreshApp.instance?.mainAndSubCatDataModel?.SubCategoryId.toString())
                    map.put("ProductName", UbboFreshApp.instance?.mainAndSubCatDataModel?.Name.toString())
                    Analytics.trackEvent("Category clicked", map)

                    CategoriesFragment.runable?.let { Handler().postDelayed(it,10) }
                    //MainActivity.binding.coordinateLayout.snakBar("No Subcategories")
                }
            }else
            {
                UbboFreshApp.instance?.mainAndSubCatDataModel=cmodel.mMainAndSubCatDataModel

                val map=HashMap<String,String>()
                map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                map.put("ProductId", UbboFreshApp.instance?.mainAndSubCatDataModel?.SubCategoryId.toString())
                map.put("ProductName", UbboFreshApp.instance?.mainAndSubCatDataModel?.Name.toString())
                Analytics.trackEvent("Category clicked", map)

                CategoriesFragment.runable?.let { Handler().postDelayed(it,10) }
                //MainActivity.binding.coordinateLayout.snakBar("No Subcategories")
            }

        })
    }
    private fun setDynamicHeight(gridView: GridView,noOfColumns:Int) {
        val gridViewAdapter = gridView.adapter
            ?: // adapter is not set yet
            return

        var totalHeight: Int //total height to set on grid view

        val items = gridViewAdapter.count //no. of items in the grid

        val rows: Int //no. of rows in grid


        val listItem = gridViewAdapter.getView(0, null, gridView)
        listItem.measure(0, 0)
        totalHeight = listItem.measuredHeight

        val x: Float
            if (items > noOfColumns) {
                x = (items / noOfColumns).toFloat()

                //Check if exact no. of rows of rows are available, if not adding 1 extra row
                rows = if (items % noOfColumns !== 0) {
                    (x + 1).toInt()
                } else {
                    x.toInt()
                }
                totalHeight *= rows
                //Adding any vertical space set on grid view
                totalHeight += gridView.verticalSpacing * rows
            }

        //Setting height on grid view

        //Setting height on grid view
        val params = gridView.layoutParams
        params.height = totalHeight
        gridView.layoutParams = params

    }

    override fun getItemCount(): Int {
        return if (mlist != null) {
            mlist.size
        } else {
            0
        }
    }

    fun notifyChange(list: ArrayList<MainAndSubCatDataModel>) {
        //this.msublist = mCategoriesList
        notifyDataSetChanged()
    }

    inner class ViewHolder(var mFragCatExpandRowBinding: FragCatExpandRowBinding) :
        RecyclerView.ViewHolder(mFragCatExpandRowBinding.root)
    {
        init {
            mFragCatExpandRowBinding.textview.setTypeface(UbboFreshApp.instance?.latoregular)
        }
    }
    companion object
    {
        var adapter:HomeCategoriesGridAdapter?=null
    }
}