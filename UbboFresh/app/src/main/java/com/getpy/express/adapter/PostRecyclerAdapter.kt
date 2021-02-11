package com.getpy.express.adapter

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
import com.getpy.express.databinding.ItemLoadingBinding
import com.getpy.express.databinding.ProductsItemsRowBinding
import com.getpy.express.ui.main.MainActivity
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

class PostRecyclerAdapter(val preference: PreferenceProvider,val fm: FragmentManager, val context: Context,  val mPostItems: ArrayList<ProductsDataModel>?) : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val binding: ProductsItemsRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.products_items_row, parent, false)
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
            if (position == (mPostItems!!.size - 1)) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return mPostItems?.size ?: 0
    }

    fun addItems(postItems: List<ProductsDataModel?>?) {
        for(i in 0 until postItems!!.size)
        {
           val model=postItems.get(i)
           model?.let { mPostItems?.add(it) }
        }
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        mPostItems!!.add(ProductsDataModel())
        notifyItemInserted(mPostItems.size - 1)
    }
    fun LoadingItems() {
        isLoaderVisible = false
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

    fun getItem(position: Int): ProductsDataModel {
        return mPostItems!![position]
    }

    inner class ViewHolder //ButterKnife.bind(this, itemView);
    internal constructor(var mBinding: ProductsItemsRowBinding) : BaseViewHolder(mBinding.root) {
        override fun clear() {}
        override fun onBind(position: Int, mholder: BaseViewHolder) {
            super.onBind(position, mholder)
            val model = mPostItems?.get(position)
            var url: String=""
            if (model?.imageLinkFlag != null && model.productPicUrl!=null) {
                if (model?.imageLinkFlag.equals("R")) {
                    url = UbboFreshApp.instance?.imageLoadUrl + model?.productPicUrl
                } else {
                    url = model.productPicUrl
                }
            } else {
                if(model?.productPicUrl!=null)
                {
                    url = UbboFreshApp.instance?.imageLoadUrl+model?.productPicUrl
                }else
                {
                    url = UbboFreshApp.instance?.imageLoadUrl.toString()
                }
            }
            mBinding.image.load(url) {
                placeholder(R.drawable.ic_no_image_found)
                error(R.drawable.ic_no_image_found)
            }
            mBinding.text1.text = model?.productName
            mBinding.text2.text = model?.sellingPrice?.let { formatStrWithPrice(it) }
            if(model?.mrp!=null && model.sellingPrice!=null) {
                if (model.sellingPrice.toDouble() < model.mrp.toDouble()) {
                    mBinding.text3.showView()
                    mBinding.strike.showView()
                    mBinding.offerText.showView()
                    val discount = ((model.mrp.toDouble()
                            .minus(model.sellingPrice.toDouble())) * 100).div(model.mrp.toDouble())
                    mBinding.offerText.text = formatString(discount) + "% OFF"
                    mBinding.text3.text =
                            "(" + formatStrWithPrice(model.mrp) + ")"//Constants.priceSymbol+model.mrp
                    mBinding.strike.text = "(" + formatStrWithPrice(model.mrp) + ")"
                } else {
                    mBinding.offerText.hideView()
                    mBinding.text3.hideView()
                    mBinding.strike.hideView()
                }
            }else
            {
                mBinding.offerText.hideView()
                mBinding.text3.hideView()
                mBinding.strike.hideView()
            }
            mBinding.imageLayout.setOnClickListener(View.OnClickListener {
                val model=mPostItems?.get(adapterPosition)
                model?.let {
                    mPostItems?.let {it1->
                        val sheet = DemoBottomSheetFragment(preference,false,adapterPosition,it,it1)
                        sheet.show(fm, "DemoBottomSheetFragment")
                    }
                }
            })
            mBinding.butLayout.setOnClickListener(View.OnClickListener {
                val addText=mBinding.countTxt.text.toString()
                if(addText.equals("Add"))
                {
                    mBinding.remove.showView()
                    mBinding.add.showView()
                    addItems(mBinding,adapterPosition)
                }
            })
            mBinding.add.setOnClickListener(View.OnClickListener {
                addItems(mBinding,adapterPosition)
            })

            mBinding.remove.setOnClickListener(View.OnClickListener {
                removeItems(mBinding,adapterPosition)
            })
            if(UbboFreshApp.instance?.hashMap!!.containsKey(model?.citrineProdId))
            {
                val count=UbboFreshApp.instance?.hashMap?.get(model?.citrineProdId)
                mBinding.remove.showView()
                mBinding.add.showView()
                mBinding.countTxt.setText(count?.itemCount.toString())
            }else
            {
                mBinding.remove.hideView()
                mBinding.add.hideView()
                mBinding.countTxt.setText("Add")
            }
            mholder.setIsRecyclable(false)
        }
        fun addItems(mBinding:ProductsItemsRowBinding, position:Int)
        {
            val model=mPostItems?.get(position)
            mBinding.remove.visibility=View.VISIBLE
            var addText=mBinding.countTxt.text.toString()
            var count:Int
            if(addText.equals("Add"))
            {
                count=0
            }else
            {
                count=addText.toInt()
            }
            count=(count+1)
            mBinding.countTxt.setText(count.toString())
            if(UbboFreshApp.instance?.hashMap!!.containsKey(model?.citrineProdId))
            {
                model?.itemCount=count
                model?.citrineProdId?.let { UbboFreshApp.instance?.hashMap?.put(it,model) }
                for(i in 0 until UbboFreshApp.instance?.carItemsList?.size!!)
                {
                    val mm=UbboFreshApp.instance?.carItemsList?.get(i)
                    if(mm?.citrineProdId.equals(model?.citrineProdId))
                    {
                        UbboFreshApp.instance?.carItemsList?.removeAt(i)
                        model?.let { UbboFreshApp.instance?.carItemsList?.add(i, it) }
                    }
                }
            }else
            {
                model?.itemCount=count
                model?.let { UbboFreshApp.instance?.carItemsList?.add(it) }
                model?.citrineProdId?.let { UbboFreshApp.instance?.hashMap?.put(it,model) }
            }

            val map= HashMap<String, String>()
            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
            model?.citrineProdId.let { map.put("productid", it.toString()) }
            model?.productName.let { map.put("productname",it.toString()) }
            map.put("itemcount", model?.itemCount.toString())
            Analytics.trackEvent("product item add but clicked", map)

            MainActivity.setupBadge()
            ProductsFragment.runnable?.let { Handler().postDelayed(it,10) }
        }
        fun removeItems(mBinding:ProductsItemsRowBinding, position: Int)
        {
            var count=mBinding.countTxt.text.toString().toInt()
            var pos:Int=0
            if(count!=0) {
                val model=mPostItems?.get(position)
                for(i in 0 until UbboFreshApp.instance?.carItemsList!!.size)
                {
                    val mm=UbboFreshApp.instance?.carItemsList?.get(i)
                    if(mm?.citrineProdId.equals(model?.citrineProdId))
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
                    mBinding.countTxt.text = "Add"
                    mBinding.remove.hideView()
                    mBinding.add.hideView()
                    //removing data from db
                    ProductsFragment.removerunnable?.let { Handler().postDelayed(it,10) }
                } else {
                    count = --count
                    UbboFreshApp.instance?.productsDataModel?.itemCount = count
                    UbboFreshApp.instance?.hashMap?.put(UbboFreshApp.instance?.productsDataModel?.citrineProdId!!, UbboFreshApp.instance?.productsDataModel!!)
                    mBinding.countTxt.text = count.toString()
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