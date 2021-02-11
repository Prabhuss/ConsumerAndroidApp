package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.R
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.databinding.ItemLoadingBinding
import com.getpy.express.databinding.MyOrdersRowBinding
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

class MyOrdersRecyclerAdapter(val fm: FragmentManager, val context: Context, val mPostItems: ArrayList<CustomerInvoiceData>?) : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val binding: MyOrdersRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_orders_row, parent, false)
                ViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val itemLoadingBinding: ItemLoadingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
                ProgressHolder(itemLoadingBinding)
            }
            else -> throw IllegalArgumentException("Different view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, holder)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == mPostItems!!.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return mPostItems?.size ?: 0
    }

    fun addItems(postItems: List<CustomerInvoiceData?>?) {
        for(i in 0 until postItems!!.size)
        {
           val model=postItems.get(i)
           model?.let { mPostItems?.add(it) }
        }
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        mPostItems!!.add(CustomerInvoiceData())
        notifyItemInserted(mPostItems.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = mPostItems!!.size - 1
        val item = getItem(position)
        if (item != null) {
            mPostItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        mPostItems!!.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): CustomerInvoiceData {
        return mPostItems!![position]
    }

    inner class ViewHolder //ButterKnife.bind(this, itemView);
    internal constructor(var mBinding: MyOrdersRowBinding) : BaseViewHolder(mBinding.root) {
        override fun clear() {}
        override fun onBind(position: Int, mholder: BaseViewHolder) {
            super.onBind(position, mholder)
            val model = mPostItems?.get(position)
            mBinding.image.setImageResource(R.drawable.ic_myorder_default_img)
            mBinding.textview1.text="Order ID:"+model?.InvoiceId
            mBinding.textview2.text=model?.CreatedDate
            mBinding.textview3.text="Amount:"+model?.PayableAmount
            mBinding.textview4.text="Payment Mode:"+model?.PaymentMode
            mBinding.textview5.text="Status:"+model?.OrderStatus
        }


    }

    inner class ProgressHolder //ButterKnife.bind(this, itemView);
    internal constructor(var binding: ItemLoadingBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int, holder: BaseViewHolder) {
            super.onBind(position, holder)
        }

        override fun clear() {}

    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
    }

}