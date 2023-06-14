import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.capstone.chotracker.data.response.article.DataItem

import com.capstone.chotracker.databinding.ItemListArticleBinding
import com.capstone.chotracker.utils.StringUtils
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(private val articleList: List<DataItem>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    class ArticleViewHolder(var binding: ItemListArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ItemListArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val (_,_,title, desc,_,imageUrl, createAt) = articleList[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .transform(RoundedCorners(10))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.ivItemPhoto)
        holder.binding.tvTitlle.text = title
        holder.binding.Desc.text = desc
        holder.binding.CreateAt.text = createAt?.let { formatDate(it) }
        holder.itemView.setOnClickListener{
            onItemClickCallback.onItemClicked(articleList[holder.adapterPosition])
        }

        val apiData : String? = desc
        val startIndex = 0
        val endIndex = 138
        val result = StringUtils.cutStringFromAPI(apiData, startIndex, endIndex)
        holder.binding.Desc.text = result
    }

    private fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate!!)
    }


    override fun getItemCount(): Int {
        return articleList.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataItem)
    }
}
