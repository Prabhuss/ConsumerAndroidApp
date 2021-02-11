package com.getpy.express.ui.ordersummary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.Utils.NoInternetExcetion
import com.getpy.express.Utils.okDialogWithOneAct
import com.getpy.express.Utils.snakBar
import com.getpy.express.adapter.OrderSummaryAdapter
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.data.model.InvocieLineItems
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityOrderSummaryBinding
import com.getpy.express.ui.main.MainActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class OrderSummaryActivity : AppCompatActivity(),KodeinAware{
    override val kodein by kodein()
    private val factory: OrderSumModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var binding:ActivityOrderSummaryBinding
    lateinit var viewmodel:OrderSummaryViewModel
    var cmodel:CustomerInvoiceData?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_order_summary)
        viewmodel=ViewModelProviders.of(this,factory).get(OrderSummaryViewModel::class.java)
        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )

        val ilist=intent.extras?.getParcelableArrayList<InvocieLineItems>("list")
        cmodel=intent.extras?.getParcelable<CustomerInvoiceData>("model")

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.setItemAnimator(null)
        val adapter= ilist?.let { cmodel?.let { it1 -> OrderSummaryAdapter(supportFragmentManager,this, it, it1) } }
        binding.recyclerview.adapter=adapter
        binding.totItemsValue.text=ilist?.size.toString()
        binding.totMrpValue.text=cmodel?.TotalInvoiceAmount
        binding.discountValue.text=cmodel?.DiscountAmount
        binding.deliveryChargesValue.text="Free"
        binding.paymentModValue.text=cmodel?.PaymentMode
        binding.totalValue.text=cmodel?.TotalInvoiceAmount

        binding.reorder.setOnClickListener {
            callReorderService()
        }

        binding.image.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
    fun init()
    {
        binding.ordSummary.setTypeface(UbboFreshApp.instance?.latobold)
        binding.orderDetails.setTypeface(UbboFreshApp.instance?.latobold)
        binding.totItems.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.totItemsValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.totMrp.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.totMrpValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.discount.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.discountValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.deliveryCharges.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.deliveryChargesValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.paymentMod.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.paymentModValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.toal.setTypeface(UbboFreshApp.instance?.latobold)
        binding.totalValue.setTypeface(UbboFreshApp.instance?.latobold)
    }
    fun callReorderService()
    {
        val jsonobject= JSONObject()
        jsonobject.put("merchant_id", preference.getIntData(Constants.saveMerchantIdKey))
        jsonobject.put("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonobject.put("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonobject.put("invoice_id",cmodel?.InvoiceId)
        lifecycleScope.launch {
            try {
                val response= cmodel?.InvoiceId?.let {
                    viewmodel.reorderInvoiceItems(
                            preference.getIntData(Constants.saveMerchantIdKey),
                            preference.getStringData(Constants.saveMobileNumkey),
                            preference.getStringData(Constants.saveaccesskey),
                            it)
                }
                UbboFreshApp.instance?.carItemsList= ArrayList<ProductsDataModel>()
                for(i in 0 until response?.data!!.size)
                {
                    val model=response.data?.get(i)
                    model?.itemCount= 1
                    model?.citrineProdId?.let { UbboFreshApp.instance?.hashMap?.put(it,model) }
                    model?.let { UbboFreshApp.instance?.carItemsList?.add(it) }
                }
                UbboFreshApp.instance?.isComingFromReOrder=true
                val intent = Intent(this@OrderSummaryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }catch (e: NoInternetExcetion)
            {
                MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
}