package com.example.remotebindingclientsidedemo

import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    companion object {
        private val TAG = "MyService"
        private const val GET_RANDOM_NUMBER_FLAG = 0

        class ReceiveRandomNumberHandler : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    GET_RANDOM_NUMBER_FLAG -> {
                        val messageSendRandomNumber = Message.obtain(null, GET_RANDOM_NUMBER_FLAG)
                        messageSendRandomNumber.arg1 = getRandomNumber()
                        try {
                            msg.replyTo.send(messageSendRandomNumber)
                        } catch (e: RemoteException) {
                            Log.i(TAG, "" + e.message)
                        }
                    }
                }
                super.handleMessage(msg)
            }
        }

        fun getRandomNumber(): Int {
            return mRandomNumber
        }

        private var mRandomNumber = 0
    }
}