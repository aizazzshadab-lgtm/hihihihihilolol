package com.fvatz.relayhelp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Relay Help - Step 1: a big slide button that saves an SOS on this phone.
 * Everything lives in this one file on purpose, so there is nothing else to copy.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var countText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
            setBackgroundColor(Color.parseColor("#111827"))
        }

        root.addView(label("RELAY HELP", 22f, "#F87171"))
        root.addView(label("Slide to send an SOS", 16f, "#E5E7EB"))

        val slider = SeekBar(this).apply {
            max = 100
            progress = 0
        }
        root.addView(slider)

        countText = label("Saved SOS: 0", 28f, "#FFFFFF")
        countText.setPadding(0, 64, 0, 0)
        root.addView(countText)

        statusText = label("Nothing sent yet.", 14f, "#9CA3AF")
        statusText.setPadding(0, 48, 0, 0)
        root.addView(statusText)

        setContentView(root)
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

    private fun label(value: String, size: Float, color: String) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(Color.parseColor(color))
    }

    private fun prefs() = getSharedPreferences("relayhelp", MODE_PRIVATE)

    /** The phone's little notebook of saved messages. */
    private fun messages(): JSONArray = JSONArray(prefs().getString("sos", "[]"))

    private fun saveSos() {
        val message = SosMessage(
            id = UUID.randomUUID().toString().take(8),
            people = 1,                 // TODO: ask how many people are with them
            needs = "Medical aid",      // TODO: let them pick medical / food / water / trapped
            lat = 0.0,                  // TODO: real GPS in a later step
            lng = 0.0,
            createdAt = System.currentTimeMillis()
        )

        val list = messages()
        list.put(JSONObject(message.toJson()))
        prefs().edit().putString("sos", list.toString()).apply()

        refreshCount()
        val bytes = message.toJson().toByteArray().size
        statusText.text = "Saved ${message.id} - $bytes bytes - waiting to be passed along"
    }

    private fun refreshCount() {
        countText.text = "Saved SOS: ${messages().length()}"
    }
}

/**
 * One emergency cry for help, kept tiny so it travels fast over Bluetooth / Wi-Fi.
 */
data class SosMessage(
    val id: String,
    val people: Int,
    val needs: String,
    val lat: Double,
    val lng: Double,
    val createdAt: Long,
    val hops: Int = 0
) {
    fun toJson(): String = JSONObject()
        .put("id", id)
        .put("p", people)
        .put("n", needs)
        .put("la", lat)
        .put("lo", lng)
        .put("t", createdAt)
        .put("h", hops)
        .toString()

    companion object {
        fun fromJson(raw: String): SosMessage? = try {
            val o = JSONObject(raw)
            SosMessage(
                id = o.getString("id"),
                people = o.optInt("p", 1),
                needs = o.optString("n", ""),
                lat = o.optDouble("la", 0.0),
                lng = o.optDouble("lo", 0.0),
                createdAt = o.optLong("t", 0L),
                hops = o.optInt("h", 0)
            )
        } catch (e: Exception) {
            null
        }
    }
}
