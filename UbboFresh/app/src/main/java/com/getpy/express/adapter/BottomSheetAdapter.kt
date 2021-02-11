package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.R
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.databinding.BottomSheetRowBinding


class BottomSheetAdapter(val context: Context, var mCategoriesList:ArrayList<ProductsDataModel> ) : RecyclerView.Adapter<BottomSheetAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<BottomSheetRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.bottom_sheet_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(mHolder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)

    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            1
        } else {
            1
        }
    }

    fun udateSubList(mCategoriesList: ArrayList<ProductsDataModel>) {
        this.mCategoriesList = mCategoriesList
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mNavListRowBinding:BottomSheetRowBinding ) :
        RecyclerView.ViewHolder(mNavListRowBinding.root)
    {
        init {

        }
    }
}