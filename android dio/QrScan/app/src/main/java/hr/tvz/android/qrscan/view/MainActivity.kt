package hr.tvz.android.qrscan.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hr.tvz.android.qrscan.databinding.ActivityMainBinding
import hr.tvz.android.qrscan.model.AdminLoginModel
import hr.tvz.android.qrscan.model.JwtToken
import hr.tvz.android.qrscan.presenter.AdminLoginPresenter


class MainActivity : AppCompatActivity(),IAdminLogin.IMainActivity {

    private lateinit var binding: ActivityMainBinding
    lateinit var presenter: AdminLoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = AdminLoginPresenter(this, AdminLoginModel())
    }

    override fun loginSuccess(jwtToken: JwtToken) {
        binding.errorMessage.text = ""
        getSharedPreferences("tokenPreference", MODE_PRIVATE).edit().putString("token", jwtToken.token).apply()
        val intent = Intent(applicationContext, MoviesActivity::class.java)
        startActivity(intent);
    }

    override fun loginFailure(errorMessage: String) {
        binding.errorMessage.text = errorMessage
    }

    @SuppressLint("SetTextI18n")
    fun onLogin(view: View?) {

        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val ipv4 = binding.addressInput.text.toString()

        if(username.length < 5  || username.length > 50) {
            binding.errorMessage.text = "Korisničko ime mora imati između 5 i 50 znakova"
        } else if(ipv4.length < 1) {
            binding.errorMessage.text = "Ip adresa mora biti unesena"
        }else {
            val settings = applicationContext.getSharedPreferences("ipAddress", MODE_PRIVATE)
            val editor = settings.edit()
            editor.putString("ipAddress", ipv4);
            editor.apply();
            presenter.onLogin(username, password, ipv4)
        }
    }


}