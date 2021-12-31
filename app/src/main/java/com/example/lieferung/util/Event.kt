package com.example.lieferung.util

import android.util.Log

open class Event<out T>(val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {    //이벤트가 이미 처리된 상태면
            Log.d("로그_이벤트: ", "이벤트가 이미 처리된 상태")
            null    //null 반환
        } else {    //그렇지 않으면
            Log.d("로그_이벤트","이벤트가 처리되지 않은 상태")
            hasBeenHandled = true   //이벤트가 처리되었다고 표시 후
            content //값 반환
        }
    }

    //이벤트 처리 여부에 상관없이 값 반환
    fun peekContent(): T = content
}