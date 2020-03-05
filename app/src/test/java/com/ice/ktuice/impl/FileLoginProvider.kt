package com.ice.ktuice.impl

import java.io.File
import java.io.FileNotFoundException

class FileLoginProvider{
    companion object {
        fun getLoginFromFile(): Pair<String, String>{
            val file = File("ktulogin.local")
            if(!file.exists()){
                throw FileNotFoundException("For this test to work, a file at /app/ktulogin.local must exists" +
                        " and contain the login information to ktu ais")
            }
            val inputStream = file.inputStream()
            inputStream.bufferedReader().use {
                val lines = it.readLines()
                val username = lines[0]
                val password = lines[1]
                return Pair(username, password)
            }
        }
    }
}