package com.fanqie.xfqdeobf.util.data

import com.fanqie.xfqdeobf.R
import com.fanqie.xfqdeobf.util.decodeToDataClass
import com.fanqie.xfqdeobf.util.hostInfo
import kotlinx.serialization.Serializable

object Licenses {
    @Serializable
    data class AboutLibraries(
        val libraries: List<LibraryLicense>
    )

    @Serializable
    data class DeveloperInfo(
        val name: String
    )

    @Serializable
    data class LibraryLicense(
        val uniqueId: String,
        val website: String? = null,
        val licenses: List<String>,
        val developers: List<DeveloperInfo>
    ) {
        fun getAuthor(): String {
            return developers.joinToString(",") { it.name }
        }
    }

    val list: List<LibraryLicense> by lazy {
        val libs = hostInfo.application.resources.openRawResource(R.raw.aboutlibraries)
        val content = libs.bufferedReader().use { x -> x.readText() }
        val info: AboutLibraries = content.decodeToDataClass()
        info.libraries.filter { it.website != null }
    }
}
