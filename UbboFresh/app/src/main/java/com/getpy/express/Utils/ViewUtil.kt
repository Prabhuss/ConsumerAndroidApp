package com.getpy.express.Utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.OkCustomDialogBinding
import com.getpy.express.databinding.YesornoCustomDialogBinding
import com.getpy.express.ui.auth.LoginActivity
import com.getpy.express.ui.main.MainActivity
import com.getpy.express.ui.multistore.MultiStoreActivity
import java.util.regex.Pattern

const val REG = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}\$"
var PATTERN: Pattern = Pattern.compile(REG)
fun CharSequence.isPhoneNumber() : Boolean = PATTERN.matcher(this).find()
fun Context.toast(message:String)
{
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}
fun Context.log(message: String)
{
    Log.d("otp",message)
}
fun ProgressBar.show()
{
    visibility= View.VISIBLE
}
fun ProgressBar.dismiss()
{
    visibility= View.GONE
}

fun View.showView()
{
    visibility=View.VISIBLE
}
fun View.hideView()
{
    visibility=View.GONE
}

fun getStringData(preference: PreferenceProvider,key:String):String
{
    return preference.getStringData(key)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}
fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun TextInputLayout.markRequiredInRed() {
    hint = buildSpannedString {
        append(hint)
        color(Color.RED) { append(" *") } // Mind the space prefix.
    }
}
fun checkStringIsEmpty(text:String):Boolean
{
    if(TextUtils.isEmpty(text))
    {
        return true
    }else
    {
        if(text.equals(" "))
        {
            return true
        }else{
            return false
        }

    }
}

fun View.snakBar(message: String)
{
    Snackbar.make(this,message,Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok"){
            snackbar.dismiss()
        }
    }.show()
}

fun formatString(value:Double):String
{
    return String.format("%.2f",value)
}
fun formatStrWithPrice(value:String):String
{
    var str:String=""
    try {
        if(checkStringIsEmpty(value)) {
            return str
        }else
        {
            str= Constants.priceSymbol + String.format("%.2f", value.toDouble())
        }
    }catch (e:NumberFormatException)
    {
        e.printStackTrace()
    }
    return str
}
fun multiPlyVaue(val1:Int,val2:String):String
{
    return Constants.priceSymbol+formatString(val1.toDouble().times(val2.toDouble()))
}
fun Context.networkDialog()
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding : OkCustomDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.cancelText.hideView()
    binding.header.text = Constants.appName
    binding.message.text="Please check network"
    binding.okText.text="Ok"
    binding.okText.setOnClickListener { dialog.dismiss() }
    if(this!=null)
    dialog.show()
}

fun Context.okDialogWithOneAct(title:String,message:String,sendOtpBut:AppCompatButton)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding :OkCustomDialogBinding= DataBindingUtil.inflate(
        LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.header.text = title
    binding.message.text=message
    binding.okText.text="Ok"
    binding.cancelText.hideView()
    binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.okText.setOnClickListener {
        sendOtpBut.setBackgroundResource(R.drawable.rounded_corner)
        sendOtpBut.setTextColor(Color.BLACK)
        dialog.dismiss()
    }
    if(this!=null)
    dialog.show()
}
fun Context.okDialogWithOneAct(title:String,message:String)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding :OkCustomDialogBinding= DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.cancelText.hideView()
    binding.header.text = title
    binding.message.text=message
    binding.okText.text="Ok"
    binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.okText.setOnClickListener {
        dialog.dismiss()
    }
    if(this!=null)
    dialog.show()
}

fun Context.okDialogWithNavigateToLogin(activity: Activity,preference:PreferenceProvider,title:String,message:String)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding :OkCustomDialogBinding= DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.header.text = title
    binding.message.text=message
    binding.okText.text="Ok"
    binding.cancelText.hideView()
    binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.okText.setOnClickListener {
        dialog.dismiss()
        preference.saveBoolData(Constants.saveMultistore,false)
        preference.saveBoolData(Constants.savelogin,false)
        val intent= Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity.finish()
    }
    if(this!=null)
    dialog.show()
}

fun Context.okLogOutDialog(activity: Activity,preference:PreferenceProvider,title:String,message:String)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding :OkCustomDialogBinding= DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.header.text = title
    binding.message.text=message
    binding.okText.text="Ok"
    binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.okText.setOnClickListener {
        dialog.dismiss()
        UbboFreshApp.instance?.instructionString=""
        preference.saveBoolData(Constants.saveMultistore,false)
        preference.saveBoolData(Constants.savelogin,false)
        val intent= Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity.finish()
    }
    binding.cancelText.setOnClickListener(View.OnClickListener {
        dialog.dismiss()
    })
    if(this!=null)
        dialog.show()
}

fun Context.okDialogWithNavigateToStore(activity: Activity,preference:PreferenceProvider,title:String,message:String)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding :OkCustomDialogBinding= DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.header.text = title
    binding.message.text=message
    binding.okText.text="Ok"
    binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.okText.setOnClickListener {
        dialog.dismiss()
        if(UbboFreshApp.instance?.totCatList!=null) {
            UbboFreshApp.instance?.totCatList?.clear()
        }
        val intent = Intent(this, MultiStoreActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity.finish()
    }
    binding.cancelText.setOnClickListener(View.OnClickListener {
        dialog.dismiss()
    })
    if(this!=null)
        dialog.show()
}



fun Context.navigateToMain(title:String,message:String)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding :OkCustomDialogBinding= DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.header.text = title
    binding.message.text=message
    binding.cancelText.hideView()
    binding.okText.text="Ok"
    binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.okText.setOnClickListener {
        dialog.dismiss()
        MainActivity.navcontroller?.navigate(R.id.homeFragment)
    }
    if(this!=null)
    dialog.show()
}

fun Context.yesOrNoDialogWithTwoAct(title:String,message:String)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding: YesornoCustomDialogBinding = DataBindingUtil.inflate(
        LayoutInflater.from(this), R.layout.yesorno_custom_dialog, null, false);
    dialog.setContentView(binding.root)
    binding.header.text = title
    binding.message.text=message
    binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.yesText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.noText.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
    binding.yesText.setOnClickListener(View.OnClickListener {  })
    binding.noText.setOnClickListener(View.OnClickListener {  })
    if(this!=null)
    dialog.show()
}
