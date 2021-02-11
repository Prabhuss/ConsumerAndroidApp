package com.getpy.express.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.fresh.views.Products.ProductsFragment
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.bottomsheet.DemoBottomSheetFragment
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ProductsItemsRowBinding
import com.getpy.express.ui.main.MainActivity


class ProductItemsAdapter(val preference: PreferenceProvider,val fm:FragmentManager,val context: Context, var mCategoriesList:ArrayList<ProductsDataModel>) : RecyclerView.Adapter<ProductItemsAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<ProductsItemsRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.products_items_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(mholder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        var url: String=""
        if (model.imageLinkFlag != null && model.productPicUrl!=null) {
            if (model.imageLinkFlag.equals("R")) {
                url = UbboFreshApp.instance?.imageLoadUrl + model.productPicUrl
            } else {
                url = model.productPicUrl
            }
        } else {
            if(model.productPicUrl!=null)
            {
                url = UbboFreshApp.instance?.imageLoadUrl+model.productPicUrl
            }else
            {
                url = UbboFreshApp.instance?.imageLoadUrl.toString()
            }
        }
        /*val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_no_image_found)
                .error(R.drawable.ic_no_image_found)
        Glide.with(context).load(url).apply(options).into(mholder.mBinding.image)*/
        mholder.mBinding.image.load(url) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }
        mholder.mBinding.text1.text = model.productName
        if(model.sellingPrice!=null) {
            mholder.mBinding.text2.text = formatStrWithPrice(model.sellingPrice)
        }

        /*if(UbboFreshApp.instance?.carItemsList!=null)
        {
            if(UbboFreshApp.instance?.carItemsList!!.size>0)
            {
                for(j in 0 until UbboFreshApp.instance?.carItemsList!!.size)
                {
                    val cmodel=UbboFreshApp.instance?.carItemsList?.get(j)
                    if(cmodel?.citrineProdId.equals(model.citrineProdId))
                    {
                        mholder.mBinding.remove.showView()
                        mholder.mBinding.add.showView()
                    }
                }
            }
        }*/
        if(model.mrp!=null) {
            if (model.sellingPrice.toDouble() < model.mrp.toDouble()) {
                mholder.mBinding.text3.showView()
                mholder.mBinding.strike.showView()
                mholder.mBinding.offerText.showView()
                val discount = ((model.mrp.toDouble()
                    .minus(model.sellingPrice.toDouble())) * 100).div(model.mrp.toDouble())
                mholder.mBinding.offerText.text = formatString(discount) + "% OFF"
                mholder.mBinding.text3.text =
                    "(" + formatStrWithPrice(model.mrp) + ")"//Constants.priceSymbol+model.mrp
                mholder.mBinding.strike.text = "(" + formatStrWithPrice(model.mrp) + ")"
            } else {
                mholder.mBinding.offerText.hideView()
                mholder.mBinding.text3.hideView()
                mholder.mBinding.strike.hideView()
            }
        }else
        {
            mholder.mBinding.offerText.hideView()
            mholder.mBinding.text3.hideView()
            mholder.mBinding.strike.hideView()
        }
        mholder.mBinding.butLayout.setOnClickListener(View.OnClickListener {
            val addText=mholder.mBinding.countTxt.text.toString()
            if(addText.equals("Add"))
            {
                mholder.mBinding.remove.showView()
                mholder.mBinding.add.showView()
                addItems(mholder,i)
            }
        })
        mholder.mBinding.add.setOnClickListener(View.OnClickListener {
            addItems(mholder,i)
        })

        mholder.mBinding.remove.setOnClickListener(View.OnClickListener {
            removeItems(mholder,i)
        })
        if(UbboFreshApp.instance?.hashMap!!.containsKey(model.citrineProdId))
        {
            val count=UbboFreshApp.instance?.hashMap?.get(model.citrineProdId)
            mholder.mBinding.remove.showView()
            mholder.mBinding.add.showView()
            mholder.mBinding.countTxt.setText(count?.itemCount.toString())
        }else
        {
            mholder.mBinding.remove.hideView()
            mholder.mBinding.add.hideView()
            mholder.mBinding.countTxt.setText("Add")
        }
        mholder.setIsRecyclable(false)
    }
    fun updateList(list:ArrayList<ProductsDataModel>)
    {
        mCategoriesList=list
        notifyDataSetChanged()
    }
    fun addItems(mholder: DeveloperViewHolder,position:Int)
    {
        val model=mCategoriesList.get(position)
        mholder.mBinding.remove.visibility=View.VISIBLE
        var addText=mholder.mBinding.countTxt.text.toString()
        var count:Int
        if(addText.equals("Add"))
        {
            count=0
        }else
        {
            count=addText.toInt()
        }
        count=(count+1)
        mholder.mBinding.countTxt.setText(count.toString())
        if(UbboFreshApp.instance?.hashMap!!.containsKey(model.citrineProdId))
        {
            model.itemCount=count
            UbboFreshApp.instance?.hashMap?.put(model.citrineProdId,model)
            for(i in 0 until UbboFreshApp.instance?.carItemsList?.size!!)
            {
                val mm=UbboFreshApp.instance?.carItemsList?.get(i)
                if(mm?.citrineProdId.equals(model.citrineProdId))
                {
                    UbboFreshApp.instance?.carItemsList?.removeAt(i)
                    UbboFreshApp.instance?.carItemsList?.add(i,model)
                }
            }
        }else
        {
            model.itemCount=count
            UbboFreshApp.instance?.carItemsList?.add(model)
            UbboFreshApp.instance?.hashMap?.put(model.citrineProdId,model)
        }

        val map= HashMap<String, String>()
        map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
        map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
        map.put("productid", model.citrineProdId)
        map.put("productname",model.productName)
        map.put("itemcount", model.itemCount.toString())
        Analytics.trackEvent("product item add but clicked", map)

        MainActivity.setupBadge()
        ProductsFragment.runnable?.let { Handler().postDelayed(it,10) }
    }
    fun removeItems(mholder: DeveloperViewHolder,position: Int)
    {
        var count=mholder.mBinding.countTxt.text.toString().toInt()
        var pos:Int=0
        if(count!=0) {
            val model=mCategoriesList.get(position)
            for(i in 0 until UbboFreshApp.instance?.carItemsList!!.size)
            {
                val mm=UbboFreshApp.instance?.carItemsList?.get(i)
                if(mm?.citrineProdId.equals(model.citrineProdId))
                {
                    pos=i
                    UbboFreshApp.instance?.productsDataModel= UbboFreshApp.instance?.carItemsList?.get(i)
                }
            }
            if (count == 1) {
                count = 0
                UbboFreshApp.instance?.productsDataModel?.itemCount = count
                UbboFreshApp.instance?.carItemsList?.removeAt(pos)
                UbboFreshApp.instance?.hashMap?.remove(UbboFreshApp.instance?.productsDataModel?.citrineProdId)
                mholder.mBinding.countTxt.text = "Add"
                mholder.mBinding.remove.hideView()
                mholder.mBinding.add.hideView()
                //removing data from db
                ProductsFragment.removerunnable?.let { Handler().postDelayed(it,10) }
            } else {
                count = --count
                UbboFreshApp.instance?.productsDataModel?.itemCount = count
                UbboFreshApp.instance?.hashMap?.put(UbboFreshApp.instance?.productsDataModel?.citrineProdId!!, UbboFreshApp.instance?.productsDataModel!!)
                mholder.mBinding.countTxt.text = count.toString()
                //inserting data into db
                ProductsFragment.runnable?.let { Handler().postDelayed(it,10) }
            }

            val map= HashMap<String, String>()
            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
            map.put("productid", UbboFreshApp.instance?.productsDataModel?.citrineProdId.toString())
            map.put("productname",UbboFreshApp.instance?.productsDataModel?.productName.toString())
            map.put("itemcount", UbboFreshApp.instance?.productsDataModel?.itemCount.toString())
            Analytics.trackEvent("product item add but clicked", map)

            MainActivity.setupBadge()
        }
    }
    override fun getItemCount(): Int {
        return if (mCategoriesList.size>0) {
            mCategoriesList.size
        } else {
            0
        }
    }

    fun setDeveloperList(mCategoriesList: ArrayList<ProductsDataModel>) {
        this.mCategoriesList = mCategoriesList
        notifyDataSetChanged()
    }

    inner class DeveloperViewHolder(var mBinding: ProductsItemsRowBinding) :
        RecyclerView.ViewHolder(mBinding.root)
    {
        init {
            mBinding.text1.setTypeface(UbboFreshApp.instance?.latoheavy)
            mBinding.text2.setTypeface(UbboFreshApp.instance?.latobold)
            mBinding.countTxt.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.imageLayout.setOnClickListener(View.OnClickListener {
                val model=mCategoriesList.get(adapterPosition)
                val sheet = DemoBottomSheetFragment(preference,false,adapterPosition,model,mCategoriesList)
                sheet.show(fm, "DemoBottomSheetFragment")
            })
        }
    }
}