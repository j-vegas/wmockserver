package com.wmockserver.utils.ext

import com.github.tomakehurst.wiremock.http.Request
import com.wmockserver.utils.Constants.ADMIN
import com.wmockserver.utils.Constants.STUB

fun Request.isAdmin() = this.url.startsWith("/__admin")

fun Request.prefix() = if (this.isAdmin()) ADMIN else STUB