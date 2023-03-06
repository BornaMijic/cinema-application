package hr.tvz.android.qrscan.model

import hr.tvz.android.qrscan.model.service.AdminLoginService
import hr.tvz.android.qrscan.model.service.ServiceRetrofit
import hr.tvz.android.qrscan.view.IAdminLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminLoginModel: IAdminLogin.IAdminLoginModel {
    override fun login(listener: IAdminLogin.IAdminLoginModel.Finished, username: String, password: String, ipv4: String) {
        if(ipv4.isEmpty()) {
            listener.loginFailure("Došlo je do pogreške")
        } else {
            ServiceRetrofit().createAndGetService(AdminLoginService::class.java, ipv4).adminLogin(User(username, password)).enqueue(object : Callback<JwtToken> {
                override fun onResponse(call: Call<JwtToken>, response: Response<JwtToken>) {
                    if(response.isSuccessful) {
                        listener.loginSuccess(response.body()!!)
                    } else {
                        listener.loginFailure("Krivo korisničko ime ili lozinka")
                    }
                }

                override fun onFailure(call: Call<JwtToken>, t: Throwable) {
                    listener.loginFailure("Došlo je do pogreške")
                }
            }
            )
        }

    }
}