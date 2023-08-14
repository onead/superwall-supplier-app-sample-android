package tw.com.one2direct.superwall_sample
import android.app.NotificationChannel
import android.app.NotificationManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        println("MyFirebaseMessagingService Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendMessageTokenToServer(token)
    }

    //註冊推播token
    private fun sendMessageTokenToServer(pushToken: String) {
        val androidID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val sp =this.getSharedPreferences("Login", MODE_PRIVATE)
        val memberID = sp.getString("memberID","")
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url("${getString(R.string.serverUrl)}/channel/app/api/registerNotification")
            .addHeader("Content-Type", "application/json")
            .post(
                JSONObject()
                    .put("memberID", memberID)
                    .put("pushToken", pushToken)
                    .put("deviceID", androidID)
                    .put("os", "android")
                    .toString()
                    .toRequestBody())
            .build()
        okHttpClient.newCall(request).enqueue( object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("onFailure$e")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val jsonObject =JSONObject(response.body?.string())
                println("sendMessageTokenToServer:$jsonObject")
            }
        })
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val channel = NotificationChannel(
            "channel1", "channel1",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "channel1")
        notification.setChannelId("channel1")
        notification.setContentTitle(message.data["title"])
        notification.setContentText(message.data["body"])
        manager.notify(121, notification.build())
    }
}