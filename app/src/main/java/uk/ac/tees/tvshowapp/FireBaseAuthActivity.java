package uk.ac.tees.tvshowapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

/**
 * The activity which is opened when you launch the app.
 * Detects if the user is logged in and directs them to the login screen if necessary.
 * <p>
 * Uses FirebaseUI to handle login.
 */
public class FireBaseAuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_auth);

        // if the user is already logged in then launch main activity, else show the login screen
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            createSignInIntent();
        }
    }

    /**
     * show the FirebaseUI login screen and allows for the user to sign in to their account
     */
    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.FirebaseTheme)
                        .setLogo(R.drawable.launch_screen)
                        .setIsSmartLockEnabled(false)
                        .setTosAndPrivacyPolicyUrls(
                                "https://github.com/Camburg/AppTermsPrivacy/blob/master/Privacy%20Policy.md",
                                "https://github.com/Camburg/AppTermsPrivacy/blob/master/Terms%20of%20Service.md")
                        .build(),
                RC_SIGN_IN);
    }

    //Handles the result of the Firebase Login Attempt
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (response == null) {
                    createSignInIntent();
                } else {
                    // handle error esponse.getError().getErrorCode()
                }
            }
        }
    }


    /**
     * Signs out the user from the app.
     */
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this);
    }
}
