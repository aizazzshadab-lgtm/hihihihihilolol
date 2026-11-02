package com.fvatz.relayhelp

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Step 1 of Relay Help: a big slide button that saves an SOS on this phone.
 * No Bluetooth yet, no internet yet. Just "it remembers".
 */
class MainActivity : AppCompatActivity() {

    private lateinit var slider: SeekBar
    private lateinit var countText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slider = findViewById(R.id.sosSlider)
        countText = findViewById(R.id.countText)
        statusText = findViewById(R.id.statusText)

        refreshCount()

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
                // Slide almost all the way -> send. Deliberate, so pockets don't trigger it.
                if (progress >= 95) {
                    bar.progress = 0
                    saveSos()
                }
            }

            override fun onStartTrackingTouch(bar: SeekBar) {}
            override fun onStopTrackingTouch(bar: SeekBar) {}
        })
    }

    /** The phone's little notebook of saved messages. */
    private fun messages(): JSONArray =
        JSONArray(prefs().getString(KEY_SOS, "[]"))

    private fun prefs() = getSharedPreferences("relayhelp", MODE_PRIVATE)

    private fun saveSos() {
        val message = SosMessage(
            id = UUID.randomUUID().toString().take(8),
            people = 1,                    // TODO: ask the user how many people
            needs = "Medical aid",         // TODO: let them pick medical / food / water / trapped
            lat = 0.0,                     // TODO: real GPS in a later step
            lng = 0.0,
            createdAt = System.currentTimeMillis()
        )

        val list = messages()
        list.put(JSONObject(message.toJson()))
        prefs().edit().putString(KEY_SOS, list.toString()).apply()

        refreshCount()
        val bytes = message.toJson().toByteArray().size
        statusText.text = "Saved ${message.id}  •  $bytes bytes  •  waiting to be passed along"
    }

    private fun refreshCount() {
        countText.text = "Saved SOS: ${messages().length()}"
    }

    private companion object {
        const val KEY_SOS = "sos"
    }
}
