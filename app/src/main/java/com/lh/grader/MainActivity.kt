package com.lh.grader

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.serialport.SerialPortFinder
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.lh.grader.adapter.DataAdapter
import com.lh.grader.comn.Device
import com.lh.grader.comn.SerialPortManager
import com.lh.grader.message.IMessage
import com.lh.grader.model.GpsModel
import com.lh.grader.model.ResultData
import com.lh.grader.model.SaveModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.tencent.mmkv.MMKV
import java.lang.reflect.Array
import java.math.BigDecimal


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
        EventBus.getDefault().register(this)
        ScreenUtils.setFullScreen(this)
        initView()
        //????????????
        serialSwitch()
        //????????????
        mBtnOrder.setOnClickListener {
            sendData()
        }
        initRv()

        val rootDir = MMKV.initialize(this)
        Log.i("?????????", rootDir)




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
        if (message.name == "??????1") {
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
                mTvChange1.text = "?????????$timeStr\n?????????${resultData.sn}\n" +
                        "??????-${resultData.gps.longUnit}?????????${(resultData.gps.long) - (lastData1!!.gps.long)}\n" +
                        "??????-${resultData.gps.latUnit}?????????${(resultData.gps.lat) - (lastData1!!.gps.lat)}\n" +
                        "???????????????${(resultData.gps.altitude) - (lastData1!!.gps.altitude)}"
            }
            lastData1 = resultData
        }
        if (message.name == "??????2") {
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
                lateinit  var bigDecimal: BigDecimal
                try {
                     bigDecimal = (mDataAdapter.data[mDataAdapter.data.size - 1].gps.altitude) - (mDataAdapter2.data[mDataAdapter2.data.size - 1].gps.altitude)

                }catch (e:Exception){
                    Log.i(TAG, "onMessageEvent: "+e.localizedMessage.toString())
                    bigDecimal=BigDecimal(0)
                }

//                findViewById<TextView>(R.id.tcChangeFirst).text = "?????????\n ???????????????"+bigDecimal.toPlainString()


                mTvChange2.text = "?????????$timeStr\n?????????${resultData.sn}\n" +
                        "??????-${resultData.gps.longUnit}?????????${(resultData.gps.long) - (lastData2!!.gps.long)}\n" +
                        "??????-${resultData.gps.latUnit}?????????${(resultData.gps.lat) - (lastData2!!.gps.lat)}\n" +
                        "???????????????${(resultData.gps.altitude) - (lastData2!!.gps.altitude)}\n" +
                        "?????????????????????"+bigDecimal
            }

            lastData2 = resultData

//            try {

