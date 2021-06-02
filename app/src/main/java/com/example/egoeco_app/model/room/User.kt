package com.example.egoeco_app.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.io.Serializable
import java.text.SimpleDateFormat

@Entity(tableName = "user")
data class User(
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
) : BaseEntity(), Serializable {
    @Ignore
    fun getRawByteData(data: Int) = data.toString(16)
}

