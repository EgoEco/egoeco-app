package com.example.egoeco_app.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

open class BaseEntity {
    @PrimaryKey(autoGenerate = true) @ColumnInfo var id: Long = 0L
    @ColumnInfo var createdDate: Long? = null
}