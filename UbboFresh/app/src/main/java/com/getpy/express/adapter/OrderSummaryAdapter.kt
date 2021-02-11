package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.bottomsheet.SummaryBottomSheetFragment
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.data.model.InvocieLineItems
import com.getpy.express.databinding.MyOrdersRowBinding

class OrderSummaryAdapter(val fm:FragmentManager,val context: Context, var mCategoriesList:ArrayList<InvocieLineItems>,val cmodel:CustomerInvoiceData) : RecyclerView.Adapter<OrderSummaryAdapter.DeveloperViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<MyOrdersRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.my_orders_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        if(model.ProductImage!=null)
        {
            holder.mItemCategoryRowBinding.image.load(model.ProductImage) {
                placeholder(R.drawable.ic_no_image_found)
                error(R.drawable.ic_no_image_found)
            }
        }else
        {
            holder.mItemCategoryRowBinding.image.load(UbboFreshApp.instance?.imageLoadUrl) {
                placeholder(R.drawable.ic_no_image_found)
                error(R.drawable.ic_no_image_found)
            }
        }

        holder.mItemCategoryRowBinding.textview1.text=model.ProductName
        holder.mItemCategoryRowBinding.textview2.text="Quantity:"+model.Quantity
        holder.mItemCategoryRowBinding.textview3.text="Per Unit Price:"+model.UnitPriceAfterDiscount
        holder.mItemCategoryRowBinding.textview4.visibility=View.GONE
        holder.mItemCategoryRowBinding.textview5.text="Total Price:"+model.TotalPrice

    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList!!.size
        } else {
            0
        }
    }

    fun setDeveloperList(mCategoriesList: ArrayList<InvocieLineItems>) {
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
            mItemCategoryRowBinding.cardview.setOnClickListener(View.OnClickListener {
                try {
                    val model=mCategoriesList.get(adapterPosition)
                    val sheet = SummaryBottomSheetFragment(false,adapterPosition,model,mCategoriesList,cmodel)
                    sheet.show(fm, "DemoBottomSheetFragment")
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }

            })
        }
    }
}