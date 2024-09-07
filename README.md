# pagseguro_smart_flutter

API developed in Flutter/Android with the aim of integrating mobile applications with the PagSeguro acquirer's credit card machine, specifically on the A930 machine

<h1 align="center">
  <br>
   <img width="700" src="https://files.readme.io/82e8e19-gallery-right-2x2.png" />
  <br>
</h1>

## Android Manifest

To integrate the library, the PlugPagService library in Android applications is
You need to add the following permission to AndroidManifest.xml.

```xml
<permission android:name="br.com.uol.pagseguro.permission.MANAGE_PAYMENTS"/>
```

## Intent-filter
So that your app can be chosen as the default pay and receive app
Card insertion intents, you need to add the following code in your
AndroidManifest.xml inside your main Activity.

```xml
<intent-filter>
 <action android:name="br.com.uol.pagseguro.PAYMENT"/>
 <category android:name="android.intent.category.DEFAULT"/>
</intent-filter>
```

