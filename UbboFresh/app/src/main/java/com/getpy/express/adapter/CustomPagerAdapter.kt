package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.databinding.ItemPagerRowBinding

class CustomPagerAdapter(val context: Context, val images: ArrayList<MainAndSubCatDataModel>) : PagerAdapter() {
    override fun getCount(): Int = 2
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemPagerRowBinding.inflate(LayoutInflater.from(context), container, false)
        binding.imageView.setText("Tabs")
        container.addView(binding.root)
        return binding.root
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}