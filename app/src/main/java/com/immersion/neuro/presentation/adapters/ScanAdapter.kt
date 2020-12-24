package com.immersion.neuro.presentation.adapters

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.immersion.neuro.R
import com.immersion.neuro.connection.ble.BleDevice
import com.immersion.neuro.connection.ble.ConnectionStatus
import com.immersion.neuro.model.DataDisplay
import kotlinx.android.synthetic.main.item_scanned_device.view.*

class ScanAdapter(private val scanListener: ScanItemClickListener) :
    ListAdapter<BleDevice, ScanAdapter.ViewHolder>(ScanDiffCallback()) {

    private lateinit var dataDisplay: DataDisplay

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_scanned_device, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), scanListener)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val batteryInfo: TextView = view.batteryInfo
        private val heartRate: TextView = view.heartRate
        private val address: TextView = view.scan_address
        private val connected: TextView = view.connected
        private val connecting: ProgressBar = view.connect_progress
        private val services: RecyclerView = view.services
        private val disconnect: ImageView = view.disconnect

        fun bind(item: BleDevice, scanListener: ScanItemClickListener) {
            address.text = view.context.getString(
                R.string.two_rows, item.device.name
                    ?: "Unknown", item.device.address
            )

            try {
                if (dataDisplay.battery != null)
                    batteryInfo.text = dataDisplay.battery
                if (dataDisplay.heartRate != null)
                    heartRate.text = dataDisplay.heartRate
            }catch (e: Exception){

            }

            if (item.services.isNotEmpty()) {
                services.layoutManager = LinearLayoutManager(view.context)
                val itemDecor = DividerItemDecoration(view.context, RecyclerView.VERTICAL)
                services.addItemDecoration(itemDecor)
                val serviceAdapter = ServiceAdapter(scanListener)
                services.adapter = serviceAdapter
                serviceAdapter.submitList(item.services)
            }

            when (item.connectionStatus) {
                is ConnectionStatus.NotEngaged -> {
                    connected.visibility = View.GONE
                    connecting.visibility = View.GONE
                    services.visibility = View.GONE
                    disconnect.visibility = View.GONE
                    batteryInfo.visibility = View.GONE
                    heartRate.visibility = View.GONE
                }
                is ConnectionStatus.Connecting -> {
                    connected.visibility = View.GONE
                    connecting.visibility = View.VISIBLE
                    services.visibility = View.GONE
                    disconnect.visibility = View.GONE
                    batteryInfo.visibility = View.GONE
                    heartRate.visibility = View.GONE
                }
                is ConnectionStatus.Success -> {
                    connected.visibility = View.VISIBLE
                    connecting.visibility = View.GONE
                    services.visibility = View.VISIBLE
                    disconnect.visibility = View.VISIBLE
                    batteryInfo.visibility = View.VISIBLE
                    heartRate.visibility = View.VISIBLE
                }
                is ConnectionStatus.Error -> {
                    connected.visibility = View.GONE
                    connecting.visibility = View.GONE
                    services.visibility = View.GONE
                    disconnect.visibility = View.GONE
                    batteryInfo.visibility = View.GONE
                    heartRate.visibility = View.GONE
                }
            }

            disconnect.setOnClickListener { scanListener.onDisconnectClicked() }

            if (item.connectionStatus is ConnectionStatus.NotEngaged || item.connectionStatus is ConnectionStatus.Error) {
                view.setOnClickListener {
                    scanListener.onScanItemClicked(item.device)
                }
            }
        }
    }

    class ScanDiffCallback : DiffUtil.ItemCallback<BleDevice>() {
        override fun areItemsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean =
            oldItem?.device?.address == newItem?.device?.address

        override fun areContentsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean =
            oldItem == newItem
    }

    fun updateData(data: DataDisplay) {
        this.dataDisplay = data
        notifyDataSetChanged()
    }

    interface ScanItemClickListener {
        fun onDisconnectClicked()
        fun onScanItemClicked(device: BluetoothDevice)
        fun onSetCharacteristicNotifications(characteristic: BluetoothGattCharacteristic)
        fun onCharacteristicIndicate(characteristic: BluetoothGattCharacteristic)
        fun onReadCharacteristic(characteristic: BluetoothGattCharacteristic)
        fun onWriteCharacteristic(characteristic: BluetoothGattCharacteristic, value: String)
    }
}