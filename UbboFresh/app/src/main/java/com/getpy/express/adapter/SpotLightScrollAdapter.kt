package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.data.model.SpotLightModel
import com.getpy.express.databinding.SpotListRowBinding

class SpotLightScrollAdapter(var context:Context,var mCategoriesList:ArrayList<SpotLightModel>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<SpotLightScrollAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<SpotListRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.spot_list_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(mDeveloperViewHolder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        mDeveloperViewHolder.mBinding.textview.text=model.name
    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList.size
        } else {
            0
        }
    }



    inner class DeveloperViewHolder(var mBinding: SpotListRowBinding) :
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