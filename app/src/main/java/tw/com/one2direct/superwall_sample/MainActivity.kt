package tw.com.one2direct.superwall_sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sp =this.getSharedPreferences("Login", MODE_PRIVATE)
        if(sp.contains("memberID")){
            val jsonObject = JSONObject()
            jsonObject.put("memberID",sp.getString("memberID",""))
            jsonObject.put("birthYear",sp.getInt("birthYear",0))//一般狀況下不會由app取得生日，此為範例
            jsonObject.put("gender",sp.getString("gender",""))//一般狀況下不會由app取得性別，此為範例
            //app 啟動時取得任務牆網址
            getUrl(jsonObject)
            val textViewInfo=findViewById<android.widget.TextView>(R.id.textViewInfo)
            textViewInfo.setText(sp.getString("memberID","") + " " + sp.getInt("birthYear",0) + " " + sp.getString("gender",""))
        }else{
            val intern=android.content.Intent(this, LoginActivity::class.java)
            startActivity(intern)
        }
        val buttonLogout=findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {logout() }

        //取得推播權限
        val permissionState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    //取得任務牆網址
    private fun getUrl(jsonObject:JSONObject) {
        val okHttpClient = OkHttpClient()
        val body = jsonObject.toString().toRequestBody()
        val request = Request.Builder()
            .url("${getString(R.string.serverUrl)}/channel/app/api/getUrl")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()
        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("onFailure$e")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val jo =JSONObject(response.body?.string())
                runOnUiThread {
                    val button=findViewById<Button>(R.id.buttonSuperWall)
                    button.visibility=Button.VISIBLE
                    button.setOnClickListener {
                        //開啟任務牆
                        val intent = android.content.Intent(this@MainActivity, WebViewActivity::class.java)
                        intent.putExtra("url", jo.getString("url"))
                        startActivity(intent)
                    }
                }
            }
        })
    }

    //登出
    private fun logout(){
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        removeMessageToken(androidId)
        val sp =this.getSharedPreferences("Login", MODE_PRIVATE)
        sp.edit().clear().apply()
        Firebase.messaging.deleteToken()
        val intern=android.content.Intent(this, LoginActivity::class.java)
        startActivity(intern)
    }


    //====================================================非發點app 不用參考以下功能====================================================

    //綁定Api
    private fun binding(token:String,memberID:String) {
        val okHttpClient = OkHttpClient()
        val jsonObject = JSONObject()
        jsonObject.put("userID",memberID)
        jsonObject.put("token",token)
        val body = jsonObject.toString().toRequestBody()
        val request = Request.Builder()
            .url("${getString(R.string.serverUrl)}/channel/app/api/bind")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()
        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("onFailure$e")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val jo =JSONObject(response.body?.string())
                println("binding:$jsonObject")
                val msg = if(jo.getBoolean("ok")){
                    "綁定成功"
                }else{
                    "綁定失敗"
                }
                runOnUiThread {
                    android.widget.Toast.makeText(this@MainActivity, msg, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    //移除推播Token
    private fun removeMessageToken(deviceID: String) {
        val os:String="android"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url("${getString(R.string.serverUrl)}/channel/app/api/revokeNotification")
            .addHeader("Content-Type", "application/json")
            .post(
                ("{\"deviceID\":\"$deviceID\",\"os\":\"$os\"}").toRequestBody())
            .build()
        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("onFailure$e")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val jsonObject =JSONObject(response.body?.string())
                println("removeMessageToken:$jsonObject")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //URL Scheme 觸發綁定
        if(Intent.ACTION_VIEW==intent.action){
            val sp =this.getSharedPreferences("Login", MODE_PRIVATE)
            val uri=intent.data
            val action= uri?.getQueryParameter("action");
            val token= uri?.getQueryParameter("token");
            if(action.equals("binding") && sp.contains("memberID")){
                binding(token!!,sp.getString("memberID","")!!)
                intent.data=null
            }
        }
    }
}