package fr.epita.android.apping_x2_p21_ghibliquiz

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.GsonBuilder
import fr.epita.android.apping_x2_p21_ghibliquiz.Interfaces.GhibliInterface
import fr.epita.android.apping_x2_p21_ghibliquiz.Models.FilmModel
import kotlinx.android.synthetic.main.activity_people.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PeopleActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people)

        button.setOnClickListener(this@PeopleActivity)

        val originIntent = intent
        val baseUrl = originIntent.getStringExtra("FILM_BASE_URL")
        val filmId = originIntent.getStringExtra("FILM_ID")
        val correctBool = originIntent.getBooleanExtra("CORRECT", false)
        val peopleName = originIntent.getStringExtra("CHARACTER_NAME")

        if (!correctBool) {
            correct.text = "WRONG !"
            correct.setTextColor(Color.RED)
        } else {
            correct.text = "RIGHT !"
            correct.setTextColor(Color.GREEN)
        }

        characterView.text = peopleName

        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GhibliInterface = retrofit.create(GhibliInterface::class.java)

        val callbackFilm = object : Callback<FilmModel> {
            override fun onResponse(call: Call<FilmModel>, response: Response<FilmModel>) {
                val responseCode = response.code()
                if (responseCode == 200) {
                    if (response.body() != null) {
                        val film = response.body()!!

                        titleView.text = film.title
                        synopsisView.text = film.description
                        directorView.text = film.director
                        yearView.text = film.release_date
                    }
                }
            }

            override fun onFailure(call: Call<FilmModel>, t: Throwable) {
                Log.d("App failure", t.message)
            }
        }

        service.getFilmDetail(filmId).enqueue(callbackFilm)
    }

    override fun onClick(clickedView: View?) {
        if (clickedView != null) {
            when (clickedView.id) {
                R.id.button -> {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://google.com/search?q=" + titleView.text)
                    startActivity(openURL)
                }
                else -> {
                    Log.d("App failure", "onClick -> Would you like to know more -> else reached")
                }
            }
        }
    }
}
