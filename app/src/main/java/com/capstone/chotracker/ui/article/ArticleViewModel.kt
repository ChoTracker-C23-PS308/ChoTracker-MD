import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.chotracker.data.api.general.ApiConfigGeneral
import com.capstone.chotracker.data.api.general.ApiServiceGeneral
import com.capstone.chotracker.data.response.article.DataItem
import com.capstone.chotracker.data.response.article.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class ArticleViewModel : ViewModel() {

    var artcle:List<DataItem> = listOf()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isError: Boolean = false

    fun getArticle(token: String) {
        _isLoading.value = true
        val api = ApiConfigGeneral.getApiGeneral().getArticle("Bearer $token")
        api.enqueue(object : retrofit2.Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    isError = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        artcle = responseBody.data
                    }
                    _message.value = responseBody?.message.toString()

                } else {
                    isError = true
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                _isLoading.value =false
                isError = true
                _message.value = t.message.toString()
            }


        })

    }
}

