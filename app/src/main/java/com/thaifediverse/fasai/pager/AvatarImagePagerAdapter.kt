package com.thaifediverse.fasai.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thaifediverse.fasai.ViewMediaAdapter
import com.thaifediverse.fasai.fragment.ViewMediaFragment

class AvatarImagePagerAdapter(
        activity: FragmentActivity,
        private val avatarUrl: String
) : ViewMediaAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            ViewMediaFragment.newAvatarInstance(avatarUrl)
        } else {
            throw IllegalStateException()
        }
    }

    override fun getItemCount() = 1

    override fun onTransitionEnd(position: Int) {
    }
}
