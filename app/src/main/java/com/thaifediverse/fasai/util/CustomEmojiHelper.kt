/* Copyright 2020 Tusky Contributors
 * 
 * This file is a part of Tusky.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>. */

@file:JvmName("CustomEmojiHelper")
package com.thaifediverse.fasai.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan
import android.view.View

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.thaifediverse.fasai.entity.Emoji

import java.lang.ref.WeakReference
import java.util.regex.Pattern

/**
 * replaces emoji shortcodes in a text with EmojiSpans
 * @param text the text containing custom emojis
 * @param emojis a list of the custom emojis (nullable for backward compatibility with old mastodon instances)
 * @param view a reference to the a view the emojis will be shown in (should be the TextView, but parents of the TextView are also acceptable)
 * @return the text with the shortcodes replaced by EmojiSpans
*/
fun CharSequence.emojify(emojis: List<Emoji>?, view: View) : CharSequence {
    if(emojis.isNullOrEmpty())
        return this

    val builder = SpannableStringBuilder.valueOf(this)

    emojis.forEach { (shortcode, url) ->
        val matcher = Pattern.compile(":$shortcode:", Pattern.LITERAL)
            .matcher(this)

        while(matcher.find()) {
            val span = EmojiSpan(WeakReference(view))

            builder.setSpan(span, matcher.start(), matcher.end(), 0);
            Glide.with(view)
                .asBitmap()
                .load(url)
                .into(span.getTarget())
        }
    }
    return builder
}

class EmojiSpan(val viewWeakReference: WeakReference<View>) : ReplacementSpan() {
    var imageDrawable: Drawable? = null
    
    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?) : Int {
        if (fm != null) {
            /* update FontMetricsInt or otherwise span does not get drawn when
             * it covers the whole text */
            val metrics = paint.fontMetricsInt
            fm.top = metrics.top
            fm.ascent = metrics.ascent
            fm.descent = metrics.descent
            fm.bottom = metrics.bottom
        }
        
        return (paint.textSize * 1.2).toInt()
    }
    
    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        imageDrawable?.let { drawable ->
            canvas.save()

            val emojiSize = (paint.textSize * 1.1).toInt()
            drawable.setBounds(0, 0, emojiSize, emojiSize)

            var transY = bottom - drawable.bounds.bottom
            transY -= paint.fontMetricsInt.descent / 2;

            canvas.translate(x, transY.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }
    }
    
    fun getTarget(): Target<Bitmap> {
        return object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                viewWeakReference.get()?.let { view ->
                    imageDrawable = BitmapDrawable(view.context.resources, resource)
                    view.invalidate()
                }
            }
            
            override fun onLoadCleared(placeholder: Drawable?) {}
        }
    }
}