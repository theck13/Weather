package com.heckofanapp.weather.data.repository

import com.heckofanapp.weather.core.model.domain.location.Location

interface SearchRepository {
    suspend fun search(
        query: String,
    ): List<Location>
}
