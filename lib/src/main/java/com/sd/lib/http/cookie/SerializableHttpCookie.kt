package com.sd.lib.http.cookie

import java.io.Serializable
import java.net.HttpCookie

class SerializableHttpCookie : Serializable {
    val name: String
    var value: String? = null

    var comment: String? = null
    var commentURL: String? = null
    var discard: Boolean = false
    var domain: String? = null
    var maxAge: Long = -1
    var path: String? = null
    var portlist: String? = null
    var secure: Boolean = false
    var version: Int = 1

    constructor(cookie: HttpCookie) {
        this.name = cookie.name
        this.value = cookie.value
        this.comment = cookie.comment
        this.commentURL = cookie.commentURL
        this.discard = cookie.discard
        this.domain = cookie.domain
        this.maxAge = cookie.maxAge
        this.path = cookie.path
        this.portlist = cookie.portlist
        this.secure = cookie.secure
        this.version = cookie.version
    }

    fun toHttpCookie(): HttpCookie {
        return HttpCookie(name, value).apply {
            comment = this@SerializableHttpCookie.comment
            commentURL = this@SerializableHttpCookie.commentURL
            discard = this@SerializableHttpCookie.discard
            domain = this@SerializableHttpCookie.domain
            maxAge = this@SerializableHttpCookie.maxAge
            path = this@SerializableHttpCookie.path
            portlist = this@SerializableHttpCookie.portlist
            secure = this@SerializableHttpCookie.secure
            version = this@SerializableHttpCookie.version
        }
    }

    companion object {
        fun from(cookie: HttpCookie): SerializableHttpCookie {
            return SerializableHttpCookie(cookie)
        }
    }
}