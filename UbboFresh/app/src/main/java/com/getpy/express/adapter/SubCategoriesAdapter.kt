package com.getpy.express.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.fresh.views.home.HomeFragment
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.data.model.CategoriesExpModel
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.NavListRowBinding
import com.getpy.express.ui.account.AccountFragment
import com.getpy.express.ui.cart.CartFragment
import com.getpy.express.ui.categories.CategoriesFragment


class SubCategoriesAdapter(val preference: PreferenceProvider,var mCategoriesList:ArrayList<MainAndSubCatDataModel>,val cmodel:CategoriesExpModel ) : RecyclerView.Adapter<SubCategoriesAdapter.DeveloperViewHolder>() {
    interface OnMainSubCatClick
    {
        fun onMainSubClick(postion:Int)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<NavListRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.nav_list_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(mDeveloperViewHolder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        //mDeveloperViewHolder.mItemCategoryRowBinding.image.setImageResource(R.drawable.grocery)
        mDeveloperViewHolder.mNavListRowBinding.textview.text=model.Name
        mDeveloperViewHolder.mNavListRowBinding.textview.setOnClickListener {
            UbboFreshApp.instance?.isComingfromCatOrMen=true
            UbboFreshApp.instance?.isSubCatPos=i
            UbboFreshApp.instance?.mainAndSubCatDataModel=cmodel.mMainAndSubCatDataModel

            val map= HashMap<String, String>()
            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
            map.put("ctegoryId", UbboFreshApp.instance?.mainAndSubCatDataModel?.SubCategoryId.toString())
            map.put("categoryName",UbboFreshApp.instance?.mainAndSubCatDataModel?.Name.toString())
            Analytics.trackEvent("slide menu sub category clicked", map)

            CartFragment.naviagateRunnable?.let { Handler().postDelayed(it,10) }
            HomeFragment.runable?.let { Handler().postDelayed(it,10) }
            CategoriesFragment.runable?.let { Handler().postDelayed(it,10) }
            AccountFragment.runnable?.let { Handler().postDelayed(it,10) }

        }
    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList.size
        } else {
            0
        }
    }

    fun udateSubList(mCategoriesList: ArrayList<MainAndSubCatDataModel>) {
        this.mCategoriesList = mCategoriesList
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mNavListRowBinding:NavListRowBinding ) :
        RecyclerView.ViewHolder(mNavListRowBinding.root)
    {
        init {
            mNavListRowBinding.textview.setTypeface(UbboFreshApp.instance?.latoregular)
        }
    }
}