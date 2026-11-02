package com.fvatz.relayhelp

import org.json.JSONObject

/**
 * One emergency cry for help.
 * Kept intentionally tiny so it travels fast over Bluetooth / Wi-Fi.
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
