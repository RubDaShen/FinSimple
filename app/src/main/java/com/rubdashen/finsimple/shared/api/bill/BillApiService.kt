package     com.rubdashen.finsimple.shared.api.bill

import      com.rubdashen.finsimple.shared.api.bill.request.BillCreationRequest
import com.rubdashen.finsimple.shared.api.bill.request.BillUpdateRequest
import      com.rubdashen.finsimple.shared.api.bill.response.BillCreationResponse
import com.rubdashen.finsimple.shared.api.bill.response.BillInformationResponse
import com.rubdashen.finsimple.shared.api.bill.response.BillUpdateResponse
import      com.rubdashen.finsimple.shared.api.bill.response.BillViewInformationResponse
import      retrofit2.Call
import      retrofit2.http.Body
import retrofit2.http.DELETE
import      retrofit2.http.GET
import      retrofit2.http.Header
import      retrofit2.http.POST
import retrofit2.http.PUT
import      retrofit2.http.Query



public interface BillApiService
{
    @GET("api/v1/bill/billsView")
    public fun billsViewInformation(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<List<BillViewInformationResponse>>

    @POST("api/v1/bill/createBill")
    public fun createBill(
        @Header("Authorization") token: String,
        @Body body: BillCreationRequest
    ): Call<BillCreationResponse>

    @GET("api/v1/bill/billInformation")
    public fun billsInformation(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<BillInformationResponse>

    @PUT("api/v1/bill/updateBill")
    public fun updateBill(
        @Header("Authorization") token: String,
        @Body body: BillUpdateRequest
    ): Call<BillUpdateResponse>

    @DELETE("api/v1/bill/deleteBill")
    public fun deleteBill(
        @Header("Authorization") token: String,
        @Body body: BillUpdateRequest
    ): Call<BillUpdateResponse>
}