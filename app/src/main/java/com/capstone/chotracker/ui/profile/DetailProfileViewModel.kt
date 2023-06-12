import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.data.api.general.ApiConfigGeneral
import com.capstone.chotracker.data.api.general.ApiServiceGeneral
import com.capstone.chotracker.data.response.profile.ProfileUserResponse
import com.capstone.chotracker.data.response.profile.UpdateUserResponse
import kotlinx.coroutines.launch

class DetailProfileViewModel : ViewModel() {
    private val apiService: ApiServiceGeneral = ApiConfigGeneral.getApiGeneral()


    fun getUserById(userId: String): LiveData<ProfileUserResponse> {
        val result = MutableLiveData<ProfileUserResponse>()

        viewModelScope.launch {
            try {
                val response = apiService.getUserById(userId)

            } catch (e: Exception) {


            }
        }

        return result
    }


    fun updateUser(userId: String, data: UpdateUserResponse): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        viewModelScope.launch {
            try {
                val response = apiService.updateUser(userId, data)

            } catch (e: Exception) {

                result.value = false
            }
        }

        return result
    }
}
