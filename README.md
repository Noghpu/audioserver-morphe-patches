# audioserver Morphe patches

Morphe patches for a self-hosted home audio server.

## Use

Add this repository as a patch source in Morphe Manager:
`https://github.com/Noghpu/audioserver-morphe-patches`

Patch YouTube Music together with the official Morphe Patches. Before patching, open the options of
**Share to audio server** and set the server URL and API token.

## Patches list

<!-- PATCHES_START EXPANDED -->
<!-- PATCHES_END -->

## Development

Build locally: `./gradlew buildAndroid` → `patches/build/libs/patches-*.mpp`.
Releases are made by GitHub Actions from conventional commits on `main`
(`feat:` → minor, `fix:` → patch, `chore:` → no release).
