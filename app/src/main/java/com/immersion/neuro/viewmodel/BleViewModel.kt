package com.immersion.neuro.viewmodel

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.immersion.neuro.connection.ble.BleDevice
import com.immersion.neuro.connection.ble.ConnectionStatus
import com.immersion.neuro.connection.ble.GattPropertiesMapping
import com.immersion.neuro.model.DataDisplay
import com.immersion.neuro.connection.network.repository.BatteryRepository
import com.immersion.neuro.connection.network.repository.HeartRateRepository
import java.io.UnsupportedEncodingException
import java.util.*


class BleViewModel : ViewModel() {

    companion object {
        const val TAG = "BleViewModel"
        const val SCAN_PERIOD = 10_000L // scanning for 5 seconds
    }

    private var bleAdapter: BluetoothAdapter? = null

    private val scanResultsObservable: MutableLiveData<HashMap<String, BleDevice>> =
        MutableLiveData()

    fun getScanResults() = scanResultsObservable

    private var dataDisplay = DataDisplay()
    private val dateDisplayObservable: MutableLiveData<DataDisplay> = MutableLiveData()
    fun getHeartBatteryInfo() = dateDisplayObservable

    private val scanResults = hashMapOf<String, BleDevice>()

    private var isScanning = false

    private var isConnected = false

    private var scanHandler: Handler? = null

    private var gatt: BluetoothGatt? = null

    private var connectedDevice: BleDevice? = null

    private val connectionStatusLiveData: MutableLiveData<BleDevice> = MutableLiveData()

    fun getConnectionStatus() = connectionStatusLiveData

