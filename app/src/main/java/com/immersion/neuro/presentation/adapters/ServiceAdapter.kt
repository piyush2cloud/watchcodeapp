package com.immersion.neuro.presentation.adapters

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.immersion.neuro.R
import com.immersion.neuro.connection.ble.GattService
import com.immersion.neuro.utils.getShortUuid
import kotlinx.android.synthetic.main.item_service.view.*

class ServiceAdapter(private val scanListener: ScanAdapter.ScanItemClickListener) : ListAdapter<BluetoothGattService, ServiceAdapter.ViewHolder>(
    ServiceDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_service, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), scanListener)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val uuid: TextView = view.uuid
        private val charRecycler: RecyclerView = view.characteristics

        fun bind(item: BluetoothGattService, scanListener: ScanAdapter.ScanItemClickListener) {
            val typeString =
                    if (item.type == SERVICE_TYPE_PRIMARY) view.context.getString(R.string.service_primary)
                    else view.context.getString(R.string.service_secondary)

            val serviceName = try {
                GattService.getGattServiceName(item.uuid.getShortUuid()) + " - " + item.uuid.getShortUuid()
            } catch (e: IllegalStateException) {
                view.context.getString(R.string.unknown_service, item.uuid.getShortUuid())
            }

            uuid.text = view.context.getString(R.string.two_rows, serviceName, typeString)

            charRecycler.layoutManager = LinearLayoutManager(view.context)
            charRecycler.adapter = CharacteristicsAdapter(scanListener)
            (charRecycler.adapter as? CharacteristicsAdapter)?.submitList(item.characteristics)

//            item.characteristics.get(0).descriptors.get(0).characteristic.uuid.getShortUuid()
//            item.characteristics.get(1).descriptors.get(0).characteristic.uuid.getShortUuid()
//
//            item.characteristics.get(0).uuid.getShortUuid()
//            item.characteristics.get(1).uuid.getShortUuid()
//
//            GattService.getGattServiceName(item.characteristics.get(0).uuid.getShortUuid()) + " - " + item.uuid.getShortUuid()

        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<BluetoothGattService>() {

        override fun areItemsTheSame(
            oldItem: BluetoothGattService,
            newItem: BluetoothGattService
        ): Boolean = oldItem?.uuid == newItem?.uuid

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BluetoothGattService,
            newItem: BluetoothGattService
        ): Boolean = oldItem == newItem
    }
}