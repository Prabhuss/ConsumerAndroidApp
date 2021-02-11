package com.getpy.express.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.getpy.express.R
import com.getpy.express.ui.main.MainActivity
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


/**
 * Service called when firebase cloud message received
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var numMessages = 0
    private var contentView: RemoteViews? = null
    private var notification: Notification? = null
    private var notificationManager: NotificationManager? = null
    private val NotificationID = 1005
    private var mBuilder: NotificationCompat.Builder? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data= remoteMessage.data as Map<String,String>
        if(data.size>0) {
            val t = data["title"]
            val b = data["body"]
            val i = data["image_url"]
            val bitmap = getBitmapfromUrl(i);
            if(bitmap!=null) {
                sendNotification(t ?: "GetPY", b ?: "notification", bitmap)
            }else
            {
                sendNotification(t ?: "GetPY", b ?: "notification")
            }
            //sendNotification(data,t?:"GetPY",b?:"notification")
        }
        /*try {
            val url = URL(i)
            val myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            var notification = NotificationCompat.Builder(this, R.string.notification_channel_id.toString())
                    .setSmallIcon(R.drawable.ic_price_tag_icon)
                    .setContentTitle(t)
                    .setContentText(b)
                    .setLargeIcon(myBitmap)
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(myBitmap))
                    .build()
        } catch (e: IOException) {
            println(e)
        }*/
        //generatePictureStyleNotification(this,t!!,b!!,i!!).execute()
    }
    private fun sendNotification(title:String,messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_png)
                .setContentTitle(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
    private fun sendNotification(title:String,messageBody: String, image: Bitmap) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setLargeIcon(image) /*Notification icon image*/
                .setSmallIcon(R.drawable.ic_launcher_png)
                .setContentTitle(messageBody)
                .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(image)) /*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }
    private fun sendNotification(data:Map<String,String>,title: String,body:String)
    {
        val collapsedviews=RemoteViews(packageName,R.layout.notification_collapse)
        val expandviews= RemoteViews(packageName,R.layout.notification_exand)

        collapsedviews.setTextViewText(R.id.title,title)
        collapsedviews.setTextViewText(R.id.message,body)
        collapsedviews.setImageViewResource(R.id.image,R.drawable.ic_launcher_png)
        if(data["image_url"]!=null) {
            val uri = Uri.parse(data["image_url"])
            expandviews.setImageViewUri(R.id.imageview,uri)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(
                this,
                getString(R.string.notification_channel_id)
            )
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_png)
                .setContentTitle(data["title"])
                .setContentText(data["body"])
                .setCustomContentView(collapsedviews)
                .setCustomBigContentView(expandviews)
                // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                .setContentIntent(pendingIntent)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id),
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESC
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(channel)
        }
        assert(notificationManager != null)
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun RunNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        contentView = RemoteViews(packageName, R.layout.notification_exand)
        contentView?.setImageViewResource(R.id.image, R.mipmap.ic_launcher_new2)
        val switchIntent = Intent(this, MainActivity::class.java)
        val pendingSwitchIntent = PendingIntent.getBroadcast(this, 1020, switchIntent, 0)
        //contentView?.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent)
        //mBuilder?.setSmallIcon(R.mipmap.newicon)
        mBuilder?.setAutoCancel(false)
        mBuilder?.setOngoing(true)
        mBuilder?.setPriority(Notification.PRIORITY_HIGH)
        mBuilder?.setOnlyAlertOnce(true)
        mBuilder?.build()?.flags = Notification.FLAG_NO_CLEAR or Notification.PRIORITY_HIGH
        mBuilder?.setContent(contentView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channel = NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager?.createNotificationChannel(channel)
            mBuilder?.setChannelId(channelId)
        }
        notification = mBuilder?.build()
        notificationManager?.notify(NotificationID, notification)
    }


    class generatePictureStyleNotification(private val mContext: Context, private val title: String, private val message: String, private val imageUrl: String) : AsyncTask<String?, Void?, Bitmap?>() {
        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("key", "value")
            val pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT)
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notif = Notification.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_png)
                    .setLargeIcon(result)
                    .setStyle(Notification.BigPictureStyle().bigPicture(result))
                    .build()
            notif.flags = notif.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(1, notif)
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val `in`: InputStream
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                `in` = connection.inputStream
                return BitmapFactory.decodeStream(`in`)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

    }

    override fun onNewToken(registrationToken: String) {
        Log.d("token", "Firebase #onNewToken registrationToken=$registrationToken")
    }

    companion object {
        const val FCM_PARAM = "picture"
        private const val CHANNEL_NAME = "FCM"
        private const val CHANNEL_DESC = "Firebase Cloud Messaging"
    }
}