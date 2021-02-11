package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.data.model.MultiStoreDataModel
import com.getpy.express.databinding.MultiStoreDataRowBinding


class MultiStoreAdater(val context: Context, var list:ArrayList<MultiStoreDataModel>,val listener: OnItemClickListener): RecyclerView.Adapter<MultiStoreAdater.DeveloperViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(item: MultiStoreDataModel?)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<MultiStoreDataRowBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.multi_store_data_row, viewGroup, false
        )
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, i: Int) {
        var model = list?.get(i)
        //mDeveloperViewHolder.mDeveloperListItemBinding.mulistoreData = currentStudent
        /*val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_no_image_found)
                .error(R.drawable.ic_no_image_found)
        Glide.with(context).load(model.LogoLink).apply(options).into(holder.mBinding.imageview)*/
        holder.mBinding.imageview.load(model.LogoLink) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }
        holder.mBinding.storeName.text=model.NameofStore
        holder.mBinding.storeAddr.text=model.Address
        holder.mBinding.storePin.text=model.Pincode
    }

    override fun getItemCount(): Int {
        return if (list != null) {
            list.size
        } else {
            0
        }
    }

    fun setDeveloperList(mDeveloperModel: ArrayList<MultiStoreDataModel>) {
        this.list = mDeveloperModel
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mBinding: MultiStoreDataRowBinding) :
            RecyclerView.ViewHolder(mBinding.root)
    {
          init {
              mBinding.storeName.setTypeface(UbboFreshApp.instance?.latobold)
              mBinding.storePin.setTypeface(UbboFreshApp.instance?.latoblack)
              mBinding.storeAddr.setTypeface(UbboFreshApp.instance?.latoblack)
              mBinding.root.setOnClickListener(View.OnClickListener {
                  listener.onItemClick(list.get(adapterPosition))
              })
          }
    }
}