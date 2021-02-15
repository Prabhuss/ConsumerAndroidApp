package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.databinding.MyOrdersRowBinding
import com.getpy.express.listeners.ItemClickListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersAdapter(val context: Context, var mCategoriesList:ArrayList<CustomerInvoiceData>,val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MyOrdersAdapter.DeveloperViewHolder>() {
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
        holder.mItemCategoryRowBinding.textview2.text= model.CreatedDate?.let { createdDate(it) }
        holder.mItemCategoryRowBinding.textview3.text="Amount:"+model.PayableAmount
        holder.mItemCategoryRowBinding.textview4.text="Payment Mode:"+model.PaymentMode
        holder.mItemCategoryRowBinding.textview5.text="Status:"+model.OrderStatus

    }

    fun createdDate(d:String):String
    {
        val form = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        var date: Date? = null
        try {
            date = form.parse(d.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val postFormater = SimpleDateFormat("MMM dd, yyyy hh:mm")
        val newDateStr: String = postFormater.format(date)
        return newDateStr
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