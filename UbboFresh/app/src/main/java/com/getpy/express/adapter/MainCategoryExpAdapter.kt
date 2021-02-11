package com.getpy.express.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.fresh.views.home.HomeFragment
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.Utils.toast
import com.getpy.express.data.model.CategoriesExpModel
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.MainCategoriesRowBinding
import com.getpy.express.ui.account.AccountFragment
import com.getpy.express.ui.cart.CartFragment
import com.getpy.express.ui.categories.CategoriesFragment

class MainCategoryExpAdapter(
        val preference: PreferenceProvider,val context: Context,
        var mCategoriesList:ArrayList<CategoriesExpModel>) : RecyclerView.Adapter<MainCategoryExpAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int):ViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<MainCategoriesRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.main_categories_row, viewGroup, false)
        return ViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(viewHolder:ViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        viewHolder.mBinding.textview.text=model.mMainAndSubCatDataModel?.Name
        adapter= model.list?.let { SubCategoriesAdapter(preference,it,model) }
        viewHolder.mBinding.expandRecyclerview.setHasFixedSize(true)
        val manager=LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        viewHolder.mBinding.expandRecyclerview.setLayoutManager(manager)
        viewHolder.mBinding.expandRecyclerview.adapter=adapter
        val isExpandable=model.Expandable
        if(isExpandable)
        {

            viewHolder.mBinding.expandLayout.visibility=  View.VISIBLE

        }else
        {
            viewHolder.mBinding.expandLayout.visibility=  View.GONE
        }
        viewHolder.mBinding.headerView.setOnClickListener(View.OnClickListener {
            val cmodel = mCategoriesList.get(i)
            if(cmodel.list!=null) {
                if(cmodel.list!!.size>0) {
                    cmodel.Expandable = !cmodel.Expandable
                    notifyItemChanged(i)
                }else
                {
                    UbboFreshApp.instance?.mainAndSubCatDataModel=cmodel.mMainAndSubCatDataModel

                    val map= HashMap<String, String>()
                    map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                    map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                    map.put("ctegoryId", UbboFreshApp.instance?.mainAndSubCatDataModel?.SubCategoryId.toString())
                    map.put("categoryName",UbboFreshApp.instance?.mainAndSubCatDataModel?.Name.toString())
                    Analytics.trackEvent("slide menu category clicked", map)


                    HomeFragment.runable?.let { Handler().postDelayed(it,10) }
                    CategoriesFragment.runable?.let { Handler().postDelayed(it,10) }
                    AccountFragment.runnable?.let { Handler().postDelayed(it,10) }
                    CartFragment.naviagateRunnable?.let { Handler().postDelayed(it,10) }
                    //context.toast("No SubCategories")
                }
            }else
            {
                UbboFreshApp.instance?.mainAndSubCatDataModel=cmodel.mMainAndSubCatDataModel

                val map= HashMap<String, String>()
                map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                map.put("ctegoryId", UbboFreshApp.instance?.mainAndSubCatDataModel?.SubCategoryId.toString())
                map.put("categoryName",UbboFreshApp.instance?.mainAndSubCatDataModel?.Name.toString())
                Analytics.trackEvent("slide menu sub category1 clicked", map)

                HomeFragment.runable?.let { Handler().postDelayed(it,10) }
                CategoriesFragment.runable?.let { Handler().postDelayed(it,10) }
                AccountFragment.runnable?.let { Handler().postDelayed(it,10) }
                CartFragment.naviagateRunnable?.let { Handler().postDelayed(it,10) }
                //context.toast("No SubCategories")
            }
        })
    }
    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList.size
        } else {
            0
        }
    }

    public fun notifyChange(list: ArrayList<MainAndSubCatDataModel>,position:Int) {
        if(list.size>0)
        {

        }else
        {
            context.toast("No Subcategories")
        }
        //this.mCategoriesList = mCategoriesList
        //notifyDataSetChanged()
    }

    inner class ViewHolder(var mBinding: MainCategoriesRowBinding) :
        RecyclerView.ViewHolder(mBinding.root)
    {
        init {
            mBinding.textview.setTypeface(UbboFreshApp.instance?.latoregular)
        }
    }
    companion object{
        var adapter:SubCategoriesAdapter?=null
    }
}