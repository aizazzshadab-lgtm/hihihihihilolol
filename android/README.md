# Relay Help — Android starter (Step 1)

This is the first slice of the app: **one big slide button that saves an SOS on the phone**.
No Bluetooth, no internet, no Firebase yet. It exists so you can prove the phone remembers.

## Put these files into your project

1. Android Studio → New Project → **Empty Views Activity** → language **Kotlin** → name `RelayHelp`.
2. Copy the files from this folder into your project, keeping the same paths:
   - `app/src/main/java/com/fvatz/relayhelp/MainActivity.kt`
   - `app/src/main/java/com/fvatz/relayhelp/SosMessage.kt`
   - `app/src/main/res/layout/activity_main.xml`
3. If your package name is different (e.g. `com.example.relayhelp`), change the `package` line
   at the top of both `.kt` files to match yours. The layout path stays the same.
4. Run ▶ on a real phone (USB debugging on). If you use an emulator, use one with Google APIs.

## What you should see

- Dark screen, red `RELAY HELP`, a slider.
- Drag the slider all the way right → **Saved SOS: 1**, and a line like
  `Saved 3f9a1c2b • 92 bytes • waiting to be passed along`.
- Drag twice more → **Saved SOS: 3**.
- Force-close the app and reopen it → **still 3**. That's the whole point of this step.

## Deliberate shortcuts (fix these in the next steps)

- `people` is always 1 and `needs` is always "Medical aid" — add a picker.
- `lat` / `lng` are `0.0` until you add GPS (FusedLocationProvider).
- Storage is `SharedPreferences`, not Room yet. Fine for the demo; move to Room once
  the phone-to-phone passing works, because that's when you need real records and a sync queue.

## Next step: two phones whisper

Add the Nearby Connections dependency to `app/build.gradle`:

```gradle
implementation("com.google.android.gms:play-services-nearby:19.3.0")
```

Then add the permissions in `AndroidManifest.xml` (`BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE`,
`BLUETOOTH_CONNECT`, `ACCESS_FINE_LOCATION`, `NEARBY_WIFI_DEVICES`) and make the app
**discover one other phone and show its name**. Only after that, send `SosMessage.toJson()`
across and save it with `SosMessage.fromJson()`.

## Message format

One SOS is a flat JSON object with 1–2 letter keys, ~90–120 bytes:

```json
{"id":"3f9a1c2b","p":4,"n":"Medical aid","la":10.53,"lo":76.21,"t":1699999999999,"h":0}
```

`id` is how a phone knows it already saw a message (stops the chain from looping forever).
`h` is the hop count — bump it every time a phone passes the message on.
