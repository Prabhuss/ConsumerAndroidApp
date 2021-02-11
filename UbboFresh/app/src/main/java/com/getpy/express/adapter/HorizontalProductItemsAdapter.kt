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
import com.getpy.fresh.views.home.HomeFragment
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.bottomsheet.DemoBottomSheetFragment
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ProductsItemsRowBinding
import com.getpy.express.ui.main.MainActivity

class HorizontalProductItemsAdapter(val preferences: PreferenceProvider,val manager:FragmentManager,val context: Context, var mCategoriesList:ArrayList<ProductsDataModel>, val listener: ItemClickListener) : RecyclerView.Adapter<HorizontalProductItemsAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<ProductsItemsRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.products_items_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(mholder: DeveloperViewHolder, i: Int) {
        if(mCategoriesList.size>5)
        {
            if (i == 5) {
                mholder.mBinding.topLayout.visibility= View.GONE
                mholder.mBinding.viewAllText.visibility=View.VISIBLE
            } else {
                bindData(mholder,i)
            }
        }else
        {
            if (i == (mCategoriesList.size-1)) {
                mholder.mBinding.topLayout.visibility= View.GONE
                mholder.mBinding.viewAllText.visibility=View.VISIBLE
            } else {
                bindData(mholder,i)
            }
        }
    }
    fun bindData(mholder:DeveloperViewHolder,position: Int)
    {
        mholder.mBinding.topLayout.visibility= View.VISIBLE
        mholder.mBinding.viewAllText.visibility=View.GONE
        val model = mCategoriesList.get(position)
        val url: String
        if (model.imageLinkFlag != null) {
            if (model.imageLinkFlag.equals("R")) {
                url = UbboFreshApp.instance?.imageLoadUrl + model.productPicUrl
            } else {
                url = model.productPicUrl
            }
        } else {
            if(model.productPicUrl!=null)
            {
                url = UbboFreshApp.instance?.imageLoadUrl + model.productPicUrl
            }else {
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
        mholder.mBinding.text2.text = formatStrWithPrice(model.sellingPrice)
        if(model.sellingPrice.toDouble()<model.mrp.toDouble()) {
            mholder.mBinding.text3.showView()
            mholder.mBinding.strike.showView()
            mholder.mBinding.offerText.showView()
            val discount = ((model.mrp.toDouble().minus(model.sellingPrice.toDouble()))*100).div(model.mrp.toDouble())
            mholder.mBinding.offerText.text= formatString(discount)+"% OFF"
            mholder.mBinding.text3.text = "(" + formatStrWithPrice(model.mrp) + ")"
            mholder.mBinding.strike.text = "(" + formatStrWithPrice(model.mrp) + ")"
        }else
        {
            mholder.mBinding.offerText.hideView()
            mholder.mBinding.text3.hideView()
            mholder.mBinding.strike.hideView()
        }

        if(UbboFreshApp.instance?.hashMap!!.containsKey(model.citrineProdId))
        {
            val count=UbboFreshApp.instance?.hashMap!!.get(model.citrineProdId)
            mholder.mBinding.remove.showView()
            mholder.mBinding.add.showView()
            mholder.mBinding.countTxt.setText(count?.itemCount.toString())
        }else
        {
            mholder.mBinding.remove.hideView()
            mholder.mBinding.add.hideView()
            mholder.mBinding.countTxt.setText("Add")
        }

        mholder.mBinding.butLayout.setOnClickListener(View.OnClickListener {
            var addText=mholder.mBinding.countTxt.text.toString()
            if(addText.equals("Add"))
            {
                mholder.mBinding.remove.showView()
                mholder.mBinding.add.showView()
                addItems(mholder,position)
            }

        })

        mholder.mBinding.add.setOnClickListener(View.OnClickListener {
            addItems(mholder,position)
        })

        mholder.mBinding.remove.setOnClickListener(View.OnClickListener {
            removeItems(mholder,position)
        })
        mholder.setIsRecyclable(false)
    }
    fun addItems(mholder: DeveloperViewHolder, position:Int)
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
        MainActivity.setupBadge()
        HomeFragment.addRunnable?.let { Handler().postDelayed(it,10) }
    }
    fun removeItems(mholder: DeveloperViewHolder, position: Int)
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
                HomeFragment.removeRunnable?.let { Handler().postDelayed(it,10) }
            } else {
                count = --count
                UbboFreshApp.instance?.productsDataModel?.itemCount = count
                UbboFreshApp.instance?.hashMap?.put(UbboFreshApp.instance?.productsDataModel?.citrineProdId!!, UbboFreshApp.instance?.productsDataModel!!)
                mholder.mBinding.countTxt.text = count.toString()
                HomeFragment.addRunnable?.let { Handler().postDelayed(it,10) }
            }
            MainActivity.setupBadge()
        }
    }
    override fun getItemCount(): Int {
        return if (mCategoriesList.size>0) {
            if(mCategoriesList.size>5) {
                6
            }else
            {
                mCategoriesList.size
            }
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
            mBinding.root.setOnClickListener(View.OnClickListener {
                listener.onItemClick(mBinding.root,adapterPosition)
            })
            mBinding.imageLayout.setOnClickListener(View.OnClickListener {
                val model=mCategoriesList.get(adapterPosition)

                val map=HashMap<String,String>()
                map.put("mobileNum",preferences.getStringData(Constants.saveMobileNumkey))
                map.put("merchantid", preferences.getIntData(Constants.saveMerchantIdKey).toString())
                map.put("ProductId", model.citrineProdId.toString())
                map.put("ProductName", model.productName.toString())
                Analytics.trackEvent("top/deals products clicked", map)


                val sheet = model.let { it2 -> DemoBottomSheetFragment(preferences,true,adapterPosition,it2,mCategoriesList) }
                sheet.show(manager, "DemoBottomSheetFragment")
            })
            mBinding.text1.setTypeface(UbboFreshApp.instance?.latoheavy)
            mBinding.text2.setTypeface(UbboFreshApp.instance?.latobold)
            mBinding.countTxt.setTypeface(UbboFreshApp.instance?.latoregular)
            mBinding.viewAllText.setTypeface(UbboFreshApp.instance?.latoregular)
        }
    }
}