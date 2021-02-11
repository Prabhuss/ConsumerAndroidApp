package com.getpy.express.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.getpy.express.R
import com.getpy.express.UbboFreshApp.Companion.instance
import com.getpy.express.data.model.MainAndSubCatDataModel
import java.util.*

class HomeCategoriesGridAdapter //public constructor
    (
    //context
    private val context: Context,
    //data source of the list adapter
    private var items: ArrayList<MainAndSubCatDataModel>?
) : BaseAdapter() {

    fun updateList(mCategoriesList: ArrayList<MainAndSubCatDataModel>?) {
        items = mCategoriesList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (items != null) {
            items!!.size
        } else {
            0
        }
        //returns total of items in the list
    }

    override fun getItem(position: Int): Any {
        return items!![position] //returns list item at the specified position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.item_category_grid_row, parent, false)
            holder = ViewHolder(convertView)
            convertView.tag = holder
        } else {
            holder =
                convertView.tag as ViewHolder
        }
        val model = items!![position]
        val url: String?
        url = if (model.ImageLinkFlag != null) {
            if (model.ImageLinkFlag == "R") {
                instance!!.imageLoadUrl + model.PicURL
            } else {
                model.PicURL
            }
        } else {
            if (model.PicURL != null) {
                instance!!.imageLoadUrl + model.PicURL
            } else {
                model.PicURL
            }
        }
        /*val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_no_image_found)
            .error(R.drawable.ic_no_image_found)
        Glide.with(context).load(url).apply(options).into(holder.imageView)*/
        holder.imageView.load(url) {
            placeholder(R.drawable.ic_no_image_found)
            error(R.drawable.ic_no_image_found)
        }
        holder.text.text = model.Name
        return convertView!!
    }

    private inner class ViewHolder(view: View) {
        var imageView: ImageView
        var text: TextView

        init {
            imageView = view.findViewById<View>(R.id.image) as ImageView
            text = view.findViewById<View>(R.id.textview) as TextView
            text.typeface = instance!!.latoheavy
        }
    }

}