package hr.tvz.android.qrscan.view

import hr.tvz.android.qrscan.model.JwtToken

interface IAdminLogin {
    interface IMainActivity {
        fun loginSuccess(jwtToken: JwtToken)
        fun loginFailure(errorMessage: String)
    }

    interface IAdminLoginPresenter {
        fun onLogin(username: String, password: String, ipv4: String)
    }

    interface IAdminLoginModel {
        interface Finished {
            fun loginSuccess(jwtToken: JwtToken)
            fun loginFailure(errorMessage: String)
        }

        fun login(listener: Finished, username: String, password: String, ipv4: String)
    }
}