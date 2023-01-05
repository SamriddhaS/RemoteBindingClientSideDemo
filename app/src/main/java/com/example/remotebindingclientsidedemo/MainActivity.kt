package com.example.remotebindingclientsidedemo

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.remotebindingclientsidedemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var randomNumber = 0
    private val TAG = "MyService"
    private val GET_RANDOM_NUMBER_FLAG = 0
    private var isBound=false
    private var randomNumberRequestMessenger: Messenger? = null
    private var randomNumberReceiveMessenger:Messenger? = null
    private var randomNumberServiceConnection:ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(arg0: ComponentName) {
            randomNumberRequestMessenger = null
            randomNumberReceiveMessenger = null
            isBound = false
        }

        override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {
            randomNumberRequestMessenger = Messenger(binder)
            randomNumberReceiveMessenger = Messenger(ReceiveRandomNumberHandler())
            isBound = true
        }
    }
    private var serviceIntent:Intent?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent()
        serviceIntent?.component = ComponentName(
            "com.example.remotebindingservicesidedemo",
            "com.example.remotebindingservicesidedemo.MyService"
        )

        binding.button.setOnClickListener {
            bindToRemoteService()
        }
        binding.button2.setOnClickListener {
            unbindFromService()
        }
        binding.button3.setOnClickListener {
            getRandomNumber()
        }

    }

    private fun unbindFromService() {
        if(isBound){
            unbindService(randomNumberServiceConnection)
            isBound=false
            Toast.makeText(this,"Service Unbound",Toast.LENGTH_SHORT).show();
        }
    }

    private fun bindToRemoteService() {
        bindService(serviceIntent, randomNumberServiceConnection, BIND_AUTO_CREATE);
        Toast.makeText(this,"Service bound",Toast.LENGTH_SHORT).show()
    }

    private fun getRandomNumber(){
        if (isBound) {
            val requestMessage = Message.obtain(null, GET_RANDOM_NUMBER_FLAG)
            requestMessage.replyTo = randomNumberReceiveMessenger
            try {
                randomNumberRequestMessenger?.send(requestMessage)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Service Unbound, can't get random number", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class ReceiveRandomNumberHandler : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                randomNumber = 0
                when (msg.what) {
                    GET_RANDOM_NUMBER_FLAG -> {
                        randomNumber = msg.arg1
                        binding.tvNumber.text = "Random Number : $randomNumber"
                    }
                }
                super.handleMessage(msg)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}