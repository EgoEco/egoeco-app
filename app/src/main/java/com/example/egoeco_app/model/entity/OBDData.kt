package com.example.egoeco_app.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.io.Serializable
import java.text.SimpleDateFormat

@Entity(tableName = "obd_data")
data class OBDData(
    @ColumnInfo var prefix1: Int = 0,
    @ColumnInfo var prefix2: Int = 0,
    @ColumnInfo var engRPM_A: Int = 0,
    @ColumnInfo var engRPM_B: Int = 0,
    @ColumnInfo var vehicleSpd: Int = 0,
    @ColumnInfo var ecoDriveLevel: Int = 0,
    @ColumnInfo var reserved: Int = 0,
    @ColumnInfo var checkSum: Int = 0,
    @ColumnInfo var rpm: Int = 0,
    @ColumnInfo var timeStamp: Long = 0,
    @ColumnInfo var timeString: String = "",
) : BaseEntity() {

    @Ignore
    @ExperimentalUnsignedTypes
    fun validate(): Boolean {
        val sum = sumOf(
            prefix1, prefix2, engRPM_A, engRPM_B, vehicleSpd, ecoDriveLevel, reserved
        )
        var cs = sum.toUByte()
        cs = cs and 0xFF.toUByte()
        cs = (cs.inv() + 1.toUByte()).toUByte()
        if (prefix1 != 85 || prefix2 != 1 || reserved != 0) return false
        return checkSum == cs.toInt()
    }

    @Ignore
    fun initialize() {
        initTimeString()
        initRPM()
    }

    @Ignore
    fun initTimeString() {
        val sdf = SimpleDateFormat("hh:mm:ss")
        timeString = sdf.format(timeStamp)
    }

    @Ignore
    fun initRPM() {
        rpm = (256 * engRPM_A + engRPM_B) / 4
    }

    @Ignore
    fun sumOf(vararg arg: Int): Int {
        return arg.sum()
    }
}

