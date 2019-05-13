package org.twodee.p2piano

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import org.twodee.rattler.PermittedActivity

class MainActivity : PermittedActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.soloButton).setOnClickListener {
      startActivity(Intent(this, KeyboardActivity::class.java))
    }

    findViewById<Button>(R.id.hostButton).setOnClickListener {
      startActivity(Intent(this, HostActivity::class.java))
    }

    findViewById<Button>(R.id.joinButton).setOnClickListener {
      startActivity(Intent(this, JoinActivity::class.java))
    }
  }
}
