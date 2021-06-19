package com.programmers.kmooc.repositories

import android.util.Log
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.network.HttpClient
import com.programmers.kmooc.utils.DateUtil
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class KmoocRepository {

    /**
     * 국가평생교육진흥원_K-MOOC_강좌정보API
     * https://www.data.go.kr/data/15042355/openapi.do
     */

    private val httpClient = HttpClient("http://apis.data.go.kr/B552881/kmooc")
    private val serviceKey =
        "9%2FCIHgZI1SKc5ppDSwmx0REDZtF61KNeVHqxA54N6MpyAwrf9v%2BOzBvfOxoQyh8%2F8a26oASfPpEFCmnuncdGGA%3D%3D"

    fun list(completed: (LectureList?) -> Unit) {
        httpClient.getJson(
            "/courseList",
            mapOf("ServiceKey" to serviceKey, "Mobile" to 1)
        ) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
            result.onFailure {
                completed(null)
                Log.d("TEST", "FAILED1 ${it.message}")
            }
        }
    }

    fun next(currentPage: LectureList, completed: (LectureList?) -> Unit) {
        val nextPageUrl = currentPage.next
        httpClient.getJson(nextPageUrl, emptyMap()) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
            result.onFailure {
                completed(null)
                Log.d("TEST", "FAILED2 ${it.message}")
            }
        }
    }

    fun detail(courseId: String, completed: (Lecture?) -> Unit) {
        httpClient.getJson(
            "/courseDetail",
            mapOf("CourseId" to courseId, "serviceKey" to serviceKey)
        ) { result ->
            result.onSuccess {
                completed(parseLecture(JSONObject(it)))
            }
            result.onFailure {
                completed(null)
                Log.d("TEST", "FAILED3 ${it.message}")
            }
        }
    }

    private fun parseLectureList(jsonObject: JSONObject): LectureList {

        val pagination = jsonObject.getJSONObject("pagination")

        val data = ArrayList<Lecture>()
        val obj = jsonObject.getJSONArray("results")

        for (i in 0 until obj.length()) {
            data.add(parseLecture(obj.getJSONObject(i)))
//            Log.d("TEST", "data : ${data[i]}")
        }

        val a = LectureList(
            pagination.getInt("count"),
            pagination.getInt("num_pages"),
            pagination.getString("previous"),
            pagination.getString("next"),
            data
        )

        return a
    }


    private fun parseLecture(results: JSONObject): Lecture {
//        val results = jsonObject.getJSONObject("results")

        val courseImage = results.getJSONObject("media").getJSONObject("image").getString("small")
        val courseImageLarge = results.getJSONObject("media").getJSONObject("image").getString("large")

        Log.d("TEST", "1 : $courseImage")
        Log.d("TEST", "2 : $courseImageLarge")

        val a = Lecture(
            id = results.getString("id"),
            number = results.getString("number"),
            name = results.getString("name"),
            classfyName = results.getString("classfy_name"),
            middleClassfyName = results.getString("middle_classfy_name"),
            courseImage = courseImage,
            courseImageLarge = courseImageLarge,
            shortDescription = results.getString("short_description"),
            orgName = results.getString("org_name"),
            start = DateUtil.parseDate(results.getString("start")),
            end = DateUtil.parseDate(results.getString("end")),
            teachers = results.optString("teachers"),
            overview = results.optString("overview")
        )

        Log.d("TEST", "TEST : ${a.toString()}")

        return a
    }
}