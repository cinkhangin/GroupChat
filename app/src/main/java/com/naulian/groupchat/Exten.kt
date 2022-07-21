package com.naulian.groupchat

import android.widget.Toast
import androidx.fragment.app.Fragment

class Exten {
}

fun String.firstCap() : String{
   return this[0].toString().toUpperCase()
}

fun Fragment.showToast(message: String){
   Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun <T> T.myapply(action : (T) -> Unit){
   action(this)
}

fun <T> T.myLet(action : (T) -> Unit){
   if(this != null) action(this)
}

fun <T> T.myalso(action: (T) -> Unit) : T{
   action(this)
   return this
}