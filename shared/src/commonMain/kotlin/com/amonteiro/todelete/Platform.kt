package com.amonteiro.todelete

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform