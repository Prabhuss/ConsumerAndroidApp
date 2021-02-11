package com.getpy.express.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.data.db.entities.CustomerAddressData
import com.getpy.express.databinding.CustomerAddrRowBinding
import com.getpy.express.ui.cart.cartactivities.AddAddressActivity

class CustomerAddressAdapter(var context:Context, var mCategoriesList:List<CustomerAddressData>): RecyclerView.Adapter<CustomerAddressAdapter.DeveloperViewHolder>() {
    var selectedmodel:CustomerAddressData?=null
    var isSelected:Boolean?=false
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<CustomerAddrRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.customer_addr_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(mDeveloperViewHolder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        if(model.TagName.equals(Constants.address1))
        {
            mDeveloperViewHolder.mBinding.title.text="Home Address"
        }
        if(model.TagName.equals(Constants.address2))
        {
            mDeveloperViewHolder.mBinding.title.text="Work address"
        }
        if(model.TagName.equals(Constants.address3))
        {
            mDeveloperViewHolder.mBinding.title.text="Other Address"
        }
        if(model.ischecked)
        {
            mDeveloperViewHolder.mBinding.radioBut.isChecked=true
        }else
        {
            mDeveloperViewHolder.mBinding.radioBut.isChecked=false
        }
        mDeveloperViewHolder.mBinding.name.text=model.FirstName
        mDeveloperViewHolder.mBinding.address.text=model.Address1
        mDeveloperViewHolder.mBinding.landmarkText.text=model.Address2
    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList.size
        } else {
            0
        }
    }

    fun getModel():CustomerAddressData?
    {
        return selectedmodel
    }

    inner class DeveloperViewHolder(var mBinding: CustomerAddrRowBinding) :
        RecyclerView.ViewHolder(mBinding.root)
    {
        init {
            mBinding.name.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.address.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.title.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.change.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.change.setOnClickListener(View.OnClickListener {
                try {
                    val intent=Intent(context, AddAddressActivity::class.java)
                    intent.putExtra("model",mCategoriesList.get(adapterPosition))
                    context.startActivity(intent)
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }

            })
            mBinding.radioBut.setOnClickListener(View.OnClickListener {
                val model=mCategoriesList.get(adapterPosition)
                for(i in 0 until mCategoriesList.size)
                {
                    val model1=mCategoriesList.get(i)
                    if(model.TagName.equals(model1.TagName))
                    {
                        model.ischecked=true
                        selectedmodel=model
                    }else
                    {
                        model1.ischecked=false
                    }
                }
                notifyDataSetChanged()
            })
        }
    }
}