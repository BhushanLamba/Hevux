package com.softbrain.hevix.network;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WEB_SERVICE {
    @FormUrlEncoded
    @POST("api/Login")
    Call<JsonObject> login(@Field("username") String username,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("api/GetBalance")
    Call<JsonObject> getBalance(@Field("UserId") String UserId);

    @FormUrlEncoded
    @POST("api/CustomerList")
    Call<JsonObject> getCustomers(@Field("UserId") String UserId,
                                  @Field("Days") String Days);


    @FormUrlEncoded
    @POST("api/ProductList")
    Call<JsonObject> getProducts(@Field("UserId") String UserId);

    @FormUrlEncoded
    @POST("api/PendingBills")
    Call<JsonObject> getPendingBill(@Field("UserId") String UserId,
                                    @Field("DueDate") String DueDate);

    @FormUrlEncoded
    @POST("api/GetBillDetails")
    Call<JsonObject> getBillDetails(@Field("UserId") String UserId,
                                    @Field("BillNo") String BillNo);

    @FormUrlEncoded
    @POST("api/WalletLedger")
    Call<JsonObject> getLedger(@Field("UserId") String UserId,
                               @Field("Datefrom") String Datefrom,
                               @Field("Dateto") String Dateto);

    @FormUrlEncoded
    @POST("api/StockLedger")
    Call<JsonObject> getStockLedger(@Field("UserId") String UserId,
                               @Field("Datefrom") String Datefrom,
                               @Field("Dateto") String Dateto);


    @FormUrlEncoded
    @POST("api/AddtoCart")
    Call<JsonObject> addToCart(@Field("ProductId") String ProductId,
                               @Field("ProductName") String ProductName,
                               @Field("CustomerId") String CustomerId,
                               @Field("LoginUserId") String LoginUserId,
                               @Field("QNT") String QNT);


    @FormUrlEncoded
    @POST("api/ViewCart")
    Call<JsonObject> getCart(@Field("CustomerId") String CustomerId,
                             @Field("LoginUserId") String LoginUserId);

    @FormUrlEncoded
    @POST("api/DeleteCart")
    Call<JsonObject> deleteCartItem(@Field("CartId") String CartId,
                                    @Field("LoginUserId") String LoginUserId);


    @FormUrlEncoded
    @POST("api/OrderBooking")
    Call<JsonObject> bookOrder(@Field("CustomerId") String CustomerId,
                               @Field("LoginUserId") String LoginUserId,
                               @Field("paymentmod") String paymentmod,
                               @Field("ReceivedAmt") String ReceivedAmt,
                               @Field("CustName") String CustName,
                               @Field("CustAddress") String CustAddress,
                               @Field("MobileNo") String MobileNo,
                               @Field("CustArea") String CustArea,
                               @Field("Remarks") String Remarks);

    @FormUrlEncoded
    @POST("api/BillReport")
    Call<JsonObject> getReport(@Field("UserId") String UserId,
                               @Field("PaymentStatus") String PaymentStatus);

    @FormUrlEncoded
    @POST("api/AddCustomer")
    Call<JsonObject> addCustomer(@Field("ShopName") String ShopName,
                                 @Field("CustomerName") String CustomerName,
                                 @Field("EmailId") String EmailId,
                                 @Field("Phone") String Phone,
                                 @Field("Days") String Days,
                                 @Field("FullAddress") String FullAddress,
                                 @Field("PinCode") String PinCode,
                                 @Field("State") String State,
                                 @Field("Area") String Area,
                                 @Field("AreaId") String AreaId,
                                 @Field("Remarks") String Remarks);

    @FormUrlEncoded
    @POST("api/AreaList")
    Call<JsonObject> getArea(@Field("UserId") String UserId,
                             @Field("Days") String Days);

    @FormUrlEncoded
    @POST("api/Paybalance")
    Call<JsonObject> payPendingBalance(@Field("LoginUserId") String LoginUserId,
                                       @Field("PaymentMode") String PaymentMode,
                                       @Field("BillNo") String BillNo,
                                       @Field("Remarks") String Remarks,
                                       @Field("Amount") String Amount);


    @FormUrlEncoded
    @POST("api/AddtoReturnCart")
    Call<JsonObject> addToReturnCart(@Field("ProductId") String ProductId,
                                     @Field("ProductName") String ProductName,
                                     @Field("CustomerId") String CustomerId,
                                     @Field("LoginUserId") String LoginUserId,
                                     @Field("BillNo") String BillNo,
                                     @Field("QNT") String QNT);

    @FormUrlEncoded
    @POST("api/ViewReturnCart")
    Call<JsonObject> getReturnCart(@Field("CustomerId") String CustomerId,
                                   @Field("LoginUserId") String LoginUserId,
                                   @Field("BillNo") String BillNo);

    @FormUrlEncoded
    @POST("api/OrderReturn")
    Call<JsonObject> bookReturnOrder(@Field("CustomerId") String CustomerId,
                                     @Field("LoginUserId") String LoginUserId,
                                     @Field("agentname") String agentname,
                                     @Field("Billno") String Billno,
                                     @Field("Remarks") String Remarks);

    @FormUrlEncoded
    @POST("api/DeleteReturnCart")
    Call<JsonObject> deleteReturnCartItem(@Field("CartId") String CartId,
                                          @Field("LoginUserId") String LoginUserId);

}

