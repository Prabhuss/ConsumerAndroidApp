package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.data.model.GetOfferDetailsData
import com.getpy.express.databinding.SlidingPagerRowBinding

class HorizontalScrollImagesAdapter(val context: Context, var mCategoriesList:ArrayList<GetOfferDetailsData> ,val listener: ItemClickListener) : RecyclerView.Adapter<HorizontalScrollImagesAdapter.DeveloperViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeveloperViewHolder {
        val mDeveloperListItemBinding = DataBindingUtil.inflate<SlidingPagerRowBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.sliding_pager_row, viewGroup, false)
        return DeveloperViewHolder(mDeveloperListItemBinding)
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, i: Int) {
        val model = mCategoriesList.get(i)
        /*val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_no_image_found)
            .error(R.drawable.ic_no_image_found)
        Glide.with(context).load(model.OfferImgURL).apply(options).into(holder.binding.imageView)*/
        holder.binding.imageView.load(model.OfferImgURL) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }

    }

    override fun getItemCount(): Int {
        return if (mCategoriesList != null) {
            mCategoriesList!!.size
        } else {
            0
        }
    }
    inner class DeveloperViewHolder(var binding: SlidingPagerRowBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        init {
           binding.root.setOnClickListener(View.OnClickListener {
               listener.onItemClick(binding.root,adapterPosition)
           })
        }
    }
}