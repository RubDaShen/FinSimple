package     com.rubdashen.finsimple.shared.api.user

import      com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import      retrofit2.Call
import      retrofit2.http.GET
import      retrofit2.http.Header
import      retrofit2.http.Query



public interface UserApiService
{
    @GET("api/v1/user/findById")
    public fun userInformation(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<UserInformationResponse>
}