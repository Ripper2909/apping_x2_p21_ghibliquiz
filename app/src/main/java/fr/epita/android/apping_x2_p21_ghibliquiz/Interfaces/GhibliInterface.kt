package fr.epita.android.apping_x2_p21_ghibliquiz.Interfaces

import fr.epita.android.apping_x2_p21_ghibliquiz.Models.FilmModel
import fr.epita.android.apping_x2_p21_ghibliquiz.Models.PeopleModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GhibliInterface {

    @GET("people")
    fun listPeople() : Call<ArrayList<PeopleModel>>

    @GET("films/{id}")
    fun getFilmDetail(@Path("id") id: String) : Call<FilmModel>
}