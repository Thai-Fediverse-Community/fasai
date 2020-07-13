package com.thaifediverse.fasai.components.search

enum class SearchType(val apiParameter: String) {
    Status("statuses"),
    Account("accounts"),
    Hashtag("hashtags")
}