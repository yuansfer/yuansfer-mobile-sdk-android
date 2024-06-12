package com.pockyt.pay.cashapp

import android.os.AsyncTask
import app.cash.paykit.core.CashAppPay
import app.cash.paykit.core.CashAppPayFactory
import app.cash.paykit.core.CashAppPayListener
import app.cash.paykit.core.CashAppPayState
import app.cash.paykit.core.models.sdk.CashAppPayCurrency
import app.cash.paykit.core.models.sdk.CashAppPayPaymentAction
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.CashAppReq
import com.pockyt.pay.resp.CashAppResp
import com.pockyt.pay.util.PockytCodes
import java.math.BigDecimal

class CashAppStrategy : IPaymentStrategy<CashAppReq, CashAppResp>, CashAppPayListener {

    private lateinit var payKitSdk: CashAppPay
    private var payResp: ((CashAppResp) -> Unit)? = null

    override fun requestPay(req: CashAppReq, resp: (CashAppResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(CashAppResp(PockytCodes.DUPLICATE, "CashApp payment is already in progress."))
            return
        }
        payResp = resp
        payKitSdk = if (req.sandboxEnv) CashAppPayFactory.createSandbox(req.clientId) else CashAppPayFactory.create(req.clientId)
        payKitSdk.registerForStateUpdates(this)
        processPayment(req.requestData, req.redirectUrl)
    }

    private fun processPayment(requestData: CashAppRequestData, redirectURI: String) {
        val payAction: CashAppPayPaymentAction = when (requestData) {
            is CashAppRequestData.OneTimeAction -> {
                CashAppPayPaymentAction.OneTimeAction(
                    CashAppPayCurrency.USD,
                    requestData.amount?.let { BigDecimal.valueOf(it).multiply(BigDecimal.valueOf(100)) }?.toInt(),
                    requestData.scopeId)
            }
            is CashAppRequestData.OnFileAction -> {
                CashAppPayPaymentAction.OnFileAction(requestData.scopeId, requestData.accountReferenceId)
            }
        }
        val asyncTask = CashAppAsyncTask {
            payKitSdk.createCustomerRequest(payAction, redirectURI)
        }
        if (asyncTask.status != AsyncTask.Status.RUNNING) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun cashAppPayStateDidChange(newState: CashAppPayState) {
        when (newState) {
            is CashAppPayState.Approved -> handleApprovedState()
            CashAppPayState.Authorizing, CashAppPayState.CreatingCustomerRequest -> handleLoadingState()
            CashAppPayState.Declined -> handleDeclinedState()
            CashAppPayState.NotStarted, CashAppPayState.PollingTransactionStatus,
            CashAppPayState.UpdatingCustomerRequest, CashAppPayState.RetrievingExistingCustomerRequest,
            CashAppPayState.Refreshing -> handleOptionalLoadingState()
            is CashAppPayState.CashAppPayExceptionState -> handleExceptionState(newState)
            is CashAppPayState.ReadyToAuthorize -> payKitSdk.authorizeCustomerRequest()
        }
    }

    private fun handleApprovedState() {
        payResp?.invoke(CashAppResp(PockytCodes.SUCCESS))
        payResp = null
    }

    private fun handleLoadingState() {
        // Use this state to display loading status if desired.
    }

    private fun handleDeclinedState() {
        payResp?.invoke(CashAppResp(PockytCodes.CANCEL, "Declined by user"))
        payResp = null
    }

    private fun handleOptionalLoadingState() {
        // Use this state to display loading status if desired.
    }

    private fun handleExceptionState(newState: CashAppPayState.CashAppPayExceptionState) {
        payResp?.invoke(CashAppResp(PockytCodes.ERROR, newState.exception.toString()))
        payResp = null
    }

}

