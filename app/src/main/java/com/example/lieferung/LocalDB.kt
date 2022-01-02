package com.example.lieferung

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class LocalDB (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        //DB 생성시 실행
        if (db != null) {
            createDatabase(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //DB 버전 변경시 실행
        val sql: String = "DROP TABLE if exists ${LocalDatas.userData.TABLE_NAME}"

        if (db != null) {
            db.execSQL(sql)
            onCreate(db)
        }  //버전이 변경되면 기존 Table을 삭제 후 재생성함
    }

    fun createDatabase(db: SQLiteDatabase) {
        //Table이 존재하지 않는 경우 생성
        var sql: String = "CREATE TABLE if not exists ${LocalDatas.userData.TABLE_NAME} (" +
                            "${BaseColumns._ID} integer primary key autoincrement," +
                            "${LocalDatas.userData.COLUMN_NAME_NAME} varchar(15)," +
                            "${LocalDatas.userData.COLUMN_NAME_ID} varchar(15)," +
                            "${LocalDatas.userData.COLUMN_NAME_PASSWORD} varchar(20)" +
                            ");"
        db.execSQL(sql)
    }

    fun registerUser(name: String, id: String, password: String) {
        //Table에 insert하는 함수
        val db = this.writableDatabase
        val values = ContentValues().apply {  //insert될 데이터값
            put(LocalDatas.userData.COLUMN_NAME_NAME, name)
            put(LocalDatas.userData.COLUMN_NAME_ID, id)
            put(LocalDatas.userData.COLUMN_NAME_PASSWORD, password)
        }
        val newRowld = db?.insert(LocalDatas.userData.TABLE_NAME, null, values)
        //insert 후 insert된 primary key column의 값(_ID) 반환환
   }

    fun checkIdExists(id: String): Boolean {
        //DB 내에 같은 ID의 유저가 존재하는지 확인
        val db = this.readableDatabase

        //return 받고자 하는 column의 값의 array
        val projection = arrayOf(BaseColumns._ID)
        //LocalDatas.userData.COLUMN_NAME_NAME, LocalDatas.userData.COLUMN_NAME_ID, LocalDatas.userData.COLUMN_NAME_PASSWORD

        //WHERE "id" = id and "password" = password 구문 적용하는 부분
        val selection = "${LocalDatas.userData.COLUMN_NAME_ID} = ?"
        val selectionArgs = arrayOf(id)

        val cursor = db.query(
            LocalDatas.userData.TABLE_NAME, //TABLE
            projection,         //return 받고자 하는 컬럼
            selection,          //where 조건
            selectionArgs,      //where 조건에 해당하는 값의 배열
            null,               //그룹 조건
            null,               //having 조건
            null                //orderby 조건
        )

        if (cursor.count > 0) {     //반환된 cursor 값이 존재
            return true
        } else {    //반환된 cursor 값이 없음
            return false
        }
    }

    //login은 firebase로
//    fun login(id: String, password: String): Boolean {
//        val db = this.readableDatabase
//
//        //return 받고자 하는 column 값의 array
//        val projection = arrayOf(BaseColumns._ID)
//        //LocalDatas.userData.COLUMN_NAME_NAME, LocalDatas.userData.COLUMN_NAME_ID, LocalDatas.userData.COLUMN_NAME_PASSWORD
//
//        //WHERE "id" = id and "password" = password 구문 적용하는 부분
//        val selection = "${LocalDatas.userData.COLUMN_NAME_ID} = ? AND " +
//                        "${LocalDatas.userData.COLUMN_NAME_PASSWORD} = ?"
//        val selectionArgs = arrayOf(id, password)
//
//        val cursor = db.query(
//            LocalDatas.userData.TABLE_NAME, //TABLE
//            projection,         //return 받고자 하는 컬럼
//            selection,          //where 조건
//            selectionArgs,      //where 조건에 해당하는 값의 배열
//            null,               //그룹 조건
//            null,               //having 조건
//            null                //orderby 조건
//        )
//
//        if (cursor.count > 0) {     //반환된 cursor 값이 null이면
//            return true
//        } else {    //반환된 cursor 값이 null이 아니면
//            return false
//        }
//    }
}