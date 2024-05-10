package com.and.dialogfragment

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.and.datamodel.DrugDataModel
import com.and.databinding.FragmentSettingDrugDialogBinding

class SettingDrugDialogFragment : DialogFragment() {
    interface OnButtonClickListener {
        fun onRemoveCategoryBtnClick(removeDrugDataModel: DrugDataModel)
        fun onChangeNameBtnClick(oldDrugDataModel: DrugDataModel, newDrugDataModel: DrugDataModel)
        fun onRemoveDetailBtnClick(selectedDrugDataModel: DrugDataModel, selectedDetails: List<String>)
    }
    private var _binding: FragmentSettingDrugDialogBinding? = null
    private val binding get() = _binding!!

    var onButtonClickListener: OnButtonClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingDrugDialogBinding.inflate(inflater, container, false)

        val categoryInfo = arguments?.getSerializable("categoryInfo", DrugDataModel::class.java)?: DrugDataModel()

        binding.apply {
            removeCategoryBtn.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("삭제 할까요?")
                val listener = DialogInterface.OnClickListener { _, ans ->
                    when (ans) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            onButtonClickListener?.onRemoveCategoryBtnClick(categoryInfo)
                            dismiss()
                        }
                    }
                }
                builder.setPositiveButton("네", listener)
                builder.setNegativeButton("아니요", null)
                builder.show()
            }

            changeNameBtn.setOnClickListener {
                val writeDialogFragment = WriteDialogFragment()
                writeDialogFragment.clickYesListener = WriteDialogFragment.OnClickYesListener {
                    onButtonClickListener?.onChangeNameBtnClick(categoryInfo, DrugDataModel(it, categoryInfo.details))
                }
                writeDialogFragment.show(requireActivity().supportFragmentManager, "changeCategory")
                dismiss()
            }

            removeDetailBtn.setOnClickListener {
                if(categoryInfo.details.isEmpty()) {
                    Toast.makeText(requireContext(), "Detail이 없어요!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val deleteDetailFragment = DeleteDetailFragment().apply {
                    val bundle = Bundle()
                    bundle.putSerializable("categoryInfo", categoryInfo)
                    arguments = bundle
                }
                deleteDetailFragment.onRemoveButtonClickListener = DeleteDetailFragment.OnRemoveButtonClickListener {
                    onButtonClickListener?.onRemoveDetailBtnClick(categoryInfo, it)
                }
                deleteDetailFragment.show(requireActivity().supportFragmentManager, "deleteDetails")
                dismiss()
            }
        }
        isCancelable = true
        this.dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.dialog?.window!!.setGravity(Gravity.BOTTOM)
        this.dialog?.window!!.attributes.y = 40
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun resizeDialog() {
        val params: ViewGroup.LayoutParams? = this.dialog?.window?.attributes
        val deviceWidth = Resources.getSystem().displayMetrics.widthPixels
        params?.width = (deviceWidth * 0.95).toInt()
        this.dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}