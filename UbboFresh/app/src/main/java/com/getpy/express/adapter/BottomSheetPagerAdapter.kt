package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import coil.load
import com.getpy.express.R
import com.getpy.express.data.model.SlidingImageData
import com.getpy.express.databinding.BottomPagerRowBinding

class BottomSheetPagerAdapter(val context: Context, val images: ArrayList<SlidingImageData>) : PagerAdapter() {
    override fun getCount(): Int = images.size
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = BottomPagerRowBinding.inflate(LayoutInflater.from(context), container, false)
        val model=images.get(position)
        val url=model.OfferImgURL?:""
        /*val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_no_image_found)
            .error(R.drawable.ic_no_image_found)
        Glide.with(context).load(url).apply(options).into(binding.imageView)*/
        binding.imageView.load(url) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }
        container.addView(binding.root)
        return binding.root
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}