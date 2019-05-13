package org.twodee.p2piano

import android.Manifest
import android.os.Bundle
import android.widget.Toast

class JoinActivity : KeyboardActivity() {
  private var hostEndpoint: String? = null

  // Exercise

  // Exercise

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

  // Exercise

  // Exercise

  // Exercise

  // Exercise
  private fun discover() {
  }

  // Exercise
  override fun sendMidiMessage(bytes: ByteArray) {
  }
}

