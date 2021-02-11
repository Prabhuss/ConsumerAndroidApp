package com.getpy.express.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.BottomSheetPagerAdapter
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.model.SlidingImageData
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentDemoSheetBinding
import com.getpy.express.ui.cart.CartFragment
import com.getpy.express.ui.main.MainActivity
class CartBottomSheetFragment(val preference: PreferenceProvider,val isCmgFrmHm:Boolean, val position:Int, val model:ProductsDataModel, val plist:MutableList<ProductsDataModel>) : SuperBottomSheetFragment() {
    lateinit var binding:FragmentDemoSheetBinding
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_demo_sheet, container, false)
        val list=ArrayList<SlidingImageData>()
        for(i in 0 until 3)
        {
            if(i==0)
            {
                val data= SlidingImageData()
                if(model.productPicUrl!=null)
                {
                    if(model.imageLinkFlag!=null) {
                        if (model.imageLinkFlag.equals("R")) {
                            data.OfferImgURL= UbboFreshApp.instance?.imageLoadUrl+model.productPicUrl
                        } else {
                            data.OfferImgURL= model.productPicUrl
                        }
                    }else
                    {
                        if(model.productPicUrl==null)
                        {
                            data.OfferImgURL= UbboFreshApp.instance?.imageLoadUrl
                        }else
                        {
                            data.OfferImgURL= UbboFreshApp.instance?.imageLoadUrl+model.productPicUrl
                        }

                    }

                }else
                {
                    data.OfferImgURL= ""
                }
                list.add(data)
            }else
            {
                val data= SlidingImageData()
                list.add(data)
            }

        }
        val viewPagerAdapter = context?.let { BottomSheetPagerAdapter(it,list) }
        binding.viewPager.setAdapter(viewPagerAdapter)
        binding.productName.text=model.productName
        if(model.sellingPrice.toDouble()<model.mrp.toDouble()) {
            binding.strikeLine.showView()
            binding.mrp.showView()
            binding.discount.showView()
            binding.yousave.showView()
            val discount = ((model.mrp.toDouble().minus(model.sellingPrice.toDouble()))*100).div(model.mrp.toDouble())
            binding.discount.text= formatString(discount) +"%"
            binding.mrp.text = "(" + formatStrWithPrice(model.mrp) + ")"//Constants.priceSymbol+model.mrp
            binding.strikeLine.text = "(" +formatStrWithPrice(model.mrp) + ")"
        }else
        {
            binding.strikeLine.hideView()
            binding.mrp.hideView()
            binding.discount.hideView()
            binding.yousave.hideView()
        }
        binding.price.text= formatStrWithPrice(model.sellingPrice)
        binding.desc.text=model.productDesc
        /*binding.packDem.text=""
        binding.brand.text=""
        binding.manfac.text=""
        binding.cntyor.text=""*/


        binding.productName.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.discount.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.price.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.mrp.setTypeface(UbboFreshApp.instance?.latoregular)

        binding.inclueLayout.productName.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.inclueLayout.countText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.inclueLayout.totPrice.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.inclueLayout.productPrice.setTypeface(UbboFreshApp.instance?.latoregular)

        binding.packDem.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.brand.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.manfac.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.cntyor.setTypeface(UbboFreshApp.instance?.latoregular)




        binding.inclueLayout.productName.text=model.productName
        binding.inclueLayout.productPrice.text="Item Price"+formatStrWithPrice(model.sellingPrice)
        binding.inclueLayout.totPrice.text="Items Cost"+formatStrWithPrice(model.sellingPrice)

        if(UbboFreshApp.instance?.hashMap!!.containsKey(model.citrineProdId))
        {
            val count=UbboFreshApp.instance?.hashMap!!.get(model.citrineProdId)
            binding.inclueLayout.countText.setText(count?.itemCount.toString())
            binding.inclueLayout.remove.showView()
            binding.inclueLayout.add.showView()
        }else
        {
            binding.inclueLayout.remove.hideView()
            binding.inclueLayout.add.hideView()
            binding.inclueLayout.countText.setText("Add")
        }
        binding.inclueLayout.layout.setOnClickListener(View.OnClickListener {
            binding.inclueLayout.remove.showView()
            binding.inclueLayout.add.showView()
            addItems(model)
        })
        binding.inclueLayout.add.setOnClickListener(View.OnClickListener {
            addItems(model)
        })
        binding.inclueLayout.remove.setOnClickListener(View.OnClickListener {
            removeItems(position)
        })
        return binding.root
    }

    fun addItems(model:ProductsDataModel)
    {
        binding.inclueLayout.remove.visibility=View.VISIBLE
        var addText=binding.inclueLayout.countText.text.toString()
        var count:Int
        if(addText.equals("Add"))
        {
            count=0
        }else
        {
            count=addText.toInt()
        }
        count=(count+1)
        binding.inclueLayout.countText.setText(count.toString())
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
        map.put("productid",model.citrineProdId.toString())
        map.put("productname",model.productName.toString())
        map.put("itemcount", model.itemCount.toString())
        Analytics.trackEvent("product item add but clicked", map)

        MainActivity.setupBadge()
        CartFragment.adapter?.notifyDataSetChanged()
        CartFragment.addRunnable?.let { Handler().postDelayed(it,10) }

    }

    fun removeItems(position: Int)
    {
        var count=binding.inclueLayout.countText.text.toString().toInt()
        var pos:Int=0
        if(count!=0) {
            val model=plist.get(position)
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
                binding.inclueLayout.countText.text = "Add"
                binding.inclueLayout.remove.hideView()
                binding.inclueLayout.add.hideView()
                //removing data from db
                CartFragment.adapter?.notifyDataSetChanged()
                CartFragment.removeRunnable?.let { Handler().postDelayed(it,10) }

            } else {
                count = --count
                UbboFreshApp.instance?.productsDataModel?.itemCount = count
                UbboFreshApp.instance?.hashMap?.put(UbboFreshApp.instance?.productsDataModel?.citrineProdId!!, UbboFreshApp.instance?.productsDataModel!!)
                binding.inclueLayout.countText.text = count.toString()
                //inserting data into db
                CartFragment.adapter?.notifyDataSetChanged()
                CartFragment.runnable?.let { Handler().postDelayed(it, 10) }

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