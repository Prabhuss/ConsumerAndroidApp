package com.getpy.express.ui.shopping

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getpy.fresh.views.home.OffersViewModel
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.VerticalScrollImagesAdapter
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.model.GetOfferDetailsData
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentShoppingBinding
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.main.MainActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShoppingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShoppingFragment : InjectionFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val factory: OffersViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    private lateinit var viewmodel : OffersViewModel
    private lateinit var binding: FragmentShoppingBinding
    private val appDataBase: AppDataBase by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_shopping, container, false)
        viewmodel= ViewModelProviders.of(this,factory).get(OffersViewModel::class.java)
        binding.offersRecyclerview.setHasFixedSize(true)
        binding.offersRecyclerview.itemAnimator=null
        binding.offersRecyclerview.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        binding.pbar.show()
        getOfferDetails()
        return binding.root
    }

    fun getOfferDetails()
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response=viewmodel.getOfferDetails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                binding.pbar.dismiss()
                if(response.status?.toLowerCase().equals(Constants.status)) {
                    binding.offersRecyclerview.showView()
                    val adapter = activity?.let {
                        response.data?.let { it1 ->
                            VerticalScrollImagesAdapter(it, it1, object : ItemClickListener {
                                override fun onItemClick(view: View?, position: Int) {
                                    binding.pbar.show()
                                    UbboFreshApp.instance?.isCmgfromHomeItemClick = true
                                    val model = response.data!!.get(position)
                                    binding.pbar.show()
                                    getofferproducts(model)
                                }

                            })
                        }
                    }
                    binding.offersRecyclerview.adapter = adapter
                }else
                {
                    binding.offersRecyclerview.hideView()
                }
            }catch (e: NoInternetExcetion)
            {
                binding.pbar.dismiss()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.pbar.dismiss()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }

        }
    }
    fun getofferproducts(model: GetOfferDetailsData)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response= viewmodel.getofferProduct(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey),
                        model.OfferCode!!)
                binding.pbar.dismiss()
                if(response.status.toLowerCase().equals(Constants.status)) {
                    UbboFreshApp.instance?.pordlist = response.data
                    callSubCategories()
                }else
                {
                    if(response.message.toLowerCase().equals(Constants.message))
                    {
                        activity?.okDialogWithNavigateToLogin(requireActivity(),preference,Constants.appName,response.message)
                    }else
                    {
                        activity?.okDialogWithOneAct("Error",response.message)
                    }
                }
            }catch (e: NoInternetExcetion)
            {
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }

        }

    }
    fun callSubCategories()
    {
        if(MainActivity.navcontroller?.currentDestination?.id==R.id.shoppingFragment) {
            MainActivity.navcontroller?.navigate(R.id.action_shoppingFragment_to_productsFragment)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShoppingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShoppingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}