//            }catch (e:Exception){
//                Log.i(TAG, "onMessageEvent: "+e.localizedMessage)
//            }


        }




    }

    /**
     * ????????????
     */
    private fun sendData() {
        val text: String = mEtOrder.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(text) || text.length % 2 != 0) {
            ToastUtils.showLong("????????????")
            return
        }
        SerialPortManager.instance().sendCommand(text)
    }

    /**
     * ????????????
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
                    ToastUtils.showLong("??????????????????")
                    flag = true
                } else {
                    ToastUtils.showLong("??????????????????")
                    flag = false
                    mSwitch.isChecked = false
                }

            } else {
                SerialPortManager.instance().close()
                SerialPortManager.instance2().close()
                flag = false
            }
            //??????????????????
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
        //??????????????????
        val serialPortFinder = SerialPortFinder()
        // ??????
        var mDevices = serialPortFinder.allDevicesPath
        if (mDevices.isEmpty()) {
            mDevices = arrayOf(
                "?????????????????????"
            )
        }
        //??????adapter
        val deviceAdapter = ArrayAdapter(this, R.layout.spinner_default_item, mDevices)
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item)
        //?????????adapter
        val baudratesAdapter = ArrayAdapter<String>(
            this,
            R.layout.spinner_default_item,
            resources.getStringArray(R.array.baudrates)
        )
        baudratesAdapter.setDropDownViewResource(R.layout.spinner_item)


        //??????1
        mSpDevice1 = findViewById(R.id.SpDevice1)
        mSpDevice1.adapter = deviceAdapter
        mSpDevice1.setSelection(8)
        //??????2
        mSpDevice2 = findViewById(R.id.SpDevice2)
        mSpDevice2.adapter = deviceAdapter
        mSpDevice2.setSelection(6)

        //?????????
        mBaudRate = findViewById(R.id.SpBaudRate)
        mBaudRate.adapter = baudratesAdapter
        mBaudRate.setSelection(12)

        //????????????
        mSwitch = findViewById(R.id.Switch)

        //???????????????
        mEtOrder = findViewById(R.id.etOrder)
        //????????????
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


        findViewById<Button>(R.id.saveLog).setOnClickListener {
            val kv1 = MMKV.defaultMMKV()
            val kv2 = MMKV.defaultMMKV()

            val data1 = kv1.decodeString("data1")
            val data2 = kv2.decodeString("data2")
            if (data1 == null) {
                val array =
                    arrayListOf(SaveModel(TimeUtils.getNowString(), mDataAdapter.data))

                kv1.encode("data1", GsonUtils.toJson(array))
            } else {
                val array =
                    GsonUtils.fromJson<ArrayList<SaveModel>>(
                        data1,
                        GsonUtils.getListType(SaveModel::class.java)
                    )
                array.add(SaveModel(TimeUtils.getNowString(), mDataAdapter.data))
                kv1.encode("data1", GsonUtils.toJson(array))
            }

            if (data2 == null) {
                val array =
                    arrayListOf(SaveModel(TimeUtils.getNowString(), mDataAdapter2.data))

                kv2.encode("data2", GsonUtils.toJson(array))
            } else {
                val array =
                    GsonUtils.fromJson<ArrayList<SaveModel>>(
                        data2,
                        GsonUtils.getListType(SaveModel::class.java)
                    )
                array.add(SaveModel(TimeUtils.getNowString(), mDataAdapter2.data))
                kv2.encode("data2", GsonUtils.toJson(array))
            }
            ToastUtils.showShort("??????????????????")


        }
        findViewById<Button>(R.id.loadLog).setOnClickListener {
            val kv = MMKV.defaultMMKV()

            val data1 = kv.decodeString("data1")
            val array1 =
                GsonUtils.fromJson<ArrayList<SaveModel>>(
                    data1,
                    GsonUtils.getListType(SaveModel::class.java)
                )
            if(null==array1||array1.size==0){
                ToastUtils.showShort("????????????")
                return@setOnClickListener
            }
            val arrayEmpty = arrayListOf<String>()
            for (i in array1.indices) {
                arrayEmpty.add(array1[i].time)
            }
            LogUtils.i(arrayEmpty.toString())
            val array = arrayOfNulls<String>(arrayEmpty.size)
            for (i in arrayEmpty.indices) {
                array[i]= arrayEmpty[i]
            }

            LogUtils.i("array??????",arrayEmpty.toString())

            AlertDialog.Builder(this).setItems(array
            ) { p0, p1 ->
                mDataAdapter.setNewData(array1[p1].resultData)
            }.show()
        }

        findViewById<Button>(R.id.loadLog2).setOnClickListener {
            val kv = MMKV.defaultMMKV()
            val data2 = kv.decodeString("data2")
            val array2 =
                GsonUtils.fromJson<ArrayList<SaveModel>>(
                    data2,
                    GsonUtils.getListType(SaveModel::class.java)
                )
            if(null==array2||array2.size==0){
                ToastUtils.showShort("????????????")
                return@setOnClickListener
            }
            val arrayEmpty = arrayListOf<String>()
            for (i in array2.indices) {
                arrayEmpty.add(array2[i].time)
            }
            LogUtils.i(arrayEmpty.toString())
            val array = arrayOfNulls<String>(arrayEmpty.size)
            for (i in arrayEmpty.indices) {
                array[i]= arrayEmpty[i]
            }


            AlertDialog.Builder(this).setItems(array
            ) { p0, p1 ->
                mDataAdapter2.setNewData(array2[p1].resultData)
            }.show()
        }

        findViewById<TextView>(R.id.btnClearAllLog).setOnClickListener {
            AlertDialog.Builder(this).setNegativeButton("??????"
            ) { p0, p1 ->
                p0.cancel()
            }.setPositiveButton("??????"
            ) { p0, p1 ->
                val kv = MMKV.defaultMMKV()
                kv.clearAll()
            }.show()

        }

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