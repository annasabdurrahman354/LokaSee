package com.bangkit.lokasee.data

import com.bangkit.lokasee.R

/**
 * A static data store of [SearchSuggestion]s.
 */
object SearchSuggestionStore {

  val YESTERDAY_SUGGESTIONS = listOf(
    SearchSuggestion(
      R.drawable.ic_filter,
      "481 Van Brunt Street",
      "Brooklyn, NY"
    ),
    SearchSuggestion(
      R.drawable.ic_home,
      "Home",
      "199 Pacific Street, Brooklyn, NY"
    )
  )

  val THIS_WEEK_SUGGESTIONS = listOf(
    SearchSuggestion(
      R.drawable.ic_filter,
      "BEP GA",
      "Forsyth Street, New York, NY"
    ),
    SearchSuggestion(
      R.drawable.ic_filter,
      "Sushi Nakazawa",
      "Commerce Street, New York, NY"
    ),
    SearchSuggestion(
      R.drawable.ic_filter,
      "IFC Center",
      "6th Avenue, New York, NY"
    )
  )
}
