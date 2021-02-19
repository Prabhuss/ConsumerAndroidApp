package com.getpy.express.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.BottomSheetPagerAdapter
import com.getpy.express.data.model.CustomerInvoiceData
import com.getpy.express.data.model.InvocieLineItems
import com.getpy.express.data.model.SlidingImageData
import com.getpy.express.databinding.InstructionFragmentDemoSheetBinding
import com.getpy.express.databinding.SummaryFragmentDemoSheetBinding

class InstructionBottomSheetFragment() : SuperBottomSheetFragment() {
    lateinit var binding:InstructionFragmentDemoSheetBinding
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding=DataBindingUtil.inflate(inflater,R.layout.instruction_fragment_demo_sheet, container, false)
        init()
        binding.close.setOnClickListener(View.OnClickListener {
           dismiss()
        })
        binding.addBut.setOnClickListener {
            UbboFreshApp.instance?.instructionString=binding.addInstEdit.text.toString()
            dismiss()
        }

        binding.addInstEdit.doAfterTextChanged {
            UbboFreshApp.instance?.instructionString=it.toString()
        }

        if(!TextUtils.isEmpty(UbboFreshApp.instance?.instructionString))
        {
            binding.addInstEdit.setText(UbboFreshApp.instance?.instructionString)
        }

        return binding.root
    }
    fun init()
    {
        binding.instDesc.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.addInstEdit.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.text.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.addBut.setTypeface(UbboFreshApp.instance?.latoregular)
    }


}