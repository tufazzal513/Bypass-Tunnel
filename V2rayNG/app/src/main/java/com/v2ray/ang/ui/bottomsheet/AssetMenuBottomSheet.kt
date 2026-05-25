package com.v2ray.ang.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.v2ray.ang.R

class AssetMenuBottomSheet : BaseBottomSheetFragment() {

    interface OnAssetMenuOptionClickListener {
        fun onAssetMenuOptionClicked(viewId: Int)
    }

    private var mListener: OnAssetMenuOptionClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAssetMenuOptionClickListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnAssetMenuOptionClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.uwu_bottom_sheet_asset_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clickListener = View.OnClickListener {
            mListener?.onAssetMenuOptionClicked(it.id)
            dismiss()
        }

        val actionIds = listOf(
            R.id.add_file,
            R.id.add_url,
            R.id.add_qrcode
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
        const val TAG = "AssetMenuBottomSheet"
    }
}
