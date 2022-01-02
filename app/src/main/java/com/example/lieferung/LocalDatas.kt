package com.example.lieferung

import android.provider.BaseColumns

object LocalDatas {  //로컬 데이터베이스의 자료형태로 정의된 objectt

    object userData: BaseColumns {  //users 라는 DB테이블의 데이터 컬럼 내용 정리
        const val TABLE_NAME = "users"
        const val COLUMN_NAME_NAME = "name" //컬럼명 작성
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_PASSWORD = "password"
    }
        //만약 다른 DB형식을 지정하고 싶다면 동일한 방식으로 추가
}