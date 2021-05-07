package com.sunilmishra.android.workmanagerdemo

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sunilmishra.android.workmanagerdemo.NotificationWorker

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var tvStatus: TextView? = null
    var btnSend: Button? = null
    var btnStorageNotLow: Button? = null
    var btnBatteryNotLow: Button? = null
    var btnRequiresCharging: Button? = null
    var btnDeviceIdle: Button? = null
    var btnNetworkType: Button? = null
    var mRequest: OneTimeWorkRequest? = null
    var mWorkManager: WorkManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        tvStatus = findViewById(R.id.tvStatus)
        btnSend = findViewById(R.id.btnSend)
        mWorkManager = WorkManager.getInstance()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        btnSend = findViewById(R.id.btnSend)
        btnStorageNotLow = findViewById(R.id.buttonStorageNotLow)
        btnBatteryNotLow = findViewById(R.id.buttonBatteryNotLow)
        btnRequiresCharging = findViewById(R.id.buttonRequiresCharging)
        btnDeviceIdle = findViewById(R.id.buttonDeviceIdle)
        btnNetworkType = findViewById(R.id.buttonNetworkType)
        btnSend!!.setOnClickListener(this)
        btnStorageNotLow!!.setOnClickListener(this)
        btnBatteryNotLow!!.setOnClickListener(this)
        btnRequiresCharging!!.setOnClickListener(this)
        btnDeviceIdle!!.setOnClickListener(this)
        btnNetworkType!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        tvStatus!!.text = ""
        val mConstraints: Constraints
        when (v.id) {
            R.id.btnSend -> mRequest =
                    OneTimeWorkRequest.Builder(NotificationWorker::class.java).build()
            R.id.buttonStorageNotLow -> {
                /**
                 * Constraints
                 * If TRUE task execute only when storage's is not low
                 */
                mConstraints = Constraints.Builder().setRequiresStorageNotLow(true).build()
                /**
                 * OneTimeWorkRequest with requiresStorageNotLow Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                        .setConstraints(mConstraints).build()
            }
            R.id.buttonBatteryNotLow -> {
                /**
                 * Constraints
                 * If TRUE task execute only when battery isn't low
                 */
                mConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
                /**
                 * OneTimeWorkRequest with requiresBatteryNotLow Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                        .setConstraints(mConstraints).build()
            }
            R.id.buttonRequiresCharging -> {
                /**
                 * Constraints
                 * If TRUE while the device is charging
                 */
                mConstraints = Constraints.Builder().setRequiresCharging(true).build()
                /**
                 * OneTimeWorkRequest with requiresCharging Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                        .setConstraints(mConstraints).build()
            }
            R.id.buttonDeviceIdle -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /**
                 * Constraints
                 * If TRUE while the  device is idle
                 */
                mConstraints = Constraints.Builder().setRequiresDeviceIdle(true).build()
                /**
                 * OneTimeWorkRequest with requiresDeviceIdle Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                        .setConstraints(mConstraints).build()
            }
            R.id.buttonNetworkType -> {
                /**
                 * Constraints
                 * Network type is conneted
                 */
                mConstraints =
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                /**
                 * OneTimeWorkRequest with requiredNetworkType Connected Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                        .setConstraints(mConstraints).build()
            }
            else -> {
            }
        }
        /**
         * Fetch the particular task status using request ID
         */
        mWorkManager!!.getWorkInfoByIdLiveData(mRequest!!.id).observe(this, { workInfo ->
            if (workInfo != null) {
                val state = workInfo.state
                tvStatus!!.append(
                        """
    $state
    
    """.trimIndent()
                )
            }
        })
        /**
         * Enqueue the WorkRequest
         */
        mWorkManager!!.enqueue(mRequest!!)
    }

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }
}