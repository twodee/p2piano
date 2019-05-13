package org.twodee.p2piano

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class HostActivity : KeyboardActivity() {

  // Exercise
  private val payloadListener = object : PayloadCallback() {
    override fun onPayloadReceived(id: String, payload: Payload) {
      payload.asBytes()?.let { bytes ->
        sendMidiMessage(bytes)
      }
    }

    override fun onPayloadTransferUpdate(id: String, update: PayloadTransferUpdate) {}
  }

  // Exercise
  private val connectionListener = object : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(id: String, info: ConnectionInfo) {
      Toast.makeText(this@HostActivity, "Connecting to ${info.endpointName}.", Toast.LENGTH_SHORT).show()
      Nearby.getConnectionsClient(this@HostActivity).acceptConnection(id, payloadListener)
    }

    override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
      when (result.status.statusCode) {
        ConnectionsStatusCodes.STATUS_OK -> {
          Toast.makeText(this@HostActivity, "Connected", Toast.LENGTH_SHORT).show()
        }
        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
          Toast.makeText(this@HostActivity, "Rejected", Toast.LENGTH_SHORT).show()
        }
        ConnectionsStatusCodes.STATUS_ERROR -> {
          Toast.makeText(this@HostActivity, "Error", Toast.LENGTH_SHORT).show()
        }
      }
    }

    override fun onDisconnected(endpoint: String) {
      Toast.makeText(this@HostActivity, "Disconnected", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    requestPermissions(permissions, 100, {
      advertise()
    }, {
      Toast.makeText(this, "Location not permitted.", Toast.LENGTH_LONG).show()
    })
  }

  // Exercise
  private fun advertise() {
    val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
    Nearby.getConnectionsClient(this).startAdvertising("Chris", "P2Piano", connectionListener, options)
      .addOnSuccessListener {
        Toast.makeText(this, "Advertising...", Toast.LENGTH_LONG).show()
      }.addOnFailureListener {
        Toast.makeText(this, "Failed to advertise...", Toast.LENGTH_LONG).show()
      }
  }
}
