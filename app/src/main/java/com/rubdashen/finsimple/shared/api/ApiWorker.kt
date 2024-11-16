package     com.rubdashen.finsimple.shared.api

import com.rubdashen.finsimple.menu.wallet.bills.models.BillBaseInformation
import com.rubdashen.finsimple.shared.api.bill.BillApiService
import com.rubdashen.finsimple.shared.api.bill.response.BillCreationResponse
import com.rubdashen.finsimple.shared.api.bill.response.BillInformationResponse
import com.rubdashen.finsimple.shared.api.bill.response.BillUpdateResponse
import      com.rubdashen.finsimple.shared.api.bill.response.BillViewInformationResponse
import      com.rubdashen.finsimple.shared.api.user.UserApiService
import      com.rubdashen.finsimple.shared.api.user.request.UserLoginRequest
import      com.rubdashen.finsimple.shared.api.user.request.UserRegisterRequest
import      com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings
import      okhttp3.MediaType.Companion.toMediaType
import      okhttp3.OkHttpClient
import      okhttp3.Request
import      okhttp3.RequestBody
import      retrofit2.Call
import      retrofit2.Retrofit
import      retrofit2.converter.gson.GsonConverterFactory



public final class ApiWorker
{
    companion object {
    //	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
    //				    Members and Fields
    //	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

        //	-------------------------------------------
        //					Variables
        //	-------------------------------------------
        private val m_Client = OkHttpClient()



    //	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
    //			        Functions and Methods
    //	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

        //	-------------------------------------------
        //			        Functions
        //	-------------------------------------------
        public fun loginUser(login: UserLoginRequest): Request {
            val requestBody: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), login.toJsonString())

            return Request.Builder()
                .url(FinSimpleSettings.apiUrl + "/api/v1/user/login")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()
        }
        public fun registerUser(register: UserRegisterRequest): Request {
            val requestBody: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), register.toJsonString())

            return Request.Builder()
                .url(FinSimpleSettings.apiUrl + "/api/v1/user/register")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()
        }

        public fun userInformation(): Call<UserInformationResponse> {
            val retrofit = Retrofit.Builder()
                .baseUrl(FinSimpleSettings.apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(UserApiService::class.java)
            val call = service.userInformation(
                "Bearer ${UserWrapperSettings.token}", UserWrapperSettings.userId
            )

            return call
        }

        public fun billsViewInformation(): Call<List<BillViewInformationResponse>> {
            val retrofit = Retrofit.Builder()
                .baseUrl(FinSimpleSettings.apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(BillApiService::class.java)
            val call = service.billsViewInformation(
                "Bearer ${UserWrapperSettings.token}", UserWrapperSettings.userId
            )

            return call
        }
        public fun createBill(bill: BillBaseInformation): Call<BillCreationResponse> {
            val retrofit = Retrofit.Builder()
                .baseUrl(FinSimpleSettings.apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(BillApiService::class.java)
            val call = service.createBill(
                "Bearer ${UserWrapperSettings.token}",
                bill.toCreationRequest()
            )

            return call
        }
        public fun billInformation(id: Int): Call<BillInformationResponse> {
            val retrofit = Retrofit.Builder()
                .baseUrl(FinSimpleSettings.apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(BillApiService::class.java)
            val call = service.billsInformation(
                "Bearer ${UserWrapperSettings.token}", id
            )

            return call
        }
        public fun updateBill(id: Int, bill: BillBaseInformation): Call<BillUpdateResponse> {
            val retrofit = Retrofit.Builder()
                .baseUrl(FinSimpleSettings.apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(BillApiService::class.java)
            val call = service.updateBill(
                "Bearer ${UserWrapperSettings.token}", bill.toUpdateRequest(id)
            )

            return call
        }

        //	-------------------------------------------
        //		        Setters and Getters
        //	-------------------------------------------
        public fun client(): OkHttpClient = this.m_Client
    }
}