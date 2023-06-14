import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.chotracker.R
import com.capstone.chotracker.data.response.profile.ProfileUserResponse
import com.capstone.chotracker.data.response.profile.UpdateUserResponse
import com.capstone.chotracker.databinding.ActivityDetailProfileBinding

import com.google.firebase.auth.FirebaseAuth

class DetailProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileBinding
    private lateinit var viewModel: DetailProfileViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DetailProfileViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser?.uid ?: ""

        viewModel.getUserById(userId).observe(this, Observer { profileUserResponse ->
            showUserProfile(profileUserResponse)
        })

        binding.buttonUpdate.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val gender = binding.gender.text.toString()
            val birthDate = binding.birth.text.toString()

            val updateUserResponse = UpdateUserResponse(name, email)

            viewModel.updateUser(userId, updateUserResponse).observe(this, Observer { isUpdated ->
                if (isUpdated) {
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_home, fragment)
                        .commit()
                } else {

                }
            })
        }
    }

    private fun showUserProfile(profileUserResponse: ProfileUserResponse) {
        val data = profileUserResponse.data

        binding.name.setText(data.name)
        binding.email.setText(data.email)
        binding.gender.setText(data.gender)
        binding.birth.setText(data.birthDate)
    }

}
