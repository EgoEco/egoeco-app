package com.example.egoeco_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.ObdDataViewholderBinding
import com.example.egoeco_app.model.OBDData

class OBDListAdapter :
    ListAdapter<OBDData, OBDDataViewHolder>(OBDDataDiffUtilCallback()) {
    lateinit var listener: OBDListAdapterListener

    interface OBDListAdapterListener {
        fun onClicked(data: OBDData)
    }

    fun setOBDListAdapterListener(listener: OBDListAdapterListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OBDDataViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.obd_data_viewholder, parent, false)
        return OBDDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: OBDDataViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.root.setOnClickListener {
            listener.onClicked(getItem(holder.adapterPosition))
        }
    }

}

class OBDDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: ObdDataViewholderBinding by lazy { ObdDataViewholderBinding.bind(itemView) }
    fun bind(data: OBDData) {
        binding.apply {
            obdDataVehicleIdTextView.text = data.vehicleId.toString()
            obdDataRPMTextView.text = data.rpm.toString()
        }
    }
}

class OBDDataDiffUtilCallback : DiffUtil.ItemCallback<OBDData>() {
    override fun areItemsTheSame(oldItem: OBDData, newItem: OBDData) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: OBDData, newItem: OBDData) = oldItem == newItem
}
