package com.example.mohbook.other

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.theartofdev.edmodo.cropper.CropImage

//This class defines the intent to launch the secondActivity and also represents the data passed and received
class CropActivityResultContract : ActivityResultContract<Any?, Uri?>() {

    override fun createIntent(context: Context, input: Any?): Intent {
        return CropImage.activity().getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return CropImage.getActivityResult(intent).uri
    }
}