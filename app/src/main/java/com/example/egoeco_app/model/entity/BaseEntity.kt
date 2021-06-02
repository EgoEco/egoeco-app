package com.example.egoeco_app.model.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.Serializable

open class BaseEntity : Serializable {
    @PrimaryKey(autoGenerate = true) @ColumnInfo var id: Long = 0L
    @ColumnInfo var createdDate: Long? = null
}