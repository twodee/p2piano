package org.twodee.p2piano

import android.Manifest
import android.os.Bundle
import android.widget.Toast

class HostActivity : KeyboardActivity() {

  // Exercise

  // Exercise

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
  }
}
