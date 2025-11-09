package com.example.fopsmart.data

import com.example.fopsmart.data.model.*
import com.example.fopsmart.data.network.MainApiService
import retrofit2.Response

class ProfileRepository(private val apiService: MainApiService) {

    suspend fun getProfile(token: String): Response<ProfileResponse> {
        return apiService.getProfile("Bearer $token")
    }

    suspend fun updateProfile(
        token: String,
        firstName: String,
        lastName: String,
        email: String
    ): Response<UpdateProfileResponse> {
        val request = UpdateProfileRequest(firstName, lastName, email)
        return apiService.updateProfile("Bearer $token", request)
    }

    suspend fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Response<ChangePasswordResponse> {
        val request = ChangePasswordRequest(currentPassword, newPassword, confirmPassword)
        return apiService.changePassword("Bearer $token", request)
    }

    suspend fun getFopConfig(token: String): Response<FopConfigResponse> {
        return apiService.getFopConfig("Bearer $token")
    }

    suspend fun updateFopConfig(
        token: String,
        fopGroup: Int,
        taxSystem: String
    ): Response<UpdateFopResponse> {
        val request = UpdateFopRequest(fopGroup, taxSystem)
        return apiService.updateFopConfig("Bearer $token", request)
    }

    suspend fun deleteAccount(
        token: String,
        password: String,
        confirmation: String
    ): Response<DeleteAccountResponse> {
        val request = DeleteAccountRequest(password, confirmation)
        return apiService.deleteAccount("Bearer $token", request)
    }
}