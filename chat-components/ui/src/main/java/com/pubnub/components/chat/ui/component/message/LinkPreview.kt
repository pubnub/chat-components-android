package com.pubnub.components.chat.ui.component.message

import androidx.annotation.WorkerThread
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber

object LinkPreview {
    @WorkerThread
    suspend fun getContent(url: String): Content? = runBlocking {
        try {
            val document = Jsoup.connect(url).get()

            val link = document.getMetaContent("og:url") ?: url
            val image = document.getMetaContent("og:image")
            val title = document.getMetaContent("og:title")
            val description = document.getMetaContent("og:description")

            val content = Content(link, image, title, description)
            return@runBlocking content

        } catch (e: Exception) {
            Timber.e(e)
            return@runBlocking null
        }
    }

    private fun Document.getMetaContent(property: String): String? =
        getElementsByTag("meta").firstOrNull { it.attr("property") == property }?.attr("content")


    data class Content(
        val url: String,
        val imageUrl: String?,
        val title: String?,
        val description: String?,
    )
}