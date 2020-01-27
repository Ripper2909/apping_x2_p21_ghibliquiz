package fr.epita.android.apping_x2_p21_ghibliquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import fr.epita.android.apping_x2_p21_ghibliquiz.Adapters.AnswerAdapter
import fr.epita.android.apping_x2_p21_ghibliquiz.Interfaces.GhibliInterface
import fr.epita.android.apping_x2_p21_ghibliquiz.Models.FilmModel
import fr.epita.android.apping_x2_p21_ghibliquiz.Models.PeopleModel
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val baseUrl = "https://ghibliapi.herokuapp.com/"

    private var people = PeopleModel(
        "", "", "", ArrayList()
    )

    private var film = FilmModel(
        "", "", "", "", ""
    )

    fun listInitializer(answsers: ArrayList<PeopleModel>) {
        val itemClickListener = View.OnClickListener {
            val peopleName = it.tag as String
            getDetails(peopleName)
        }

        answers.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )

        answers.setHasFixedSize(true)
        answers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        answers.adapter = AnswerAdapter(this, answsers, itemClickListener)
    }

    private fun peopleChecker(peopleName: String): Boolean {
        return peopleName == people.name
    }

    private fun getFilmId(url: String): String {
        return url.substring("https://ghibliapi.herokuapp.com/films/".length)
    }

    private fun getDetails(peopleName: String) {
        val explicitIntent = Intent(this, PeopleActivity::class.java)

        explicitIntent.putExtra("FILM_ID", film.id)
        explicitIntent.putExtra("CHARACTER_NAME", people.name)
        explicitIntent.putExtra("CORRECT", peopleChecker(peopleName))
        explicitIntent.putExtra("FILM_BASE_URL", this.baseUrl)

        startActivity(explicitIntent)
    }

    fun getAnswer(
        peopleList: ArrayList<PeopleModel>,
        filmId: String
    ): ArrayList<PeopleModel> {
        val answers: ArrayList<PeopleModel> = arrayListOf()
        var answer = false

        for (i in peopleList) {
            if (!answer) {
                if (i.films.contains("https://ghibliapi.herokuapp.com/films/$filmId") && answers.size < 6) {
                    answers.add(i)
                    answer = true
                }
            } else {
                if (!i.films.contains("https://ghibliapi.herokuapp.com/films/$filmId") && answers.size < 6) {
                    answers.add(i)
                }
            }
        }

        return answers
    }

    fun getFilmId(peopleList: ArrayList<PeopleModel>): String {
        val random = Random().nextInt(peopleList.size)
        val answer = peopleList[random].films[0]
        people = peopleList[random]

        return getFilmId(answer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GhibliInterface = retrofit.create(GhibliInterface::class.java)

        val callbackFilm = object : Callback<FilmModel> {
            override fun onResponse(call: Call<FilmModel>, response: Response<FilmModel>) {
                val responseCode = response.code()
                if (responseCode == 200) {
                    if (response.body() != null) {
                        film = response.body()!!
                        question.text =
                            "Which one of these characters can be found in the movie " + film.title + " ?"
                    }
                }
            }

            override fun onFailure(call: Call<FilmModel>, t: Throwable) {
                Log.d("App failure", t.message)
            }
        }

        val callbackPeople = object : Callback<ArrayList<PeopleModel>> {
            override fun onResponse(
                call: Call<ArrayList<PeopleModel>>,
                response: Response<ArrayList<PeopleModel>>
            ) {
                val responseCode = response.code()
                if (responseCode == 200) {
                    if (response.body() != null) {
                        val peopleList = response.body()!!
                        val filmId: String = getFilmId(peopleList)
                        val answers: ArrayList<PeopleModel> = getAnswer(peopleList, filmId)

                        listInitializer(answers)
                        service.getFilmDetail(filmId).enqueue(callbackFilm)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<PeopleModel>>, t: Throwable) {
                Log.d("App failure", t.message)
            }
        }

        service.listPeople().enqueue(callbackPeople)
    }
}
