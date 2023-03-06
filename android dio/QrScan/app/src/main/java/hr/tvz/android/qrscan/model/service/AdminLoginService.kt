package hr.tvz.android.qrscan.model.service

import hr.tvz.android.qrscan.model.JwtToken
import hr.tvz.android.qrscan.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AdminLoginService {
    @POST("api/admin/login")
    fun adminLogin(@Body user: User): retrofit2.Call<JwtToken>
}