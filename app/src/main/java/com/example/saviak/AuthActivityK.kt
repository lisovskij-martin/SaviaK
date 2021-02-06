package com.example.saviak

import android.app.UiModeManager.MODE_NIGHT_NO
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.example.api.model.user.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rengwuxian.materialedittext.MaterialEditText

class AuthActivityK : AppCompatActivity() {
    private var btnSignIn: Button? = null
    private var btnRegister: Button? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var users: DatabaseReference? = null
    private var root: RelativeLayout? = null
    private var mSettings: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnRegister = findViewById(R.id.btnRegister)
        root = findViewById(R.id.root_element)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db?.getReference("People")
        mSettings = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        checkPreferences();
        //btnRegister.setOnClickListener(View.OnClickListener { showRegisterWindow() })
        //btnSignIn.setOnClickListener(View.OnClickListener { showSignInWindow() })
        btnRegister?.setOnClickListener { showRegisterWindow() }
        btnSignIn?.setOnClickListener { showSignInWindow() }
    }

    private fun checkPreferences() {
        if (mSettings!!.contains(PREFERENCES_LOGIN) && mSettings!!.contains(PREFERENCES_PASSWORD)) {
            // выводим данные в TextView
            val login = mSettings?.getString(
                PREFERENCES_LOGIN,
                ""
            )
            val password = mSettings?.getString(
                PREFERENCES_PASSWORD,
                ""
            )
            auth!!.signInWithEmailAndPassword(login!!, password!!)
                .addOnSuccessListener {
                    root?.let { Snackbar.make(it, "Сессия сохранена", Snackbar.LENGTH_SHORT).show() }
                    startActivity(Intent(this, AviaMainActivityK::class.java))
                }.addOnFailureListener { e ->
                    root?.let {
                        Snackbar.make(it, "Ошибка авторизации." + e.message, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun showSignInWindow() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Вход")
        dialog.setMessage("Введите данные для входа")
        val inflater = LayoutInflater.from(this)
        val sign_in_window = inflater.inflate(R.layout.window_sigh_in, null)
        dialog.setView(sign_in_window)
        val email: MaterialEditText = sign_in_window.findViewById(R.id.emailField)
        val password: MaterialEditText = sign_in_window.findViewById(R.id.passwordField)
        dialog.setNegativeButton(
            "Отменить"
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        dialog.setPositiveButton("Войти",
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (TextUtils.isEmpty(email.text.toString())) {
                    Snackbar.make(root!!, "Введите вашу почту", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (password.text!!.length < 5) {
                    Snackbar.make(
                        root!!,
                        "Введите ваш пароль (более 5 символов)",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                auth!!.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnSuccessListener { //ЗАПИСЬ СЕССИИ В PREFERENCIES
                        val editor = mSettings!!.edit()
                        editor.putString(
                            PREFERENCES_LOGIN,
                            email.text.toString()
                        )
                        editor.putString(
                            PREFERENCES_PASSWORD,
                            password.text.toString()
                        )
                        editor.apply()
                        startActivity(Intent(this, AviaMainActivityK::class.java))
                    }.addOnFailureListener { e ->
                        Snackbar.make(
                            root!!,
                            "Ошибка авторизации." + e.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            })
        dialog.show()
    }

    private fun showRegisterWindow() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Регистрация")
        dialog.setMessage("Введите все данные для регистрации")
        val inflater = LayoutInflater.from(this)
        val register_window = inflater.inflate(R.layout.window_register, null)
        dialog.setView(register_window)
        val email: MaterialEditText = register_window.findViewById(R.id.emailField)
        val name: MaterialEditText = register_window.findViewById(R.id.nameField)
        val password: MaterialEditText = register_window.findViewById(R.id.passwordField)
        val phone: MaterialEditText = register_window.findViewById(R.id.phoneField)
        dialog.setNegativeButton(
            "Отменить"
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        dialog.setPositiveButton("Добавить",
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (TextUtils.isEmpty(email.text.toString())) {
                    Snackbar.make(root!!, "Введите вашу почту", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(name.text.toString())) {
                    Snackbar.make(root!!, "Введите ваше имя", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(phone.text.toString())) {
                    Snackbar.make(root!!, "Введите ваш телефон", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (password.text!!.length < 5) {
                    Snackbar.make(
                        root!!,
                        "Введите ваш пароль (более 5 символов)",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                auth!!.createUserWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                )
                    .addOnSuccessListener {
                        val user = User(
                            email.text.toString(), name.text.toString(),
                            password.text.toString(), phone.text.toString()
                        )
                        users!!.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(user)
                            .addOnSuccessListener {
                                Snackbar.make(
                                    root!!,
                                    "Пользователь добавлен!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                    }
            })
        dialog.show()
    }

    companion object {
        const val PREFERENCES_LOGIN = "LOGIN"
        const val PREFERENCES_PASSWORD = "PASSWORD"
        const val PREFERENCES_FILE = "mysettings"
    }
}