package fr.epita.android.apping_x2_p21_ghibliquiz.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.android.apping_x2_p21_ghibliquiz.Models.PeopleModel
import fr.epita.android.apping_x2_p21_ghibliquiz.R

const val genderMaleIcon = "https://img.icons8.com/plasticine/100/000000/male.png"
const val genderFemaleIcon = "https://img.icons8.com/plasticine/100/000000/female.png"

class AnswerAdapter(
    private val context: Context,
    private val data: ArrayList<PeopleModel>,
    private val itemOnClickListener: View.OnClickListener
) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.name)
        val ageView: TextView = itemView.findViewById(R.id.age)
        val genderView: ImageView = itemView.findViewById(R.id.gender)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater
            .from(context)
            .inflate(R.layout.activity_answer, parent, false)

        rowView.setOnClickListener(itemOnClickListener)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val index = data[position]

        holder.nameView.text = index.name
        holder.ageView.text = index.age

        if (index.gender == "Male") {
            Glide
                .with(context)
                .load(genderMaleIcon)
                .error(AppCompatResources.getDrawable(context, R.drawable.error))
                .into(holder.genderView)
        } else {
            Glide
                .with(context)
                .load(genderFemaleIcon)
                .error(AppCompatResources.getDrawable(context, R.drawable.error))
                .into(holder.genderView)
        }

        holder.itemView.tag = index.name
    }
}