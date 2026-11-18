package app.audioserver.extension;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Share target added by the "Share to audio server" patch. Posts the shared text to
 * {@code <server>/api/ingest} and shows the server's one-line reply as a toast.
 * Server URL and token come from application meta-data; the Kids alias adds a category.
 */
public class ShareToServerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context app = getApplicationContext();
        String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String url, token, category;
        try {
            PackageManager pm = getPackageManager();
            Bundle appMeta = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            Bundle aliasMeta = pm.getActivityInfo(getComponentName(), PackageManager.GET_META_DATA).metaData;
            url = appMeta.getString("audioserver.url");
            token = appMeta.getString("audioserver.token");
            category = aliasMeta == null ? null : aliasMeta.getString("audioserver.category");
        } catch (PackageManager.NameNotFoundException e) {
            toast(app, "Audio server: patch configuration missing");
            finish();
            return;
        }
        finish(); // Theme.NoDisplay: must finish before onResume.

        if (text == null) {
            toast(app, "Audio server: nothing to send");
            return;
        }
        new Thread(() -> toast(app, post(url + "/api/ingest", token, text, category))).start();
    }

    private static String post(String url, String token, String text, String category) {
        HttpURLConnection c = null;
        try {
            JSONObject body = new JSONObject().put("text", text);
            if (category != null) body.put("category", category);
            c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(30000);
            c.setRequestMethod("POST");
            c.setRequestProperty("Authorization", "Bearer " + token);
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);
            try (OutputStream out = c.getOutputStream()) {
                out.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }
            int code = c.getResponseCode();
            InputStream in = code < 400 ? c.getInputStream() : c.getErrorStream();
            String reply = in == null ? "" : read(in).trim();
            return reply.isEmpty() ? "Audio server: HTTP " + code : reply;
        } catch (Exception e) {
            return "Audio server not reachable: " + e.getMessage();
        } finally {
            if (c != null) c.disconnect();
        }
    }

    private static String read(InputStream in) throws java.io.IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        for (int n; (n = in.read(chunk)) > 0; ) buf.write(chunk, 0, n);
        in.close();
        return buf.toString("UTF-8");
    }

    private static void toast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }
}
