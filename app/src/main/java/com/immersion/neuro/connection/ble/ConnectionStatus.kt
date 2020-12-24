package com.immersion.neuro.connection.ble


sealed class ConnectionStatus {

    object NotEngaged : ConnectionStatus() {
    }

    object Connecting : ConnectionStatus() {
    }

    object Success : ConnectionStatus() {
    }

    object Error : ConnectionStatus() {
    }
}