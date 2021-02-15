package com.getpy.express.ui.myorders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.Utils.*
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.data.model.GetOrderResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityMyOrdersBinding
import com.getpy.express.listeners.PaginationListener
import com.getpy.express.ui.myorders.fragments.AllOrdersFragment
import com.getpy.express.ui.myorders.fragments.CompletedOrdersFragment
import com.getpy.express.ui.myorders.fragments.RejectedOrdersFragment
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyOrdersActivity : AppCompatActivity(),KodeinAware {
    override val kodein by kodein()
    private val factory: MyOrdersModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var binding:ActivityMyOrdersBinding
    lateinit var viewmodel:AllOrdersViewModel
    var getOrdResponse:GetOrderResponse?=null
    var completedList:ArrayList<CustomerInvoiceData>?=null
    var rejectedList:ArrayList<CustomerInvoiceData>?=null
    var allOrdersList:ArrayList<CustomerInvoiceData>?=null

    companion object{
        var loadRunnable:Runnable?=null
        var currentPage: Int = PaginationListener.PAGE_START
        var isLastPage = false
        private val totalPage = 10
        var isLoading = false
        var itemCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_my_orders)
        viewmodel=ViewModelProviders.of(this,factory).get(AllOrdersViewModel::class.java)
        setSupportActionBar(binding.toolbar)

        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )

        binding.back.setOnClickListener(View.OnClickListener {
            finish()
        })
        loadRunnable= Runnable {
            getOrders()
        }
        binding.pbar.show()
        getOrders()
    }
    private fun setupViewPager(viewPager: ViewPager,response: GetOrderResponse) {
        splitArrays(response)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        response.data.invocieLineItems?.let { allOrdersList?.let { it1 -> AllOrdersFragment.newInstance(it1, it) } }?.let { adapter.addFragment(it, "All Orders") }
        completedList?.let { response.data.invocieLineItems?.let { it1 -> CompletedOrdersFragment.newInstance(it, it1) } }?.let { adapter.addFragment(it, "Complete") }
        rejectedList?.let { response.data.invocieLineItems?.let { it1 -> RejectedOrdersFragment.newInstance(it, it1) } }?.let { adapter.addFragment(it, "Rejected") }
        viewPager.adapter = adapter
    }
    private fun splitArrays(response: GetOrderResponse)
    {
        completedList = ArrayList<CustomerInvoiceData>()
        rejectedList = ArrayList<CustomerInvoiceData>()
        allOrdersList = ArrayList<CustomerInvoiceData>()
        if (response.data != null) {
            if (response.data.customerInvoiceData != null) {
                val custInvoiceList = response.data.customerInvoiceData
                for (i in 0 until custInvoiceList!!.size) {
                    val model = custInvoiceList.get(i)
                    if (model.OrderStatus?.toLowerCase().equals("delivered") || model.OrderStatus?.toLowerCase().equals("completed")) {
                        completedList?.add(model)
                    }
                    if (model.OrderStatus?.toLowerCase().equals("rejected") || model.OrderStatus?.toLowerCase().equals("cancelled") ) {
                        rejectedList?.add(model)
                    }
                    allOrdersList?.add(model)
                }
            }
        }


        //-------------------------------------------------------

        //-------------------------------------------------------
    }
    internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    fun getOrders()
    {
        val range=getDateRange()
        val rangeStrArr=range.split("T")
        val startDate=rangeStrArr[0]
        val endDate=rangeStrArr[1]
        val jsonobject= JSONObject()
        jsonobject.put("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonobject.put("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonobject.put("merchant_id", preference.getIntData(Constants.saveMerchantIdKey))
        jsonobject.put("start_date", startDate)
        jsonobject.put("page_size", 100)
        jsonobject.put("page_number", 1)
        jsonobject.put("end_date", endDate)

        Log.i("getMultipleStoreDetails",jsonobject.toString())

          lifecycleScope.launch {
              try {
                  getOrdResponse=viewmodel.callAllOrders(preference.getStringData(Constants.saveaccesskey),
                      preference.getStringData(Constants.saveMobileNumkey),
                      preference.getIntData(Constants.saveMerchantIdKey),
                      startDate,
                      10, currentPage,
                      endDate)
                  binding.pbar.dismiss()
                  if(getOrdResponse?.status.equals("ERROR"))
                  {
                      okDialogWithOneAct(Constants.appName,"No orders for this store")
                  }else
                  {
                      getOrdResponse.let {
                          setupViewPager(binding.viewpager, getOrdResponse!!)
                          binding.tabs.setupWithViewPager(binding.viewpager)
                      }
                  }

              }catch (e: NoInternetExcetion)
              {
                  binding.pbar.dismiss()
                  networkDialog()
              }catch (e:CancellationException)
              {
                  binding.pbar.dismiss()
                  Log.i("scope","job is canceled")
              }
              catch (e:Exception)
              {
                  binding.pbar.dismiss()
                  okDialogWithOneAct("Error",e.message.toString())
              }
          }
    }

    fun getDateRange(): String {
        var begining: Date
        var end: Date
        run {
            val calendar: Calendar = getCalendarForNow()
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            setTimeToBeginningOfDay(calendar)
            begining = calendar.getTime()
        }
        run {
            val calendar: Calendar = getCalendarForNow()
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            setTimeToEndofDay(calendar)
            end = calendar.getTime()
        }
        val date=getDateString(begining) +"T"+ getDateString(end)
        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateString(d:Date) : String
    {
        val sdf = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val parsedDate = sdf.parse(d.toString())
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val strDate = dateFormat.format(parsedDate)
        return strDate
    }

    private fun getCalendarForNow(): Calendar {
        val calendar: Calendar = GregorianCalendar.getInstance()
        calendar.setTime(Date())
        return calendar
    }

    private fun setTimeToBeginningOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setTimeToEndofDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }
}