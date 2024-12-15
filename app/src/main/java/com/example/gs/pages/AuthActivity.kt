package com.example.gs.pages

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gs.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inizializza Firebase Authentication
        auth = Firebase.auth

        // Controlla se l'utente è già autenticato
        if (auth.currentUser != null) {
            navigateToMainActivity(isNewUser = false)
            return
        }

        setContentView(R.layout.activity_login)

        // Collega gli elementi della UI
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Listener per il login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (validateInput(email, password)) {
                signIn(email, password)
            }
        }

        // Listener per la registrazione
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (validateInput(email, password)) {
                createAccount(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        // Validazione email
        if (email.isEmpty()) {
            emailEditText.error = "Email è richiesta"
            emailEditText.requestFocus()
            return false
        }

        // Validazione email (formato valido)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Inserisci un'email valida"
            emailEditText.requestFocus()
            return false
        }

        // Validazione password
        if (password.isEmpty()) {
            passwordEditText.error = "Password è richiesta"
            passwordEditText.requestFocus()
            return false
        }

        // Validazione password (minimo 6 caratteri)
        if (password.length < 6) {
            passwordEditText.error = "La password deve essere lunga almeno 6 caratteri"
            passwordEditText.requestFocus()
            return false
        }

        return true
    }

    private fun signIn(email: String, password: String) {
        loginButton.isEnabled = false

        // Autenticazione con Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loginButton.isEnabled = true
                if (task.isSuccessful) {
                    navigateToMainActivity(isNewUser = false)
                } else {
                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                            "Credenziali non valide"
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                            "Utente non trovato"
                        else -> "Autenticazione fallita: ${task.exception?.localizedMessage}"
                    }
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createAccount(email: String, password: String) {
        registerButton.isEnabled = false

        // Creazione di un nuovo utente con Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                registerButton.isEnabled = true
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(baseContext,
                                    "Registrazione completata. Controlla la tua email per la verifica.",
                                    Toast.LENGTH_LONG).show()
                                navigateToMainActivity(isNewUser = true)
                            }
                        }
                } else {
                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                            "Email già registrata"
                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                            "Password troppo debole"
                        else -> "Registrazione fallita: ${task.exception?.localizedMessage}"
                    }
                    // Mostra un messaggio di errore appropriato
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Navigazione alla MainActivity
    private fun navigateToMainActivity(isNewUser: Boolean) {
        val intent = if (isNewUser) {
            Intent(this, TourActivity::class.java)
        } else {
            Intent(this, LoadingActivity::class.java)
        }
        intent.putExtra("USER_EMAIL", auth.currentUser?.email)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}