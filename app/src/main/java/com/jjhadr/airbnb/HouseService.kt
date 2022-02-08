package com.jjhadr.airbnb

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET(value = "v3/036593de-7005-4edb-9e03-bfba107da0b4")
    fun getHouseList(): Call<HouseDto>
}