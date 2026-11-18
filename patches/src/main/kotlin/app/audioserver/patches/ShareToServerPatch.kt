package app.audioserver.patches

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.patch
import app.morphe.patcher.patch.resourceXml
import app.morphe.patcher.patch.stringOption

private const val ACTIVITY = "app.audioserver.extension.ShareToServerActivity"

/**
 * Adds two Android share targets ("Send to server", "Send to server (Kids)") that post the shared
 * text to the audio server. Only the manifest is edited and one extension class is added; nothing
 * in YouTube Music's own code is touched, so app updates should not break it.
 */
@Suppress("unused")
val shareToServerPatch = patch(
    name = "Share to audio server",
    description = "Adds 'Send to server' and 'Send to server (Kids)' to Android's share menu. " +
        "Before patching, tap this patch's options (gear icon) and set the Server URL and API token; " +
        "patching fails without them.",
    default = true,
) {
    compatibleWith(
        Compatibility(
            name = "YouTube Music",
            packageName = "com.google.android.apps.youtube.music",
            apkFileType = ApkFileType.APK_REQUIRED,
            appIconColor = 0xFF0000,
            targets = listOf(AppTarget(version = null, isExperimental = true)),
        )
    )

    extendWith("extensions/extension.mpe")

    val serverUrl by stringOption(
        key = "serverUrl",
        title = "Server URL",
        description = "Base URL of the audio server: http://<server-ip>:8080 at home, or the server's " +
            "Tailscale address. Required.",
        required = true,
    )
    val token by stringOption(
        key = "token",
        title = "API token",
        description = "The value of AUDIOSERVER_TOKEN on the server. Required.",
        required = true,
    )

    execute {
        resourceXml("AndroidManifest.xml").use { manifest ->
            val application = manifest.elements("application").first()
            for ((name, value) in listOf("audioserver.url" to serverUrl!!.trimEnd('/'), "audioserver.token" to token!!)) {
                application.appendChild("meta-data") {
                    setAttribute("android:name", name)
                    setAttribute("android:value", value)
                }
            }
            application.appendChild("activity") {
                setAttribute("android:name", ACTIVITY)
                setAttribute("android:exported", "false")
                setAttribute("android:excludeFromRecents", "true")
                setAttribute("android:theme", "@android:style/Theme.NoDisplay")
            }
            for ((suffix, label, category) in listOf(
                Triple("Default", "Send to server", null),
                Triple("Kids", "Send to server (Kids)", "kids"),
            )) {
                application.appendChild("activity-alias") {
                    setAttribute("android:name", ACTIVITY + suffix)
                    setAttribute("android:targetActivity", ACTIVITY)
                    setAttribute("android:label", label)
                    setAttribute("android:exported", "true")
                    appendChild("intent-filter") {
                        appendChild("action") { setAttribute("android:name", "android.intent.action.SEND") }
                        appendChild("category") { setAttribute("android:name", "android.intent.category.DEFAULT") }
                        appendChild("data") { setAttribute("android:mimeType", "text/plain") }
                    }
                    if (category != null) {
                        appendChild("meta-data") {
                            setAttribute("android:name", "audioserver.category")
                            setAttribute("android:value", category)
                        }
                    }
                }
            }
        }
    }
}