    private val gattClientCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnectGattServer(ConnectionStatus.Error)
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true
                connectedDevice?.let {
                    connectionStatusLiveData.postValue(
                        BleDevice(
                            it.device,
                            it.services,
                            connectionStatus = ConnectionStatus.Success
                        )
                    )
                }
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disconnectGattServer(ConnectionStatus.Error)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }

            gatt?.services?.let { services ->


                //Search For Battery Information
                val batteryService: BluetoothGattService =
                    gatt?.getService(UUID.fromString(GattPropertiesMapping.GattProperty.BATTERY_SERVICE.value));
                if (batteryService == null) {
                    Log.d(TAG, "Battery service not found!");
                    return;
                }
                val batteryLevel: BluetoothGattCharacteristic =
                    batteryService.getCharacteristic(UUID.fromString(GattPropertiesMapping.GattProperty.BATTERY_LEVEL.value));
                if (batteryLevel == null) {
                    Log.d(TAG, "Battery level not found!");
                    return;
                }
                gatt.readCharacteristic(batteryLevel);


                //Search For Device and Device Serial Information
//                var deviceInfo : BluetoothGattService = gatt?.getService(UUID.fromString(GattPropertiesMapping.GattProperty.DEVICE_INFO_SERVICE.value));
//                if(deviceInfo == null) {
//                    Log.d(TAG, "Device service not found!");
//                }
//                var manufactureName : BluetoothGattCharacteristic = deviceInfo.getCharacteristic(UUID.fromString(GattPropertiesMapping.GattProperty.MANUFACTURE_NAME.value));
//                if(manufactureName == null) {
//                    Log.d(TAG, "Manufacture Name not found!");
//                    return;
//                }
//                gatt.readCharacteristic(manufactureName);


//                connectedDevice?.let {
//                    connectedDevice = BleDevice(it.device, services, ConnectionStatus.Success())
//                    connectionStatusLiveData.postValue(connectedDevice)
//                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            if (characteristic?.uuid?.toString()
                    .equals(GattPropertiesMapping.GattProperty.HEART_RATE_MEASUREMENT.value)!!
            ) {
                val messageString: String = try {
                    characteristic?.value?.elementAt(1).toString()
                } catch (e: UnsupportedEncodingException) {
                    Log.e(TAG, "Unable to convert message bytes to string")
                    "Error"
                }

                Log.d("PIYUSH", messageString)
                dataDisplay.heartRate = "Heart Rate : ".plus(messageString)
                dateDisplayObservable.postValue(dataDisplay)

                HeartRateRepository.sendHeartData(messageString.toInt())
                Log.i(TAG, "characteristic value: $messageString")
                setValueToCharacteristic(characteristic)
            }


        }

        fun convertFromInteger(i: Int): UUID? {
            val MSB = 0x0000000000001000L
            val LSB = -0x7fffff7fa064cb05L
            val value = (i and (-0x1.toLong()).toInt()).toLong()
            return UUID(MSB or (value shl 32), LSB)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            val characteristic =
                gatt!!.getService(convertFromInteger(0x180D))
                    .getCharacteristic(convertFromInteger(0x2A39))
            characteristic.value = byteArrayOf(1, 1)
            gatt.writeCharacteristic(characteristic)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.i(TAG, "onCharacteristicWrite status: $status")
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Log.i(
                TAG, "onCharacteristicRead status: $status, value: ${
                    String(
                        characteristic?.value
                            ?: byteArrayOf(0)
                    )
                }"
            )

            if (characteristic?.uuid?.toString()
                    .equals(GattPropertiesMapping.GattProperty.BATTERY_LEVEL.value)!!
            ) {
                val value: Int? =
                    characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                value?.let { Integer.toString(it) }?.let {
                    Log.d("PIYUSH", it)
                    dataDisplay.battery = "Battery : ".plus(it)
                    BatteryRepository.sendBatteryData(it.toInt())
                };
                readDeviceInfo(gatt);
            }

            if (characteristic?.uuid?.toString()
                    .equals(GattPropertiesMapping.GattProperty.MANUFACTURE_NAME.value)!!
            ) {
                val value: String? = characteristic?.getStringValue(0);
                if (value != null) {
                    Log.d("PIYUSH", value)
                    dataDisplay.manufacturer = "Manufacturer : ".plus(value)
                    readHeartInfo(gatt);
                };
            }
            setValueToCharacteristic(characteristic)
        }

        private fun setValueToCharacteristic(characteristic: BluetoothGattCharacteristic?) {
            connectedDevice?.let {
                if (it.services.isEmpty()) {
                    Log.i(TAG, "No services with characteristics found.")
                    return
                }
                it.services.first { it.characteristics.contains(characteristic) }
                    .getCharacteristic(characteristic?.uuid)?.value = characteristic?.value
                connectionStatusLiveData.postValue(
                    BleDevice(
                        it.device,
                        it.services,
                        ConnectionStatus.Success
                    )
                )
            }

        }
    }

    fun readDeviceInfo(gatt: BluetoothGatt?) {
        val deviceInfo: BluetoothGattService =
            gatt?.getService(UUID.fromString(GattPropertiesMapping.GattProperty.DEVICE_INFO_SERVICE.value))!!;
        if (deviceInfo == null) {
            Log.d(TAG, "Device service not found!");
        }
        val manufactureName: BluetoothGattCharacteristic =
            deviceInfo.getCharacteristic(UUID.fromString(GattPropertiesMapping.GattProperty.MANUFACTURE_NAME.value));
        if (manufactureName == null) {
            Log.d(TAG, "Manufacture Name not found!");
            return;
        }
        gatt.readCharacteristic(manufactureName);
    }

    fun readHeartInfo(gatt: BluetoothGatt?) {
        //Search For Heart Rate
        val heartInfo: BluetoothGattService =
            gatt?.getService(UUID.fromString(GattPropertiesMapping.GattProperty.HEART_SERVICE.value))!!;
        if (heartInfo == null) {
            Log.d(TAG, "Heart service not found!");
        }
        val heartRateInfo: BluetoothGattCharacteristic =
            heartInfo.getCharacteristic(UUID.fromString(GattPropertiesMapping.GattProperty.HEART_RATE_MEASUREMENT.value));
        val heartControlPoint: BluetoothGattCharacteristic =
            heartInfo.getCharacteristic(UUID.fromString(GattPropertiesMapping.GattProperty.HEART_RATE_CONTROL_POINT.value));
        if (heartRateInfo != null && heartControlPoint != null) {
            Log.d(TAG, "Heart Rate  not found!");
            val descriptor: BluetoothGattDescriptor =
                heartRateInfo.getDescriptor(UUID.fromString(GattPropertiesMapping.GattProperty.HEART_MEASRUE_DESCRIPTOR.value));
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
            gatt.setCharacteristicNotification(heartRateInfo, true)
            return;
        }
    }


    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                Log.d("PIYUSH", it.device.toString());
                var isIncluded : Boolean = false;
                it.scanRecord?.serviceUuids?.forEach{
                    if(it?.equals(ParcelUuid.fromString(GattPropertiesMapping.GattProperty.HEART_SERVICE.value))!!){
                        isIncluded=true;
                    }
                }
                if (isIncluded)
                    scanResults.put(it.device.address, BleDevice(it.device))
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach {
                scanResults[it.device.address] = BleDevice(it.device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.i(TAG, "Scan failed with errorCode $errorCode")
            scanResultsObservable.postValue(hashMapOf())
        }
    }

    fun setBluetoothAdapter(adapter: BluetoothAdapter?) {
        bleAdapter = adapter
    }

    fun isBleEnabled(): Boolean = bleAdapter != null && bleAdapter?.isEnabled == true

    fun scan() {
        val filters = arrayListOf<ScanFilter>()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        bleAdapter?.bluetoothLeScanner?.startScan(filters, settings, scanCallback)

        isScanning = true
        scanHandler = Handler()
        scanHandler?.postDelayed(this::stopScan, SCAN_PERIOD)
    }

    fun stopScan() {
        if (isScanning && bleAdapter != null && (bleAdapter?.isEnabled == true) && bleAdapter?.bluetoothLeScanner != null) {
            bleAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
            scanResultsObservable.postValue(scanResults)

            isScanning = false
            scanHandler = null
        }
    }

    fun connectDevice(device: BluetoothDevice, context: Context) {
        connectedDevice = BleDevice(device)
        gatt = device.connectGatt(context, false, gattClientCallback)
    }

    fun disconnectGattServer(status: ConnectionStatus) {
        connectedDevice?.let {
            connectionStatusLiveData.postValue(BleDevice(it.device, it.services, status))
        }
        isConnected = false
        gatt?.disconnect()
        gatt?.close()
    }

    fun signUpForCharacteristicNotifications(characteristic: BluetoothGattCharacteristic) {
//        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;

        try {
            characteristic.descriptors.get(0)
                .setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            gatt?.writeDescriptor(characteristic.descriptors.get(0))
        } catch (e: Exception) {

        }

//        val initialized = gatt?.setCharacteristicNotification(characteristic, true)
//        Log.i(TAG, "Characteristic notifications initialized: $initialized")
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        val success: Boolean = gatt?.readCharacteristic(characteristic) ?: false
        Log.i(TAG, "read characteristic success: $success")
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, value: String) {
        characteristic.setValue(value)
        val success: Boolean = gatt?.writeCharacteristic(characteristic) ?: false
        Log.i(TAG, "write characteristic success: $success")
    }

    fun signUpForCharacteristicIndications(characteristic: BluetoothGattCharacteristic) {
        characteristic.writeType = BluetoothGattCharacteristic.PROPERTY_INDICATE
        val initialized = gatt?.setCharacteristicNotification(characteristic, true)
        Log.i(TAG, "Characteristic indications initialized: $initialized")
    }


}