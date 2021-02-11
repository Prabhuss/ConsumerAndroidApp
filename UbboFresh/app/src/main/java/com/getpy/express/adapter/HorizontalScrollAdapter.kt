package com.getpy.express.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.databinding.HorizontalListRowBinding

class HorizontalScrollAdapter(var mCategoriesList:ArrayList<MainAndSubCatDataModel> ,val itemClickListener: ItemClickListener) : RecyclerView.Adapter<HorizontalScrollAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<HorizontalListRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.horizontal_list_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(mDeveloperViewHolder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        //mDeveloperViewHolder.mItemCategoryRowBinding.image.setImageResource(R.drawable.grocery)
        if(model.isSelectedSubTab!!)
        {
            mDeveloperViewHolder.mBinding.textview.setTextColor(Color.WHITE)
            mDeveloperViewHolder.mBinding.layout.setBackgroundResource(R.drawable.rounded_corner_selected)
        }else
        {
            mDeveloperViewHolder.mBinding.textview.setTextColor(Color.BLACK)
            mDeveloperViewHolder.mBinding.layout.setBackgroundResource(R.drawable.rounded_corner)
        }
        mDeveloperViewHolder.mBinding.textview.text=model.Name
    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList.size
        } else {
            0
        }
    }

    fun setDeveloperList(mCategoriesList: ArrayList<MainAndSubCatDataModel>) {
        this.mCategoriesList = mCategoriesList
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mBinding: HorizontalListRowBinding) :
        RecyclerView.ViewHolder(mBinding.root)
    {
        init {
            mBinding.textview.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.root.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(mBinding.root,adapterPosition)
            })
        }
    }
}