package permissions.dispatcher.ktx

import androidx.lifecycle.*

internal class PermissionRequestViewModel : ViewModel() {
    private val permissionRequestResult = MutableLiveData<Event<PermissionResult>>()

    fun postPermissionRequestResult(permissionResult: PermissionResult) =
        permissionRequestResult.postValue(Event(permissionResult))

    inline fun observe(
        owner: LifecycleOwner,
        crossinline requiresPermission: Fun,
        noinline onPermissionDenied: Fun?,
        noinline onNeverAskAgain: Fun?
    ) {
        permissionRequestResult.observe(owner, Observer {
            when (it.getContentIfNotHandled()) {
                PermissionResult.GRANTED -> requiresPermission.invoke()
                PermissionResult.DENIED -> onPermissionDenied?.invoke()
                PermissionResult.DENIED_AND_DISABLED -> onNeverAskAgain?.invoke()
                else -> Unit
            }
        })
    }

    fun removeObservers(owner: LifecycleOwner) = permissionRequestResult.removeObservers(owner)
}
