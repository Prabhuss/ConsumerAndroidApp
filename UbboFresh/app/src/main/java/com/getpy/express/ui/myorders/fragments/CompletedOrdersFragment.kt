package com.getpy.express.ui.myorders.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.Utils.Constants
import com.getpy.express.adapter.MyOrdersAdapter
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.data.model.InvocieLineItems
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.AllOrdersFragmentBinding
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.myorders.AllOrdersViewModel
import com.getpy.express.ui.myorders.MyOrdersModelFactory
import com.getpy.express.ui.ordersummary.OrderSummaryActivity
import org.kodein.di.generic.instance
import java.util.HashMap

class CompletedOrdersFragment(val completeList: ArrayList<CustomerInvoiceData>,val invoieList:ArrayList<InvocieLineItems>) : InjectionFragment() {
    private val factory: MyOrdersModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    private lateinit var viewModel: AllOrdersViewModel
    private lateinit var binding: AllOrdersFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.all_orders_fragment, container, false)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.setItemAnimator(null)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.layoutManager=layoutManager
        val adapater= completeList.let { activity?.let { it1 ->
            MyOrdersAdapter(it1,it,object:ItemClickListener{
                override fun onItemClick(view: View?, position: Int) {
                    val model=completeList.get(position)

                    val map= HashMap<String,String>()
                    map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                    map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                    map.put("InvoiceType ","GetPY")
                    map.put("PaymentMode", "COD")
                    map.put("InvoiceId", model.InvoiceId.toString())
                    map.put("TotalAmount", model.TotalInvoiceAmount.toString())
                    Analytics.trackEvent("Order Summary clicked", map)

                    allOrdersData(model)
                }
            })
        }
        }

        /*val model=custInvoList.get(position)
        for(i in 0 until invoieList.size)
        {
            val imodel=invoieList.get(i)
            if(model.InvoiceId==imodel.CustomerInvoiceId)
            {
                val intent= Intent(activity, OrderSummaryActivity::class.java)
                intent.putExtra("model",imodel)
                startActivity(intent)
            }
        }*/
        binding.recyclerview.adapter=adapater
        return binding.root
    }
    fun allOrdersData(model:CustomerInvoiceData)
    {
        val ilist=ArrayList<InvocieLineItems>()
        for(i in 0 until invoieList.size)
        {
            val imodel=invoieList.get(i)
            if(model.CustomerInvoiceId==imodel.CustomerInvoiceId)
            {
                ilist.add(imodel)
            }

        }
        for(i in 0 until invoieList.size)
        {
            val imodel=invoieList.get(i)
            if(model.CustomerInvoiceId==imodel.CustomerInvoiceId)
            {
                val intent= Intent(activity,OrderSummaryActivity::class.java)
                intent.putExtra("list",ilist)
                intent.putExtra("model",model)
                startActivity(intent)
                break
            }

        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this,factory).get(AllOrdersViewModel::class.java)
        // TODO: Use the ViewModel
    }
    companion object {
        var runnable:Runnable?=null
        fun newInstance(completeList: ArrayList<CustomerInvoiceData>,invoiceList:ArrayList<InvocieLineItems>) =
                CompletedOrdersFragment(completeList,invoiceList)
    }
}