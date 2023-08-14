#  SuperWall Android Sample App

## SuperWall 任務牆流程
1. app啟動時 Call 自家伺服器 API 取得任務牆網址 (參考MainActivity.kt > getUrl())
2. 用webview開啟任務牆網址 並實作一個 closeBtn，點擊後關閉任務牆(參考WebViewActivity.kt)


## SuperWall 點數綁定流程 (非發點app 不需要實作)
1. 透過url scheme開啟APP並帶入綁定token (參考MainActivity.kt > onResume() 及AndroidManifest.xml)
```javascript
//url scheme 格式如下
<scheme>://?action=binding&token=token=<bindingToken>
```
```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <data android:scheme="<scheme>"/>
</intent-filter>
```
2. call 自家伺服器綁定API(參考MainActivity.kt > binding())