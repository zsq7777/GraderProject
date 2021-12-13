package com.lh.grader.model

import java.math.BigDecimal

/**
 * $GNGGA
 * 060721.00   UTC时间
 * 3413.7115133  纬度
 * N  纬度半球   北纬
 * 10852.8574291  经度
 * E  经度半球  东经
 * 5   GPS状态
 * 12  使用卫星数量
 * 3.23  HDOP-水平精度因子
 * 480.403  海拔高度
 * M  米
 * -27.861   大地水准面高度异常差值
 * M  米
 * 1.0   差分GPS数据期限
 * 0000  差分参考基站标号
 * *4A
 */

/**
 * @param header
 * @param utcTime UTC时间
 * @param lat 纬度
 * @param latUnit 北纬 南纬
 * @param long  经度
 * @param longUnit  东经  西经
 * @param gpsStatus GPS状态
 * @param gpsCount 使用卫星数量
 * @param spjdyz HDOP-水平精度因子
 * @param altitude   海拔高度
 * @param unitStr1 米
 * @param ddszmgdyccz 大地水准面高度异常差值
 * @param unitStr2 米
 * @param gpsQx 差分GPS数据期限
 * @param jzbh 差分参考基站标号
 */
data class GpsModel(
    val header: String,
    val utcTime: Double,
    val lat: BigDecimal,
    val latUnit: String,
    val long: BigDecimal,
    val longUnit: String,
    val gpsStatus: Int,
    val gpsCount: Int,
    val spjdyz: Double,
    val altitude: BigDecimal,
    val unitStr1: String,
    val ddszmgdyccz: Double,
    val unitStr2: String,
    val gpsQx: Double,
    val jzbh: String
)
