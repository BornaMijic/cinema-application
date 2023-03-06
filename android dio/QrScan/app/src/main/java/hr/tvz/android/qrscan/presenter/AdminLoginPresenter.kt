package hr.tvz.android.qrscan.presenter

import hr.tvz.android.qrscan.model.AdminLoginModel
import hr.tvz.android.qrscan.model.JwtToken
import hr.tvz.android.qrscan.view.IAdminLogin

class AdminLoginPresenter(private var iMainActivity: IAdminLogin.IMainActivity, private var adminLogin: AdminLoginModel): IAdminLogin.IAdminLoginPresenter, IAdminLogin.IAdminLoginModel.Finished {
    override fun loginSuccess(jwtToken: JwtToken) {
        iMainActivity.loginSuccess(jwtToken)
    }

    override fun loginFailure(errorMessage: String) {
        iMainActivity.loginFailure(errorMessage)
    }

    override fun onLogin(username: String, password: String, ipv4: String) {
            adminLogin.login(this, username, password, ipv4)
    }

}