package com.getpy.express.ui.categories
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.getpy.fresh.adaters.CategoriesExandAdapter
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.data.model.CategoriesExpModel
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentCategoriesBinding
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.main.MainActivity
import com.getpy.express.ui.main.MainViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class CategoriesFragment : InjectionFragment(),KodeinAware {
    private val factory: MainViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)
        val view=binding.root
        MainActivity.binding.activityMainAppbarlayout.showView()
        MainActivity.binding.selectStore.hideView()
        MainActivity.binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
        MainActivity.binding.activityMainToolbarTitle.setText("Categories")
        //binding.recyclerview.setHasFixedSize(true)
        runable= Runnable {
            if(MainActivity.navcontroller?.currentDestination?.id==R.id.categoriesFragment) {
                MainActivity.navcontroller?.navigate(R.id.action_categoriesFragment_to_productsFragment)
            }
        }
        binding.pbar.show()
        if(UbboFreshApp.instance?.totCatList==null) {
            UbboFreshApp.instance?.totCatList= ArrayList<CategoriesExpModel>()
            callMainCategories(preference.getIntData(Constants.saveMerchantIdKey))
        }else if(UbboFreshApp.instance?.totCatList?.size==0)
        {
            callMainCategories(preference.getIntData(Constants.saveMerchantIdKey))
        }else
        {
            binding.pbar.dismiss()
            expAdapter= activity?.let { CategoriesExandAdapter(preference,it, UbboFreshApp.instance?.totCatList!!)}
            binding.recyclerview.adapter=expAdapter
        }
        return view
    }

    override fun onResume() {
        super.onResume()
//        if(totCatList==null) {
//            callMainCategories(preference.getIntData(Constants.saveMerchantIdKey))
//        }else
//        {
//            expAdapter= activity?.let { CategoriesExandAdapter(it,totCatList!!)}
//            binding.recyclerview.adapter=expAdapter
//        }
    }

    fun callMainCategories(merchantid:Int)
    {
        lifecycleScope.launch {
            try {
                val mainAndSuncatModel= MainActivity.viewmodel.MainAndSubGategory(
                        preference.getStringData(Constants.saveaccesskey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        merchantid,
                        "NULL"
                )
                val list= mainAndSuncatModel?.data!!
                callSubcategories(list,merchantid)
            }catch (e: NoInternetExcetion)
            {
                binding.pbar.dismiss()
                MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
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

    fun callSubcategories(list:ArrayList<MainAndSubCatDataModel>,merchantid: Int)
    {
        lifecycleScope.launch {
            try {
                for(i in 0 until list.size)
                {
                    val model=list.get(i)
                    val subCat1= MainActivity.viewmodel.MainAndSubGategory(
                            preference.getStringData(Constants.saveaccesskey),
                            preference.getStringData(Constants.saveMobileNumkey),
                            merchantid,
                            model.SubCategoryId!!
                    )
                    UbboFreshApp.instance?.totCatList?.add(CategoriesExpModel(model,subCat1.data))
                }
                binding.pbar.dismiss()
                expAdapter= activity?.let { UbboFreshApp.instance?.totCatList?.let { it1 -> CategoriesExandAdapter(preference,it, it1) } }
                binding.recyclerview.adapter=expAdapter


            }catch (e: NoInternetExcetion)
            {
                binding.pbar.dismiss()
                MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
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
    companion object
    {
        var runable:Runnable?=null
        var expAdapter:CategoriesExandAdapter?=null
    }

}