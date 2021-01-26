/* Copyright 2018 Conny Duck
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

package com.thaifediverse.fasai.adapter

import android.text.method.LinkMovementMethod
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import com.thaifediverse.fasai.R
import com.thaifediverse.fasai.entity.Emoji
import com.thaifediverse.fasai.entity.Field
import com.thaifediverse.fasai.entity.IdentityProof
import com.thaifediverse.fasai.interfaces.LinkListener
import com.thaifediverse.fasai.util.*
import kotlinx.android.synthetic.main.item_account_field.view.*

class AccountFieldAdapter(private val linkListener: LinkListener) : RecyclerView.Adapter<AccountFieldAdapter.ViewHolder>() {

    var emojis: List<Emoji> = emptyList()
    var fields: List<Either<IdentityProof, Field>> = emptyList()

    override fun getItemCount() = fields.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account_field, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val proofOrField = fields[position]

        if(proofOrField.isLeft()) {
            val identityProof = proofOrField.asLeft()

            viewHolder.nameTextView.text = identityProof.provider
            viewHolder.valueTextView.text = LinkHelper.createClickableText(identityProof.username, identityProof.profileUrl)

            viewHolder.valueTextView.movementMethod = LinkMovementMethod.getInstance()

            viewHolder.valueTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,  R.drawable.ic_check_circle, 0)
        } else {
            val field = proofOrField.asRight()
            val emojifiedName = field.name.emojify(emojis, viewHolder.nameTextView)
            viewHolder.nameTextView.text = emojifiedName

            val emojifiedValue = field.value.emojify(emojis, viewHolder.valueTextView)
            LinkHelper.setClickableText(viewHolder.valueTextView, emojifiedValue, null, linkListener)

            if(field.verifiedAt != null) {
                viewHolder.valueTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,  R.drawable.ic_check_circle, 0)
            } else {
                viewHolder.valueTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0 )
            }
        }

    }

    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val nameTextView: TextView = rootView.accountFieldName
        val valueTextView: TextView = rootView.accountFieldValue
    }

}