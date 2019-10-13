package com.example.ahmad2.shopproject;

import android.util.Log;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface RetrofitService {

    @POST("readuserpassfromdb.php")
    Call<User> getUser(@Body Login login);

    @POST("insertuserpasstodb.php")
    Call<ResponseMessageToken> insertUser(@Body RequestUserApiKey requestUserApiKey);

    @POST("editpassfromdb.php")
    Call<ResponseMessageToken> editPass(@Body Login login);

    @HTTP(method = "DELETE",path = "deleteuserforadmin.php",hasBody = true)
    Call<ResponseMessageToken> deleteUser(@Body RequestUserEditForAdmin requestUserEditForAdmin);

    @PUT("updateuserforadmin.php")
    Call<ResponseMessageToken> updateSeen(@Body RequestUserEditForAdmin requestUserEditForAdmin);

    @PUT("updateusertodb.php")
    Call<ResponseMessageToken> updateUserInformation(@Body RequestUserApiKey requestUserApiKey);

    @POST("sendsmsforrecovery.php")
    Call<ResponseMessageToken> sendSmsRecovery(@Body PanelInfo panelInfo);

    @POST("readuserforadmin.php")
    Call<ResponseUserListToken> getUserListForAdmin(@Body Login login);

    @POST("searchregisterfromdb.php")
    Call<ResponseRegisterListToken> searchRegisterListForAdmin(@Body RequestUserEditForAdmin requestUserEditForAdmin);
// insert
    @POST("insertobjecttodb.php")
    Call<ResponseMessageToken> insertObject(@Body RequestObjectApiKey requestObjectApiKey);

    @POST("insertorderusertodb.php")
    Call<ResponseMessageToken> insertOrder(@Body RequestOrder requestOrder);

    @PUT("updateobjectindb.php")
    Call<ResponseMessageToken> updateObject(@Body RequestObjectApiKey requestObjectApiKey);

    @HTTP(method = "DELETE",path = "deleteobjectfromdb.php",hasBody = true)
    Call<ResponseMessageToken> deleteObject(@Body RequestObjectApiKey requestObjectApiKey);

    @POST("readobjectforuser.php")
    Call<ResponseObjectListToken> getObjectListForUser(@Body Login login);

    @POST("readobjectforuseragree.php")
    Call<ResponseObjectListToken> getObjectListForUserAgree(@Body Login login);

    @POST("readobjectforadmin.php")
    Call<ResponseObjectListToken> getObjectListForAdmin(@Body Login login);

    @POST("searchobjectforuser.php")
    Call<ResponseObjectListToken> getObjectListBySearchForuUser(@Body Login login);

    @POST("searchobjectforadmin.php")
    Call<ResponseObjectListToken> getObjectListBySearchForAdmin(@Body Login login);

    @POST("readorderforuser.php")
    Call<ResponseOrderListToken> getOrderListForUser(@Body Login login);

    @POST("readorderforadmin.php")
    Call<ResponseOrderListToken> getOrderListForAdmin(@Body Login login);

    @POST("searchorderuserforadmin.php")
    Call<ResponseOrderListToken> getOrderListForAdminBySearch(@Body RequestUserEditForAdmin requestUserEditForAdmin);

    @PUT("updateorder.php")
    Call<ResponseMessageToken> updateOrder(@Body RequestOrder requestOrder);

    @HTTP(method = "DELETE",path = "deleteorderfromdb.php",hasBody = true)
    Call<ResponseMessageToken> deleteOrder(@Body RequestOrder requestOrder);

    @PUT("updateseen.php")
    Call<ResponseMessageToken> updateSeen(@Body RequestOrder requestOrder);


    @POST("insertshoptodb.php")
    Call<ResponseMessageToken> insertShop(@Body RequestShopApiKey requestShopApiKey);

    @POST("readshoplistfromdb.php")
    Call<ResponseShopListToken> getShopList(@Body Login login);

    @POST("readshoplistforagree.php")
    Call<ResponseShopListToken> getShopListAgree(@Body Login login);

    @PUT("updateshopinfotodb.php")
    Call<ResponseMessageToken> updateShopInfo(@Body RequestShopApiKey requestShopApiKey);

    @POST("NonSendSmsForRegister.php")
    Call<ResponseMessageToken> sendSmsRegister(@Body PanelInfo panelInfo);

    @POST("registeruser.php")
    Call<ResponseMessageToken> newRegister(@Body Login login);

    @POST("readregisterforadmin.php")
    Call<ResponseRegisterListToken> getRegisteList(@Body Login login);

    @POST("searchshopfromdb.php")
    Call<ResponseShopListToken> getShopListBySearch(@Body Login login);

    @POST("authorizationnew.php")
    Call<ResponseShopListToken> authorization(@Body Login login);

    @POST("getinformationapp.php")
    Call<ResponseTokenInformation> getMessageApp(@Body Login login);

//    @GET("insertinformation.php")
//    Call<ResponseMessageToken> insertInformation(@Query("message") String message);
//OR
    @FormUrlEncoded
    @POST("insertinformation.php")
    Call<ResponseMessageToken> insertInformation(@Field("version") String version,@Field("message") String message);

    @POST("insertcomment.php")
    Call<ResponseMessageToken> insertComment(@Body Login login);

    @POST("readobjectforstore.php")
    Call<ResponseObjectListToken> getObjectListForStore(@Body Login login);

    @POST("searchobjectforstore.php")
    Call<ResponseObjectListToken> getObjectListForStoreBySearch(@Body Login login);

    @PUT("insertobjecttosell.php")
    Call<ResponseMessageToken> insertObjectToSell(@Body Login login);

    @PUT("deleteobjectfromsell.php")
    Call<ResponseMessageToken> deleteObjectFromSell(@Body Login login);

    @POST("readsellspecial.php")
    Call<ResponseShopListToken> getSellSpecial(@Body Login login);

    @POST("readsellspecialagree.php")
    Call<ResponseShopListToken> getSellSpecialAgree(@Body Login login);

    @PUT("insertsellspecialtodb.php")
    Call<ResponseMessageToken> insertSellSpecial(@Body RequestShopApiKey requestShopApiKey);

    @POST("checkisshop.php")
    Call<ResponseMessageToken> checkShop(@Body Login login);

    @PUT("updateobjectagree.php")
    Call<ResponseMessageToken> updateObjectAgree(@Body RequestLoginObject requestLoginObject);

    @PUT("updateshopagree.php")
    Call<ResponseMessageToken> updateShopAgree(@Body RequestLoginShop requestLoginShop);

    @PUT("updatesellspecialagree.php")
    Call<ResponseMessageToken> updateSellSpecialAgree(@Body RequestLoginShop requestLoginShop);

    @POST("readcomment.php")
    Call<ResponseCommentListToken> getCommentList(@Body Login login);

    @HTTP(method = "DELETE",path = "deletecomment.php",hasBody = true)
    Call<ResponseMessageToken> deleteComment(@Body RequestLoginComment requestLoginComment);

    @HTTP(method = "DELETE",path = "deletesellspecial.php",hasBody = true)
    Call<ResponseMessageToken> deleteSellSpecial(@Body RequestLoginShop requestLoginShop);

    @HTTP(method = "DELETE",path = "deleteshop.php",hasBody = true)
    Call<ResponseMessageToken> deleteShop(@Body RequestLoginShop requestLoginShop);

    @HTTP(method = "DELETE",path = "deleteobject.php",hasBody = true)
    Call<ResponseMessageToken> deleteObjectByAdmin(@Body RequestLoginObject requestLoginObject);

    @POST("checkregister.php")
    Call<ResponseMessageToken> checkRegister(@Body Login login);

    @PUT("updateshowinfo.php")
    Call<ResponseMessageToken> updateShowInfo(@Body Login login);
}
