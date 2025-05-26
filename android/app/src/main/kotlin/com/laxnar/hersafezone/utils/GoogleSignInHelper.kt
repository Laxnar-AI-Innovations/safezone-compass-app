
package com.laxnar.hersafezone.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.laxnar.hersafezone.R
import kotlinx.coroutines.tasks.await

class GoogleSignInHelper(private val context: Context) {
    
    private val auth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        GoogleSignIn.getClient(context, gso)
    }
    
    suspend fun signIn(): Boolean {
        return try {
            val signInIntent = googleSignInClient.signInIntent
            // Note: In a real implementation, you would need to handle the activity result
            // This is a simplified version for demonstration
            val account = GoogleSignIn.getLastSignedInAccount(context)
            
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user != null
            } else {
                false
            }
        } catch (e: ApiException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }
    
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }
}
