package org.twodee.p2piano

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class JoinActivity : KeyboardActivity() {
  private var hostEndpoint: String? = null

  // Exercise
  private val payloadListener = object : PayloadCallback() {
    override fun onPayloadReceived(endpoint: String, payload: Payload) {}
    override fun onPayloadTransferUpdate(endpoint: String, update: PayloadTransferUpdate) {}
  }

  // Exercise
  private val connectionListener = object : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(id: String, info: ConnectionInfo) {
      Toast.makeText(this@JoinActivity, "Connecting to ${info.endpointName}.", Toast.LENGTH_SHORT).show()
      Nearby.getConnectionsClient(this@JoinActivity).acceptConnection(id, payloadListener)
    }

    override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
      when (result.status.statusCode) {
        ConnectionsStatusCodes.STATUS_OK -> {
          hostEndpoint = endpoint
          Toast.makeText(this@JoinActivity, "Connected", Toast.LENGTH_SHORT).show()
        }
        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
          Toast.makeText(this@JoinActivity, "Rejected", Toast.LENGTH_SHORT).show()
        }
        ConnectionsStatusCodes.STATUS_ERROR -> {
          Toast.makeText(this@JoinActivity, "Error", Toast.LENGTH_SHORT).show()
        }
      }
    }

    override fun onDisconnected(endpoint: String) {
      Toast.makeText(this@JoinActivity, "Disconnected", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    octave = 5

    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    requestPermissions(permissions, 100, {
      discover()
    }, {
      Toast.makeText(this, "Location not permitted.", Toast.LENGTH_LONG).show()
    })
  }

  private fun joinHost(endpoint: Endpoint) {
    Nearby.getConnectionsClient(this@JoinActivity)
      .requestConnection("CLIENT", endpoint.id, connectionListener)
      .addOnSuccessListener {
        Toast.makeText(this@JoinActivity, "Connected to ${endpoint.name}.", Toast.LENGTH_LONG).show()
      }
      .addOnFailureListener {
        Toast.makeText(this@JoinActivity, "Rejected by ${endpoint.name}.", Toast.LENGTH_LONG).show()
      }
  }

  // Exercise
  private fun showEndpointChooser(items: List<Endpoint>): ArrayAdapter<Endpoint> {
    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

    AlertDialog.Builder(this).run {
      setTitle("Choose host...")
      setAdapter(adapter) { _, i -> joinHost(items[i]) }
      setOnDismissListener {
        Nearby.getConnectionsClient(this@JoinActivity).stopDiscovery()
      }
      show()
    }

    return adapter
  }

  // Exercise
  private fun createDiscoverListener(items: MutableList<Endpoint>, adapter: ArrayAdapter<Endpoint>): EndpointDiscoveryCallback {
    return object : EndpointDiscoveryCallback() {
      override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
        items.add(Endpoint(id, info))
        adapter.notifyDataSetChanged()
      }

      override fun onEndpointLost(p0: String) {
        items.removeIf { it.id == p0 }
        adapter.notifyDataSetChanged()
      }
    }
  }

  // Exercise
  private fun discover() {
    val items = mutableListOf<Endpoint>()
    val adapter = showEndpointChooser(items)
    val callback = createDiscoverListener(items, adapter)

    val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
    Nearby.getConnectionsClient(this).startDiscovery("P2Piano", callback, options).addOnSuccessListener {
      Toast.makeText(this, "Looking for hosts...", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
      Toast.makeText(this, "Discovery failed!", Toast.LENGTH_LONG).show()
    }
  }

  // Exercise
  override fun sendMidiMessage(bytes: ByteArray) {
    super.sendMidiMessage(bytes)
    hostEndpoint?.let {
      Nearby.getConnectionsClient(this).sendPayload(it, Payload.fromBytes(bytes))
    }
  }
}

