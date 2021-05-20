package com.example.egoeco_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.io.Serializable

@Entity(tableName = "obd_data")
data class OBDData(
    @ColumnInfo var prefix1: Int = 0,
    @ColumnInfo var prefix2: Int = 0,
    @ColumnInfo var EngRPM_A: Int = 0,
    @ColumnInfo var EngRPM_B: Int = 0,
    @ColumnInfo var VehicleSpd: Int = 0,
    @ColumnInfo var EcoDriveLevel: Int = 0,
    @ColumnInfo val reserved: Int = 0,
    @ColumnInfo var CheckSum: Int = 0,

    ) : BaseEntity(), Serializable {
    @Ignore
    fun initCheckSum() {
        CheckSum = sumOf(prefix1, prefix2, EngRPM_A, EngRPM_B, VehicleSpd, EcoDriveLevel, reserved)

    }

    fun sumOf(vararg arg: Int): Int {
        return arg.sum()
    }
}

