package com.getpy.fresh.adaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.databinding.ItemCategoryGridrowBinding

class CategoriesGridAdapter(var mCategoriesList:ArrayList<MainAndSubCatDataModel> ) : RecyclerView.Adapter<CategoriesGridAdapter.DeveloperViewHolder>() {
    var onItemClick: ((pos: Int, view: View) -> Unit)? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<ItemCategoryGridrowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_category_gridrow, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(mDeveloperViewHolder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        mDeveloperViewHolder.mItemCategoryGridrowBinding.image.setImageResource(R.mipmap.ic_launcher_new2)
        mDeveloperViewHolder.mItemCategoryGridrowBinding.textview.text=model.Name
    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList!!.size
        } else {
            0
        }
    }

    fun updateList(mCategoriesList: ArrayList<MainAndSubCatDataModel>) {
        this.mCategoriesList = mCategoriesList
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mItemCategoryGridrowBinding: ItemCategoryGridrowBinding) :
        RecyclerView.ViewHolder(mItemCategoryGridrowBinding.root), View.OnClickListener
    {
        override fun onClick(v: View) {
            onItemClick?.invoke(adapterPosition, v)
        }
        init {
            itemView.setOnClickListener(this)
            mItemCategoryGridrowBinding.textview.setTypeface(UbboFreshApp.instance?.latoregular)
        }
    }
}