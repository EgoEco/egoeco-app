package com.example.egoeco_app.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.io.Serializable

@Entity(tableName = "user")
data class User(
    @ColumnInfo val email: String,
    @ColumnInfo val userId: String,
    @ColumnInfo val level: Int,
    @ColumnInfo val msrl: Int,
    @ColumnInfo val point: Int,
    @ColumnInfo val pw: String
) : BaseEntity() {
    @Ignore
    fun getRawByteData(data: Int) = data.toString(16)
}

