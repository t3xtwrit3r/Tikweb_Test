package com.mubin.tikweb_test.repository

import com.mubin.tikweb_test.api.ApiService
import javax.inject.Inject

class AppRepository
@Inject
constructor(private val apiService: ApiService) {

    //suspend fun getPhotos(page: Int) = apiService.getPhotos(page, 20, "popular", Constants.CLIENT_ID)

}