package org.twodee.p2piano

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import org.twodee.rattler.PermittedActivity

open class KeyboardActivity : PermittedActivity() {
  private var midiDevice: MidiDevice? = null
  private var port: MidiInputPort? = null
  protected var octave: Int = 4

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_keyboard)

    // The keys are just vanilla Views arranged and colored to look like a
    // keyboard.
    val keys: List<View> = listOf(
      findViewById(R.id.c),
      findViewById(R.id.cd),
      findViewById(R.id.d),
      findViewById(R.id.de),
      findViewById(R.id.e),
      findViewById(R.id.f),
      findViewById(R.id.fg),
      findViewById(R.id.g),
      findViewById(R.id.ga),
      findViewById(R.id.a),
      findViewById(R.id.ab),
      findViewById(R.id.b)
    )

    keys.forEach { key ->
      // Each view has its semitone/halfstep offset set in its tag attribute.
      // C is 0, and B is 11.
      val halfstep = key.tag.toString().toInt()

      // The keys' drawable is a selector that toggles between the key's color
      // when not pressed and cornflower blue when pressed. We must set the
      // selector state explicitly since we are using vanilla views.
      key.setOnTouchListener { view, event ->
        val midiNumber = (12 * (octave + 1) + halfstep).toByte()
        when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            view.isPressed = true
            sendNoteMessage(midiNumber, 127)
            true
          }
          MotionEvent.ACTION_UP -> {
            view.isPressed = false
            sendNoteMessage(midiNumber, 0)
            true
          }
          else -> false
        }
      }
    }
  }

  private fun setupMidi() {
    val midiManager = getSystemService(Context.MIDI_SERVICE) as MidiManager
    val infos = midiManager.devices

    midiManager.openDevice(infos[0], {
      midiDevice = it
      port = it.openInputPort(0)
    }, null)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.actionbar, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.setup_midi_button -> {
      setupMidi()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  private fun sendNoteMessage(midiNumber: Byte, velocity: Byte) {
    // A note message is three bytes. The most-significant nibble of the first
    // byte is 1001 (0x90) and the least is the channel in 0-15. The second
    // byte is the note's MIDI number. C4 is 60, and C5 is 72. The byte is
    // the velocity or force of the note. 0 means stop playing.
    val buffer = byteArrayOf(0x90.toByte(), midiNumber, velocity)
    sendMidiMessage(buffer)
  }

  protected open fun sendMidiMessage(bytes: ByteArray) {
    port?.send(bytes, 0, bytes.size)
  }
}

