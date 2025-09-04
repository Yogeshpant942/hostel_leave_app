import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.hostelleaveapp.Util.EmailSender
import com.google.common.util.concurrent.ListenableFuture

class EmailWorker(appContext: Context, params: WorkerParameters) :
    ListenableWorker(appContext, params) {

    override fun startWork(): ListenableFuture<Result> {
        val toEmail = inputData.getString("toEmail") ?: return immediateFailedFuture()
        val subject = inputData.getString("subject") ?: return immediateFailedFuture()
        val body = inputData.getString("body") ?: return immediateFailedFuture()

        return CallbackToFutureAdapter.getFuture { completer ->
            EmailSender.sendEmail(toEmail, subject, body) { success, message ->
                Log.d("EmailWorker", message)
                if (success) {
                    completer.set(Result.success())
                } else {
                    completer.set(Result.failure())
                }
            }
        }
    }

    private fun immediateFailedFuture(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { it.set(Result.failure()) }
    }
}
