package com.getpy.express.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.BottomSheetPagerAdapter
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.data.model.InvocieLineItems
import com.getpy.express.data.model.SlidingImageData
import com.getpy.express.databinding.SummaryFragmentDemoSheetBinding

class SummaryBottomSheetFragment(val isCmgFrmHm:Boolean, val position:Int, val model:InvocieLineItems,
                                 val plist:ArrayList<InvocieLineItems>,val cmodel:CustomerInvoiceData) : SuperBottomSheetFragment() {
    lateinit var binding:SummaryFragmentDemoSheetBinding
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding=DataBindingUtil.inflate(inflater,R.layout.summary_fragment_demo_sheet, container, false)
        val list=ArrayList<SlidingImageData>()
        for(i in 0 until 3)
        {
            if(i==0)
            {
                val data= SlidingImageData()
                if(model.ProductImage!=null)
                {
                    data.OfferImgURL= model.ProductImage

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
        binding.productName.text=model.ProductName
        if(model.Discount!="0") {
            binding.strikeLine.showView()
            binding.mrp.showView()
            binding.discount.showView()
            binding.yousave.showView()
            val discount = model.Discount
            binding.discount.text= discount?.toDouble()?.let { formatString(it) } +"%"
            binding.mrp.text = "(" + model.UnitPrice?.let { formatStrWithPrice(it) } + ")"//Constants.priceSymbol+model.mrp
            binding.strikeLine.text = "(" + model.UnitPrice?.let { formatStrWithPrice(it) } + ")"
        }else
        {
            binding.strikeLine.hideView()
            binding.mrp.hideView()
            binding.discount.hideView()
            binding.yousave.hideView()
        }
        binding.price.text= model.UnitPriceAfterDiscount?.let { formatStrWithPrice(it) }
        binding.desc.text=""
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

        binding.inclueLayout.productName.text=model.ProductName
        binding.inclueLayout.productPrice.text="Item Price"+ model.UnitPrice?.let { formatStrWithPrice(it) }
        binding.inclueLayout.totPrice.text="Items Cost"+ model.UnitPrice?.let { formatStrWithPrice(it) }

        return binding.root
    }

}