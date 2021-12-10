package com.lh.grader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.serialport.SerialPortFinder
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.lh.grader.comn.Device
import com.lh.grader.comn.SerialPortManager

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    lateinit var mSpDevice1: AppCompatSpinner
    lateinit var mSpDevice2: AppCompatSpinner
    lateinit var mBaudRate: AppCompatSpinner
    lateinit var mSwitch: SwitchCompat

    lateinit var mEtOrder: EditText
    lateinit var mBtnOrder: Button
    lateinit var mBtnClear: Button
    lateinit var mSwLog: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ScreenUtils.setFullScreen(this)
        AdaptScreenUtils.adaptWidth(resources, 1024)
        initView()
        //串口开关
        serialSwitch()
        //发送命令
        mBtnOrder.setOnClickListener {
            sendData()
        }

    }

    /**
     * 发送命令
     */
    private  fun sendData() {
        val text: String = mEtOrder.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(text) || text.length % 2 != 0) {
            ToastUtils.showLong("无效数据")
            return
        }
        SerialPortManager.instance().sendCommand(text)
    }

    /**
     * 串口开关
     */
    private fun serialSwitch() {
        findViewById<SwitchCompat>(R.id.Switch).setOnCheckedChangeListener { btn, isOpen ->
            var flag = false
            if (isOpen) {
                var mOpened = SerialPortManager.instance().open(
                    Device(
                        mSpDevice1.selectedItem.toString(),
                        mBaudRate.selectedItem.toString()
                    )
                ) != null
                var mOpened2 = SerialPortManager.instance().open(
                    Device(
                        mSpDevice2.selectedItem.toString(),
                        mBaudRate.selectedItem.toString()
                    )
                ) != null
                if (mOpened && mOpened2) {
                    ToastUtils.showLong("串口打开成功")
                    flag = true
                } else {
                    ToastUtils.showLong("串口打开失败")
                    flag = false
                    mSwitch.isChecked = false
                }

            } else {
                SerialPortManager.instance().close()
                flag = false
            }
            //变更控件状态
            updateViewState(flag)
        }
    }

    private fun updateViewState(flag: Boolean) {
        mSpDevice1.isEnabled = !flag
        mSpDevice2.isEnabled = !flag
        mBaudRate.isEnabled = !flag
        mEtOrder.isEnabled = flag
        mBtnOrder.isEnabled = flag
        mBtnClear.isEnabled = flag
        mSwLog.isEnabled = flag

    }

    private fun initView() {
        //获取设备列表
        val serialPortFinder = SerialPortFinder()
        // 设备
        var mDevices = serialPortFinder.allDevicesPath
        if (mDevices.isEmpty()) {
            mDevices = arrayOf(
                "找不到串口设备"
            )
        }
        //设备adapter
        val deviceAdapter = ArrayAdapter(this, R.layout.spinner_default_item, mDevices)
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item)
        //波特率adapter
        val baudratesAdapter = ArrayAdapter<String>(
            this,
            R.layout.spinner_default_item,
            resources.getStringArray(R.array.baudrates)
        )
        baudratesAdapter.setDropDownViewResource(R.layout.spinner_item)


        //串口1
        mSpDevice1 = findViewById(R.id.SpDevice1)
        mSpDevice1.adapter = deviceAdapter
        //串口2
        mSpDevice2 = findViewById(R.id.SpDevice2)
        mSpDevice2.adapter = deviceAdapter
        //波特率
        mBaudRate = findViewById(R.id.SpBaudRate)
        mBaudRate.adapter = baudratesAdapter
        //串口开关
        mSwitch = findViewById(R.id.Switch)

        //命令输入框
        mEtOrder = findViewById(R.id.etOrder)
        //命令按钮
        mBtnOrder=findViewById(R.id.btnOrder)
        mSpDevice1.isFocusable = true
        mSpDevice1.isFocusableInTouchMode = true

        mBtnClear = findViewById(R.id.btnClear)
        mSwLog = findViewById(R.id.SwLog)

    }
}