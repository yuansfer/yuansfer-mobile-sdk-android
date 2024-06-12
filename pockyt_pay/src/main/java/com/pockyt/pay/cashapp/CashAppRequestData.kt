package com.pockyt.pay.cashapp

import android.os.Parcel
import android.os.Parcelable

sealed class CashAppRequestData : Parcelable {

    abstract val scopeId: String?

    data class OneTimeAction(
        val amount: Double?,
        override val scopeId: String?
    ) : CashAppRequestData() {

        companion object CREATOR : Parcelable.Creator<OneTimeAction> {
            override fun createFromParcel(parcel: Parcel): OneTimeAction {
                return OneTimeAction(parcel)
            }

            override fun newArray(size: Int): Array<OneTimeAction?> {
                return arrayOfNulls(size)
            }
        }

        private constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeDouble(amount ?: 0.0)
            parcel.writeString(scopeId)
        }

        override fun describeContents(): Int {
            return 0
        }
    }

    data class OnFileAction(
        override val scopeId: String?,
        val accountReferenceId: String? = null
    ) : CashAppRequestData() {

        companion object CREATOR : Parcelable.Creator<OnFileAction> {
            override fun createFromParcel(parcel: Parcel): OnFileAction {
                return OnFileAction(parcel)
            }

            override fun newArray(size: Int): Array<OnFileAction?> {
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


