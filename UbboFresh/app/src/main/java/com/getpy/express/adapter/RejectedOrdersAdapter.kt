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
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.databinding.MyOrdersRowBinding

class RejectedOrdersAdapter(val context: Context, var mCategoriesList:ArrayList<CustomerInvoiceData>,val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RejectedOrdersAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<MyOrdersRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.my_orders_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        holder.mItemCategoryRowBinding.image.setImageResource(R.drawable.ic_myorder_default_img)
        holder.mItemCategoryRowBinding.textview1.text="Order ID:"+model.InvoiceId
        holder.mItemCategoryRowBinding.textview2.text=model.CreatedDate
        holder.mItemCategoryRowBinding.textview3.text="Amount:"+model.PayableAmount
        holder.mItemCategoryRowBinding.textview4.text="Payment Mode:"+model.PaymentMode
        holder.mItemCategoryRowBinding.textview5.text="Status:"+model.OrderStatus

    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList!!.size
        } else {
            0
        }
    }

    fun setDeveloperList(mCategoriesList: ArrayList<CustomerInvoiceData>) {
        this.mCategoriesList = mCategoriesList
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mItemCategoryRowBinding: MyOrdersRowBinding) :
        RecyclerView.ViewHolder(mItemCategoryRowBinding.root)
    {

        init {
            mItemCategoryRowBinding.textview1.setTypeface(UbboFreshApp.instance?.latoregular)
            mItemCategoryRowBinding.textview2.setTypeface(UbboFreshApp.instance?.latoregular)
            mItemCategoryRowBinding.textview3.setTypeface(UbboFreshApp.instance?.latoregular)
            mItemCategoryRowBinding.textview4.setTypeface(UbboFreshApp.instance?.latoregular)
            mItemCategoryRowBinding.textview5.setTypeface(UbboFreshApp.instance?.latoregular)
            mItemCategoryRowBinding.root.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(mItemCategoryRowBinding.root,adapterPosition)
            })
        }
    }
}