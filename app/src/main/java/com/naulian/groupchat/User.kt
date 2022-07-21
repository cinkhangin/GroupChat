package com.naulian.groupchat

data class User(
    val name : String = "",
    var userId : String = "",
){
    fun generateId(){
        userId = "000123"
    }
}
