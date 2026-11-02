# Relay Help — Android starter (Step 1)

**One file. That's the whole thing.**

`app/src/main/java/com/fvatz/relayhelp/MainActivity.kt` contains the SOS screen *and* the
`SosMessage` data class, so there is nothing to copy between folders and no XML layout to touch.

## How to use it (no file copying)

1. Android Studio → New Project → **Empty Views Activity** → Kotlin → name `RelayHelp`.
2. In the project tree open `app > java > com > fvatz > relayhelp > MainActivity.kt`
   (yours may be under `com > example > relayhelp` — that's fine).
3. Select **all** the code in that file (Ctrl+A) and paste the whole file from this repo over it.
4. If the first line says `package com.example.relayhelp` in your project, change the first line
   of the pasted code to match your project's package name. That's the only edit.
5. Ignore `activity_main.xml` — the screen is built in code, so the old layout file is unused.
6. Press ▶ on a real phone (USB debugging on).

## What you should see

- Dark screen, red `RELAY HELP`, a slider.
- Drag the slider all the way right → **Saved SOS: 1** and a line like
  `Saved 3f9a1c2b - 92 bytes - waiting to be passed along`.
- Drag twice more → **Saved SOS: 3**.
- Force-close the app and reopen it → **still 3**. That's the point of this step.

## Deliberate shortcuts (fix these next)

- `people` is always 1 and `needs` is always "Medical aid" — add a picker.
- `lat` / `lng` are `0.0` until you add GPS (FusedLocationProvider).
- Storage is `SharedPreferences`, not Room yet. Move to Room once phone-to-phone passing works.

## Next step: two phones whisper

Add the Nearby Connections dependency in `app/build.gradle`:

```gradle
implementation("com.google.android.gms:play-services-nearby:19.3.0")
```

Add permissions in `AndroidManifest.xml` (`BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE`,
`BLUETOOTH_CONNECT`, `ACCESS_FINE_LOCATION`, `NEARBY_WIFI_DEVICES`), then make the app
**discover one other phone and show its name**. Only after that, send `SosMessage.toJson()`
across and save it with `SosMessage.fromJson()`.

## Message format

One SOS is a flat JSON object with 1–2 letter keys, ~90–120 bytes:

```json
{"id":"3f9a1c2b","p":4,"n":"Medical aid","la":10.53,"lo":76.21,"t":1699999999999,"h":0}
```

`id` is how a phone knows it already saw a message (stops the chain looping forever).
`h` is the hop count — bump it every time a phone passes the message on.
