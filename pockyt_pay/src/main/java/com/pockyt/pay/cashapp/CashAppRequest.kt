package com.pockyt.pay.cashapp

import android.os.Parcel
import android.os.Parcelable

sealed class CashAppRequest : Parcelable {

    abstract val scopeId: String?

    data class OneTimeRequest(
        override val scopeId: String?,
        val amount: Double?
    ) : CashAppRequest() {

        companion object CREATOR : Parcelable.Creator<OneTimeRequest> {
            override fun createFromParcel(parcel: Parcel): OneTimeRequest {
                return OneTimeRequest(parcel)
            }

            override fun newArray(size: Int): Array<OneTimeRequest?> {
                return arrayOfNulls(size)
            }
        }

        private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(scopeId)
            parcel.writeDouble(amount ?: 0.0)
        }

        override fun describeContents(): Int {
            return 0
        }
    }

    data class OnFileRequest(
        override val scopeId: String?,
        val accountReferenceId: String? = null
    ) : CashAppRequest() {

        companion object CREATOR : Parcelable.Creator<OnFileRequest> {
            override fun createFromParcel(parcel: Parcel): OnFileRequest {
                return OnFileRequest(parcel)
            }

            override fun newArray(size: Int): Array<OnFileRequest?> {
                return arrayOfNulls(size)
            }
        }

        private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(scopeId)
            parcel.writeString(accountReferenceId)
        }

        override fun describeContents(): Int {
            return 0
        }
    }

}


