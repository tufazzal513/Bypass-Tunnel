package com.v2ray.ang.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.v2ray.ang.R

class MoreMenuBottomSheet : BaseBottomSheetFragment() {

    interface OnMoreOptionClickListener {
        fun onMoreOptionClicked(viewId: Int)
    }

    private var mListener: OnMoreOptionClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMoreOptionClickListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnMoreOptionClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.uwu_bottom_sheet_more_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clickListener = View.OnClickListener {
            mListener?.onMoreOptionClicked(it.id)
            dismiss()
        }

        val actionIds = listOf(
            R.id.service_restart,
            R.id.del_all_config,
            R.id.del_duplicate_config,
            R.id.del_invalid_config,
            R.id.export_all,
            R.id.ping_all,
            R.id.real_ping_all,
            R.id.sort_by_test_results,
            R.id.locate_selected_config,
            R.id.sub_update
        )

        actionIds.forEach { id ->
            view.findViewById<View>(id)?.setOnClickListener(clickListener)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {
        const val TAG = "MoreMenuBottomSheet"
    }
}
