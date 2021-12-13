package com.lh.grader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.serialport.SerialPortFinder
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.lh.grader.adapter.DataAdapter
import com.lh.grader.comn.Device
import com.lh.grader.comn.SerialPortManager
import com.lh.grader.message.IMessage
import com.lh.grader.model.GpsModel
import com.lh.grader.model.ResultData
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    lateinit var mRv1: RecyclerView
    lateinit var mRv2: RecyclerView

    lateinit var mTvChange1: TextView
    lateinit var mTvChange2: TextView

    private var mData1: ArrayList<ResultData> = ArrayList()
    private var mData2: ArrayList<ResultData> = ArrayList()
    private lateinit var mDataAdapter: DataAdapter
    private lateinit var mDataAdapter2: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ScreenUtils.setFullScreen(this)
        initView()
        //串口开关
        serialSwitch()
        //发送命令
        mBtnOrder.setOnClickListener {
            sendData()
        }
        initRv()
        EventBus.getDefault().register(this)
    }

    private fun initRv() {
        mRv1 = findViewById(R.id.rv1)
        mRv2 = findViewById(R.id.rv2)

        mDataAdapter = DataAdapter()
        mRv1.adapter = mDataAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        mRv1.layoutManager = linearLayoutManager
        mRv1.hasFixedSize()
        mDataAdapter.setNewData(mData1)

        mDataAdapter2 = DataAdapter()
        mRv2.adapter = mDataAdapter2
        val linearLayoutManager2 = LinearLayoutManager(this)
        mRv2.layoutManager = linearLayoutManager2
        mRv2.hasFixedSize()
        mDataAdapter.setNewData(mData2)

    }

    private var lastData1: ResultData? = null
    private var lastData2: ResultData? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(message: IMessage) {

        if (!mSwLog.isChecked) {
            return
        }

        val msg = toStringHex2(message.message)!!
        val jsonObject = JSONObject(msg)
        val snStr = jsonObject.getString("sn")
        val gpsStr = jsonObject.getString("gps")
        val time = jsonObject.getString("time")
        val timeStr =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(time.toLong() * 1000)
        if (message.name == "串口1") {
            val split = gpsStr.split(",")
            val gpsModel = GpsModel(
                split[0],
                split[1].toDouble(),
                split[2].toBigDecimal(),
                split[3],
                split[4].toBigDecimal(),
                split[5],
                split[6].toInt(),
                split[7].toInt(),
                split[8].toDouble(),
                split[9].toBigDecimal(),
                split[10],
                split[11].toDouble(),
                split[12],
                split[13].toDouble(),
                split[14],
            )
            val resultData = ResultData(snStr, gpsModel, timeStr)
            mDataAdapter.addData(resultData)
            mRv1.smoothScrollToPosition(mDataAdapter.itemCount - 1)
            if (lastData1 == null) {
                lastData1 = resultData
            } else {
                mTvChange1.text = "时间：$timeStr\n设备：${resultData.sn}\n" +
                        "经度-${resultData.gps.longUnit}变化：${(resultData.gps.long)-(lastData1!!.gps.long)}\n" +
                        "纬度-${resultData.gps.latUnit}变化：${(resultData.gps.lat)-(lastData1!!.gps.lat)}\n" +
                        "高程变化：${(resultData.gps.altitude)-(lastData1!!.gps.altitude)}"
            }
            lastData1 = resultData
        }
        if (message.name == "串口2") {
            val split = gpsStr.split(",")
            val gpsModel = GpsModel(
                split[0],
                split[1].toDouble(),
                split[2].toBigDecimal(),
                split[3],
                split[4].toBigDecimal(),
                split[5],
                split[6].toInt(),
                split[7].toInt(),
                split[8].toDouble(),
                split[9].toBigDecimal(),
                split[10],
                split[11].toDouble(),
                split[12],
                split[13].toDouble(),
                split[14],
            )
            val resultData = ResultData(snStr, gpsModel, timeStr)
            mDataAdapter2.addData(resultData)
            mRv2.smoothScrollToPosition(mDataAdapter2.itemCount - 1);
            if (lastData2 == null) {
                lastData2 = resultData
            } else {
                mTvChange2.text = "时间：$timeStr\n设备：${resultData.sn}\n" +
                        "经度-${resultData.gps.longUnit}变化：${(resultData.gps.long)-(lastData2!!.gps.long)}\n" +
                        "纬度-${resultData.gps.latUnit}变化：${(resultData.gps.lat)-(lastData2!!.gps.lat)}\n" +
                        "高程变化：${(resultData.gps.altitude)-(lastData2!!.gps.altitude)}"
            }

            lastData2 = resultData

        }

    }

    /**
     * 发送命令
     */
    private fun sendData() {
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
                var mOpened2 = SerialPortManager.instance2().open(
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
                SerialPortManager.instance2().close()
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
        mSpDevice1.setSelection(8)
        //串口2
        mSpDevice2 = findViewById(R.id.SpDevice2)
        mSpDevice2.adapter = deviceAdapter
        mSpDevice2.setSelection(6)

        //波特率
        mBaudRate = findViewById(R.id.SpBaudRate)
        mBaudRate.adapter = baudratesAdapter
        mBaudRate.setSelection(12)

        //串口开关
        mSwitch = findViewById(R.id.Switch)

        //命令输入框
        mEtOrder = findViewById(R.id.etOrder)
        //命令按钮
        mBtnOrder = findViewById(R.id.btnOrder)
        mSpDevice1.isFocusable = true
        mSpDevice1.isFocusableInTouchMode = true

        mBtnClear = findViewById(R.id.btnClear)
        mSwLog = findViewById(R.id.SwLog)

        mBtnClear.setOnClickListener {
            mDataAdapter.setNewData(null)
            mDataAdapter2.setNewData(null)
        }


        mTvChange1 = findViewById(R.id.tvChange1)
        mTvChange2 = findViewById(R.id.tvChange2)

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    fun toStringHex2(s: String): String? {
        var s = s
        val baKeyword = ByteArray(s.length / 2)
        for (i in baKeyword.indices) {
            try {
                baKeyword[i] = (0xff and s.substring(
                    i * 2, i * 2 + 2
                ).toInt(16)).toByte()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            s = String(baKeyword)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return s
    }
}