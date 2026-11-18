# audioserver Morphe patches

Morphe patches for a self-hosted home audio server.

## Use

Add this repository as a patch source in Morphe Manager:
`https://github.com/Noghpu/audioserver-morphe-patches`

Patch YouTube Music together with the official Morphe Patches. Before patching, open the options of
**Share to audio server** and set the server URL and API token.

## Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0](https://github.com/Noghpu/audioserver-morphe-patches/releases/tag/v1.0.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;1 patches total
<details open>
<summary>📦 YouTube Music&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**Package name:** `com.google.android.apps.youtube.music`

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Share to audio server](#share-to-audio-server) | Adds 'Send to server' and 'Send to server (Kids)' to Android's share menu. Before patching, tap this patch's options (gear icon) and set the Server URL and API token; patching fails without them. | • Server URL<br>• API token |

</details>

<!-- PATCHES_END -->

## Development

Build locally: `./gradlew buildAndroid` → `patches/build/libs/patches-*.mpp`.
Releases are made by GitHub Actions from conventional commits on `main`
(`feat:` → minor, `fix:` → patch, `chore:` → no release).
