package com.thaifediverse.fasai.appstore

import com.thaifediverse.fasai.TabData
import com.thaifediverse.fasai.entity.Account
import com.thaifediverse.fasai.entity.Poll
import com.thaifediverse.fasai.entity.Status

data class FavoriteEvent(val statusId: String, val favourite: Boolean) : Dispatchable
data class ReblogEvent(val statusId: String, val reblog: Boolean) : Dispatchable
data class BookmarkEvent(val statusId: String, val bookmark: Boolean) : Dispatchable
data class MuteConversationEvent(val statusId: String, val mute: Boolean) : Dispatchable
data class UnfollowEvent(val accountId: String) : Dispatchable
data class BlockEvent(val accountId: String) : Dispatchable
data class MuteEvent(val accountId: String) : Dispatchable
data class StatusDeletedEvent(val statusId: String) : Dispatchable
data class StatusComposedEvent(val status: Status) : Dispatchable
data class StatusScheduledEvent(val status: Status) : Dispatchable
data class ProfileEditedEvent(val newProfileData: Account) : Dispatchable
data class PreferenceChangedEvent(val preferenceKey: String) : Dispatchable
data class MainTabsChangedEvent(val newTabs: List<TabData>) : Dispatchable
data class PollVoteEvent(val statusId: String, val poll: Poll) : Dispatchable
data class DomainMuteEvent(val instance: String): Dispatchable